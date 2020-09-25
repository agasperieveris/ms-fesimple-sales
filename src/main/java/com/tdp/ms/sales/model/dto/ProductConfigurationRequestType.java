package com.tdp.ms.sales.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class: Request. <br/>
 * <b>Copyright</b>: &copy; 2019 Telef&oacute;nica del Per&uacute;<br/>
 * <b>Company</b>: Telef&oacute;nica del Per&uacute;<br/>
 *
 * @author Telef&oacute;nica del Per&uacute; (TDP) <br/>
 *         <u>Service Provider</u>: Everis Per&uacute; SAC (EVE) <br/>
 *         <u>Developed by</u>: <br/>
 *         <ul>
 *         <li>Developer Ronald Barón</li>
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
@Builder
public class ProductConfigurationRequestType {
    private String installationAddressId;
    private String productOrderId;
    private String orderReferenceNumber;
    private String allocationID;
    private String appointmentId;
    private String appointmentNumber;
    private String sourceApp;
    private String newProductOfferingProductSpecID;
    private String cip;
    private String upfrontIndicator;
    private List<OrderAttributes> orderAttributes;
    private ShipmentDetails shipmentDetails;
    @JsonProperty("ServiceabilityInfo")
    private ServiceabilityInfo serviceabilityInfo;
    private List<NewProducts> newProducts;
    private List<OrderActionAttributes> orderActionAttributes;
    private ProductChanges productChanges;

}
