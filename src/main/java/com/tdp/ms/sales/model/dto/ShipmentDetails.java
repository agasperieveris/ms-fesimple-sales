package com.tdp.ms.sales.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class: ShipmentDetails. <br/>
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
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShipmentDetails {
    private String recipientFirstName;
    private String recipientLastName;
    private String recipientTelephoneNumber;
    private String shipmentInstructions;
    private String provinceOfShippingAddress;
    private String shippingLocality;
    private String shopAddress;
    private String shopName;
    private String shipmentOption;
    private String collectStoreId;
    private String shipmentAddressId;
    private String shipmentSiteId;
    private String recipientEmail;

}
