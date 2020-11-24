package com.tdp.ms.sales.eventflow.model;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

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
