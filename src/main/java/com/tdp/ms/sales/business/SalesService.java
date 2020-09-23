package com.tdp.ms.sales.business;

import com.tdp.ms.sales.model.entity.Sale;
import com.tdp.ms.sales.model.request.GetSalesRequest;
import com.tdp.ms.sales.model.response.SalesResponse;
import java.util.Map;
import reactor.core.publisher.Mono;

/**
 * Class: SalesService. <br/>
 * <b>Copyright</b>: &copy; 2019 Telef&oacute;nica del Per&uacute;<br/>
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
 *         <li>YYYY-MM-DD Creaci&oacute;n del proyecto.</li>
 *         </ul>
 * @version 1.0
 */

public interface SalesService {

    /**
     * Registra los datos de un nueva venta en la BBDD de la Web Convergente.
     *
     * @author @srivasme
     * @param request Datos de la nueva venta
     * @return SalesResponse, datos de la nueva venta registrada en la BBDD de la Web Convergente
     */
    Mono<SalesResponse> getSale(GetSalesRequest request);

    /**
     * Registra los datos de un nueva venta en la BBDD de la Web Convergente.
     *
     * @author @srivasme
     * @param request Datos de la nueva venta
     * @return SalesResponse, datos de la nueva venta registrada en la BBDD de la Web Convergente
     */
    Mono<SalesResponse> post(Sale request);

    /**
     * Actualiza los datos de la venta en la BBDD.
     *
     * @author @srivasme
     * @param request Datos de la venta actualizados
     * @return SalesResponse, datos actualizados de la venta
     */
    Mono<SalesResponse> put(Sale request);

    /**
     * Se crea un order y se actualiza los datos del sale, el orderId tambien se actualiza dentro de sale.
     *
     * @author @srivasme
     * @param request Datos de la venta actualizados
     * @param headersMap headers de la consulta
     * @return SalesResponse, datos actualizados de la venta
     */
    Mono<SalesResponse> confirmationSalesLead(SalesResponse request, Map<String, String> headersMap);
}
