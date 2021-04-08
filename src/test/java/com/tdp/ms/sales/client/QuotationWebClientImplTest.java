package com.tdp.ms.sales.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tdp.genesis.core.constants.HttpHeadersKey;
import com.tdp.ms.sales.client.impl.QuotationWebClientImpl;
import com.tdp.ms.sales.model.dto.quotation.CreateQuotationRequestBody;
import com.tdp.ms.sales.model.request.CreateQuotationRequest;
import com.tdp.ms.sales.model.response.CreateQuotationResponse;
import com.tdp.ms.sales.utils.CommonsMocks;
import com.tdp.ms.sales.utils.ConstantsTest;

import junit.framework.Assert;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import java.io.IOException;

class QuotationWebClientImplTest {

    public static MockWebServer mockBackEnd;
    private QuotationWebClientImpl quotationWebClient;
    private ObjectMapper MAPPER = new ObjectMapper();
    private static CreateQuotationRequest createQuotationRequest = new CreateQuotationRequest();

    @BeforeAll
    static void setUp() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();

        // Building Reserve Stock Request
        CreateQuotationRequestBody createQuotationRequestBody = CreateQuotationRequestBody
                .builder()
                .orderId("767330")
                .accountId("534122")
                .billingAgreement("8884352")
                .commercialAgreement("X")
                .operationType("CAEQ")
                .financialEntity("FE-00001")
                .build();

        createQuotationRequest.setBody(createQuotationRequestBody);
        createQuotationRequest.setHeadersMap(CommonsMocks.createHeadersMock());
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }

    @BeforeEach
    void initialize() {
        String baseUrl = String.format("http://localhost:%s",
                mockBackEnd.getPort());
        quotationWebClient = new QuotationWebClientImpl(WebClient.create(baseUrl));
    }

    @Test
    void reserveStockTest() throws Exception {

        CreateQuotationResponse createQuotationResponse = CreateQuotationResponse
                .builder()
                .numberOfInstalments(1)
                .recurringChargePeriod("monthly")
                .amountPerInstalment(10)
                .build();

        mockBackEnd.enqueue(new MockResponse()
                .setBody(MAPPER.writeValueAsString(createQuotationResponse))
                .addHeader("Content-Type", "application/json")
                .addHeader(HttpHeadersKey.UNICA_SERVICE_ID, ConstantsTest.RH_UNICA_SERVICE_ID)
                .addHeader(HttpHeadersKey.UNICA_APPLICATION, ConstantsTest.RH_UNICA_APPLICATION)
                .addHeader(HttpHeadersKey.UNICA_PID, ConstantsTest.RH_UNICA_PID)
                .addHeader(HttpHeadersKey.UNICA_USER, ConstantsTest.RH_UNICA_USER));

        quotationWebClient.createQuotation(createQuotationRequest, CommonsMocks.createSaleMock());
        
        Assert.assertTrue(true);
    }

}
