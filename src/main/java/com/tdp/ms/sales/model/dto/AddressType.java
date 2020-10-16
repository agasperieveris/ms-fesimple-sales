package com.tdp.ms.sales.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class: AddressType. <br/>
 * <b>Copyright</b>: &copy; 2019 Telef&oacute;nica del Per&uacute;<br/>
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
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class AddressType {
    private String streetNr;
    private String streetNrSuffix;
    private String streetNrLast;
    private String streetNrLastSuffix;
    private String streetName;
    private String streetType;
    private String streetSuffix;
    private String postcode;
    private String city;
    private String cityCode;
    private String stateOrProvince;
    private String region;
    private String regionCode;
    private String country;
    private CoordinatesType coordinates;
    private String floor;
    private String apartment;
    private Boolean isDangerous;
    private String addressFormat;
    private String timeZone;
    private String areaCode;
    private String neighborhood;
    private String neighborhoodCode;
    private String comments;
    private Boolean isValid;
    private String housingComplexType;
    private String housingComplex;
    private String block;
    private String townCenterType;
    private String townCenterName;
    private String townCenterCode;
    private String lot;
    private String urbanization;
    private String buildingName;
    private String buildingType;
}
