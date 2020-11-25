package com.tdp.ms.sales.client.impl;

import com.tdp.genesis.core.constants.HttpHeadersKey;
import com.tdp.ms.sales.client.GetSkuWebClient;
import com.tdp.ms.sales.model.response.GetSkuResponse;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

/**
 * Class: GetSkuWebClientImpl. <br/>
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
 *         <li>2020-09-23 Creaci&oacute;n del proyecto.</li>
 *         </ul>
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
public class GetSkuWebClientImpl implements GetSkuWebClient {

    private final WebClient webClientInsecure;

    @Value("${application.endpoints.sku.create_sku}")
    private String urlCreateSku;

    @Override
    public Flux<GetSkuResponse> createSku(String channelId, String planGroup, String simSapId, double simPrice,
                                          String operationType, String customerSegment, String storeId,
                                          String subscriberType, String dealerId, String deviceSapId, String devicePrice,
                                          Map<String, String> headersMap) {
        return webClientInsecure
                .get()
                .uri(urlCreateSku, channelId, planGroup, simSapId, simPrice, operationType, customerSegment,
                        storeId, subscriberType, dealerId, deviceSapId, devicePrice)
                .header(HttpHeadersKey.UNICA_APPLICATION, headersMap.get(HttpHeadersKey.UNICA_APPLICATION))
                .header(HttpHeadersKey.UNICA_PID, headersMap.get(HttpHeadersKey.UNICA_PID))
                .header(HttpHeadersKey.UNICA_SERVICE_ID, headersMap.get(HttpHeadersKey.UNICA_SERVICE_ID))
                .header(HttpHeadersKey.UNICA_USER, headersMap.get(HttpHeadersKey.UNICA_USER))
                .header(HttpHeadersKey.UNICA_TIMESTAMP, "2020-10-28T17:15:20.509-0400")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(GetSkuResponse.class);
    }
}
