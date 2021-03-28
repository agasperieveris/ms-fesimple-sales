package com.tdp.ms.sales.business.v2.productType.factory;

import com.tdp.ms.sales.business.v2.productType.MovistarTotalImpl;
import com.tdp.ms.sales.business.v2.productType.StandAloneImpl;
import com.tdp.ms.sales.model.request.PostSalesRequest;
import com.tdp.ms.sales.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProductTypeFactory {
    @Autowired
    private StandAloneImpl standAlone;

    @Autowired
    private MovistarTotalImpl movistarTotal;

    // Aquí van los condicionales para extraer el tipo de producto (MT, StandAlone)
    public IProductType getProductType(PostSalesRequest request) {
        String productType = request.getSale().getProductType();
        if (productType.equalsIgnoreCase(Constants.MT)) {
            return movistarTotal;
        }
        return standAlone;
    }
}
