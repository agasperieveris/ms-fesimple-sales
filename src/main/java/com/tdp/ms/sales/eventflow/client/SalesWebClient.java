package com.tdp.ms.sales.eventflow.client;

import com.tdp.ms.sales.model.dto.KeyValueType;
import com.tdp.ms.sales.model.entity.Sale;
import java.util.List;
import java.util.Map;

/**
 * Interface: SalesWebClient. <br/>
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
 *         <li>2020-11-17 Creaci&oacute;n del proyecto.</li>
 *         </ul>
 * @version 1.0
 */
public interface SalesWebClient {
    void putSale(String salesId, Sale request, Map<String, String> headersMap);

    /**
     * Valida que se hayan ingresado los nuevos keys en el flujo de eventos.
     *
     * @author @srivasme
     * @param eventFlow Numero de flujo de evento en el que se encuentra
     * @param stepFlow Paso de flujo de eventos
     * @param additionalData additionalData de objeto Sale
     * @return String, null si está bien. Caso contrario, devuelve el log del error
     */
    String validateBeforeUpdate(String eventFlow, String stepFlow, List<KeyValueType> additionalData);
}
