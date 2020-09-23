package com.tdp.ms.sales.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class: NewProductInNewOfferingInstanceConfigurationType. <br/>
 * <b>Copyright</b>: &copy; 2019 Telef&oacute;nica del Per&uacute;<br/>
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
public class NewProductInNewOfferingInstanceConfigurationType {
    private String productCatalogId;
    private String temporaryId;
    private String productId;
    private String bundleCatalogId;
    private String productOfferingInstanceId;
    private String subscriptionGroupId;
    private String baId;
    private String accountId;
    private String invoicingCompany;
    private String installationAddressId;
    private String productOrderItemReferenceNumber;
    private String productOrderItemId;
    private String productOrderItemType;
    private TopLevelProductConfigurationType productConfiguration;
}
