package com.tdp.ms.sales.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tdp.genesis.core.constants.HttpHeadersKey;
import com.tdp.genesis.core.exception.GenesisException;
import com.tdp.genesis.core.exception.GenesisExceptionBuilder;
import com.tdp.genesis.core.starter.web.client.catalog.utils.ExceptionsKind;
import com.tdp.genesis.core.utils.PropertyUtils;
import com.tdp.ms.sales.client.impl.StockWebClientImpl;
import com.tdp.ms.sales.model.ExceptionDto;
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
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.env.PropertyResolver;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import static org.mockito.Mockito.mock;

public class StockWebClientImplTest {
    private static final HashMap<String,String> headersMap = mappingHeaders();

    public static MockWebServer mockBackEnd;
    private StockWebClientImpl stockWebClientImpl;
    private ObjectMapper MAPPER = new ObjectMapper();
    private static ReserveStockRequest reserveStockRequest = new ReserveStockRequest();

    @BeforeAll
    static void setUp() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();

        preparePropertyResolverForPropertyUtils();

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

    private static void preparePropertyResolverForPropertyUtils() {
        PropertyResolver resolver = mock(PropertyResolver.class);
        PropertyUtils.setResolver(resolver);
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
    public void fallbackReserveStock_On_BadRequest_Test() throws JsonProcessingException, GenesisException {
        GenesisExceptionBuilder builder = GenesisException.builder();

        builder.exceptionId("SVC0001")
                .userMessage("Test")
                .exceptionText("Test")
                .wildcards(new String[]{"Bad Request"})
                .addDetail(true)
                .withDescription("Test");

        GenesisException ge = builder.build();

        WebClientResponseException webClientResponseException =
                new WebClientResponseException("There was a problem from Reserve Stock FE+Simple Service",
                        400, "Problem", null, MAPPER.writeValueAsBytes(ge), null);

        stockWebClientImpl.fallbackReserveStock(webClientResponseException);
    }

    @Test
    public void fallbackReserveStock_On_NotFound_Exception_Test() throws JsonProcessingException, GenesisException {
        GenesisExceptionBuilder builder = GenesisException.builder();

        builder.exceptionId("SVC0004")
                .userMessage("Test")
                .exceptionText("Test")
                .wildcards(new String[]{"Not FOund"})
                .addDetail(true)
                .withDescription("Test");

        GenesisException ge = builder.build();

        WebClientResponseException webClientResponseException =
                new WebClientResponseException("There was a problem from Reserve Stock FE+Simple Service",
                        404, "Problem", null, MAPPER.writeValueAsBytes(ge), null);

        stockWebClientImpl.fallbackReserveStock(webClientResponseException);
    }

    @Test
    public void fallbackReserveStock_On_ServerFailed_Exception_Test() throws JsonProcessingException, GenesisException {
        GenesisExceptionBuilder builder = GenesisException.builder();

        builder.exceptionId("SVR1008")
                .userMessage("Test")
                .exceptionText("Test")
                .wildcards(new String[]{"Service Failed"})
                .addDetail(true)
                .withDescription("Test");

        GenesisException ge = builder.build();

        WebClientResponseException webClientResponseException =
                new WebClientResponseException("There was a problem from Reserve Stock FE+Simple Service",
                        500, "Problem", null, MAPPER.writeValueAsBytes(ge), null);

        stockWebClientImpl.fallbackReserveStock(webClientResponseException);
    }

    @Test
    public void fallbackReserveStock_On_Unauthorized_Exception_Test() throws JsonProcessingException, GenesisException {
        GenesisExceptionBuilder builder = GenesisException.builder();

        builder.exceptionId("SVC0001")
                .userMessage("Test")
                .exceptionText("Test")
                .wildcards(new String[]{"Unauthorized"})
                .addDetail(true)
                .withDescription("Test");

        GenesisException ge = builder.build();

        WebClientResponseException webClientResponseException =
                new WebClientResponseException("There was a problem from Reserve Stock FE+Simple Service",
                        401, "Problem", null, MAPPER.writeValueAsBytes(ge), null);

        stockWebClientImpl.fallbackReserveStock(webClientResponseException);
    }

    @Test
    public void fallbackReserveStock_On_IllegalState_Exception_Test() throws GenesisException {
        stockWebClientImpl.fallbackReserveStock(new IllegalStateException());
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
