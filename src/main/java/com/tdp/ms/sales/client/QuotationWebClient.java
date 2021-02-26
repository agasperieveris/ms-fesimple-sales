package com.tdp.ms.sales.client;

import com.tdp.genesis.core.exception.GenesisException;
import com.tdp.ms.sales.model.entity.Sale;
import com.tdp.ms.sales.model.request.CreateQuotationRequest;
import com.tdp.ms.sales.model.response.CreateQuotationResponse;
import reactor.core.publisher.Mono;

/**
 * Interface: QuotationWebClient. <br/>
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
 *         <li>2020-11-03 Creaci&oacute;n del proyecto.</li>
 *         </ul>
 * @version 1.0
 */
public interface QuotationWebClient {

    Mono<CreateQuotationResponse> createQuotation(CreateQuotationRequest request, Sale sale);

    Mono<CreateQuotationResponse> throwExceptionCreateQuotation(Sale sale, Throwable error) throws GenesisException;

}
