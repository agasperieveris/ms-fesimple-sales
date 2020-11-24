package com.tdp.ms.sales.business;

import com.tdp.ms.sales.model.entity.Sale;
import com.tdp.ms.sales.model.request.GetSalesRequest;
import java.util.Map;

import reactor.core.publisher.Flux;
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
    Mono<Sale> getSale(GetSalesRequest request);

    /**
     * Registra los datos de un nueva venta en la BBDD de la Web Convergente.
     *
     * @author @srivasme
     * @param request Datos de la nueva venta
     * @return SalesResponse, datos de la nueva venta registrada en la BBDD de la Web Convergente
     */
    Mono<Sale> post(Sale request, Map<String, String> headersMap);

    /**
     * Actualiza los datos de la venta en la BBDD.
     *
     * @author @srivasme
     * @param request Datos de la venta actualizados
     * @return SalesResponse, datos actualizados de la venta
     */
    Mono<Sale> put(String salesId, Sale request, Map<String, String> headersMap);

    /**
     * Actualiza los datos de la venta en la BBDD - FLUJO EVENTOS.
     *
     * @author @srivasme
     * @param request Datos de la venta actualizados
     * @return SalesResponse, datos actualizados de la venta
     */
    Mono<Sale> putEvent(String salesId, Sale request, Map<String, String> headersMap);

    /**
     * Se listan las ventas dependiendo de los parámetros que se le pasen.
     *
     * @author @srivasme
     * @return Sale
     */
    Flux<Sale> getSaleList(String saleId, String dealerId,
                           String idAgent, String customerId, String nationalID, String nationalIdType,
                           String status, String channelId, String storeId, String orderId,
                           String startDateTime, String endDateTime, String size, String pageCount,
                           String page, String maxResultCount);
}
