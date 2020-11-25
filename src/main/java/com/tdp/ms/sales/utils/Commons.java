package com.tdp.ms.sales.utils;

import com.tdp.genesis.core.constants.HttpHeadersKey;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;

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

    public static String getTimeNowInMillis() throws ParseException {
        String dateFormat = "yyyy/MM/dd HH:mm:ss";
        String fechaActualSistema = "";
        ZoneId zone = ZoneId.of("America/Lima");
        ZonedDateTime date = ZonedDateTime.now(zone);
        DateTimeFormatter formatter = DateTimeFormatter
                .ofPattern(dateFormat);
        fechaActualSistema = date.format(formatter);

        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        Date date2 = (Date) sdf.parse(fechaActualSistema);
        long timeInMillis = date2.getTime();

        return String.valueOf(timeInMillis);
    }

    public static HashMap<String,String> fillHeaders(String serviceId, String application, String pid, String user) {
        HashMap<String, String> headersMap = new HashMap<>();
        headersMap.put(HttpHeadersKey.UNICA_SERVICE_ID, serviceId);
        headersMap.put(HttpHeadersKey.UNICA_APPLICATION, application);
        headersMap.put(HttpHeadersKey.UNICA_PID, pid);
        headersMap.put(HttpHeadersKey.UNICA_USER, user);

        return headersMap;
    }
}
