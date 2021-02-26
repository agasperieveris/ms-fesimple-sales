package com.tdp.ms.sales.client.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tdp.genesis.core.constants.HttpHeadersKey;
import com.tdp.genesis.core.exception.GenesisException;
import com.tdp.genesis.core.exception.GenesisExceptionBuilder;
import com.tdp.ms.sales.client.QuotationWebClient;
import com.tdp.ms.sales.model.entity.Sale;
import com.tdp.ms.sales.model.request.CreateQuotationRequest;
import com.tdp.ms.sales.model.response.CreateQuotationResponse;
import com.tdp.ms.sales.repository.SalesRepository;
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
 * Class: QuotationWebClientImpl. <br/>
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
 *         <li>2020-11-03 Creaci&oacute;n del proyecto.</li>
 *         </ul>
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
public class QuotationWebClientImpl implements QuotationWebClient {

    private final WebClient webClientInsecure;

    @Autowired
    private SalesRepository salesRepository;

    @Value("${application.endpoints.quotation.create_quotation}")
    private String createQuotationUrl;

    private static final Logger LOG = LoggerFactory.getLogger(QuotationWebClientImpl.class);

    @Override
    public Mono<CreateQuotationResponse> createQuotation(CreateQuotationRequest request, Sale sale) {
        LOG.info("->Quotation Request: ".concat(new Gson().toJson(request.getBody())));
        return webClientInsecure
                .post()
                .uri(createQuotationUrl)
                .header(HttpHeadersKey.UNICA_SERVICE_ID, request.getHeadersMap().get(HttpHeadersKey.UNICA_SERVICE_ID))
                .header(HttpHeadersKey.UNICA_APPLICATION, request.getHeadersMap().get(HttpHeadersKey.UNICA_APPLICATION))
                .header(HttpHeadersKey.UNICA_PID, request.getHeadersMap().get(HttpHeadersKey.UNICA_PID))
                .header(HttpHeadersKey.UNICA_USER, request.getHeadersMap().get(HttpHeadersKey.UNICA_USER))
                .bodyValue(request.getBody())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(CreateQuotationResponse.class)
                .onErrorResume(throwable -> throwExceptionCreateQuotation(sale, throwable));
    }

    // TODO: Definir al método throwExceptionCreateQuotation como privado, por tema de los test con Mockito no funciona
    @Override
    public Mono<CreateQuotationResponse> throwExceptionCreateQuotation(Sale sale, Throwable error)
                                                                                            throws GenesisException {
        sale.setStatus("NEGOCIACION");
        return salesRepository.save(sale)
                .flatMap(saleSaved -> this.throwException(saleSaved, error));
    }

    private Mono<CreateQuotationResponse> throwException(Sale saleSaved, Throwable error) {
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
                        .wildcards(new String[]{"Bad Request from Create Quotation FE+Simple Service"})
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
