package com.tdp.ms.sales.business.v2.parrillaMt;

import com.tdp.ms.sales.business.v2.parrillaMt.factory.IParrillaMt;
import com.tdp.ms.sales.business.v2.parrillaMt.factory.ParrillaAbstract;
import com.tdp.ms.sales.model.entity.Sale;
import com.tdp.ms.sales.model.request.PostSalesRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ParrillaUnoDos extends ParrillaAbstract implements IParrillaMt {
    @Override
    public Mono<Sale> processParrillaMT(PostSalesRequest request, final boolean isStatusValidado,
                                        final boolean isRetail) {
        // 1 fijo + 2 movil
        return null;
    }
}
