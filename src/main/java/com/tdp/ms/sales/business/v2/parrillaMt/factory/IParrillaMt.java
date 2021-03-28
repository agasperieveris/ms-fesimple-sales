package com.tdp.ms.sales.business.v2.parrillaMt.factory;

import com.tdp.ms.sales.model.entity.Sale;
import com.tdp.ms.sales.model.request.PostSalesRequest;
import reactor.core.publisher.Mono;

public interface IParrillaMt {
    // Aquí se ponen los servicios principales para Parrilla # MT

    Mono<Sale> processParrillaMT(PostSalesRequest request, final boolean isStatusValidado, final boolean isRetail);
}
