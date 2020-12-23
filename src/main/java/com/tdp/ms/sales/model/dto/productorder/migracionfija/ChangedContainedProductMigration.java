package com.tdp.ms.sales.model.dto.productorder.migracionfija;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import com.tdp.ms.sales.model.dto.productorder.caeq.ChangedCharacteristic;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Class: ChangedContainedProductMigration. <br/>
 * <b>Copyright</b>: &copy; 2020 Telef&oacute;nica del Per&uacute;<br/>
 * <b>Company</b>: Telef&oacute;nica del Per&uacute;<br/>
 *
 * @author Telef&oacute;nica del Per&uacute; (TDP) <br/>
 *         <u>Service Provider</u>: Everis Per&uacute; SAC (EVE) <br/>
 *         <u>Developed by</u>: <br/>
 *         <ul>
 *         <li>Sergio Rivas</li>
 *         </ul>
 *         <u>Changes</u>:<br/>
 *         <ul>
 *         <li>2020-12-15 Creaci&oacute;n del proyecto.</li>
 *         </ul>
 * @version 1.0
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChangedContainedProductMigration {
    @SerializedName("productID")
    @JsonProperty("productID")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String productId;
    private String temporaryId;
    private List<ChangedCharacteristic> changedCharacteristics;
}
