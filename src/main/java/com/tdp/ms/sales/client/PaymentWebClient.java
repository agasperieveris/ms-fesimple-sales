package com.tdp.ms.sales.client;

import com.tdp.ms.sales.model.request.GenerateCipRequest;
import com.tdp.ms.sales.model.response.GenerateCipResponse;
import reactor.core.publisher.Mono;

/**
 * Class: PaymentWebClient. <br/>
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
public interface PaymentWebClient {

    Mono<GenerateCipResponse> generateCip(GenerateCipRequest request);

}
