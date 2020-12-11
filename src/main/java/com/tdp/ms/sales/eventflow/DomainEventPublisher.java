package com.tdp.ms.sales.eventflow;

import com.tdp.ms.sales.eventflow.model.Orquestador;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;

/**
 * Class: DomainEventPublisher. <br/>
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
