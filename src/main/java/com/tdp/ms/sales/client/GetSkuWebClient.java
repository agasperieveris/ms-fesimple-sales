package com.tdp.ms.sales.client;

import com.tdp.ms.sales.model.response.GetSkuResponse;
import java.util.Map;
import reactor.core.publisher.Flux;

/**
 * Class: GetSkuWebClient. <br/>
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
public interface GetSkuWebClient {
    Flux<GetSkuResponse> createSku(String channelId, String planGroup, String simSapId, double simPrice,
                                   String operationType, String customerSegment, String storeId, String subscriberType,
                                   String dealerId, String deviceSapId, String devicePrice, Map<String, String> headersMap);
}
