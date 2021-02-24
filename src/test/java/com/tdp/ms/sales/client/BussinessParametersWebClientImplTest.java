package com.tdp.ms.sales.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tdp.genesis.core.constants.HttpHeadersKey;
import com.tdp.ms.sales.client.impl.BusinessParameterWebClientImpl;
import com.tdp.ms.sales.model.request.GetSalesCharacteristicsRequest;
import com.tdp.ms.sales.model.response.*;
import com.tdp.ms.sales.utils.ConstantsTest;
import java.io.IOException;
import java.util.HashMap;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class BussinessParametersWebClientImplTest {
    private static final HashMap<String,String> headersMap = mappingHeaders();

    public static MockWebServer mockBackEnd;
    private BusinessParameterWebClientImpl businessParameterWebClientImpl;
    private ObjectMapper MAPPER = new ObjectMapper();

    @BeforeAll
    static void setUp() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }

    @BeforeEach
    void initialize() {
        String baseUrl = String.format("http://localhost:%s",
                mockBackEnd.getPort());
        businessParameterWebClientImpl = new BusinessParameterWebClientImpl(WebClient.create(baseUrl));
    }

    @Test
    void getSalesCharacteristicsByCommercialOperationTypeTest() throws Exception {

        GetSalesCharacteristicsResponse businessParametersResponse = GetSalesCharacteristicsResponse
                .builder()
                .build();

        mockBackEnd.enqueue(new MockResponse()
                .setBody(MAPPER.writeValueAsString(businessParametersResponse))
                .addHeader("Content-Type", "application/json")
                .addHeader(HttpHeadersKey.UNICA_SERVICE_ID, ConstantsTest.RH_UNICA_SERVICE_ID)
                .addHeader(HttpHeadersKey.UNICA_APPLICATION, ConstantsTest.RH_UNICA_APPLICATION)
                .addHeader(HttpHeadersKey.UNICA_PID, ConstantsTest.RH_UNICA_PID)
                .addHeader(HttpHeadersKey.UNICA_USER, ConstantsTest.RH_UNICA_USER));

        businessParameterWebClientImpl.getSalesCharacteristicsByCommercialOperationType(GetSalesCharacteristicsRequest
                .builder()
                .commercialOperationType("CAEQ")
                .headersMap(headersMap)
                .build());
    }

    @Test
    public void getSalesCharacteristicsByCommercialOperationType_OnBadRequestStatusTest() {
        mockBackEnd.enqueue(new MockResponse().setResponseCode(400));

        Mono<GetSalesCharacteristicsResponse> result = businessParameterWebClientImpl.getSalesCharacteristicsByCommercialOperationType(GetSalesCharacteristicsRequest
                .builder()
                .commercialOperationType("CAEQ")
                .headersMap(headersMap)
                .build());

        //StepVerifier.create(result).verifyError();
    }

    @Test
    public void getSalesCharacteristicsByCommercialOperationType_OnNotFoundStatusTest() {
        mockBackEnd.enqueue(new MockResponse().setResponseCode(404));

        Mono<GetSalesCharacteristicsResponse> result = businessParameterWebClientImpl.getSalesCharacteristicsByCommercialOperationType(GetSalesCharacteristicsRequest
                .builder()
                .commercialOperationType("CAEQ")
                .headersMap(headersMap)
                .build());

        //StepVerifier.create(result).verifyError();
    }

    @Test
    void getRiskDomain_Test() throws JsonProcessingException {
        BusinessParametersResponse businessParametersResponse = BusinessParametersResponse.builder().build();

        mockBackEnd.enqueue(new MockResponse()
                .setBody(MAPPER.writeValueAsString(businessParametersResponse))
                .addHeader("Content-Type", "application/json")
                .addHeader(HttpHeadersKey.UNICA_SERVICE_ID, ConstantsTest.RH_UNICA_SERVICE_ID)
                .addHeader(HttpHeadersKey.UNICA_APPLICATION, ConstantsTest.RH_UNICA_APPLICATION)
                .addHeader(HttpHeadersKey.UNICA_PID, ConstantsTest.RH_UNICA_PID)
                .addHeader(HttpHeadersKey.UNICA_USER, ConstantsTest.RH_UNICA_USER));

        businessParameterWebClientImpl.getRiskDomain("everis.com", headersMap);
    }

    @Test
    void getBonificacionSimcard_Test() throws JsonProcessingException {
        BusinessParametersResponseObjectExt businessParametersResponseObjectExt = BusinessParametersResponseObjectExt
                .builder().build();

        mockBackEnd.enqueue(new MockResponse()
                .setBody(MAPPER.writeValueAsString(businessParametersResponseObjectExt))
                .addHeader("Content-Type", "application/json")
                .addHeader(HttpHeadersKey.UNICA_SERVICE_ID, ConstantsTest.RH_UNICA_SERVICE_ID)
                .addHeader(HttpHeadersKey.UNICA_APPLICATION, ConstantsTest.RH_UNICA_APPLICATION)
                .addHeader(HttpHeadersKey.UNICA_PID, ConstantsTest.RH_UNICA_PID)
                .addHeader(HttpHeadersKey.UNICA_USER, ConstantsTest.RH_UNICA_USER));

        businessParameterWebClientImpl.getBonificacionSimcard(headersMap);
    }

    @Test
    void getParametersSimcard_Test() throws JsonProcessingException {
        BusinessParametersResponseObjectExt businessParametersResponseObjectExt = BusinessParametersResponseObjectExt
                .builder().build();

        mockBackEnd.enqueue(new MockResponse()
                .setBody(MAPPER.writeValueAsString(businessParametersResponseObjectExt))
                .addHeader("Content-Type", "application/json")
                .addHeader(HttpHeadersKey.UNICA_SERVICE_ID, ConstantsTest.RH_UNICA_SERVICE_ID)
                .addHeader(HttpHeadersKey.UNICA_APPLICATION, ConstantsTest.RH_UNICA_APPLICATION)
                .addHeader(HttpHeadersKey.UNICA_PID, ConstantsTest.RH_UNICA_PID)
                .addHeader(HttpHeadersKey.UNICA_USER, ConstantsTest.RH_UNICA_USER));

        businessParameterWebClientImpl.getParametersSimcard(headersMap);
    }

    @Test
    void getParametersFinanciamientoFija_Test() throws JsonProcessingException {
        BusinessParametersFinanciamientoFijaResponse businessParametersFinanciamientoFijaResponse =
                BusinessParametersFinanciamientoFijaResponse
                        .builder()
                        .build();

        mockBackEnd.enqueue(new MockResponse()
                .setBody(MAPPER.writeValueAsString(businessParametersFinanciamientoFijaResponse))
                .addHeader("Content-Type", "application/json")
                .addHeader(HttpHeadersKey.UNICA_SERVICE_ID, ConstantsTest.RH_UNICA_SERVICE_ID)
                .addHeader(HttpHeadersKey.UNICA_APPLICATION, ConstantsTest.RH_UNICA_APPLICATION)
                .addHeader(HttpHeadersKey.UNICA_PID, ConstantsTest.RH_UNICA_PID)
                .addHeader(HttpHeadersKey.UNICA_USER, ConstantsTest.RH_UNICA_USER));

        businessParameterWebClientImpl.getParametersFinanciamientoFija(headersMap);
    }

    @Test
    void getParametersReasonCode_Test() throws JsonProcessingException {
        BusinessParametersReasonCode businessParametersReasonCode =
                BusinessParametersReasonCode
                        .builder()
                        .build();

        mockBackEnd.enqueue(new MockResponse()
                .setBody(MAPPER.writeValueAsString(businessParametersReasonCode))
                .addHeader("Content-Type", "application/json")
                .addHeader(HttpHeadersKey.UNICA_SERVICE_ID, ConstantsTest.RH_UNICA_SERVICE_ID)
                .addHeader(HttpHeadersKey.UNICA_APPLICATION, ConstantsTest.RH_UNICA_APPLICATION)
                .addHeader(HttpHeadersKey.UNICA_PID, ConstantsTest.RH_UNICA_PID)
                .addHeader(HttpHeadersKey.UNICA_USER, ConstantsTest.RH_UNICA_USER));

        businessParameterWebClientImpl.getParametersReasonCode(headersMap);
    }

    private static HashMap<String,String> mappingHeaders() {
        HashMap<String,String> headersMap = new HashMap();
        headersMap.put(HttpHeadersKey.UNICA_SERVICE_ID, ConstantsTest.RH_UNICA_SERVICE_ID);
        headersMap.put(HttpHeadersKey.UNICA_APPLICATION, ConstantsTest.RH_UNICA_APPLICATION);
        headersMap.put(HttpHeadersKey.UNICA_PID, ConstantsTest.RH_UNICA_PID);
        headersMap.put(HttpHeadersKey.UNICA_USER, ConstantsTest.RH_UNICA_USER);
        return headersMap;
    }

}
