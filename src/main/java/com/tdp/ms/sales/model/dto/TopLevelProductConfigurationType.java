package com.tdp.ms.sales.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class: TopLevelProductConfigurationType. <br/>
 * <b>Copyright</b>: &copy; 2020 Telef&oacute;nica del Per&uacute;<br/>
 * <b>Company</b>: Telef&oacute;nica del Per&uacute;<br/>
 *
 * @author Telef&oacute;nica del Per&uacute; (TDP) <br/>
 *         <u>Service Provider</u>: Everis Per&uacute; SAC (EVE) <br/>
 *         <u>Developed by</u>: <br/>
 *         <ul>
 *         <li>Developer name</li>
 *         </ul>
 *         <u>Changes</u>:<br/>
 *         <ul>
 *         <li>YYYY-MM-DD Creaci&oacute;n del proyecto.</li>
 *         </ul>
 * @version 1.0
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TopLevelProductConfigurationType {
    private String temporaryId;
    private String compatibilityStatus;
    @JsonProperty("productID")
    private String productId;
    private String serviceId;
    private String lineOfBusinessType;
    private List<RuleExecutionMessageType> messages;
    private ProductOfferingType productOffering;
    private TotalRecurringCalculatedPrice totalRecurringCalculatedPrice;
    private TotalRecurringCalculatedPrice totalOneTimeCalculatedPrice;
    private List<ProductDisplayType> displayableItems;
    private ProductSpecification productSpecification;
}
