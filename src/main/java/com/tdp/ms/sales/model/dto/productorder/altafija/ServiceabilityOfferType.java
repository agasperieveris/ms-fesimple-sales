package com.tdp.ms.sales.model.dto.productorder.altafija;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Class: ServiceabilityOfferType. <br/>
 * <b>Copyright</b>: &copy; 2020 Telef&oacute;nica del Per&uacute;<br/>
 * <b>Company</b>: Telef&oacute;nica del Per&uacute;<br/>
 *
 * @author Telef&oacute;nica del Per&uacute; (TDP) <br/>
 *         <u>Service Provider</u>: Everis Per&uacute; SAC (EVE) <br/>
 *         <u>Developed by</u>: <br/>
 *         <ul>
 *         <li>Cesar Gomez</li>
 *         </ul>
 *         <u>Changes</u>:<br/>
 *         <ul>
 *         <li>2020-11-11 Creaci&oacute;n del proyecto.</li>
 *         </ul>
 * @version 1.0
 */
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceabilityOfferType {
    private Number idOfferPriority;
    private List<ProductLineType> productOffer;
}
