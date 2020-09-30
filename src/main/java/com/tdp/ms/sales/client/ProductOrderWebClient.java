package com.tdp.ms.sales.client;

import com.tdp.ms.sales.model.dto.productorder.CreateProductOrderGeneralRequest;
import reactor.core.publisher.Mono;

import java.util.HashMap;

/**
 * Class: ProductOrderWebClient. <br/>
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
public interface ProductOrderWebClient {

    Mono<Object> createProductOrder(CreateProductOrderGeneralRequest request, HashMap<String,String> headersMap);

}
