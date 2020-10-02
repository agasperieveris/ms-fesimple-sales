package com.tdp.ms.sales.client.impl;

import com.tdp.genesis.core.constants.ErrorCategory;
import com.tdp.genesis.core.constants.HttpHeadersKey;
import com.tdp.genesis.core.exception.GenesisException;
import com.tdp.genesis.core.exception.GenesisExceptionBuilder;
import com.tdp.ms.sales.client.ProductOrderWebClient;
import com.tdp.ms.sales.model.dto.productorder.CreateProductOrderGeneralRequest;
import java.util.HashMap;

import com.tdp.ms.sales.model.response.ProductorderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
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

    @Value("${application.endpoints.product_order.create_product_order_url}")
    private String createProductOrderUrl;

    @Override
    public Mono<ProductorderResponse> createProductOrder(CreateProductOrderGeneralRequest request, HashMap<String,String> headersMap) {
        return webClientInsecure
                .post()
                .uri(createProductOrderUrl)
                .header(HttpHeadersKey.UNICA_SERVICE_ID, headersMap.get(HttpHeadersKey.UNICA_SERVICE_ID))
                .header(HttpHeadersKey.UNICA_APPLICATION, headersMap.get(HttpHeadersKey.UNICA_APPLICATION))
                .header(HttpHeadersKey.UNICA_PID, headersMap.get(HttpHeadersKey.UNICA_PID))
                .header(HttpHeadersKey.UNICA_USER, headersMap.get(HttpHeadersKey.UNICA_USER))
                .header("ufxauthorization", headersMap.get("ufxauthorization"))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status -> status == HttpStatus.BAD_REQUEST,
                        clientResponse -> Mono.error(
                                GenesisException
                                        .builder()
                                        .exceptionId("SVR1000")
                                        .wildcards(new String[]{"Bad Request from Post Create Product Order FE+Simple Service"})
                                        .build()))
                .onStatus(status -> status == HttpStatus.NOT_FOUND,
                        clientResponse -> Mono.error(
                                GenesisException
                                        .builder()
                                        .exceptionId("SVR1000")
                                        .wildcards(new String[]{"Not Found Status from Post Create Product Order FE+Simple Service"})
                                        .build()))
                .bodyToMono(ProductorderResponse.class);
    }

}
