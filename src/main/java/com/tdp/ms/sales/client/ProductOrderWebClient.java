package com.tdp.ms.sales.client;

import com.tdp.genesis.core.exception.GenesisException;
import com.tdp.ms.sales.model.dto.productorder.CreateProductOrderGeneralRequest;
import com.tdp.ms.sales.model.entity.Sale;
import com.tdp.ms.sales.model.response.ProductorderResponse;
import java.util.HashMap;
import reactor.core.publisher.Mono;

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

    Mono<ProductorderResponse> createProductOrder(CreateProductOrderGeneralRequest request,
                                                  HashMap<String,String> headersMap, Sale sale);

    Mono<ProductorderResponse> fallbackCreateProductOrder(Throwable error, Sale sale) throws GenesisException;

    Mono<ProductorderResponse> throwExceptionCreateProductOrder(Throwable error) throws GenesisException;
}
