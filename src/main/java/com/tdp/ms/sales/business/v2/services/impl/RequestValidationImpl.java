package com.tdp.ms.sales.business.v2.services.impl;

import com.tdp.genesis.core.exception.GenesisException;
import com.tdp.ms.sales.model.dto.DeviceOffering;
import com.tdp.ms.sales.model.dto.KeyValueType;
import com.tdp.ms.sales.model.entity.Sale;
import com.tdp.ms.sales.model.request.PostSalesRequest;
import com.tdp.ms.sales.utils.Commons;
import com.tdp.ms.sales.utils.Constants;

import java.util.ArrayList;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class RequestValidationImpl implements com.tdp.ms.sales.business.v2.services.RequestValidation {
    @Override
    public void inputValidation(PostSalesRequest request) {
        // Getting Sale object
        Sale saleRequest = request.getSale();
        commercialOperationInputValidations(saleRequest);

        if (StringUtils.isEmpty(saleRequest.getId())) {
            throw GenesisException.builder().exceptionId(Constants.BAD_REQUEST_EXCEPTION_ID)
                    .wildcards(new String[]{"id is mandatory."}).build();
        }
        if (StringUtils.isEmpty(saleRequest.getSalesId())) {
            throw GenesisException.builder().exceptionId(Constants.BAD_REQUEST_EXCEPTION_ID)
                    .wildcards(new String[]{"salesId is mandatory."}).build();
        }

        // Getting token Mcss, request header to create product order service
        String tokenMcss = "";
        for (KeyValueType kv : saleRequest.getAdditionalData()) {
            if (kv.getKey().equals(Constants.UFX_AUTHORIZATION)) {
                tokenMcss = kv.getValue();
            }
        }
        if (tokenMcss == null || tokenMcss.equals("")) {
            request.getHeadersMap().put(Constants.UFX_AUTHORIZATION, "");
        } else {
            request.getHeadersMap().put(Constants.UFX_AUTHORIZATION, tokenMcss);
        }

        // Validation if is retail
        String flowSaleValue = saleRequest.getAdditionalData().stream()
                .filter(keyValueType -> keyValueType.getKey().equalsIgnoreCase(Constants.FLOWSALE))
                .findFirst().orElse(KeyValueType.builder().value(null).build()).getValue();
        boolean isRetail = flowSaleValue.equalsIgnoreCase(Constants.RETAIL);
        boolean isStatusValidado = saleRequest.getStatus().equalsIgnoreCase(Constants.STATUS_VALIDADO);

        if (isRetail && isStatusValidado) {
            saleRequest.getCommercialOperation().stream()
                    .filter(commercialOperationType -> {
                        final boolean[] isWireless = {false};
                        commercialOperationType.getAdditionalData().stream().filter(kv ->
                                kv.getKey().equalsIgnoreCase(Constants.PRODUCT_TYPE))
                                .findFirst()
                                .ifPresent(kv -> isWireless[0] = true);
                        return isWireless[0];
                    })
                    .forEach(commercialOperationType -> {

                        if (commercialOperationType.getDeviceOffering() != null
                                && !commercialOperationType.getDeviceOffering().isEmpty()) {
                            DeviceOffering deviceOfferingSim = commercialOperationType.getDeviceOffering().stream()
                                    .filter(item -> item.getDeviceType()
                                            .equalsIgnoreCase(Constants.DEVICE_TYPE_SIM))
                                    .findFirst().orElse(null);
                            if (deviceOfferingSim != null
                                    && StringUtils.isEmpty(Commons.getStringValueByKeyFromAdditionalDataList(
                                    saleRequest.getAdditionalData(), "SIM_ICCID"))) {
                                throw GenesisException.builder()
                                        .exceptionId(Constants.BAD_REQUEST_EXCEPTION_ID)
                                        .wildcards(new String[]{
                                                "SIM_ICCID is mandatory. Must be sent into Additional Data Property "
                                                        + "with 'SIM_ICCID' key value."})
                                        .build();
                            }

                            DeviceOffering deviceOfferingSmartphone = commercialOperationType.getDeviceOffering()
                                    .stream()
                                    .filter(item -> !item.getDeviceType()
                                            .equalsIgnoreCase(Constants.DEVICE_TYPE_SIM))
                                    .findFirst()
                                    .orElse(null);
                            if (deviceOfferingSmartphone != null
                                    && StringUtils.isEmpty(Commons.getStringValueByKeyFromAdditionalDataList(
                                    saleRequest.getAdditionalData(), "MOVILE_IMEI"))) {
                                throw GenesisException.builder()
                                        .exceptionId(Constants.BAD_REQUEST_EXCEPTION_ID)
                                        .wildcards(new String[]{
                                                "MOVILE_IMEI is mandatory. Must be sent into Additional Data Property "
                                                        + "with 'MOVILE_IMEI' key value."})
                                        .build();
                            }
                        }
                    });


            if (StringUtils.isEmpty(Commons.getStringValueByKeyFromAdditionalDataList(
                    saleRequest.getAdditionalData(), Constants.NUMERO_CAJA))) {
                throw GenesisException.builder()
                        .exceptionId(Constants.BAD_REQUEST_EXCEPTION_ID)
                        .wildcards(new String[]{
                                "NUMERO_CAJA is mandatory. Must be sent into Additional Data Property "
                                        + "with 'NUMERO_CAJA' key value."})
                        .build();
            } else if (StringUtils.isEmpty(Commons.getStringValueByKeyFromAdditionalDataList(
                    saleRequest.getAdditionalData(), "NUMERO_TICKET"))) {
                throw GenesisException.builder()
                        .exceptionId(Constants.BAD_REQUEST_EXCEPTION_ID)
                        .wildcards(new String[]{
                                "NUMERO_TICKET is mandatory. Must be sent into Additional Data Property"
                                        + " with 'NUMERO_TICKET' key value."})
                        .build();
            }
        }
    }

    private void commercialOperationInputValidations(Sale saleRequest) {
        saleRequest.getCommercialOperation().forEach(commercialOperationType -> {
            if (commercialOperationType.getProductOfferings() == null) {
                commercialOperationType.setProductOfferings(new ArrayList<>());
            }
            if (commercialOperationType.getAdditionalData() == null) {
                commercialOperationType.setAdditionalData(new ArrayList<>());
            }
        });
    }
}
