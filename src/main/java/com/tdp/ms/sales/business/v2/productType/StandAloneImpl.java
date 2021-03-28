package com.tdp.ms.sales.business.v2.productType;

import com.tdp.ms.sales.business.v1.SalesManagmentService;
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
public class StandAloneImpl extends ProductTypeAbstract implements IProductType {
    @Autowired
    private SalesManagmentService salesManagmentService;

    @Override
    public Mono<Sale> processProductType(PostSalesRequest request) {
        return salesManagmentService.post(request);
    }
}
