package com.tdp.ms.sales.model.dto.productorder.portability;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Class: PortabilityDetailsType. <br/>
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
 *         <li>2020-12-01 Creaci&oacute;n del proyecto.</li>
 *         </ul>
 * @version 1.0
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PortabilityDetailsType {
    private String sourceOperator;
    private String serviceType;
    private String planType;
    private String activationDate;
    private String equipmentCommitmentEndDate;
    private String salesDepartment;
    private String consultationId;
    private String consultationGroup;
    private String documentType;
    private String documentNumber;
    private String customerName;
    private String customerEmail;
    private String customerContactPhone;
}
