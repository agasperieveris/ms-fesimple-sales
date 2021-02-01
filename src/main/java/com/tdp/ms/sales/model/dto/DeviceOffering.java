package com.tdp.ms.sales.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

import com.google.gson.annotations.SerializedName;
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
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeviceOffering {
    @SerializedName("device_type")
    @JsonProperty("device_type")
    private String deviceType;
    private String id;
    private String sapid;
    private String brand;
    private String model;
    private String gama;
    @SerializedName("display_name")
    @JsonProperty("display_name")
    private String displayName;
    private String clasificacionComercial;
    @SerializedName("costoPromedioSinIGVSoles")
    @JsonProperty("costoPromedioSinIGVSoles")
    private String costoPromedioSinIgvSoles;
    @SerializedName("sim_specifications")
    @JsonProperty("sim_specifications")
    private List<SimSpecification> simSpecifications;
    private List<Offer> offers;
    private StockType stock;
    private List<KeyValueType> additionalData;
}
