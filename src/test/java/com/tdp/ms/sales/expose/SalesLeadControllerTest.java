package com.tdp.ms.sales.expose;

import com.tdp.genesis.core.constants.HttpHeadersKey;
import com.tdp.ms.sales.business.SalesService;
import com.tdp.ms.sales.model.dto.ChannelRef;
import com.tdp.ms.sales.model.dto.KeyValueType;
import com.tdp.ms.sales.model.dto.RelatedParty;
import com.tdp.ms.sales.model.entity.Sale;
import com.tdp.ms.sales.model.response.SalesResponse;
import com.tdp.ms.sales.utils.Constants;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@AutoConfigureWebTestClient(timeout = "20000")
public class SalesLeadControllerTest {

    @Autowired
    private WebTestClient webClient;

    @MockBean
    private SalesService salesService;

    private static Sale sale;
    private static Sale saleWithFileName;
    private static Sale salesResponse;
    private static Sale saleFromEventFlow;

    @BeforeAll
    static void setup() {
        sale = Sale.builder()
                .id("FE-000000001")
                .name("Sergio")
                .description("descripcion")
                .additionalData(Collections.singletonList(KeyValueType.builder().key("string").value("string").build()))
                .build();

        saleWithFileName = Sale.builder()
                .id("FE-000000001")
                .name("Sergio")
                .description("descripcion")
                .additionalData(Collections.singletonList(KeyValueType.builder().key("filename").value("string").build()))
                .build();

        saleFromEventFlow = Sale.builder()
                .id("FE-000000001")
                .name("Cesar")
                .additionalData(Collections.singletonList(KeyValueType.builder()
                        .key(Constants.SALES_FROM_EVENT_FLOW).build()))
                .build();

        ChannelRef channel = ChannelRef
                .builder()
                .dealerId("bc12")
                .storeId("Punto de venta")
                .build();

        RelatedParty agent = RelatedParty
                .builder()
                .id("bc12")
                .customerId("string")
                .nationalId("string")
                .nationalIdType("string")
                .build();

        salesResponse = Sale
                .builder()
                .id("FE-000000001")
                .name("Sergio")
                .description("descripcion")
                .channel(channel)
                .agent(agent)
                .status("string")
                .build();
    }

    @Test
    void createdSales() {
        Mockito.when(salesService.post(any(), any()))
                .thenReturn(Mono.just(salesResponse));

        WebTestClient.ResponseSpec responseSpec = webClient.post()
                .uri("/fesimple/v1/saleslead")
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeadersKey.UNICA_SERVICE_ID, "550e8400-e29b-41d4-a716-446655440000")
                .header(HttpHeadersKey.UNICA_APPLICATION, "genesis")
                .header(HttpHeadersKey.UNICA_PID, "550e8400-e29b-41d4-a716-446655440000")
                .header(HttpHeadersKey.UNICA_USER, "genesis")
                .bodyValue(sale)
                .exchange();

        responseSpec.expectStatus().isCreated();

        responseSpec.expectBody()
                .jsonPath("$.id").isEqualTo(salesResponse.getId())
                .jsonPath("$.name").isEqualTo(salesResponse.getName())
                .jsonPath("$.description").isEqualTo(salesResponse.getDescription());

    }

    @Test
    void updateSales_Test() {
        Mockito.when(salesService.put(any(), any(), any()))
                .thenReturn(Mono.just(salesResponse));
        Mockito.when(salesService.putEvent(any(), any(), any()))
                .thenReturn(Mono.just(salesResponse));
        Mockito.when(salesService.putEventFlow1(any(), any(), any()))
                .thenReturn(Mono.just(salesResponse));

        webClient.put()
                .uri("/fesimple/v1/saleslead/FE-000000001")
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeadersKey.UNICA_SERVICE_ID, "550e8400-e29b-41d4-a716-446655440000")
                .header(HttpHeadersKey.UNICA_APPLICATION, "genesis")
                .header(HttpHeadersKey.UNICA_PID, "550e8400-e29b-41d4-a716-446655440000")
                .header(HttpHeadersKey.UNICA_USER, "genesis")
                .bodyValue(sale)
                .exchange();

        webClient.put()
                .uri("/fesimple/v1/saleslead/FE-000000001")
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeadersKey.UNICA_SERVICE_ID, "550e8400-e29b-41d4-a716-446655440000")
                .header(HttpHeadersKey.UNICA_APPLICATION, "genesis")
                .header(HttpHeadersKey.UNICA_PID, "550e8400-e29b-41d4-a716-446655440000")
                .header(HttpHeadersKey.UNICA_USER, "genesis")
                .bodyValue(saleWithFileName)
                .exchange();

        webClient.put()
                .uri("/fesimple/v1/saleslead/FE-000000001")
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeadersKey.UNICA_SERVICE_ID, "550e8400-e29b-41d4-a716-446655440000")
                .header(HttpHeadersKey.UNICA_APPLICATION, "genesis")
                .header(HttpHeadersKey.UNICA_PID, "550e8400-e29b-41d4-a716-446655440000")
                .header(HttpHeadersKey.UNICA_USER, "genesis")
                .bodyValue(saleFromEventFlow)
                .exchange();
    }

    @Test
    void getSales() {
        WebTestClient.ResponseSpec responseSpec = webClient.get()
                .uri("/fesimple/v1/saleslead/1")
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeadersKey.UNICA_SERVICE_ID, "550e8400-e29b-41d4-a716-446655440000")
                .header(HttpHeadersKey.UNICA_APPLICATION, "genesis")
                .header(HttpHeadersKey.UNICA_PID, "550e8400-e29b-41d4-a716-446655440000")
                .header(HttpHeadersKey.UNICA_USER, "genesis")
                .exchange();

        responseSpec.expectStatus().isOk();
    }

    @Test
    void getSalesList() {
        Mockito.when(salesService.getSale(any()))
                .thenReturn(Mono.just(salesResponse));

        webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/fesimple/v1/saleslead")
                        .queryParam("id", salesResponse.getId())
                        .queryParam("dealerId", salesResponse.getChannel().getDealerId())
                        .queryParam("idAgent", salesResponse.getAgent().getId())
                        .queryParam("customerId", salesResponse.getAgent().getCustomerId())
                        .queryParam("nationalID", salesResponse.getAgent().getNationalId())
                        .queryParam("nationalIDType", salesResponse.getAgent().getNationalIdType())
                        .queryParam("status", salesResponse.getStatus())
                        .queryParam("channelId", salesResponse.getChannel().getId())
                        .queryParam("storeId", salesResponse.getChannel().getDealerId())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeadersKey.UNICA_SERVICE_ID, "550e8400-e29b-41d4-a716-446655440000")
                .header(HttpHeadersKey.UNICA_APPLICATION, "genesis")
                .header(HttpHeadersKey.UNICA_PID, "550e8400-e29b-41d4-a716-446655440000")
                .header(HttpHeadersKey.UNICA_USER, "genesis")
                .header("ufxauthorization", "ufxauthorization")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(SalesResponse.class);
    }

}
