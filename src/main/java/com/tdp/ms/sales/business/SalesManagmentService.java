package com.tdp.ms.sales.business;

import com.tdp.ms.sales.model.request.PostSalesRequest;
import com.tdp.ms.sales.model.response.SalesResponse;
import reactor.core.publisher.Mono;

public interface SalesManagmentService {

    /**
     * Registra los datos de un nueva venta en la BBDD de la Web Convergente.
     *
     * @author @srivasme
     * @param request Datos de la nueva venta
     * @return SalesResponse, datos de la nueva venta registrada en la BBDD de la Web Convergente
     */
    Mono<SalesResponse> post(PostSalesRequest request);

}
