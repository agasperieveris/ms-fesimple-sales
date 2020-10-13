package com.tdp.ms.sales.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tdp.genesis.core.constants.HttpHeadersKey;
import com.tdp.ms.sales.client.impl.PaymentWebClientImpl;
import com.tdp.ms.sales.model.dto.payment.GenerateCipRequestBody;
import com.tdp.ms.sales.model.request.GenerateCipRequest;
import com.tdp.ms.sales.model.response.GenerateCipResponse;
import com.tdp.ms.sales.utils.Constants;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.HashMap;

public class PaymentWebClientImplTest {
    private static final HashMap<String,String> headersMap = mappingHeaders();

    public static MockWebServer mockBackEnd;
    private PaymentWebClientImpl paymentWebClientImpl;
    private ObjectMapper MAPPER = new ObjectMapper();
    private static GenerateCipRequest generateCipRequest = new GenerateCipRequest();

    @BeforeAll
    static void setUp() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();

        // Building Generate Cip Request
        GenerateCipRequestBody generateCipRequestBody = GenerateCipRequestBody
                .builder()
                .amount(349.99)
                .currency("SOL")
                .dateExpiry("2020/10/13 12:50:50")
                .build();

        generateCipRequest = GenerateCipRequest
                .builder()
                .body(generateCipRequestBody)
                .headersMap(headersMap)
                .build();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }

    @BeforeEach
    void initialize() {
        String baseUrl = String.format("http://localhost:%s",
                mockBackEnd.getPort());
        paymentWebClientImpl = new PaymentWebClientImpl(WebClient.create(baseUrl));
    }

    @Test
    void generateCipTest() throws Exception {

        GenerateCipResponse expectGenerateCipResponse = GenerateCipResponse
                .builder()
                .build();

        mockBackEnd.enqueue(new MockResponse()
                .setBody(MAPPER.writeValueAsString(expectGenerateCipResponse))
                .addHeader("Content-Type", "application/json")
                .addHeader(HttpHeadersKey.UNICA_SERVICE_ID, Constants.RH_UNICA_SERVICE_ID)
                .addHeader(HttpHeadersKey.UNICA_APPLICATION, Constants.RH_UNICA_APPLICATION)
                .addHeader(HttpHeadersKey.UNICA_PID, Constants.RH_UNICA_PID)
                .addHeader(HttpHeadersKey.UNICA_USER, Constants.RH_UNICA_USER));

        paymentWebClientImpl.generateCip(generateCipRequest);
    }

    private static HashMap<String,String> mappingHeaders() {
        HashMap<String,String> headersMap = new HashMap();
        headersMap.put(HttpHeadersKey.UNICA_SERVICE_ID, Constants.RH_UNICA_SERVICE_ID);
        headersMap.put(HttpHeadersKey.UNICA_APPLICATION, Constants.RH_UNICA_APPLICATION);
        headersMap.put(HttpHeadersKey.UNICA_PID, Constants.RH_UNICA_PID);
        headersMap.put(HttpHeadersKey.UNICA_USER, Constants.RH_UNICA_USER);
        return headersMap;
    }

}
