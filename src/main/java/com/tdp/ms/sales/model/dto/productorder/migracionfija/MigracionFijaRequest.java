package com.tdp.ms.sales.model.dto.productorder.migracionfija;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tdp.ms.sales.model.dto.productorder.FlexAttrType;
import com.tdp.ms.sales.model.dto.productorder.altafija.ServiceabilityInfoType;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Class: MigracionFijaRequest. <br/>
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

@Setter
@Getter
@NoArgsConstructor
public class MigracionFijaRequest {
    private List<NewProductMigracionFija> newProducts;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String cip;
    private String appointmentNumber;
    private String appointmentId;
    @JsonProperty("ServiceabilityInfo")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ServiceabilityInfoType serviceabilityInfo;
    private List<FlexAttrType> orderAttributes;
    private String sourceApp;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String upfrontIndicator;
}
