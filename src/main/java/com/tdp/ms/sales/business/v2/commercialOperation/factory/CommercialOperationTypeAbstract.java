package com.tdp.ms.sales.business.v2.commercialOperation.factory;

import com.tdp.ms.sales.business.v2.services.SalesMovistarTotalService;
import com.tdp.ms.sales.client.BusinessParameterWebClient;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class CommercialOperationTypeAbstract {
    // Aquí van los métodos comunes en la mayoría de las clases en relación a la parte de la arquitectura del proyecto,
    // por ejemplo, webClient, signalR, etc.

    @Autowired
    protected BusinessParameterWebClient businessParameterWebClient;

    @Autowired
    protected SalesMovistarTotalService salesMovistarTotalService;
}
