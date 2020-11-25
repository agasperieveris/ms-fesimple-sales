package com.tdp.ms.sales.eventflow;

import com.tdp.ms.sales.eventflow.model.Orquestador;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;

@EnableBinding(Source.class)
@Component
public class DomainEventPublisher {

    @Autowired
    private Source source;

    public void publish(Map<String, Object> headers, Orquestador orquestador) {

        GenericMessage<Orquestador> msj = new GenericMessage<>(orquestador, headers);

        this.source.output().send(msj);
    }
}
