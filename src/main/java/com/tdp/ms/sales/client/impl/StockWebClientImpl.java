package com.tdp.ms.sales.client.impl;

import com.tdp.genesis.core.constants.HttpHeadersKey;
import com.tdp.genesis.core.exception.GenesisException;
import com.tdp.genesis.core.exception.GenesisExceptionBuilder;
import com.tdp.ms.sales.client.StockWebClient;
import com.tdp.ms.sales.model.request.ReserveStockRequest;
import com.tdp.ms.sales.model.response.ReserveStockResponse;
import com.tdp.ms.commons.util.MapperUtils;
import java.util.HashMap;
import lombok.RequiredArgsConstructor;
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

    @Value("${application.endpoints.stock.reserve_stock_url}")
    private String reserveStockUrl;

    @Override
    public Mono<ReserveStockResponse> reserveStock(ReserveStockRequest request, HashMap<String, String> headersMap) {
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
                .onErrorResume(this::fallbackReserveStock);
    }

    public Mono<ReserveStockResponse> fallbackReserveStock(Throwable error) throws GenesisException {

        if (error instanceof WebClientResponseException) {
            WebClientResponseException responseException = (WebClientResponseException) error;
            HttpStatus statusException = responseException.getStatusCode();

            String responseBodyExceptionAsString = responseException.getResponseBodyAsString();
            GenesisException responseGenesisException = MapperUtils.mapper(GenesisException.class,
                    responseBodyExceptionAsString);
            GenesisExceptionBuilder builder = GenesisException.builder();

            String[] wildcardsException = responseGenesisException.getWildcards();

            if (statusException.equals(HttpStatus.BAD_REQUEST)) {
                // Throw 400 status code
                return Mono.error(builder
                        .exceptionId("SVC0001")
                        .userMessage("Bad Request from Reserve Stock FE+Simple Service")
                        .wildcards(wildcardsException)
                        .build());
            } else if (statusException.equals(HttpStatus.NOT_FOUND)) {
                // Throw 404 status code
                return Mono.error(builder
                        .exceptionId("SVC1006")
                        .userMessage("Resource Not Found from Reserve Stock FE+Simple Service")
                        .wildcards(wildcardsException)
                        .build());
            } else if (statusException.equals(HttpStatus.INTERNAL_SERVER_ERROR)) {
                // Throw 500 status code
                return Mono.error(builder
                        .exceptionId("SVR1000")
                        .userMessage("There was a problem from Reserve Stock FE+Simple Service")
                        .wildcards(wildcardsException)
                        //.wildcards(new String[]{"Reserve Stock FE+Simple Service: "})
                        .build());
            } else {
                return Mono.error(responseException);
            }
        } else {
            return Mono.error(error);
        }
    }

}
