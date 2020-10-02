package com.tdp.ms.sales.business;

import com.tdp.ms.sales.model.entity.Sale;
import com.tdp.ms.sales.model.request.PostSalesRequest;
import reactor.core.publisher.Mono;

public interface SalesManagmentService {

    /**
     * Actualiza datos de la orden de Sales.
     *
     * @author @cesargomezeveris
     * @param request Datos de la venta
     * @return SalesResponse, datos de la venta con información de la orden creada
     */
    Mono<Sale> post(PostSalesRequest request);

}
