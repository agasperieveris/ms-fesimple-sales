package com.tdp.ms.sales.client;

import com.tdp.ms.sales.model.request.GetSalesCharacteristicsRequest;
import com.tdp.ms.sales.model.response.BusinessParametersFinanciamientoFijaResponse;
import com.tdp.ms.sales.model.response.BusinessParametersReasonCode;
import com.tdp.ms.sales.model.response.BusinessParametersResponse;
import com.tdp.ms.sales.model.response.BusinessParametersResponseObjectExt;
import com.tdp.ms.sales.model.response.GetSalesCharacteristicsResponse;
import java.util.HashMap;
import reactor.core.publisher.Mono;

/**
 * Class: BusinessParameterWebClient. <br/>
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
public interface BusinessParameterWebClient {

    Mono<GetSalesCharacteristicsResponse>
        getSalesCharacteristicsByCommercialOperationType(GetSalesCharacteristicsRequest request);

    Mono<BusinessParametersResponse> getRiskDomain(String domain, HashMap<String, String> headersMap);

    Mono<BusinessParametersResponseObjectExt> getBonificacionSimcard(HashMap<String,String> headersMap);

    Mono<BusinessParametersResponseObjectExt> getParametersSimcard(HashMap<String, String> headersMap);

    Mono<BusinessParametersFinanciamientoFijaResponse> getParametersFinanciamientoFija(
                                                                                    HashMap<String, String> headersMap);

    Mono<BusinessParametersReasonCode> getParametersReasonCode(HashMap<String, String> headersMap);
}
