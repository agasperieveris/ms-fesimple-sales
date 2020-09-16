package com.tdp.ms.sales.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class: ProductSpecification. <br/>
 * <b>Copyright</b>: &copy; 2019 Telef&oacute;nica del Per&uacute;<br/>
 * <b>Company</b>: Telef&oacute;nica del Per&uacute;<br/>
 *
 * @author Telef&oacute;nica del Per&uacute; (TDP) <br/>
 *         <u>Service Provider</u>: Everis Per&uacute; SAC (EVE) <br/>
 *         <u>Developed by</u>: <br/>
 *         <ul>
 *         <li>Developer Ronald</li>
 *         </ul>
 *         <u>Changes</u>:<br/>
 *         <ul>
 *         <li>YYYY-MM-DD Creaci&oacute;n del proyecto.</li>
 *         </ul>
 * @version 1.0
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProductSpecification {
    private String temporaryId;
    private String productID;
    private String businessType;
    private String productCatalogId;
    private String name; // response
    private String serviceId;
    private boolean selected;
    private String action;
    private String status;
    private String code;
    private String id;
    private String level;

    /**
     * Constructor.
     * 
     * @param name name.
     * @param code code.
     * @param id id.
     * @param level level.
     */
    public ProductSpecification(String name, String code, String id, String level) {
        this.name = name;
        this.code = code;
        this.id = id;
        this.level = level;
    }
}
