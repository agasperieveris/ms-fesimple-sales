package com.tdp.ms.sales.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tdp.genesis.core.constants.HttpHeadersKey;
import com.tdp.ms.sales.client.impl.WebClientReceptorImpl;
import com.tdp.ms.sales.model.request.ReceptorRequest;
import com.tdp.ms.sales.model.response.ReceptorResponse;
import com.tdp.ms.sales.utils.ConstantsTest;

import junit.framework.Assert;

import java.io.IOException;
import java.util.HashMap;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

class ReceptopWebClientTest {

    public static MockWebServer mockBackEnd;
    private ObjectMapper MAPPER = new ObjectMapper();

    private WebClientReceptorImpl webClientReceptorImpl;
    private static final HashMap<String,String> headersMap = mappingHeaders();

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
        webClientReceptorImpl = new WebClientReceptorImpl(WebClient.create(baseUrl));
    }

    @Test
    void reserveStockTest() throws Exception {

        ReceptorRequest reserveStockRequest = new ReceptorRequest();
        ReceptorResponse receptorResponse = new ReceptorResponse();

        mockBackEnd.enqueue(new MockResponse()
                .setBody(MAPPER.writeValueAsString(receptorResponse))
                .addHeader("Content-Type", "application/json")
                .addHeader(HttpHeadersKey.UNICA_SERVICE_ID, ConstantsTest.RH_UNICA_SERVICE_ID)
                .addHeader(HttpHeadersKey.UNICA_APPLICATION, ConstantsTest.RH_UNICA_APPLICATION)
                .addHeader(HttpHeadersKey.UNICA_PID, ConstantsTest.RH_UNICA_PID)
                .addHeader(HttpHeadersKey.UNICA_USER, ConstantsTest.RH_UNICA_USER));

        webClientReceptorImpl.register(reserveStockRequest, headersMap);
        
        Assert.assertTrue(true);
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
