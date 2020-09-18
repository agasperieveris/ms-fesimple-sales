package com.tdp.ms.sales.model.response;

import com.tdp.ms.sales.model.dto.*;

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
    private String description;
    private String priority;
    private Channel channel;
    private Agent agent;
    private String productType;
    private List<ComercialOperationType> commercialOperation;
    private EstimatedRevenue estimatedRevenue;
    private List<ProspectContact> prospectContact;
    private List<RelatedParty> relatedParty;
    private String status;
    private String statusChangeDate;
    private String statusChangeReason;
    private String audioStatus;
    private ValidFor validFor;
    private List<AdditionalData> additionalData;
}
