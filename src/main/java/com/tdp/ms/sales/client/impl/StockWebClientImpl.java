package com.tdp.ms.sales.client.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tdp.genesis.core.constants.HttpHeadersKey;
import com.tdp.genesis.core.exception.GenesisException;
import com.tdp.genesis.core.exception.GenesisExceptionBuilder;
import com.tdp.ms.sales.client.StockWebClient;
import com.tdp.ms.sales.model.entity.Sale;
import com.tdp.ms.sales.model.request.ReserveStockRequest;
import com.tdp.ms.sales.model.response.ReserveStockResponse;
import com.tdp.ms.sales.repository.SalesRepository;
import java.util.HashMap;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

/**
 * Class: StockWebClientImpl. <br/>
 * <b>Copyright</b>: &copy; 2020 Telef&oacute;nica del Per&uacute;<br/>
 * <b>Company</b>: Telef&oacute;nica del Per&uacute;<br/>
 *
 * @author Telef&oacute;nica del Per&uacute; (TDP) <br/>
 *         <u>Service Provider</u>: Everis Per&uacute; SAC (EVE) <br/>
 *         <u>Developed by</u>: <br/>
 *         <ul>
 *         <li>Cesar Gomez</li>
 *         </ul>
 *         <u>Changes</u>:<br/>
 *         <ul>
 *         <li>2020-09-23 Creaci&oacute;n del proyecto.</li>
 *         </ul>
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
public class StockWebClientImpl implements StockWebClient {

    private final WebClient webClientInsecure;

    @Autowired
    private SalesRepository salesRepository;

    @Value("${application.endpoints.stock.reserve_stock_url}")
    private String reserveStockUrl;

    private static final Logger LOG = LoggerFactory.getLogger(StockWebClientImpl.class);

    @Override
    public Mono<ReserveStockResponse> reserveStock(ReserveStockRequest request, HashMap<String, String> headersMap,
                                                   Sale sale) {
        LOG.info("->Reserve Stock Request: ".concat(new Gson().toJson(request)));
        return webClientInsecure
                .post()
                .uri(reserveStockUrl)
                .header(HttpHeadersKey.UNICA_SERVICE_ID, headersMap.get(HttpHeadersKey.UNICA_SERVICE_ID))
                .header(HttpHeadersKey.UNICA_APPLICATION, headersMap.get(HttpHeadersKey.UNICA_APPLICATION))
                .header(HttpHeadersKey.UNICA_PID, headersMap.get(HttpHeadersKey.UNICA_PID))
                .header(HttpHeadersKey.UNICA_USER, headersMap.get(HttpHeadersKey.UNICA_USER))
                .bodyValue(request)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(ReserveStockResponse.class)
                .onErrorResume(throwable -> throwExceptionReserveStock(sale, throwable));
    }

    // TODO: Definir al método throwExceptionReserveStock como privado, por tema de los test con Mockito no funcionaba
    @Override
    public Mono<ReserveStockResponse> throwExceptionReserveStock(Sale sale, Throwable error) throws GenesisException {
        sale.setStatus("NEGOCIACION");
        return salesRepository.save(sale)
                .flatMap(saleSaved -> this.throwException(saleSaved, error));
    }

    private Mono<ReserveStockResponse> throwException(Sale saleSaved, Throwable error) {
        GenesisExceptionBuilder builder = GenesisException.builder();
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        String saleJsonString = gson.toJson(saleSaved);

        if (error instanceof WebClientResponseException) {
            WebClientResponseException responseException = (WebClientResponseException) error;
            HttpStatus statusException = responseException.getStatusCode();

            if (statusException.equals(HttpStatus.BAD_REQUEST)) {
                // Throw 400 status code
                return Mono.error(builder
                        .exceptionId("SVC0001")
                        .wildcards(new String[]{"Bad Request from Reserve Stock FE+Simple Service"})
                        .build());
            } else if (statusException.equals(HttpStatus.NOT_FOUND)) {
                // Throw 404 status code
                return Mono.error(builder
                        .exceptionId("SVC1006")
                        .wildcards(new String[]{"Resource Not Found from Reserve Stock FE+Simple Service"})
                        .build());
            } else {
                // Throw 409 status code
                return Mono.error(builder
                        .exceptionId("SVC0409")
                        .wildcards(new String[]{saleJsonString})
                        .build());
            }
        } else {
            // Throw 409 status code
            return Mono.error(builder
                    .exceptionId("SVC0409")
                    .wildcards(new String[]{saleJsonString})
                    .build());
        }
    }
}
