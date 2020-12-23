package com.tdp.ms.sales.model.dto.productorder.migracionfija;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * Class: ProductOrderMigracionFijaRequest. <br/>
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
 *         <li>2020-12-21 Creaci&oacute;n del proyecto.</li>
 *         </ul>
 * @version 1.0
 */

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductOrderMigracionFijaRequest {
    private String salesChannel;
    private MigracionFijaRequest request;
    private String customerId;
    @JsonProperty("productOfferingID")
    private String productOfferingId;
    private String onlyValidationIndicator;
    private String actionType;
}
