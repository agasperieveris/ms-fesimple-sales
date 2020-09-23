package com.tdp.ms.sales.expose;

import com.tdp.genesis.core.constants.HttpHeadersKey;
import com.tdp.ms.sales.business.SalesService;
import com.tdp.ms.sales.model.entity.Sale;
import com.tdp.ms.sales.model.response.SalesResponse;
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
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@AutoConfigureWebTestClient(timeout = "20000")
public class SalesLeadControllerTest {

    @Autowired
    private WebTestClient webClient;

    @MockBean
    private SalesService salesService;

    private static Sale sale;
    private static SalesResponse salesResponse;

    @BeforeAll
    static void setup() {
        sale = Sale
                .builder()
                .id("1")
                .name("Sergio")
                .description("descripcion")
                .build();

        salesResponse = SalesResponse
                .builder()
                .id("FE-000000001")
                .name("Sergio")
                .description("descripcion")
                .build();
    }

    @Test
    void createdSales() {
        Mockito.when(salesService.post(any()))
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
                .jsonPath("$.id").isEqualTo(sale.getId())
                .jsonPath("$.name").isEqualTo(sale.getName())
                .jsonPath("$.description").isEqualTo(sale.getDescription());

    }

    @Test
    void updateSales() {
        Mockito.when(salesService.put(any()))
                .thenReturn(Mono.just(salesResponse));

        WebTestClient.ResponseSpec responseSpec = webClient.put()
                .uri("/fesimple/v1/saleslead")
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeadersKey.UNICA_SERVICE_ID, "550e8400-e29b-41d4-a716-446655440000")
                .header(HttpHeadersKey.UNICA_APPLICATION, "genesis")
                .header(HttpHeadersKey.UNICA_PID, "550e8400-e29b-41d4-a716-446655440000")
                .header(HttpHeadersKey.UNICA_USER, "genesis")
                .bodyValue(sale)
                .exchange();

        responseSpec.expectStatus().isOk();

        responseSpec.expectBody()
                .jsonPath("$.id").isEqualTo(sale.getId())
                .jsonPath("$.name").isEqualTo(sale.getName())
                .jsonPath("$.description").isEqualTo(sale.getDescription());

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
    void confirmation() {
        Mockito.when(salesService.confirmationSalesLead(any(), any()))
                .thenReturn(Mono.just(salesResponse));

        WebTestClient.ResponseSpec responseSpec = webClient.post()
                .uri("/fesimple/v1/saleslead/confirmation")
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeadersKey.UNICA_SERVICE_ID, "550e8400-e29b-41d4-a716-446655440000")
                .header(HttpHeadersKey.UNICA_APPLICATION, "genesis")
                .header(HttpHeadersKey.UNICA_PID, "550e8400-e29b-41d4-a716-446655440000")
                .header(HttpHeadersKey.UNICA_USER, "genesis")
                .header("ufxauthorization", "")
                .bodyValue(salesResponse)
                .exchange();

        responseSpec.expectStatus().isCreated();

        responseSpec.expectBody()
                .jsonPath("$.id").isEqualTo(salesResponse.getId())
                .jsonPath("$.name").isEqualTo(salesResponse.getName())
                .jsonPath("$.description").isEqualTo(salesResponse.getDescription());

    }

    @Test
    void getSalesList() {
        Mockito.when(salesService.getSale(any()))
                .thenReturn(Mono.just(salesResponse));

        WebTestClient.ResponseSpec responseSpec = webClient.get()
                .uri("/fesimple/v1/saleslead")
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeadersKey.UNICA_SERVICE_ID, "550e8400-e29b-41d4-a716-446655440000")
                .header(HttpHeadersKey.UNICA_APPLICATION, "genesis")
                .header(HttpHeadersKey.UNICA_PID, "550e8400-e29b-41d4-a716-446655440000")
                .header(HttpHeadersKey.UNICA_USER, "genesis")
                .header("ufxauthorization", "ufxauthorization")
                .exchange();

        responseSpec.expectStatus().isOk();
    }

}
