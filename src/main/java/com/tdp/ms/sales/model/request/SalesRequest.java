package com.tdp.ms.sales.model.request;

import com.tdp.ms.commons.dto.sales.RelatedParty;
import com.tdp.ms.sales.model.dto.ChannelRef;
import com.tdp.ms.sales.model.dto.CommercialOperationType;
import com.tdp.ms.sales.model.dto.ContactMedium;
import com.tdp.ms.sales.model.dto.IdentityValidationType;
import com.tdp.ms.sales.model.dto.KeyValueType;
import com.tdp.ms.sales.model.dto.Money;
import com.tdp.ms.sales.model.dto.PaymentType;
import com.tdp.ms.sales.model.dto.TimePeriod;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class: SalesRequest. <br/>
 * <b>Copyright</b>: &copy; 2019 Telef&oacute;nica del Per&uacute;<br/>
 * <b>Company</b>: Telef&oacute;nica del Per&uacute;<br/>
 *
 * @author Telef&oacute;nica del Per&uacute; (TDP) <br/>
 *         <u>Service Provider</u>: Everis Per&uacute; SAC (EVE) <br/>
 *         <u>Developed by</u>: <br/>
 *         <ul>
 *         <li>Sergio Rivas</li>
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
public class SalesRequest {
    private List<CommercialOperationType> commercialOperationType;
    private String id;
    private List<ContactMedium> prospectContact;
    private String salesId;
    private List<KeyValueType> additionalData;
    private String name;
    private String productType;
    private Money estimatedRevenue;
    private PaymentType paymentType;
    private RelatedParty agent;
    private String description;
    private ChannelRef channel;
    private String priority;
    private List<RelatedParty> relatedParty;
    private String saleCreationDate;
    private String status;
    private String statusChangeDate;
    private String statusChangeReason;
    private String audioStatus;
    private List<IdentityValidationType> identityValidations;
    private String audioUrl;
    private TimePeriod validFor;


}
