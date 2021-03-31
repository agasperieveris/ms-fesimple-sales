package com.tdp.ms.sales.utils;

import com.tdp.genesis.core.constants.HttpHeadersKey;
import com.tdp.ms.sales.model.dto.KeyValueType;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Class: Commons. <br/>
 * <b>Copyright</b>: &copy; 2019 Telef&oacute;nica del Per&uacute;<br/>
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
 *         <li>2020-11-25 Creaci&oacute;n del proyecto.</li>
 *         </ul>
 * @version 1.0
 */
public class Commons {

    /**
     * Method to get Datetime now with CosmosDb Business Format.
     * @return String datetime
     */
    public static String getDatetimeNow() {
        String fechaActualSistema = "";
        ZoneId zone = ZoneId.of("America/Lima");
        ZonedDateTime date = ZonedDateTime.now(zone);
        DateTimeFormatter formatter = DateTimeFormatter
                .ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSZ");
        fechaActualSistema = date.format(formatter);

        return fechaActualSistema;
    }

    /**
     * Method to get Time now in Milliseconds.
     * @return String milliseconds value
     * @throws ParseException
     */
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

    /**
     * Method to insert into a HashMap request headers values.
     * @param serviceId String request parameter
     * @param application String request parameter
     * @param pid String request parameter
     * @param user String request parameter
     * @return Key (header name) - value (header value) HashMap
     */
    public static HashMap<String,String> fillHeaders(String serviceId, String application, String pid, String user) {
        HashMap<String, String> headersMap = new HashMap<>();
        headersMap.put(HttpHeadersKey.UNICA_SERVICE_ID, serviceId);
        headersMap.put(HttpHeadersKey.UNICA_APPLICATION, application);
        headersMap.put(HttpHeadersKey.UNICA_PID, pid);
        headersMap.put(HttpHeadersKey.UNICA_USER, user);

        return headersMap;
    }

    public static String getStringValueByKeyFromAdditionalDataList(List<KeyValueType> additionalData, String key) {
        final String[] stringValue = { "" };

        if (additionalData != null && !additionalData.isEmpty()) {
            additionalData.forEach(kv -> {
                if (kv.getKey().equalsIgnoreCase(key)) {
                    stringValue[0] = kv.getValue();
                }
            });
        }

        return stringValue[0];
    }
}
