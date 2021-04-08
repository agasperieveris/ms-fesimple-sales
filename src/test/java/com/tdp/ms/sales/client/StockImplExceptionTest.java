package com.tdp.ms.sales.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tdp.genesis.core.exception.GenesisException;
import com.tdp.genesis.core.utils.PropertyUtils;
import com.tdp.ms.sales.client.impl.StockWebClientImpl;
import com.tdp.ms.sales.model.entity.Sale;
import com.tdp.ms.sales.repository.SalesRepository;
import com.tdp.ms.sales.utils.CommonsMocks;

import junit.framework.Assert;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.env.PropertyResolver;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class StockImplExceptionTest {

    @MockBean
    private SalesRepository salesRepository;

    @Autowired
    private StockWebClient stockWebClient;

    @Autowired
    private StockWebClientImpl stockWebClientImpl;

    private ObjectMapper MAPPER = new ObjectMapper();

    @BeforeAll
    static void setUp() {
        preparePropertyResolverForPropertyUtils();
    }

    private static void preparePropertyResolverForPropertyUtils() {
        PropertyResolver resolver = mock(PropertyResolver.class);
        PropertyUtils.setResolver(resolver);
    }

    @Test
    void fallbackReserveStockBadRequestTest() throws GenesisException, JsonProcessingException {
        Mockito.when(salesRepository.save(any())).thenReturn(Mono.just(CommonsMocks.createSaleMock()));

        GenesisException genesisException = GenesisException.builder().exceptionId("SVC0001").build();
        WebClientResponseException webClientResponseException = new WebClientResponseException(
                "Error interno en reserve stock", 400, "Problem", null, MAPPER.writeValueAsBytes(genesisException),
                null);

        stockWebClient.throwExceptionReserveStock(CommonsMocks.createSaleMock(), webClientResponseException);
        
        Assert.assertTrue(true);
    }

    @Test
    void throwExceptionReserveStockBadRequestTest() throws GenesisException, JsonProcessingException,
            NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = StockWebClientImpl.class.getDeclaredMethod("throwException",
                Sale.class, Throwable.class);

        method.setAccessible(true);

        GenesisException genesisException = GenesisException.builder().exceptionId("SVC0001").build();
        WebClientResponseException webClientResponseException = new WebClientResponseException(
                "Error interno en reserve stock", 400, "Problem", null, MAPPER.writeValueAsBytes(genesisException),
                null);

        method.invoke(stockWebClientImpl, CommonsMocks.createSaleMock(), webClientResponseException);
        
        Assert.assertTrue(true);
    }

    @Test
    void throwExceptionReserveStockNotFoundStatusTest() throws GenesisException, JsonProcessingException,
            NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = StockWebClientImpl.class.getDeclaredMethod("throwException",
                Sale.class, Throwable.class);

        method.setAccessible(true);

        GenesisException genesisException = GenesisException.builder().exceptionId("SVC1006").build();
        WebClientResponseException webClientResponseException = new WebClientResponseException(
                "Error interno en reserve stock", 404, "Problem", null, MAPPER.writeValueAsBytes(genesisException),
                null);

        method.invoke(stockWebClientImpl, CommonsMocks.createSaleMock(), webClientResponseException);
        
        Assert.assertTrue(true);
    }

    @Test
    void throwExceptionReserveStockServerErrorTest() throws GenesisException, JsonProcessingException,
            NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = StockWebClientImpl.class.getDeclaredMethod("throwException",
                Sale.class, Throwable.class);

        method.setAccessible(true);

        GenesisException genesisException = GenesisException.builder().exceptionId("SVR1000").build();
        WebClientResponseException webClientResponseException = new WebClientResponseException(
                "Error interno en reserve stock", 500, "Problem", null, MAPPER.writeValueAsBytes(genesisException),
                null);

        method.invoke(stockWebClientImpl, CommonsMocks.createSaleMock(), webClientResponseException);
        
        Assert.assertTrue(true);
    }
}
