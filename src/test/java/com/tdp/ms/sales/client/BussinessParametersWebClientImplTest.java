package com.tdp.ms.sales.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tdp.genesis.core.constants.HttpHeadersKey;
import com.tdp.ms.sales.client.impl.BusinessParameterWebClientImpl;
import com.tdp.ms.sales.model.request.GetSalesCharacteristicsRequest;
import com.tdp.ms.sales.model.response.GetSalesCharacteristicsResponse;
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
import reactor.test.StepVerifier;

public class BussinessParametersWebClientImplTest {
    private static String UNICA_APPLICATION = "FrontendPlatform";
    private static String UNICA_PID = "e4e361d2-2676-4f76-b95d-910c143a99b3";
    private static String UNICA_SERVICE_ID = "1b567df3-0fa8-4ad2-ab0b-a97291904361";
    private static String UNICA_USER = "UserFrontend";

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
                .addHeader(HttpHeadersKey.UNICA_SERVICE_ID, UNICA_USER)
                .addHeader(HttpHeadersKey.UNICA_APPLICATION, UNICA_APPLICATION)
                .addHeader(HttpHeadersKey.UNICA_PID, UNICA_PID)
                .addHeader(HttpHeadersKey.UNICA_USER, UNICA_USER));

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

        StepVerifier.create(result).verifyError();
    }

    @Test
    public void getSalesCharacteristicsByCommercialOperationType_OnNotFoundStatusTest() {
        mockBackEnd.enqueue(new MockResponse().setResponseCode(404));

        Mono<GetSalesCharacteristicsResponse> result = businessParameterWebClientImpl.getSalesCharacteristicsByCommercialOperationType(GetSalesCharacteristicsRequest
                .builder()
                .commercialOperationType("CAEQ")
                .headersMap(headersMap)
                .build());

        StepVerifier.create(result).verifyError();
    }


    private static HashMap<String,String> mappingHeaders() {
        HashMap<String,String> headersMap = new HashMap();
        headersMap.put(HttpHeadersKey.UNICA_SERVICE_ID, UNICA_SERVICE_ID);
        headersMap.put(HttpHeadersKey.UNICA_APPLICATION, UNICA_APPLICATION);
        headersMap.put(HttpHeadersKey.UNICA_PID, UNICA_PID);
        headersMap.put(HttpHeadersKey.UNICA_USER, UNICA_USER);
        return headersMap;
    }

}
