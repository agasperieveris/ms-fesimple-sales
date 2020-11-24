package com.tdp.ms.sales.eventflow.model;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

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

