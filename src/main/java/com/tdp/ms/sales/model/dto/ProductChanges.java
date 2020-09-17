package com.tdp.ms.sales.model.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class: ProductChanges. <br/>
 * <b>Copyright</b>: &copy; 2019 Telef&oacute;nica del Per&uacute;<br/>
 * <b>Company</b>: Telef&oacute;nica del Per&uacute;<br/>
 *
 * @author Telef&oacute;nica del Per&uacute; (TDP) <br/>
 *         <u>Service Provider</u>: Everis Per&uacute; SAC (EVE) <br/>
 *         <u>Developed by</u>: <br/>
 *         <ul>
 *         <li>Developer Ronald Barón</li>
 *         </ul>
 *         <u>Changes</u>:<br/>
 *         <ul>
 *         <li>YYYY-MM-DD Creaci&oacute;n del proyecto.</li>
 *         </ul>
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductChanges {
    private String requestId;
    private List<ChangedSimpleProducts> changedSimpleProducts;
    private List<SuspendedContainedProducts> suspendedContainedProducts;
    private List<NewSimpleProducts> newSimpleProducts;
    private List<NewAssignedBillingOffers> newAssignedBillingOffers;
    private List<ResumedContainedProducts> resumedContainedProducts;
    private List<ChangedContainedProducts> changedContainedProducts;
    private List<RemovedAssignedBillingOffers> removedAssignedBillingOffers;
    private List<RemovedContainedProducts> removedContainedProducts;
    private List<NewContainedProducts> newContainedProducts;
    private PortabilityDetails portabilityDetails;
}
