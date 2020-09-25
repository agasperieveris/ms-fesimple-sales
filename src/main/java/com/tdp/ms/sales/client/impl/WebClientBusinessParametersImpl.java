package com.tdp.ms.sales.client.impl;

import com.tdp.genesis.core.constants.HttpHeadersKey;
import com.tdp.ms.sales.model.response.BusinessParametersResponse;
import lombok.RequiredArgsConstructor;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * Class: WebClientTokenImpl. <br/>
 * <b>Copyright</b>: &copy; 2020 Telef&oacute;nica del Per&uacute;<br/>
 * <b>Company</b>: Telef&oacute;nica del Per&uacute;<br/>
 *
 * @author Telef&oacute;nica del Per&uacute; (TDP) <br/>
 *         <u>Service Provider</u>: Everis Per&uacute; SAC (EVE) <br/>
 *         <u>Developed by</u>: <br/>
 *         <ul>
 *         <li>Sergio Rivas</li>
 *         </ul>
 *         <u>Changes</u>:<br/>
 *         <ul>
 *         <li>2020-07-29 Creaci&oacute;n del proyecto.</li>
 *         </ul>
 * @version 1.0
 */

@Component
@RequiredArgsConstructor
public class WebClientBusinessParametersImpl implements com.tdp.ms.sales.client.WebClientBusinessParameters {

    private final WebClient webClientInsecure;

    @Value("${application.endpoints.url.business_parameters.sequential}")
    private String urlBusinessParametersSequencialId;

    @Override
    public Mono<BusinessParametersResponse> getNewSaleSequential(String seq, Map<String, String> headers) {
        WebClient.ResponseSpec response = webClientInsecure
                .get()
                .uri(urlBusinessParametersSequencialId, "031", seq)
                .header(HttpHeadersKey.UNICA_SERVICE_ID, headers.get(HttpHeadersKey.UNICA_SERVICE_ID))
                .header(HttpHeadersKey.UNICA_APPLICATION, headers.get(HttpHeadersKey.UNICA_APPLICATION))
                .header(HttpHeadersKey.UNICA_PID, headers.get(HttpHeadersKey.UNICA_PID))
                .header(HttpHeadersKey.UNICA_USER, headers.get(HttpHeadersKey.UNICA_USER))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve();

        return response.bodyToMono(BusinessParametersResponse.class);
    }
}
