package com.tdp.ms.sales.business.v1;

import com.tdp.ms.sales.model.entity.Sale;
import com.tdp.ms.sales.model.request.PostSalesRequest;
import reactor.core.publisher.Mono;

/**
 * Interface: SalesManagmentService. <br/>
 * <b>Copyright</b>: &copy; 2019 Telef&oacute;nica del Per&uacute;<br/>
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
 *         <li>2020-09-24 Creaci&oacute;n del proyecto.</li>
 *         </ul>
 * @version 1.0
 */
public interface SalesManagmentService {

    /**
     * Actualiza datos de la orden de Sales.
     *
     * @author @cesargomezeveris
     * @param request Datos de la venta
     * @return SalesResponse, datos de la venta con información de la orden creada
     */
    Mono<Sale> post(PostSalesRequest request);

}
