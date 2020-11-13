package com.tdp.ms.sales.client.impl;

import com.tdp.genesis.core.constants.HttpHeadersKey;
import com.tdp.ms.sales.client.WebClientReceptor;
import com.tdp.ms.sales.model.request.ReceptorRequest;
import com.tdp.ms.sales.model.response.ReceptorResponse;

import java.util.Map;

import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

/**
 * Interface: WebClientReceptorImpl. <br/>
 * <b>Copyright</b>: &copy; 2020 Telef&oacute;nica del Per&uacute;<br/>
 * <b>Company</b>: Telef&oacute;nica del Per&uacute;<br/>
 *
 * @author Telef&oacute;nica del Per&uacute; (TDP) <br/>
 *         <u>Service Provider</u>: Everis Per&uacute; SAC (EVE) <br/>
 *         <u>Developed by</u>: <br/>
 *         <ul>
 *         <li>Ingrid Mendoza</li>
 *         </ul>
 *         <u>Changes</u>:<br/>
 *         <ul>
 *         <li>2020-11-12 Creaci&oacute;n del proyecto.</li>
 *         </ul>
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
public class WebClientReceptorImpl implements WebClientReceptor {
    private final WebClient webClientInsecureReceptor;

    @Override
    public Mono<ReceptorResponse> register(ReceptorRequest request, Map<String, String> headers) {
        WebClient.ResponseSpec response = webClientInsecureReceptor
                .post()
                .header(HttpHeadersKey.UNICA_SERVICE_ID, headers.get(HttpHeadersKey.UNICA_SERVICE_ID))
                .header(HttpHeadersKey.UNICA_APPLICATION, headers.get(HttpHeadersKey.UNICA_APPLICATION))
                .header(HttpHeadersKey.UNICA_PID, headers.get(HttpHeadersKey.UNICA_PID))
                .header(HttpHeadersKey.UNICA_USER, headers.get(HttpHeadersKey.UNICA_USER))
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve();

        return response.bodyToMono(ReceptorResponse.class);
    }
}
