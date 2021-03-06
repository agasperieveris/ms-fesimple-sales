package com.tdp.ms.sales.model.dto.productorder.caeq;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tdp.ms.sales.model.dto.productorder.capl.NewAssignedBillingOffers;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class: ProductChangeCaeq. <br/>
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
public class ProductChangeCaeq {
    private List<ChangedContainedProduct> changedContainedProducts;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<NewAssignedBillingOffers> newAssignedBillingOffers;
}
