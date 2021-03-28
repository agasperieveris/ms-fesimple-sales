package com.tdp.ms.sales.business.v2.productType.factory;

import com.tdp.ms.sales.model.entity.Sale;
import com.tdp.ms.sales.model.request.PostSalesRequest;
import reactor.core.publisher.Mono;

public interface IProductType {
    // Aquí se ponen los servicios principales para el tipo de producto

    Mono<Sale> processProductType(PostSalesRequest request);
}
