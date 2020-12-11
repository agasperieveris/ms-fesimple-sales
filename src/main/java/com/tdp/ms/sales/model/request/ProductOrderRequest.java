package com.tdp.ms.sales.model.request;

import com.tdp.ms.sales.model.dto.Customer;
import com.tdp.ms.sales.model.dto.ProductConfigurationRequestType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class: ProductorderRequest. <br/>
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
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ProductOrderRequest {
    private String salesChannel;
    private String productOfferingID;
    private String onlyValidationIndicator;
    private String productOrderItemID;
    private Number customerId;
    private String actionType;
    private String reasonCode;
    private String reasonText;
    private Customer customer;
    private ProductConfigurationRequestType request;
}
