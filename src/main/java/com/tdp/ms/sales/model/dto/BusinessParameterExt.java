package com.tdp.ms.sales.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class: BusinessParameterExt. <br/>
 * <b>Copyright</b>: &copy; 2020 Telef&oacute;nica del Per&uacute;<br/>
 * <b>Company</b>: Telef&oacute;nica del Per&uacute;<br/>
 *
 * @author Telef&oacute;nica del Per&uacute; (TDP) <br/>
 *         <u>Service Provider</u>: Everis Per&uacute; SAC (EVE) <br/>
 *         <u>Developed by</u>: <br/>
 *         <ul>
 *         <li>Cesar Gomez</li>
 *         </ul>
 *         <u>Changes</u>:<br/>
 *         <ul>
 *         <li>2020-09-23 Creaci&oacute;n del proyecto.</li>
 *         </ul>
 * @version 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BusinessParameterExt {

    private String codComercialOperationType;
    private String codActionType;
    private String codCharacteristicId;
    private String codCharacteristicCode;
    private String nomElementName;
    private String codLevel;
    @JsonProperty("indSendtoSIMS")
    private Boolean indSendToSims;
    @JsonProperty("indSendtoRMS")
    private Boolean indSendToRms;
    private Boolean indMpactquote;
    private String codCharacteristicValue;
    private String codStatus;

}
