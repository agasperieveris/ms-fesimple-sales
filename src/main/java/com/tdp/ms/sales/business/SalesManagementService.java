package com.tdp.ms.sales.business;

import com.tdp.ms.sales.model.entity.Sale;
import java.util.Map;
import reactor.core.publisher.Mono;

/**
 * Interface: SalesManagementService. <br/>
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
 *         <li>2020-11-13 Creaci&oacute;n del proyecto.</li>
 *         </ul>
 * @version 1.0
 */
public interface SalesManagementService {
    /**
     * Registra los datos de un nueva venta en la BBDD de la Web Convergente.
     *
     * @author @srivasme
     * @param request Datos de la nueva venta
     * @return SalesResponse, datos de la nueva venta registrada en la BBDD de la Web Convergente
     */
    Mono<Sale> post(Sale request, Map<String, String> headersMap);
}
