package com.tdp.ms.sales.client.impl;

import com.tdp.genesis.core.constants.HttpHeadersKey;
import com.tdp.genesis.core.exception.GenesisException;
import com.tdp.ms.sales.client.BusinessParameterWebClient;
import com.tdp.ms.sales.model.request.GetSalesCharacteristicsRequest;
import com.tdp.ms.sales.model.response.BusinessParametersFinanciamientoFijaResponse;
import com.tdp.ms.sales.model.response.BusinessParametersResponse;
import com.tdp.ms.sales.model.response.BusinessParametersResponseObjectExt;
import com.tdp.ms.sales.model.response.GetSalesCharacteristicsResponse;
import java.util.HashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * Class: BusinessParameterWebClientImpl. <br/>
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
 *         <li>2020-09-23 Creaci&oacute;n del proyecto.</li>
 *         </ul>
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
public class BusinessParameterWebClientImpl implements BusinessParameterWebClient {

    private final WebClient webClientInsecure;

    @Value("${application.endpoints.business_parameters.get_sales_characteristics_url}")
    private String getSalesCharacteristicsUrl;

    @Value("${application.endpoints.business_parameters.get_risk_domain_url}")
    private String getRiskDomainUrl;

    @Value("${application.endpoints.business_parameters.get_bonificacion_simcard}")
    private String getBonificacionSimcardUrl;

    @Value("${application.endpoints.business_parameters.get_parameters_simcard}")
    private String getParameterSimcardUrl;

    @Value("${application.endpoints.business_parameters.get_parameters_financiamiento_fija}")
    private String getParameterFinanciamientoFijaUrl;

    @Override
    public Mono<GetSalesCharacteristicsResponse> getSalesCharacteristicsByCommercialOperationType(
                                                                        GetSalesCharacteristicsRequest request) {
        return webClientInsecure
                .get()
                .uri(getSalesCharacteristicsUrl, request.getCommercialOperationType())
                .header(HttpHeadersKey.UNICA_APPLICATION, request.getHeadersMap().get(HttpHeadersKey.UNICA_APPLICATION))
                .header(HttpHeadersKey.UNICA_PID, request.getHeadersMap().get(HttpHeadersKey.UNICA_PID))
                .header(HttpHeadersKey.UNICA_SERVICE_ID, request.getHeadersMap().get(HttpHeadersKey.UNICA_SERVICE_ID))
                .header(HttpHeadersKey.UNICA_USER, request.getHeadersMap().get(HttpHeadersKey.UNICA_USER))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status -> status == HttpStatus.BAD_REQUEST,
                    clientResponse -> Mono.error(
                            GenesisException
                                    .builder()
                                    .exceptionId("SVR1000")
                                    .wildcards(new String[]{"Bad Request from Get Sales Characteristics "
                                            + "Operation in Business Parameters FE+Simple Service"})
                                    .build()))
                .onStatus(status -> status == HttpStatus.NOT_FOUND,
                    clientResponse -> Mono.error(
                            GenesisException
                                    .builder()
                                    .exceptionId("SVR1000")
                                    .wildcards(new String[]{"Not Found Status from Get Sales Characteristics "
                                            + "Operation in Business Parameters FE+Simple Service"})
                                    .build()))
                .bodyToMono(GetSalesCharacteristicsResponse.class);
    }

    @Override
    public Mono<BusinessParametersResponse> getRiskDomain(String domain, HashMap<String, String> headersMap) {
        return webClientInsecure
                .get()
                .uri(getRiskDomainUrl, domain)
                .header(HttpHeadersKey.UNICA_APPLICATION, headersMap.get(HttpHeadersKey.UNICA_APPLICATION))
                .header(HttpHeadersKey.UNICA_PID, headersMap.get(HttpHeadersKey.UNICA_PID))
                .header(HttpHeadersKey.UNICA_SERVICE_ID, headersMap.get(HttpHeadersKey.UNICA_SERVICE_ID))
                .header(HttpHeadersKey.UNICA_USER, headersMap.get(HttpHeadersKey.UNICA_USER))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                // TODO: Validar la estructura del Genesis
                .bodyToMono(BusinessParametersResponse.class);
    }

    @Override
    public Mono<BusinessParametersResponseObjectExt> getBonificacionSimcard(String channelId,
                                                                            HashMap<String,String> headersMap) {
        return webClientInsecure
                .get()
                .uri(getBonificacionSimcardUrl, channelId)
                .header(HttpHeadersKey.UNICA_APPLICATION, headersMap.get(HttpHeadersKey.UNICA_APPLICATION))
                .header(HttpHeadersKey.UNICA_PID, headersMap.get(HttpHeadersKey.UNICA_PID))
                .header(HttpHeadersKey.UNICA_SERVICE_ID, headersMap.get(HttpHeadersKey.UNICA_SERVICE_ID))
                .header(HttpHeadersKey.UNICA_USER, headersMap.get(HttpHeadersKey.UNICA_USER))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                // TODO: Configuración del WebClient para los headers
                .bodyToMono(BusinessParametersResponseObjectExt.class);
    }

    @Override
    public Mono<BusinessParametersResponseObjectExt> getParametersSimcard(HashMap<String, String> headersMap) {
        return webClientInsecure
                .get()
                .uri(getParameterSimcardUrl)
                .header(HttpHeadersKey.UNICA_APPLICATION, headersMap.get(HttpHeadersKey.UNICA_APPLICATION))
                .header(HttpHeadersKey.UNICA_PID, headersMap.get(HttpHeadersKey.UNICA_PID))
                .header(HttpHeadersKey.UNICA_SERVICE_ID, headersMap.get(HttpHeadersKey.UNICA_SERVICE_ID))
                .header(HttpHeadersKey.UNICA_USER, headersMap.get(HttpHeadersKey.UNICA_USER))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                // TODO: Configuración del WebClient para los headers
                .bodyToMono(BusinessParametersResponseObjectExt.class);
    }

    @Override
    public Mono<BusinessParametersFinanciamientoFijaResponse> getParametersFinanciamientoFija(
                                                                                HashMap<String, String> headersMap) {
        return webClientInsecure
                .get()
                .uri(getParameterFinanciamientoFijaUrl)
                .header(HttpHeadersKey.UNICA_APPLICATION, headersMap.get(HttpHeadersKey.UNICA_APPLICATION))
                .header(HttpHeadersKey.UNICA_PID, headersMap.get(HttpHeadersKey.UNICA_PID))
                .header(HttpHeadersKey.UNICA_SERVICE_ID, headersMap.get(HttpHeadersKey.UNICA_SERVICE_ID))
                .header(HttpHeadersKey.UNICA_USER, headersMap.get(HttpHeadersKey.UNICA_USER))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                // TODO: Configuración del WebClient para los headers
                .bodyToMono(BusinessParametersFinanciamientoFijaResponse.class);
    }

}
