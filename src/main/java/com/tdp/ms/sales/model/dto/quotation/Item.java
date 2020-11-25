package com.tdp.ms.sales.model.dto.quotation;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Class: Item. <br/>
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
 *         <li>2020-11-03 Creaci&oacute;n del proyecto.</li>
 *         </ul>
 * @version 1.0
 */
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Item {
    private String offeringId;
    private String type;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String publicId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String orderActionId;
    private MoneyAmount totalCost;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private MoneyAmount taxExcludedAmount;
}
