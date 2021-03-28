package com.tdp.ms.sales.business.v2.services;

import com.tdp.ms.sales.model.request.PostSalesRequest;

public interface RequestValidation {
    void inputValidation(PostSalesRequest request);
}
