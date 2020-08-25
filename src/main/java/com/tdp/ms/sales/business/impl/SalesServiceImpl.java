package com.tdp.ms.sales.business.impl;

import com.tdp.ms.sales.business.SalesService;
import com.tdp.ms.sales.model.SalesResponse;
import org.springframework.stereotype.Service;

/**
 * Class: SalesServiceImpl. <br/>
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
@Service
public class SalesServiceImpl implements SalesService {

    @Override
    public SalesResponse get() {
        return new SalesResponse("Hello world!");
    }

    @Override
    public SalesResponse put(String name) {
        return new SalesResponse(name + " created!");
    }

}
