package com.tdp.ms.sales.business.v2.parrillaMt.factory;

import com.tdp.ms.sales.business.v2.commercialOperation.factory.CommercialOperationTypeFactory;
import com.tdp.ms.sales.business.v2.services.SalesMovistarTotalService;
import com.tdp.ms.sales.client.BusinessParameterWebClient;
import com.tdp.ms.sales.client.GetSkuWebClient;
import com.tdp.ms.sales.client.ProductOrderWebClient;
import com.tdp.ms.sales.repository.SalesRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class ParrillaAbstract {
    // Aquí van los métodos comunes en la mayoría de las clases en relación a la parte de la arquitectura del proyecto,
    // por ejemplo, webClient, signalR, etc.

    @Autowired
    protected CommercialOperationTypeFactory commercialOperationTypeFactory;

    @Autowired
    protected BusinessParameterWebClient businessParameterWebClient;

    @Autowired
    protected SalesMovistarTotalService salesMovistarTotalService;

    @Autowired
    protected ProductOrderWebClient productOrderWebClient;

    @Autowired
    protected GetSkuWebClient getSkuWebClient;

    @Autowired
    protected SalesRepository salesRepository;
}
