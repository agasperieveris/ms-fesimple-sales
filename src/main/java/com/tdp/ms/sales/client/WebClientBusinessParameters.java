package com.tdp.ms.sales.client;

import com.tdp.ms.sales.model.response.BusinessParametersResponse;
import java.util.Map;
import reactor.core.publisher.Mono;

/**
 * Interface: WebClientBusinessParameters. <br/>
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

public interface WebClientBusinessParameters {

    Mono<BusinessParametersResponse> getNewSaleSequential(String seq, Map<String, String> headers);
}
