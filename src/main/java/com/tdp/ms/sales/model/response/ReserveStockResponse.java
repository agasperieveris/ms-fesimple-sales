package com.tdp.ms.sales.model.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tdp.genesis.core.exception.GenesisException;
import com.tdp.ms.sales.model.dto.reservestock.Order;
import com.tdp.ms.sales.model.dto.reservestock.StockItem;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class: ReserveStockResponse. <br/>
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
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReserveStockResponse {
    private String id;
    private Order order;
    private List<StockItem> items;
    @JsonIgnore
    private GenesisException exceptionService;
}
