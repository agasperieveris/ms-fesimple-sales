package com.tdp.ms.sales.model.response;

import com.tdp.ms.sales.model.dto.businessparameter.BusinessParameterFinanciamientoFijaData;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class: BusinessParametersFinanciamientoFijaResponse. <br/>
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
 *         <li>2020-11-20 Creaci&oacute;n del proyecto.</li>
 *         </ul>
 * @version 1.0
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class BusinessParametersFinanciamientoFijaResponse {
    private List<BusinessParameterFinanciamientoFijaData> data = new ArrayList<>();
}
