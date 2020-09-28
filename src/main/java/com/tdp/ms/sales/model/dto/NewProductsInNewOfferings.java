package com.tdp.ms.sales.model.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class: NewProductsInNewOfferings. <br/>
 * <b>Copyright</b>: &copy; 2019 Telef&oacute;nica del Per&uacute;<br/>
 * <b>Company</b>: Telef&oacute;nica del Per&uacute;<br/>
 *
 * @author Telef&oacute;nica del Per&uacute; (TDP) <br/>
 *         <u>Service Provider</u>: Everis Per&uacute; SAC (EVE) <br/>
 *         <u>Developed by</u>: <br/>
 *         <ul>
 *         <li>Developer Ronald</li>
 *         </ul>
 *         <u>Changes</u>:<br/>
 *         <ul>
 *         <li>YYYY-MM-DD Creaci&oacute;n del proyecto.</li>
 *         </ul>
 * @version 1.0
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class NewProductsInNewOfferings {
    private String productCatalogId;
    private String temporaryId;
    private String productID;
    private String bundleCatalogId;
    private String productOfferingInstanceID;
    private String subscriptionGroupID;
    private String baId;
    private Long accountId;
    private String invoicingCompany;
    private String installationAddressId;
    private String productOrderItemReferenceNumber;
    private String productOrderItemType;
    private String productOrderItemID;
    private ProductConfiguration productConfiguration;
    private List<Messages> messages;
    private ProductOfferingType productOffering;
    private TotalRecurringCalculatedPrice totalRecurringCalculatedPrice;
    private TotalRecurringCalculatedPrice totalOneTimeCalculatedPrice;
    private List<DisplayableItems> displayableItems;
    private ProductSpecification productSpecification;
}
