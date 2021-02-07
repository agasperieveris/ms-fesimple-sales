package com.tdp.ms.sales.eventflow.client.impl;

import com.tdp.genesis.core.constants.HttpHeadersKey;
import com.tdp.ms.sales.eventflow.client.SalesWebClient;
import com.tdp.ms.sales.model.dto.KeyValueType;
import com.tdp.ms.sales.model.entity.Sale;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Class: SalesWebClientImpl. <br/>
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
 *         <li>2020-11-17 Creaci&oacute;n del proyecto.</li>
 *         </ul>
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class SalesWebClientImpl implements SalesWebClient {
    private final WebClient webClientInsecure;

    @Value("${application.endpoints.url.put_sale}")
    private String utlPutSale;

    @Override
    public void putSale(String salesId, Sale request, Map<String, String> headersMap) {
        webClientInsecure
                .put()
                .uri(utlPutSale, salesId)
                .header(HttpHeadersKey.UNICA_APPLICATION, headersMap.get(HttpHeadersKey.UNICA_APPLICATION))
                .header(HttpHeadersKey.UNICA_PID, headersMap.get(HttpHeadersKey.UNICA_PID))
                .header(HttpHeadersKey.UNICA_SERVICE_ID, headersMap.get(HttpHeadersKey.UNICA_SERVICE_ID))
                .header(HttpHeadersKey.UNICA_USER, headersMap.get(HttpHeadersKey.UNICA_USER))
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Sale.class)
                .block();
    }

    @Override
    public String validateBeforeUpdate(String eventFlow, String stepFlow, List<KeyValueType> additionalData) {
        if (eventFlow == null) {
            return "eventFlow nullPointerException";
        } else if (stepFlow == null) {
            return "stepFlow NullPointerException";
        } else if (additionalData == null) {
            return "additionalData NullPointerException";
        }

        if (eventFlow.equals("01")) {
            // flujo 1, pasos 2, 4, 6, 8
            switch (stepFlow) {
                case "02":
                    return !existFieldInAdditionalData("createContractDate", additionalData)
                            ? "No se ha añadido campo createContractDate" : "";
                case "04":
                    return "";
                case "06":
                    return !existFieldInAdditionalData("tratamientoDatosDate", additionalData)
                            ? "No se ha añadido campo createContractDate" : "";
                case "08":
                    return !existFieldInAdditionalData("afiliacionReciboDate", additionalData)
                            ? "No se ha añadido campo createContractDate" : "";
                default:
                    return "Se ha obtenido un stepFlow que no corresponde. stepFlow: " + stepFlow;
            }
        } else if (eventFlow.equals("02") && stepFlow.equals("02")) {
            // flujo 2, paso 2
            return !existFieldInAdditionalData("custodiaDate", additionalData)
                    ? "No se ha añadido campo custodiaDate" : "";
        } else if (eventFlow.equals("03")) {
            // Flujo 3: Creación de invitación
            return !existFieldInAdditionalData("DateCreateLMA", additionalData)
                    ? "No se ha añadido campo DateCreateLMA" : "";
        } else if (eventFlow.equals("99") && stepFlow.equals("01")) {

            String errorMsg = "";
            if(!existFieldInAdditionalData("motivoCancelacionEventos", additionalData)){
                errorMsg = "No se ha añadido campo motivoCancelacionEventos";
            }
            if(!existFieldInAdditionalData("fechaCancelacionEventos", additionalData)){
                errorMsg = "No se ha añadido campo fechaCancelacionEventos";
            }

            return errorMsg;

        } else {
            return "Se ha obtenido un eventFlow que no corresponde. eventFlow = " + eventFlow;
        }
    }

    private Boolean existFieldInAdditionalData(String key, List<KeyValueType> additionalData) {
        KeyValueType res = additionalData.stream()
                .filter(item -> item.getKey().equalsIgnoreCase(key)).findFirst().orElse(null);
        return res != null && res.getValue() != null && !res.getValue().isEmpty();
    }
}
