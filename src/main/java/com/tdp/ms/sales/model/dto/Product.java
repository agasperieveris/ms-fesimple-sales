package com.tdp.ms.sales.model.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class: SalesController. <br/>
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

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Product {
    private String href;
    private String id;
    private String publicId;
    private String description;
    private String name;
    private String productType;
    private List<String> tags;
    private Boolean isBundle;
    private String productSerialNumber;
    private List<AccountRefType> billingAccount;
    private EntityRefType productOffering;
    private EntityRefType productSpec;
    private List<ProductCharacteristicType> characteristic;
    private List<RelatedProductType> productRelationShip;
    private List<ComponentProdPriceType> productPrice;
    private List<EntityRefType> place;//Swagger otro objeto
    private List<AdditionalData> additionalData;
}
