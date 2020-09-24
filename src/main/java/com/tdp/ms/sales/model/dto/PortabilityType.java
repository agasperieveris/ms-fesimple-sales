package com.tdp.ms.sales.model.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class: PortabilityType. <br/>
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
public class PortabilityType {
    private String receipt;
    private String donor;
    private String nationalIdType;
    private String nationalId;
    private String productType;
    private String publicId;
    private String planType;
    private String planTypeReceiver;
    private String observations;
    private String customerName;
    private String customerEmail;
    private String customerContactPhone;
    private String customerFax;
    private String idProcessGroup;
    private String idProcess;//Nuevo Swagger
    private String idProcessCP;//Nuevo Swagger
    private String donorActivateDate;
    private String donorEquipmentContractEndDate;
    private String rejectCode;
    private String rejectDescription;
    private String identifierMessageCausedError;
    private String expirationDateDebt;
    private String amountDebt;
    private String currency;
    private List<AdditionalData> additionalData;
}
