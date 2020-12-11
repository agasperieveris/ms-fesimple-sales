package com.tdp.ms.sales.model.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class: CommercialOperationType. <br/>
 * <b>Copyright</b>: &copy; 2020 Telef&oacute;nica del Per&uacute;<br/>
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
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CommercialOperationType {
    private String id;
    private String name;
    private String action;
    private String reason;
    private ServiceAvailabilityReportType serviceAvailability;
    private CreateProductOrderResponseType order;
    private PortabilityType portability;
    private WorkOrDeliveryType workOrDeliveryType;
    private ProductInstanceType product;
    private List<OfferingType> productOfferings;
    private List<DeviceOffering> deviceOffering;
    private List<KeyValueType> additionalData;
}
