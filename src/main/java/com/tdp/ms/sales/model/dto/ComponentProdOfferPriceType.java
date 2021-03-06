package com.tdp.ms.sales.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class: ComponentProdOfferPriceType. <br/>
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
public class ComponentProdOfferPriceType {
    private String id;
    private String name;
    private String description;
    @JsonProperty("productSpecContainmentID")
    private String productSpecContainmentId;
    @JsonProperty("pricePlanSpecContainmentID")
    private String pricePlanSpecContainmentId;
    private Boolean isMandatory;
    private TimePeriodType validFor;
    private String priceType;
    private String recurringChargePeriod;
    private QuantityType unitOfMeasure;
    private MoneyType price;
    private MoneyType minPrice;
    private MoneyType maxPrice;
    private MoneyType taxAmount;
    private MoneyType priceWithTax;
    private MoneyType originalAmount;
    private MoneyType originalTaxAmount;
    private Boolean taxIncluded;
    private float taxRate;
    private String taxType;
    private List<ProdOfferPriceAlterationType> productOfferPriceAlteration;
    private List<KeyValueType> pricedComponents;
    private List<String> priceLocation;
    private EntityRefType priceConsumer;
    private List<BenefitType> benefits;
    private List<KeyValueType> additionalData;
}
