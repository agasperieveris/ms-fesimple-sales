package com.tdp.ms.sales.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class: ServiceAvailabilityReportType. <br/>
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
public class ServiceAvailabilityReportType {
    private String id;
    private Boolean isAdslAvailable;
    private Boolean isAdslSatured;
    @JsonProperty("TerminalBox")
    private String terminalBox;
    @JsonProperty("AdslBloquedSale")
    private String adslBloquedSale;
    @JsonProperty("TerminalBoxBloqued")
    private String terminalBoxBloqued;
    private String commercialAreaId;
    private String commercialAreaDescription;
    private List<AvailableOffersType> offers;
    private List<KeyValueType> additionalData;
}
