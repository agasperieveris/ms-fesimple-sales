package com.tdp.ms.sales.model.entity;

import com.microsoft.azure.spring.data.cosmosdb.core.mapping.Document;
import com.tdp.ms.sales.model.dto.AdditionalData;
import com.tdp.ms.sales.model.dto.Agent;
import com.tdp.ms.sales.model.dto.Category;
import com.tdp.ms.sales.model.dto.Channel;
import com.tdp.ms.sales.model.dto.ComercialOperationType;
import com.tdp.ms.sales.model.dto.EstimatedRevenue;
import com.tdp.ms.sales.model.dto.ProspectContact;
import com.tdp.ms.sales.model.dto.RelatedParty;
import com.tdp.ms.sales.model.dto.SalesOpportunity;
import com.tdp.ms.sales.model.dto.ValidFor;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
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
@Document(collection = "Sale")
public class Sale {
    @Id
    private String id;

    private Long salesId;

    @NotEmpty(message = "Sales property 'name' can not be empty")
    private String name;

    private String description;

    private String priority;

    private Channel channel;

    private Agent agent;

    private List<ComercialOperationType> comercialOperationType;

    private Category category;

    private String productType;

    private EstimatedRevenue estimatedRevenue;

    private List<ProspectContact> prospectContact;

    private String rating;

    private String referredDate;

    private List<RelatedParty> relatedParty;

    private SalesOpportunity salesOpportunity;

    private String status;

    private String statusChangeReason;

    private String statusChangeDate;

    private String startDateTime;

    private String endDateTime;

    private String audioStatus;

    private String type;

    private ValidFor validFor;

    private List<AdditionalData> additionalData;
}
