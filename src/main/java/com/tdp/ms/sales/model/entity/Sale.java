package com.tdp.ms.sales.model.entity;

import com.microsoft.azure.spring.data.cosmosdb.core.mapping.Document;
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
import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import org.springframework.data.annotation.Id;

/**
 * Class: Sale. <br/>
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
@EqualsAndHashCode
@ToString
@Document(collection = "Sale")
public class Sale {
    @Id
    private String id;

    private String salesId;

    @NotEmpty(message = "Sales property 'name' can not be empty")
    private String name;

    private String description;

    private String priority;

    private ChannelRef channel;

    private RelatedParty agent;

    private String productType;

    private List<CommercialOperationType> commercialOperation;

    private Money estimatedRevenue;

    private PaymentType paymenType;

    private List<ContactMedium> prospectContact;

    private List<RelatedParty> relatedParty;

    private String saleCreationDate;

    private String status;

    private String statusChangeDate;

    private String statusChangeReason;

    private String audioStatus;

    private List<IdentityValidationType> identityValidations;

    private String audioUrl;

    private TimePeriod validFor;

    private List<KeyValueType> additionalData;
}
