package com.tdp.ms.sales.model.response;

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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Class: SalesResponse. <br/>
 * <b>Copyright</b>: &copy; 2019 Telef&oacute;nica del Per&uacute;<br/>
 * <b>Company</b>: Telef&oacute;nica del Per&uacute;<br/>
 *
 * @author Telef&oacute;nica del Per&uacute; (TDP) <br/>
 *         <u>Service Provider</u>: Everis Per&uacute; SAC (EVE) <br/>
 *         <u>Developed by</u>: <br/>
 *         <ul>
 *         <li>Developer RonalD Barón</li>
 *         </ul>
 *         <u>Changes</u>:<br/>
 *         <ul>
 *         <li>YYYY-MM-DD Creaci&oacute;n del proyecto.</li>
 *         </ul>
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Setter
public class SalesResponse {
    private String salesId;
    private String id;
    private String name;
    private List<KeyValueType> additionalData;
    private String description;
    private List<IdentityValidationType> identityValidations;
    private String priority;
    private TimePeriod validFor;
    private ChannelRef channel;
    private String audioUrl;
    private RelatedParty agent;
    private String statusChangeDate;
    private String statusChangeReason;
    private String audioStatus;
    private String productType;
    private List<RelatedParty> relatedParty;
    private String saleCreationDate;
    private String status;
    private List<CommercialOperationType> commercialOperationType;
    private List<ContactMedium> prospectContact;
    private Money estimatedRevenue;
    private PaymentType paymentType;
}
