package com.tdp.ms.sales.business.v2.parrillaMt.factory;

import com.tdp.ms.sales.business.v2.parrillaMt.ParrillaTres;
import com.tdp.ms.sales.business.v2.parrillaMt.ParrillaUnoDos;
import com.tdp.ms.sales.model.request.PostSalesRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ParrillaMtFactory {
    @Autowired
    private ParrillaUnoDos parrillaUnoDos;

    @Autowired
    private ParrillaTres parrillaTres;

    public IParrillaMt getParrillaMT(PostSalesRequest request) {
        /* Parrilla 1: 1 fijo + 2 moviles (hasta 2 moviles)
        *  Parrilla 2: Es la versión mejorada de Parrilla 1 pero con mejores planes
        *  Parrilla 3: Cambia los planes a ofrecer, pero ahora la clasificación es de 1 fijo + 1 movil
        *
        *  *** Cuando conviertes un Parrilla 2 a un Parrilla 3, transformas una de las líeans del cliente a un LMA y
        *  mantienes el fijo y el movil con Parrilla 3
        *
        *  *** El concepto de parrilla es referido a solo gestión de planta y semicompletos MT */

        int sizeCommercialOperation = request.getSale().getCommercialOperation().size();
        if (sizeCommercialOperation == 2) {
            return parrillaTres;
        }
        return parrillaUnoDos;
    }
}
