package com.tdp.ms.sales.expose;

import com.tdp.genesis.core.constants.HttpHeadersKey;
import com.tdp.ms.sales.business.SalesManagementService;
import com.tdp.ms.sales.model.entity.Sale;
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
public class SalesControllerTest {
    @Autowired
    private WebTestClient webClient;

    @MockBean
    private SalesManagementService salesManagementService;

    private static Sale sale;
    private static Sale salesResponse;

    @BeforeAll
    static void setup() {
        sale = Sale
                .builder()
                .id("1")
                .name("Sergio")
                .description("descripcion")
                .build();

        salesResponse = Sale
                .builder()
                .id("1")
                .name("Sergio")
                .description("descripcion")
                .build();
    }

    @Test
    void createdSales() {
        Mockito.when(salesManagementService.post(any(), any()))
                .thenReturn(Mono.just(salesResponse));

        WebTestClient.ResponseSpec responseSpec = webClient.post()
                .uri("/fesimple/v1/sales")
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
}
