package com.tdp.ms.sales.eventflow.model;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

/**
 * Class: EstadosOrquestador. <br/>
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
@Getter
public enum EstadosOrquestador {

    ENVIADO("00"),
    RECIBIDO("01"),
    PROCESANDO("02"),
    PROCESADO_EXITO("03"),
    PROCESADO_ERROR("99");

    private String codEstado;

    private static final Map<String, EstadosOrquestador> lookup = new HashMap<String, EstadosOrquestador>();

    static {
        for (EstadosOrquestador d : EstadosOrquestador.values()) {
            lookup.put(d.getCodEstado(), d);
        }
    }

    EstadosOrquestador(String codEstado) {
        this.codEstado = codEstado;
    }

    public static EstadosOrquestador get(String codEstado) {
        return lookup.get(codEstado);
    }
}

