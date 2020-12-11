package com.tdp.ms.sales.client;

import com.tdp.ms.sales.model.request.ReceptorRequest;
import com.tdp.ms.sales.model.response.ReceptorResponse;
import java.util.HashMap;
import reactor.core.publisher.Mono;

/**
 * Interface: WebClientReceptor. <br/>
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
public interface WebClientReceptor {
    Mono<ReceptorResponse> register(ReceptorRequest request, HashMap<String,String> headers);
}
