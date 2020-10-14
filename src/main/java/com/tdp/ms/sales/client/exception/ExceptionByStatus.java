package com.tdp.ms.sales.client.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Class: ExceptionByStatus. <br/>
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
 *         <li>2020-10-14 Creaci&oacute;n del proyecto.</li>
 *         </ul>
 * @version 1.0
 */
@Getter
@RequiredArgsConstructor
public class ExceptionByStatus extends Exception {
    private final Integer exceptionStatus;
    private final String exceptionMessage;
}


