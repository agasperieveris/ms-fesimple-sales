package com.tdp.ms.sales.model.dto.productorder.altafija;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tdp.ms.sales.model.dto.productorder.caeq.ChangedContainedProduct;
import com.tdp.ms.sales.model.dto.productorder.capl.NewAssignedBillingOffers;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Class: ProductChangeAltaFija. <br/>
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
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductChangeAltaFija {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<ChangedContainedProduct> changedContainedProducts;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<NewAssignedBillingOffers> newAssignedBillingOffers;
}
