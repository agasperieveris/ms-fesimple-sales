package com.tdp.ms.sales.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class: OfferingType. <br/>
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
public class OfferingType {
    private String id;
    private String code;
    private String catalogItemType;
    private String href;
    private String correlationId;
    private String name;
    private String description;
    private String type;
    @JsonProperty("currentPlanRelationID")
    private String currentPlanRelationId;
    @JsonProperty("productOfferingProductSpecID")
    private String productOfferingProductSpecId;
    private List<CategoryTreeType> category;
    private Boolean isPromotion;
    private String billingMethod;
    private String frameworkAgreement;
    private List<ProductInstanceRefType> compatibleProducts;
    private Boolean isBundle;
    private String offeringUrl;
    private TimePeriodType validFor;
    private List<ComposingOfferingType> bundledProductOffering;
    private List<ComposingProductType> productSpecification;
    private Boolean isDowngrade;
    private List<ComponentProdOfferPriceType> productOfferingPrice;
    private List<PenaltyType> offeringPenalties;
    private UpFrontType upFront;
    private List<BenefitType> benefits;
    private List<KeyValueType> additionalData;
}
