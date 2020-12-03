package com.tdp.ms.sales.eventflow.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Class: Orquestador. <br/>
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
 *         <li>2020-12-01 Creaci&oacute;n del proyecto.</li>
 *         </ul>
 * @version 1.0
 */
@NoArgsConstructor
@Getter
@Setter
@Data
public class Orquestador implements Serializable {

    private static final long serialVersionUID = -7214611173536299397L;
    private Long msgId;
    private String msgHeader;
    private String msgPayload;
    private String codStatus;
    private Long numTx;
    private String codEventFlow;
    private String codStepFlow;
    private LocalDateTime fecpublishMsg;
    private LocalDateTime fecIniProcessMsg;
    private LocalDateTime fecFinProcessMsg;
    private String log;

}
