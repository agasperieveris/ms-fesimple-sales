package com.tdp.ms.sales.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class: Offer. <br/>
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
public class Offer {
    private String id;
    private String name;
    private String description;
    @JsonProperty("sales_channel_ids")
    private List<String> salesChannelIds;
    @JsonProperty("purchase_link")
    private String purchaseLink;
    private List<BillingOffering> billingOfferings;
    @JsonProperty("valid_until")
    private String validUntil;
    private Instalments instalments;
    private List<PriceRequires> requires;
    private List<String> skus;
}
