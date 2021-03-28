package com.tdp.ms.sales.business.v2.commercialOperation.wireline;

import com.tdp.ms.sales.business.v2.commercialOperation.factory.CommercialOperationTypeAbstract;
import com.tdp.ms.sales.business.v2.commercialOperation.factory.ICommercialOperationType;
import com.tdp.ms.sales.model.dto.CommercialOperationType;
import com.tdp.ms.sales.model.dto.productorder.CreateProductOrderGeneralRequest;
import com.tdp.ms.sales.model.entity.Sale;
import com.tdp.ms.sales.model.request.PostSalesRequest;
import com.tdp.ms.sales.model.response.BusinessParametersResponseObjectExt;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class MigracionFija extends CommercialOperationTypeAbstract implements ICommercialOperationType {
    @Override
    public Mono<Sale> createProductOrder(PostSalesRequest request, final String cipCode, String customerIdRequest,
                                         final boolean flgFinanciamiento, String productOfferingIdRequest,
                                         String channelIdRequest, final boolean isRetail, final boolean flgCasi,
                                         final boolean sendIndicator,
                                         CommercialOperationType currentCommercialOperationType, String sapidSimcard,
                                         BusinessParametersResponseObjectExt getBonificacionSim,
                                         CreateProductOrderGeneralRequest mainRequestProductOrder) {
        return null;
    }

    @Override
    public Mono<Sale> reserveStock(PostSalesRequest request, Sale saleRequest, boolean flgCasi,
                                   boolean flgFinanciamiento, String sapidSimcard,
                                   CommercialOperationType currentCommercialOperationType) {
        return null;
    }

    @Override
    public Mono<Sale> createQuotation(PostSalesRequest request, Sale sale, boolean flgCasi, boolean flgFinanciamiento,
                                      CommercialOperationType currentCommercialOperationType) {
        return null;
    }
}
