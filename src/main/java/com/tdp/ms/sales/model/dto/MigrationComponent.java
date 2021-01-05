package com.tdp.ms.sales.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tdp.ms.sales.model.dto.productorder.altafija.ProductChangeAltaFija;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Class: MigrationComponent. <br/>
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
@Getter
@Setter
@Builder
public class MigrationComponent {
    private String componentName;
    private String productId;
    private String productOfferingProductSpecId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ProductChangeAltaFija productChanges;
}