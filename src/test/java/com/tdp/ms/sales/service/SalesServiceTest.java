package com.tdp.ms.sales.service;

import com.tdp.ms.sales.business.SalesService;
import com.tdp.ms.sales.model.entity.Sale;
import com.tdp.ms.sales.model.response.SalesResponse;
import com.tdp.ms.sales.repository.SalesRepository;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class SalesServiceTest {

    @MockBean
    private SalesRepository salesRepository;

    @Autowired
    private SalesService salesService;

    private static Sale sale;
    private static Sale sale2;
    private static SalesResponse salesResponse;

    @BeforeAll
    static void setup() {
        sale = Sale
                .builder()
                .id("1")
                .name("Sergio")
                .description("descripcion")
                .build();

        sale2 = Sale
                .builder()
                .id("1")
                .salesId(Long.valueOf(1))
                .name("Sergio")
                .description("descripcion")
                .build();

        salesResponse = SalesResponse
                .builder()
                .id("1")
                .name("Sergio")
                .description("descripcion")
                .build();
    }

    @Test
    void postSaveSale() {
        Mockito.when(salesRepository.findAll(any()))
                .thenReturn(Flux.just(sale2));

        Mockito.when(salesRepository.save(any()))
                .thenReturn(Mono.just(sale));

        Mono<SalesResponse> result = salesService.post(sale);

        StepVerifier.create(result)
                .assertNext(c -> {
                    Assert.assertEquals(c.getId(), sale.getId());
                })
                .verifyComplete();


    }

    @Test
    void putSaveSale() {
        Mockito.when(salesRepository.findById(sale2.getId()))
                .thenReturn(Mono.just(sale2));

        Mockito.when(salesRepository.save(any()))
                .thenReturn(Mono.just(sale2));

        Mono<SalesResponse> result = salesService.put(sale);

        StepVerifier.create(result)
                .assertNext(c -> {
                    Assert.assertEquals(c.getId(), sale2.getId());
                })
                .verifyComplete();
    }
}
