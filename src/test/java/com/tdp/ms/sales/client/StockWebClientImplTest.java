package com.tdp.ms.sales.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tdp.genesis.core.constants.HttpHeadersKey;
import com.tdp.genesis.core.exception.GenesisException;
import com.tdp.ms.sales.client.impl.StockWebClientImpl;
import com.tdp.ms.sales.model.dto.SiteRefType;
import com.tdp.ms.sales.model.dto.reservestock.Destination;
import com.tdp.ms.sales.model.dto.reservestock.Item;
import com.tdp.ms.sales.model.dto.reservestock.Order;
import com.tdp.ms.sales.model.dto.reservestock.StockItem;
import com.tdp.ms.sales.model.request.ReserveStockRequest;
import com.tdp.ms.sales.model.response.ReserveStockResponse;
import com.tdp.ms.sales.utils.Constants;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Rule;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class StockWebClientImplTest {
    private static final HashMap<String,String> headersMap = mappingHeaders();

    public static MockWebServer mockBackEnd;
    private StockWebClientImpl stockWebClientImpl;
    private ObjectMapper MAPPER = new ObjectMapper();
    private static ReserveStockRequest reserveStockRequest = new ReserveStockRequest();

    @Rule
    ExpectedException thrown = ExpectedException.none();

    @BeforeAll
    static void setUp() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();

        // Building Reserve Stock Request
        List<String> requiredActionsList = new ArrayList<>();
        requiredActionsList.add("PR");

        List<String> usageList = new ArrayList<>();
        usageList.add("sale");

        SiteRefType site = SiteRefType
                .builder()
                .id("6523456")
                .build();
        Destination destination = Destination
                .builder()
                .site(site)
                .type("store")
                .build();

        Item item = Item
                .builder()
                .id("TMGPEHUVTP10NES001")
                .type("IMEI")
                .build();
        StockItem stockItem1 = StockItem
                .builder()
                .item(item)
                .build();
        List<StockItem> items = new ArrayList<>();
        items.add(stockItem1);

        Order order = Order
                .builder()
                .id("123321")
                .build();

        reserveStockRequest = ReserveStockRequest
                .builder()
                .reason("PRAEL")
                .requiredActions(requiredActionsList)
                .usage(usageList)
                .destination(destination)
                .channel("CC")
                .items(items)
                .orderAction("346524")
                .order(order)
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
        stockWebClientImpl = new StockWebClientImpl(WebClient.create(baseUrl));
    }

    @Test
    void reserveStockTest() throws Exception {

        ReserveStockResponse reserveStockResponse = ReserveStockResponse
                .builder()
                .build();

        mockBackEnd.enqueue(new MockResponse()
                .setBody(MAPPER.writeValueAsString(reserveStockResponse))
                .addHeader("Content-Type", "application/json")
                .addHeader(HttpHeadersKey.UNICA_SERVICE_ID, Constants.RH_UNICA_SERVICE_ID)
                .addHeader(HttpHeadersKey.UNICA_APPLICATION, Constants.RH_UNICA_APPLICATION)
                .addHeader(HttpHeadersKey.UNICA_PID, Constants.RH_UNICA_PID)
                .addHeader(HttpHeadersKey.UNICA_USER, Constants.RH_UNICA_USER));

        stockWebClientImpl.reserveStock(reserveStockRequest, headersMap);
    }

    @Test
    public void reserveStock_OnBadRequestStatusTest() {
        mockBackEnd.enqueue(new MockResponse().setResponseCode(400));

        Mono<ReserveStockResponse> result = stockWebClientImpl.reserveStock(reserveStockRequest, headersMap);

        StepVerifier.create(result).verifyError();
    }

    @Test
    public void call2_should_throw_a_WantedException__not_call1() {
        // expectations
        thrown.expect(GenesisException.class);
        thrown.expectMessage("boom");

        Mono<ReserveStockResponse> result = stockWebClientImpl.reserveStock(reserveStockRequest, headersMap);

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
