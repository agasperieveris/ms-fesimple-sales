package com.tdp.ms.sales.business.v2.services.impl;

import com.tdp.ms.sales.client.GetSkuWebClient;
import com.tdp.ms.sales.client.ProductOrderWebClient;
import com.tdp.ms.sales.client.QuotationWebClient;
import com.tdp.ms.sales.client.StockWebClient;
import com.tdp.ms.sales.repository.SalesRepository;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class SalesMovistarTotalAbstract {
    // Aquí van los métodos comunes en la mayoría de las clases en relación a la parte de la arquitectura del proyecto,
    // por ejemplo, webClient, signalR, etc.

    @Autowired
    protected ProductOrderWebClient productOrderWebClient;

    @Autowired
    protected GetSkuWebClient getSkuWebClient;

    @Autowired
    protected StockWebClient stockWebClient;

    @Autowired
    protected QuotationWebClient quotationWebClient;

    @Autowired
    protected SalesRepository salesRepository;
}
