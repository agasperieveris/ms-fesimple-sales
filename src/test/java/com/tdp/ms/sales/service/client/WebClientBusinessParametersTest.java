package com.tdp.ms.sales.service.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tdp.genesis.core.constants.HttpHeadersKey;
import com.tdp.ms.sales.client.impl.WebClientBusinessParametersImpl;
import com.tdp.ms.sales.model.dto.BusinessParameterData;
import com.tdp.ms.sales.model.response.BusinessParametersResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

public class WebClientBusinessParametersTest {
    private static String UNICA_APPLICATION = "FrontendPlatform";
    private static String UNICA_PID = "e4e361d2-2676-4f76-b95d-910c143a99b3";
    private static String UNICA_SERVICE_ID = "1b567df3-0fa8-4ad2-ab0b-a97291904361";
    private static String UNICA_USER = "UserFrontend";

    private static final Map<String,String> headersMap = mappingHeaders();

    public static MockWebServer mockBackEnd;
    private WebClientBusinessParametersImpl webClient;
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
        webClient = new WebClientBusinessParametersImpl(WebClient.create(baseUrl));
    }

    @Test
    void getNewSaleSequential() throws JsonProcessingException {
        // output
        BusinessParameterData businessParametersData = BusinessParameterData
                .builder()
                .value("FE-0000000486")
                .build();
        List<BusinessParameterData> businessParametersDataList = new ArrayList<>();
        businessParametersDataList.add(businessParametersData);

        BusinessParametersResponse businessParametersResponse = BusinessParametersResponse
                .builder()
                .data(businessParametersDataList)
                .build();

        mockBackEnd.enqueue(new MockResponse()
                .setBody(MAPPER.writeValueAsString(businessParametersResponse))
                .addHeader("Content-Type", "application/json")
                .addHeader(HttpHeadersKey.UNICA_SERVICE_ID, UNICA_USER)
                .addHeader(HttpHeadersKey.UNICA_APPLICATION, UNICA_APPLICATION)
                .addHeader(HttpHeadersKey.UNICA_PID, UNICA_PID)
                .addHeader(HttpHeadersKey.UNICA_USER, UNICA_USER)
                .addHeader(HttpHeadersKey.X_IBM_CLIENT_ID, UNICA_APPLICATION));

        webClient.getNewSaleSequential("SEQ001", headersMap);
    }

    private static Map<String,String> mappingHeaders(){
        Map<String,String> headersMap = new HashMap();
        headersMap.put(HttpHeadersKey.UNICA_SERVICE_ID, UNICA_SERVICE_ID);
        headersMap.put(HttpHeadersKey.UNICA_APPLICATION, UNICA_APPLICATION);
        headersMap.put(HttpHeadersKey.UNICA_PID, UNICA_PID);
        headersMap.put(HttpHeadersKey.UNICA_USER, UNICA_USER);
        headersMap.put("token",
                "AAIkMDVhYjFmOTctMmVmYi00OWY5LWEzYWUtNTAwNjQ0NGQyMjkxAhNSGcdmGKRcbq24iiXyZVOKIJjvtRHz_WiwFVO9axGPj-L40gUE9o_SkhCWrQWu4udugi0YV994L4E8N29wQZBqeAH2H61PsPB3a97qP1U");
        return headersMap;
    }
}
