package com.tdp.ms.sales.model.dto.quotation;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Class: CreateQuotationRequestBody. <br/>
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
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateQuotationRequestBody {
    private String orderId;
    private String accountId;
    private String billingAgreement;
    private String commercialAgreement;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String serviceIdLobConcat = "";
    private Customer customer;
    private String operationType;
    private MoneyAmount totalAmount;
    private MoneyAmount associatedPlanRecurrentCost;
    private MoneyAmount totalCustomerRecurrentCost;
    private MoneyAmount downPayment;
    private Site site;
    private String financialEntity;
    private List<Item> items = new ArrayList<>();
    private Channel channel;
}
