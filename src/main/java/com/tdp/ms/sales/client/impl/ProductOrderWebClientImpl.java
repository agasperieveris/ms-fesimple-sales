package com.tdp.ms.sales.client.impl;

import com.tdp.genesis.core.constants.HttpHeadersKey;
import com.tdp.genesis.core.exception.GenesisException;
import com.tdp.genesis.core.exception.GenesisExceptionBuilder;
import com.tdp.ms.commons.util.MapperUtils;
import com.tdp.ms.sales.client.ProductOrderWebClient;
import com.tdp.ms.sales.model.dto.productorder.CreateProductOrderGeneralRequest;
import com.tdp.ms.sales.model.entity.Sale;
import com.tdp.ms.sales.model.response.ProductorderResponse;
import com.tdp.ms.sales.repository.SalesRepository;
import com.tdp.ms.sales.utils.Constants;
import java.util.HashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

/**
 * Class: BusinessParameterWebClientImpl. <br/>
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
public class ProductOrderWebClientImpl implements ProductOrderWebClient {

    private final WebClient webClientInsecure;

    @Autowired
    private SalesRepository salesRepository;

    @Value("${application.endpoints.product_order.create_product_order_url}")
    private String createProductOrderUrl;

    @Override
    public Mono<ProductorderResponse> createProductOrder(CreateProductOrderGeneralRequest request,
                                                         HashMap<String,String> headersMap, Sale sale) {
        return webClientInsecure
                .post()
                .uri(createProductOrderUrl)
                .header(HttpHeadersKey.UNICA_SERVICE_ID, headersMap.get(HttpHeadersKey.UNICA_SERVICE_ID))
                .header(HttpHeadersKey.UNICA_APPLICATION, headersMap.get(HttpHeadersKey.UNICA_APPLICATION))
                .header(HttpHeadersKey.UNICA_PID, headersMap.get(HttpHeadersKey.UNICA_PID))
                .header(HttpHeadersKey.UNICA_USER, headersMap.get(HttpHeadersKey.UNICA_USER))
                .header(Constants.UFX_AUTHORIZATION, headersMap.get(Constants.UFX_AUTHORIZATION))
                .header("Content-Type", "application/json")
                .bodyValue(request)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(ProductorderResponse.class)
                .onErrorResume(throwable -> fallbackCreateProductOrder(throwable, sale));
    }

    @Override
    public Mono<ProductorderResponse> fallbackCreateProductOrder(Throwable error, Sale sale) throws GenesisException {
        sale.setStatus("PENDIENTE");
        return salesRepository.save(sale)
                .flatMap(saleSaved -> this.throwExceptionCreateProductOrder(error));
    }

    @Override
    public Mono<ProductorderResponse> throwExceptionCreateProductOrder(Throwable error) throws GenesisException {
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
                        .userMessage("Bad Request from Create Product Order FE+Simple Service")
                        .wildcards(wildcardsException)
                        .build());
            } else if (statusException.equals(HttpStatus.NOT_FOUND)) {
                // Throw 404 status code
                return Mono.error(builder
                        .exceptionId("SVC1006")
                        .userMessage("Resource Not Found from Create Product Order FE+Simple Service")
                        .wildcards(wildcardsException)
                        .build());
            } else if (statusException.equals(HttpStatus.INTERNAL_SERVER_ERROR)) {
                // Throw 500 status code
                return Mono.error(builder
                        .exceptionId("SVR1000")
                        .userMessage("There was a problem from Create Product Order FE+Simple Service")
                        //.wildcards(wildcardsException)
                        .build());
            } else {
                return Mono.error(responseException);
            }
        } else {
            return Mono.error(error);
        }
    }

}
