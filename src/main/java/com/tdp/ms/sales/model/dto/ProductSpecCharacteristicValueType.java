package com.tdp.ms.sales.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Class: ProductSpecCharacteristicValueType. <br/>
 * <b>Copyright</b>: &copy; 2021 Telef&oacute;nica del Per&uacute;<br/>
 * <b>Company</b>: Telef&oacute;nica del Per&uacute;<br/>
 *
 * @author Telef&oacute;nica del Per&uacute; (TDP) <br/>
 *         <u>Service Provider</u>: Everis Per&uacute; SAC (EVE) <br/>
 *         <u>Developed by</u>: <br/>
 *         <ul>
 *         <li>Developer name</li>
 *         </ul>
 *         <u>Cesar Gomez</u>:<br/>
 *         <ul>
 *         <li>06-04-2021 Creaci&oacute;n del proyecto.</li>
 *         </ul>
 * @version 1.0
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ProductSpecCharacteristicValueType {
    private String valueType;
    private QuantityType unitOfMeasure;
    @SerializedName("default")
    @JsonProperty("default")
    private Boolean defaultt;
    private String value;
    private String valueFrom;
    private String valueTo;
    private TimePeriodType validFor;
    private List<MoneyType> additionalData;
}
