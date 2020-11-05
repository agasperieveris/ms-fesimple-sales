package com.tdp.ms.sales.utils;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Commons {

    public static String getDatetimeNow() {
        String fechaActualSistema = "";
        ZoneId zone = ZoneId.of("America/Lima");
        ZonedDateTime date = ZonedDateTime.now(zone);
        DateTimeFormatter formatter = DateTimeFormatter
                .ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSZ");
        fechaActualSistema = date.format(formatter);

        return fechaActualSistema;
    }
}
