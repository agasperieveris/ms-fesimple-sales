package com.tdp.ms.sales.client.impl;

import com.tdp.genesis.core.constants.HttpHeadersKey;
import com.tdp.ms.sales.client.PaymentWebClient;
import com.tdp.ms.sales.model.request.GenerateCipRequest;
import com.tdp.ms.sales.model.response.GenerateCipResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * Class: PaymentWebClientImpl. <br/>
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
 *         <li>2020-10-12 Creaci&oacute;n del proyecto.</li>
 *         </ul>
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
public class PaymentWebClientImpl implements PaymentWebClient {

    private final WebClient webClientInsecure;

    @Value("${application.endpoints.payment.generate_cip_payment_identifier}")
    private String getRiskDomainUrl;

    @Override
    public Mono<GenerateCipResponse> generateCip(GenerateCipRequest request) {
        return webClientInsecure
                .post()
                .uri(getRiskDomainUrl)
                .header(HttpHeadersKey.UNICA_APPLICATION, request.getHeadersMap().get(HttpHeadersKey.UNICA_APPLICATION))
                .header(HttpHeadersKey.UNICA_PID, request.getHeadersMap().get(HttpHeadersKey.UNICA_PID))
                .header(HttpHeadersKey.UNICA_SERVICE_ID, request.getHeadersMap().get(HttpHeadersKey.UNICA_SERVICE_ID))
                .header(HttpHeadersKey.UNICA_USER, request.getHeadersMap().get(HttpHeadersKey.UNICA_USER))
                .bodyValue(request.getBody())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                // TODO: Identificar excepciones del servicio
                .bodyToMono(GenerateCipResponse.class);
    }

}
