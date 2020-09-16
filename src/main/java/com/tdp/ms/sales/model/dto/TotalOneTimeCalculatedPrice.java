package com.tdp.ms.sales.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class: TotalOneTimeCalculatedPrice. <br/>
 * <b>Copyright</b>: &copy; 2019 Telef&oacute;nica del Per&uacute;<br/>
 * <b>Company</b>: Telef&oacute;nica del Per&uacute;<br/>
 *
 * @author Telef&oacute;nica del Per&uacute; (TDP) <br/>
 *         <u>Service Provider</u>: Everis Per&uacute; SAC (EVE) <br/>
 *         <u>Developed by</u>: <br/>
 *         <ul>
 *         <li>Developer Ronald</li>
 *         </ul>
 *         <u>Changes</u>:<br/>
 *         <ul>
 *         <li>YYYY-MM-DD Creaci&oacute;n del proyecto.</li>
 *         </ul>
 * @version 1.0
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class TotalOneTimeCalculatedPrice {
    private double originalAmount;
    private double originalTaxAmount;
    private double proratedAmount;
    private double proratedDiscountAmount;
    private double proratedTaxAmount;
    private String chargeFrequency;
    private double discountAmountInfo;
    private double discountPercentageInfo;
    private double finalAmount;
    private double finalTaxAmount;
    private String currency;
    private String name;
    private String description;
}
