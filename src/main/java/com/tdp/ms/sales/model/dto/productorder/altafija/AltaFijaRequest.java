package com.tdp.ms.sales.model.dto.productorder.altafija;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tdp.ms.sales.model.dto.productorder.FlexAttrType;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Class: AltaFijaRequest. <br/>
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
 *         <li>2020-11-11 Creaci&oacute;n del proyecto.</li>
 *         </ul>
 * @version 1.0
 */
@Setter
@Getter
@NoArgsConstructor
public class AltaFijaRequest {
    private List<NewProductAltaFija> newProducts;
    private String appointmentId;
    private String appointmentNumber;
    @JsonProperty("ServiceabilityInfo")
    private ServiceabilityInfoType serviceabilityInfo;
    private String sourceApp;
    private List<FlexAttrType> orderAttributes;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String cip;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String upfrontIndicator;
}
