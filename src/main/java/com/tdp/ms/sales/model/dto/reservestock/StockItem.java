package com.tdp.ms.sales.model.dto.reservestock;

import com.tdp.ms.sales.model.dto.QuantityType;
import com.tdp.ms.sales.model.dto.SiteRefType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class: StockItem. <br/>
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
 *         <li>2020-10-09 Creaci&oacute;n del proyecto.</li>
 *         </ul>
 * @version 1.0
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class StockItem {
    private Item item;
    private SiteRefType site;
    private QuantityType amount;
}
