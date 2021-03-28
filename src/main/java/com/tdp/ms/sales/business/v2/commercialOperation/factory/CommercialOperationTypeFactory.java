package com.tdp.ms.sales.business.v2.commercialOperation.factory;

import com.tdp.ms.sales.business.v2.commercialOperation.wireless.AltaMovil;
import com.tdp.ms.sales.business.v2.commercialOperation.wireless.CaeqCaplMovil;
import com.tdp.ms.sales.business.v2.commercialOperation.wireless.CaeqMovil;
import com.tdp.ms.sales.business.v2.commercialOperation.wireless.CaplMovil;
import com.tdp.ms.sales.business.v2.commercialOperation.wireline.AltaFija;
import com.tdp.ms.sales.business.v2.commercialOperation.wireline.MigracionFija;
import com.tdp.ms.sales.model.dto.CommercialOperationType;
import com.tdp.ms.sales.model.dto.KeyValueType;
import com.tdp.ms.sales.model.entity.Sale;
import com.tdp.ms.sales.model.request.PostSalesRequest;
import com.tdp.ms.sales.utils.Commons;
import com.tdp.ms.sales.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CommercialOperationTypeFactory {
    @Autowired
    private AltaMovil altaMovil;
    @Autowired
    private CaeqMovil caeqMovil;
    @Autowired
    private CaplMovil caplMovil;
    @Autowired
    private CaeqCaplMovil caeqCaplMovil;

    @Autowired
    private AltaFija altaFija;
    @Autowired
    private MigracionFija migracionFija;

    // Aqui van los condicionales para extraer el tipo de operación comercial que se debe de procesar
    public ICommercialOperationType getCommercialOperationType(PostSalesRequest request,
                                                               CommercialOperationType commercialOperationType) {

        // Getting productType (WIRELINE OR WIRELESS)
        String mainProductType = Commons.getStringValueByKeyFromAdditionalDataList(
                commercialOperationType.getAdditionalData(), Constants.PRODUCT_TYPE);
        // Getting Main CommercialTypeOperation value
        String commercialOperationReason = commercialOperationType.getReason();

        if (mainProductType.equalsIgnoreCase(Constants.WIRELINE)) {
            if (commercialOperationReason.equalsIgnoreCase(Constants.ALTA)
                    && commercialOperationType.getAction().equalsIgnoreCase(Constants.PROVIDE)) {
                // Alta Fija
                return altaFija;
            } else if ((commercialOperationReason.equalsIgnoreCase(Constants.CAPL)
                    || commercialOperationReason.equalsIgnoreCase(Constants.REPLACEOFFER))
                    && commercialOperationType.getAction().equalsIgnoreCase(Constants.MODIFY)) {
                // Migración Fija
                return migracionFija;
            }
        } else if (mainProductType.equalsIgnoreCase(Constants.WIRELESS)) {
            // Recognizing Mobile Portability
            boolean isMobilePortability = commercialOperationReason.equalsIgnoreCase(Constants.PORTABILIDAD);

            // Commercial Operations Types Flags
            final boolean[] flgCapl = { false };
            final boolean[] flgCaeq = { false };
            final boolean[] flgCasi = { false };
            final boolean[] flgAlta = { false };
            setCommercialOperationFlags(commercialOperationType.getAdditionalData(), flgCapl, flgCaeq, flgAlta,
                    flgCasi);

            if (flgCapl[0] && !flgCaeq[0] && !flgCasi[0] && !flgAlta[0]) {
                // Recognizing CAPL Commercial Operation Type
                return caplMovil;
            } else if (!flgCapl[0] && flgCaeq[0] && !flgAlta[0]) {
                // Recognizing CAEQ Commercial Operation Type
                return caeqMovil;
            } else if (flgCapl[0] && flgCaeq[0] && !flgAlta[0]) {
                // Recognizing CAEQ+CAPL Commercial Operation Type
                return caeqCaplMovil;
            } else if (!flgCapl[0] && !flgCaeq[0] && flgAlta[0] || isMobilePortability) {
                // Recognizing ALTA or PORTABILIDAD Commercial Operation Type
                return altaMovil;
            }

        }
        throw Commons.buildGenesisError(Constants.BAD_REQUEST_EXCEPTION_ID, "No se encontró el productType");
    }

    private void setCommercialOperationFlags(List<KeyValueType> additionalData, final boolean[] flgCapl,
                                             final boolean[] flgCaeq, final boolean[] flgAlta,
                                             final boolean[] flgCasi) {
        // Getting Commercial Operation Types from Additional Data
        for (KeyValueType kv : additionalData) {
            String stringKey = kv.getKey();
            boolean booleanValue = kv.getValue().equalsIgnoreCase(Constants.STRING_TRUE);

            if (stringKey.equalsIgnoreCase(Constants.CAPL)) {
                flgCapl[0] = booleanValue;
            } else if (stringKey.equalsIgnoreCase(Constants.CAEQ)) {
                flgCaeq[0] = booleanValue;
            } else if (stringKey.equalsIgnoreCase(Constants.CASI)) {
                flgCasi[0] = booleanValue;
            } else if (stringKey.equalsIgnoreCase(Constants.ALTA)) {
                flgAlta[0] = booleanValue;
            }
        }
    }
}
