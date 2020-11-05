package com.tdp.ms.sales.client;

import com.tdp.genesis.core.exception.GenesisException;
import com.tdp.genesis.core.utils.PropertyUtils;
import com.tdp.ms.sales.client.impl.QuotationWebClientImpl;
import com.tdp.ms.sales.repository.SalesRepository;
import com.tdp.ms.sales.utils.CommonsMocks;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.env.PropertyResolver;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class QuotationWebClientImplExceptionTest {

    @MockBean
    private SalesRepository salesRepository;

    @Autowired
    private QuotationWebClientImpl quotationWebClient;

    @BeforeAll
    static void setUp() {
        preparePropertyResolverForPropertyUtils();
    }

    private static void preparePropertyResolverForPropertyUtils() {
        PropertyResolver resolver = mock(PropertyResolver.class);
        PropertyUtils.setResolver(resolver);
    }

    @Test
    public void fallbackReserveStockTest() throws GenesisException {
        Mockito.when(salesRepository.save(any())).thenReturn(Mono.just(CommonsMocks.createSaleMock()));

        quotationWebClient.throwExceptionCreateQuotation(CommonsMocks.createSaleMock());
    }

}
