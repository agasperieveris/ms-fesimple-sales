package com.tdp.ms.sales.client.impl;

import com.tdp.genesis.core.constants.ErrorCategory;
import com.tdp.genesis.core.constants.HttpHeadersKey;
import com.tdp.genesis.core.exception.GenesisException;
import com.tdp.genesis.core.exception.GenesisExceptionBuilder;
import com.tdp.ms.sales.client.BusinessParameterWebClient;
import com.tdp.ms.sales.client.ProductOrderWebClient;
import com.tdp.ms.sales.model.dto.productorder.CreateProductOrderGeneralRequest;
import com.tdp.ms.sales.model.request.GetSalesCharacteristicsRequest;
import com.tdp.ms.sales.model.response.GetSalesCharacteristicsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;

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

    @Value("${application.endpoints.product_order.create_product_order_url}")
    private String createProductOrderUrl;

    @Override
    public Mono<Object> createProductOrder(CreateProductOrderGeneralRequest request, HashMap<String,String> headersMap) {
        GenesisExceptionBuilder builder = GenesisException.builder();
        return webClientInsecure
                .post()
                .uri(createProductOrderUrl)
                .header(HttpHeadersKey.UNICA_SERVICE_ID, headersMap.get(HttpHeadersKey.UNICA_SERVICE_ID))
                .header(HttpHeadersKey.UNICA_APPLICATION, headersMap.get(HttpHeadersKey.UNICA_APPLICATION))
                .header(HttpHeadersKey.UNICA_PID, headersMap.get(HttpHeadersKey.UNICA_PID))
                .header(HttpHeadersKey.UNICA_USER, headersMap.get(HttpHeadersKey.UNICA_USER))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status -> status == HttpStatus.BAD_REQUEST,
                        clientResponse -> Mono.error(
                                builder.category(ErrorCategory.INVALID_REQUEST)
                                        .addDetail(true)
                                        .withComponent("sales")
                                        .withDescription("Bad Request from Post Create Product Order FE+Simple Service")
                                        .push()
                                        .build()))
                .onStatus(status -> status == HttpStatus.NOT_FOUND,
                        clientResponse -> Mono.error(
                                builder.category(ErrorCategory.RESOURCE_NOT_FOUND)
                                        .addDetail(true)
                                        .withComponent("sales")
                                        .withDescription("Not Found Status from Post Create Product Order FE+Simple Service")
                                        .push()
                                        .build()))
                .bodyToMono(Object.class);
    }

}
