package com.tdp.ms.sales.model.dto.productorder.altamobile;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tdp.ms.sales.model.dto.ShipmentDetailsType;
import com.tdp.ms.sales.model.dto.productorder.FlexAttrType;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class: AltaMobileRequest. <br/>
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
 *         <li>2020-09-24 Creaci&oacute;n del proyecto.</li>
 *         </ul>
 * @version 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AltaMobileRequest {
    private List<NewProductAltaMobile> newProducts;
    private String sourceApp;
    private List<FlexAttrType> orderAttributes;
    private ShipmentDetailsType shipmentDetails;
    //private String cip; // Consultar cuando estará solucionado el envío de cip
}
