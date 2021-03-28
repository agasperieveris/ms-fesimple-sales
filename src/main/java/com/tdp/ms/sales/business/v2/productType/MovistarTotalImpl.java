package com.tdp.ms.sales.business.v2.productType;

import com.tdp.ms.sales.business.v2.services.RequestValidation;
import com.tdp.ms.sales.business.v2.services.SalesMovistarTotalService;
import com.tdp.ms.sales.business.v2.productType.factory.IProductType;
import com.tdp.ms.sales.business.v2.productType.factory.ProductTypeAbstract;
import com.tdp.ms.sales.model.entity.Sale;
import com.tdp.ms.sales.model.request.PostSalesRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class MovistarTotalImpl extends ProductTypeAbstract implements IProductType {
    @Autowired
    private SalesMovistarTotalService salesMovistarTotalService;

    @Autowired
    private RequestValidation requestValidation;

    @Override
    public Mono<Sale> processProductType(PostSalesRequest request) {
        requestValidation.inputValidation(request); // Initial validations
        return salesMovistarTotalService.postSaleMovistarTotal(request);
    }
}
