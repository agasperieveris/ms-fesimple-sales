package com.tdp.ms.sales.model.dto.quotation;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tdp.ms.sales.model.dto.TimePeriod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Class: ContactMedium. <br/>
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
 *         <li>2020-11-03 Creaci&oacute;n del proyecto.</li>
 *         </ul>
 * @version 1.0
 */
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ContactMedium {
    @JsonProperty("@type")
    private String type;
    private String name;
    private String preferred;
    private String isActive;
    private TimePeriod validFor;
}
