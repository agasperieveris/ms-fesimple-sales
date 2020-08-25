package com.tdp.ms.sales.expose;

import java.util.HashMap;
import java.util.Map;

import com.tdp.genesis.core.constants.HttpHeadersKey;
import com.tdp.ms.sales.model.SalesRequest;
import com.tdp.ms.sales.model.SalesResponse;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

@SpringBootTest
@AutoConfigureWebTestClient(timeout = "20000")
public class SalesControllerTest {

    @Autowired
    private WebTestClient webClient;

    private static Map<String, SalesResponse> salesResponseMap = new HashMap<>();
    private static Map<String, SalesRequest> salesRequestMap = new HashMap<>();

    @BeforeAll
    public static void setup() {
        salesResponseMap.put("get", new SalesResponse("Hello world!"));
        salesResponseMap.put("post", new SalesResponse("User created!"));
        salesRequestMap.put("post", new SalesRequest("User"));
        salesRequestMap.put("empty", new SalesRequest(""));
    }

    @Test
    public void indexGetTest() {
        webClient.get()
            .uri("/sales/v1/greeting")
            .accept(MediaType.APPLICATION_JSON)
            .header(HttpHeadersKey.UNICA_SERVICE_ID,"550e8400-e29b-41d4-a716-446655440000")
            .header(HttpHeadersKey.UNICA_APPLICATION,"genesis")
            .header(HttpHeadersKey.UNICA_PID,"550e8400-e29b-41d4-a716-446655440000")
            .header(HttpHeadersKey.UNICA_USER,"genesis")
            .header("ClientId","12122322")
            .exchange()
            .expectStatus().isOk()
            .expectBody(SalesResponse.class)
            .isEqualTo(salesResponseMap.get("get"));
    }

    @Test
    public void indexPostTest() {
        webClient.post()
            .uri("/sales/v1/greeting")
            .accept(MediaType.APPLICATION_JSON)
            .header(HttpHeadersKey.UNICA_SERVICE_ID,"550e8400-e29b-41d4-a716-446655440000")
            .header(HttpHeadersKey.UNICA_APPLICATION,"genesis")
            .header(HttpHeadersKey.UNICA_PID,"550e8400-e29b-41d4-a716-446655440000")
            .header(HttpHeadersKey.UNICA_USER,"genesis")
            .body(BodyInserters.fromValue(salesRequestMap.get("post")))
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.CREATED)
            .expectBody(SalesResponse.class)
            .isEqualTo(salesResponseMap.get("post"));
    }

    @Test
    public void validationRequestTest() {
        webClient.post()
            .uri("/sales/v1/greeting")
            .accept(MediaType.APPLICATION_JSON)
            .header(HttpHeadersKey.UNICA_SERVICE_ID,"550e8400-e29b-41d4-a716-446655440000")
            .header(HttpHeadersKey.UNICA_APPLICATION,"genesis")
            .header(HttpHeadersKey.UNICA_PID,"550e8400-e29b-41d4-a716-446655440000")
            .header(HttpHeadersKey.UNICA_USER,"genesis")
            .body(BodyInserters.fromValue(salesRequestMap.get("empty")))
            .exchange()
            .expectStatus().isBadRequest()
            .expectBody()
                .jsonPath("$.exceptionId").isEqualTo("SVC0001")
                .jsonPath("$.userMessage").isEqualTo("Generic Client Error")
                .jsonPath("$.exceptionDetails[0].component").isEqualTo("sales")
                .jsonPath("$.exceptionDetails[0].description")
                    .isEqualTo("name " + SalesRequest.MSG_NOT_EMPTY);
    }
}
