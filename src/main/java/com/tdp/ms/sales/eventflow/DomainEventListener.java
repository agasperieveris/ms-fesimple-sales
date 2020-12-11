package com.tdp.ms.sales.eventflow;

import com.tdp.genesis.core.constants.HttpHeadersKey;
import com.tdp.ms.commons.util.MapperUtils;
import com.tdp.ms.sales.eventflow.client.SalesWebClient;
import com.tdp.ms.sales.eventflow.model.EstadosOrquestador;
import com.tdp.ms.sales.eventflow.model.Orquestador;
import com.tdp.ms.sales.model.entity.Sale;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * Class: DomainEventListener. <br/>
 * <b>Copyright</b>: &copy; 2020 Telef&oacute;nica del Per&uacute;<br/>
 * <b>Company</b>: Telef&oacute;nica del Per&uacute;<br/>
 *
 * @author Telef&oacute;nica del Per&uacute; (TDP) <br/>
 *         <u>Service Provider</u>: Everis Per&uacute; SAC (EVE) <br/>
 *         <u>Developed by</u>: <br/>
 *         <ul>
 *         <li>Sergio Rivas</li>
 *         </ul>
 *         <u>Changes</u>:<br/>
 *         <ul>
 *         <li>2020-12-01 Creaci&oacute;n del proyecto.</li>
 *         </ul>
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
@EnableBinding(Sink.class)
public class DomainEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(DomainEventListener.class);

    @Autowired
    private DomainEventPublisher domainEventPublisher;

    @Autowired
    private SalesWebClient salesWebClient;

    @StreamListener(Sink.INPUT)
    public void consumeMessage(@Payload Orquestador orquestador,
                               @Headers MessageHeaders headers) {

        LOGGER.info("...............ORQUESTADOR_INBOX  Mensaje recibido: '{}'", orquestador);

        ZoneId zone = ZoneId.of("America/Lima");
        orquestador.setFecIniProcessMsg(ZonedDateTime.now(zone).toLocalDateTime());

        Sale sale = MapperUtils.mapper(Sale.class, orquestador.getMsgPayload());

        try {
            // TODO: quitar headers en duro cuando se actualice ms-sale con el filter de webclient
            Map<String, String> headersMap = new HashMap<>();
            headersMap.put(HttpHeadersKey.UNICA_APPLICATION, "VISOR");
            headersMap.put(HttpHeadersKey.UNICA_PID, "550e8400-e29b-41d4-a716-446655440000");
            headersMap.put(HttpHeadersKey.UNICA_SERVICE_ID, "550e8400-e29b-41d4-a716-446655440001");
            headersMap.put(HttpHeadersKey.UNICA_USER, "jreategui");

            // Validar que se haya agregado el nuevo campo en el participante anterior
            String eventLog = salesWebClient.validateBeforeUpdate(orquestador.getCodEventFlow(),
                    orquestador.getCodStepFlow(), sale.getAdditionalData());
            if (eventLog.isEmpty()) {
                salesWebClient.putSale(sale.getSalesId(), sale, headersMap);
                orquestador.setCodStatus(EstadosOrquestador.PROCESADO_EXITO.getCodEstado());
            } else {
                orquestador.setCodStatus(EstadosOrquestador.PROCESADO_ERROR.getCodEstado());
                orquestador.setLog(eventLog);
            }

        } catch (Exception e) {
            LOGGER.error("Listener Error: " + e);
            orquestador.setCodStatus(EstadosOrquestador.PROCESADO_ERROR.getCodEstado());
            orquestador.setLog(ExceptionUtils.getStackTrace(e));
        }

        orquestador.setFecFinProcessMsg(ZonedDateTime.now(zone).toLocalDateTime());
        domainEventPublisher.publish(headers, orquestador);

    }

}
