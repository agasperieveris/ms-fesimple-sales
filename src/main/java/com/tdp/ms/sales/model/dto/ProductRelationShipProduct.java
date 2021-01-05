package com.tdp.ms.sales.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class: ProductRelationShipProduct. <br/>
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
 *         <li>2021-01-05 Creaci&oacute;n del proyecto.</li>
 *         </ul>
 * @version 1.0
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductRelationShipProduct {
    private String description;
    private Object relatedParty;
    private Object characteristic;
    private String name;
    private Object productRelationship;
    private String id;
    private String href;
    private Object place;
    private Object additionalData;
    private Object category;
    private Object productSpec;
    private String publicId;
    private String productType;
    private String startDate;
    private Object productPrice;
}