package com.tdp.ms.sales.model.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class: Installments. <br/>
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
public class Installments {
    private String type;
    private MoneyAmount rate;
    private MoneyAmount discount;
    private MoneyAmount portDiscount;
    private MoneyAmount postPagoFacil;
    private MoneyAmount totalAmount;
    private MoneyAmount penaltyAmount;
    private MoneyAmount amount;
    private Integer numberOfInstallments;
    private String recurrence;
    private MoneyAmount openingQuota;
    private MoneyAmount finalQuota;
    private List<String> installmentsInfo;
}
