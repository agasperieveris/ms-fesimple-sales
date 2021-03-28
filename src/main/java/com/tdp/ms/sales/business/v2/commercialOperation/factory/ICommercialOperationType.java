package com.tdp.ms.sales.business.v2.commercialOperation.factory;

import com.tdp.ms.sales.model.dto.CommercialOperationType;
import com.tdp.ms.sales.model.dto.productorder.CreateProductOrderGeneralRequest;
import com.tdp.ms.sales.model.entity.Sale;
import com.tdp.ms.sales.model.request.PostSalesRequest;
import com.tdp.ms.sales.model.response.BusinessParametersResponseObjectExt;
import reactor.core.publisher.Mono;

import java.text.ParseException;

public interface ICommercialOperationType {
    /* Aquí se ponen los servicios principales que POST Sales llama */

    Mono<Sale> createProductOrder(PostSalesRequest request, final String cipCode, String customerIdRequest,
                                  final boolean flgFinanciamiento, String productOfferingIdRequest,
                                  String channelIdRequest, final boolean isRetail, final boolean flgCasi,
                                  final boolean sendIndicator,
                                  CommercialOperationType currentCommercialOperationType, String sapidSimcard,
                                  BusinessParametersResponseObjectExt getBonificacionSim,
                                  CreateProductOrderGeneralRequest mainRequestProductOrder);

    Mono<Sale> reserveStock(PostSalesRequest request, Sale saleRequest, boolean flgCasi,
                            boolean flgFinanciamiento, String sapidSimcard,
                            CommercialOperationType currentCommercialOperationType);

    Mono<Sale> createQuotation(PostSalesRequest request, Sale sale, boolean flgCasi, boolean flgFinanciamiento,
                               CommercialOperationType currentCommercialOperationType);
}
