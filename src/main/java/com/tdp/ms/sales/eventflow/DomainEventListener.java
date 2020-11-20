package com.tdp.ms.sales.eventflow;

import com.microsoft.azure.spring.integration.core.AzureHeaders;
import com.microsoft.azure.spring.integration.core.api.reactor.Checkpointer;
import com.tdp.genesis.core.constants.HttpHeadersKey;
import com.tdp.ms.commons.util.MapperUtils;
import com.tdp.ms.sales.business.SalesService;
import com.tdp.ms.sales.eventflow.model.EstadosOrquestador;
import com.tdp.ms.sales.eventflow.model.Orquestador;
import com.tdp.ms.sales.model.entity.Sale;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@EnableBinding(Sink.class)
public class DomainEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(DomainEventListener.class);

    @Autowired
    private DomainEventPublisher domainEventPublisher;

    @Autowired
    private SalesService salesService;

    @StreamListener(Sink.INPUT)
    public void consumeMessage(@Payload Orquestador orquestador,
                               @Headers MessageHeaders headers,
                               @Header(AzureHeaders.CHECKPOINTER) Checkpointer checkpointer) {
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
            String eventLog = salesService.validateBeforeUpdate(orquestador.getCodEventFlow(),
                    orquestador.getCodStepFlow(), sale.getAdditionalData());
            if (eventLog.isEmpty()) {
                salesService.putEvent(sale.getSalesId(), sale, headersMap).block();
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

        //Procesar esta parte cuando ya se consuma el mensaje para que no vuelva a consumir
        checkpointer.success()
                .doOnSuccess(s -> {
                    LOGGER.info("->Mensaje '{}' comprobado checkpointed", orquestador);
                })
                .doOnError((msg) -> {
                    LOGGER.error(String.valueOf(msg));
                })
                .subscribe();
    }

    @ServiceActivator(inputChannel = "productorder-submit.$Default.errors")
    public void consumerError(Message<?> message) {
        System.out.println("**** Al invocar el Listener ERROR: " + message);
    }

    @ServiceActivator(inputChannel = "orchestrator-response.errors")
    public void producerError(Message<?> message) {
        System.out.println("AL Invocar el Producer ERROR: " + message);
    }

}
