package com.tdp.ms.sales.model.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Class: GenerateCipRequestBody. <br/>
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
 *         <li>2020-10-12 Creaci&oacute;n del proyecto.</li>
 *         </ul>
 * @version 1.0
 */
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenerateCipRequestBody {
    private String currency;
    private Number amount;
    private String transactionCode;
    private String adminEmail;
    private String dateExpiry;
    private String paymentConcept;
    private String additionalData;
    private String userEmail;
    private String userName;
    private String userLastName;
    private String userUbigeo;
    private String userCountry;
    private String userDocumentType;
    private String userDocumentNumber;
    private String userCodeCountry;
    private String userPhone;
}
