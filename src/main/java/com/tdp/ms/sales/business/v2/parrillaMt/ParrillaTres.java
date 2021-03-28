package com.tdp.ms.sales.business.v2.parrillaMt;

import com.google.gson.Gson;
import com.tdp.genesis.core.exception.GenesisException;
import com.tdp.ms.sales.business.v2.commercialOperation.factory.ICommercialOperationType;
import com.tdp.ms.sales.business.v2.parrillaMt.factory.IParrillaMt;
import com.tdp.ms.sales.business.v2.parrillaMt.factory.ParrillaAbstract;
import com.tdp.ms.sales.model.dto.*;
import com.tdp.ms.sales.model.dto.productorder.CreateProductOrderGeneralRequest;
import com.tdp.ms.sales.model.entity.Sale;
import com.tdp.ms.sales.model.request.PostSalesRequest;
import com.tdp.ms.sales.model.response.BusinessParametersResponse;
import com.tdp.ms.sales.model.response.BusinessParametersResponseObjectExt;
import com.tdp.ms.sales.utils.Commons;
import com.tdp.ms.sales.utils.Constants;

import java.text.ParseException;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ParrillaTres extends ParrillaAbstract implements IParrillaMt {
    private static final Logger LOG = LoggerFactory.getLogger(ParrillaTres.class);

    @Override
    public Mono<Sale> processParrillaMT(PostSalesRequest request, final boolean isStatusValidado,
                                        final boolean isRetail) {
        // 1 fijo + 1 movil: Solo se procesar el movil
        CommercialOperationType currentCommercialOperationType = request.getSale().getCommercialOperation().get(1);

        boolean flgFinanciamiento = setFinancingFlag(currentCommercialOperationType.getDeviceOffering());

        boolean sendIndicator = isNecessaryToSendVariablesInOrderCreation(currentCommercialOperationType);
        String cipCode = getCipCode(request.getSale().getPaymenType(), sendIndicator, flgFinanciamiento);

        final boolean[] flgCasi = {false};
        currentCommercialOperationType.getAdditionalData().stream()
                .filter(kv -> kv.getKey().equalsIgnoreCase(Constants.CASI)
                        && kv.getValue().equalsIgnoreCase(Constants.STRING_TRUE))
                .findAny()
                .ifPresent(kv -> flgCasi[0] = true);

        // Get Parameters Simcard
        return businessParameterWebClient.getParametersSimcard(request.getHeadersMap()).flatMap(simCardParameter -> {
            // Getting simcard sapid from bussiness parameter
            String sapidSimcard = getStringValueFromBusinessParameterDataListByKeyAndActiveTrue(
                    simCardParameter.getData(), Constants.SAPID);

            // Reintentos para MT
            return salesRepository.findBySalesId(request.getSale().getSalesId())
                    .defaultIfEmpty(Sale.builder().salesId(null).build())
                    // Validate existing sale
                    .flatMap(saleItem -> wirelessCase(request, saleItem, isRetail, currentCommercialOperationType,
                            sapidSimcard, cipCode, flgCasi[0], flgFinanciamiento, sendIndicator));
        });
    }

    private Mono<Sale> wirelessCase(PostSalesRequest request, Sale saleItem, final boolean isRetail,
                                    CommercialOperationType currentCommercialOperationType, String sapidSimcard,
                                    String cipCode, final boolean flgCasi, final boolean flgFinanciamiento,
                                    boolean sendIndicator) {

        if (saleItem.getSalesId() == null || saleItem.getCommercialOperation() == null
                || saleItem.getCommercialOperation().get(1).getOrder() == null) {
            // Main Function
            LOG.info("Wireless Sales Case");

            // Mobile Commercial Operations
            Sale saleRequest = request.getSale();
            boolean isDeviceOfferingNullOrEmpty = deviceOfferingIsNullOrEmpty(currentCommercialOperationType);

            if ((currentCommercialOperationType.getOrder() == null || StringUtils.isEmpty(
                    currentCommercialOperationType.getOrder().getProductOrderId()))
                    && isDeviceOfferingNullOrEmpty) {

                // Get mail Validation, dominio de riesgo - SERGIO
                Mono<BusinessParametersResponse> getRiskDomain = businessParameterWebClient
                        .getRiskDomain(retrieveDomain(saleRequest.getProspectContact()),
                                request.getHeadersMap());

                // Getting commons request properties
                String channelIdRequest = saleRequest.getChannel().getId();
                String customerIdRequest = saleRequest.getRelatedParty().get(0).getCustomerId();
                String productOfferingIdRequest = currentCommercialOperationType.getProductOfferings().get(0).getId();

                // Get Bonificacion Simcard
                Mono<BusinessParametersResponseObjectExt> getBonificacionSim = businessParameterWebClient
                        .getBonificacionSimcard(request.getHeadersMap());

                // Añadir llamada a get businessParameters - ReasonCode
                return Mono.zip(getRiskDomain, getBonificacionSim).flatMap(tuple -> {

                    if (!tuple.getT1().getData().isEmpty() && tuple.getT1().getData().get(0).getActive()) {
                        // if it is a risk domain, cancel operation
                        throw GenesisException.builder().exceptionId("SVR1000")
                                .wildcards(new String[]{"Dominio de riesgo, se canceló la operación"})
                                .build();
                    }

                    return createOrderReserverStockAndCallQuotation(tuple.getT2(), request,
                            saleRequest, cipCode, channelIdRequest, productOfferingIdRequest, customerIdRequest,
                            flgFinanciamiento, isRetail, flgCasi, sendIndicator, currentCommercialOperationType,
                            sapidSimcard);
                });

            } else {
                throw Commons.buildGenesisError(Constants.BAD_REQUEST_EXCEPTION_ID,
                        "deviceOffering or commercialOperation is/are null or empty");
            }
        }
        // It is a Retry
        request.setSale(saleItem);
        return retryRequest(request, saleItem, flgCasi, flgFinanciamiento, sapidSimcard,
                saleItem.getCommercialOperation().get(1));
    }

    private Mono<Sale> retryRequest(PostSalesRequest request, Sale sale, boolean flgCasi, boolean flgFinanciamiento,
                                    String sapidSimcard, CommercialOperationType currentCommercialOperationType) {

        // Se manda el movil y se recibe el caso correspondiente
        ICommercialOperationType iCommercialOperationType = commercialOperationTypeFactory
                .getCommercialOperationType(request, currentCommercialOperationType);

        sale.setStatus(Constants.SALES_STATUS_NUEVO);
        LOG.info("Sales Retry");
        if (currentCommercialOperationType.getOrder() != null
                && (currentCommercialOperationType.getDeviceOffering() == null
                || currentCommercialOperationType.getDeviceOffering().get(0).getStock() == null
                || StringUtils.isEmpty(currentCommercialOperationType.getDeviceOffering().get(0).getStock()
                .getReservationId()))) {

            // Retry from Reservation
            return iCommercialOperationType.reserveStock(request, sale, flgCasi, flgFinanciamiento, sapidSimcard,
                    currentCommercialOperationType).flatMap(saleReserveStockResponse -> {
                request.setSale(saleReserveStockResponse);

                return iCommercialOperationType.createQuotation(request, saleReserveStockResponse, flgCasi,
                        flgFinanciamiento, currentCommercialOperationType);
            });

        } else if (currentCommercialOperationType.getOrder() != null
                && currentCommercialOperationType.getDeviceOffering() != null
                && currentCommercialOperationType.getDeviceOffering().get(0).getStock() != null
                && !StringUtils.isEmpty(currentCommercialOperationType.getDeviceOffering().get(0).getStock()
                .getReservationId())) {

            // Retry from Create Quotation
            return iCommercialOperationType.createQuotation(request, sale, flgCasi, flgFinanciamiento,
                    currentCommercialOperationType);
        }
        throw Commons.buildGenesisError(Constants.BAD_REQUEST_EXCEPTION_ID,
                "Orden creada, reserva hecha y financiamiento ok, falla en otra cosa luego de esos 3 puntos");
    }

    private Mono<Sale> createOrderReserverStockAndCallQuotation(BusinessParametersResponseObjectExt getBonificacionSim,
                                                                PostSalesRequest request, Sale saleRequest,
                                                                String cipCode, String channelIdRequest,
                                                                String productOfferingIdRequest,
                                                                String customerIdRequest,
                                                                final boolean flgFinanciamiento,
                                                                final boolean isRetail, final boolean flgCasi,
                                                                final boolean sendIndicator,
                                                                CommercialOperationType currentCommercialOperationType,
                                                                String sapidSimcard) {

        // Building Main Request to send to Create Product Order Service
        CreateProductOrderGeneralRequest mainRequestProductOrder = new CreateProductOrderGeneralRequest();

        // Se manda el movil y se recibe el caso correspondiente
        ICommercialOperationType iCommercialOperationType = commercialOperationTypeFactory
                .getCommercialOperationType(request, currentCommercialOperationType);

        return iCommercialOperationType.createProductOrder(request, cipCode, customerIdRequest, flgFinanciamiento,
                productOfferingIdRequest, channelIdRequest, isRetail, flgCasi, sendIndicator,
                currentCommercialOperationType, sapidSimcard, getBonificacionSim, mainRequestProductOrder)
                .flatMap(saleCreateOrderRequest -> {
                    request.setSale(saleCreateOrderRequest);

                    try {
                        return validationsAndBuildings(saleCreateOrderRequest, request, currentCommercialOperationType,
                                isRetail, mainRequestProductOrder).flatMap(saleCreateOrderResponse -> {
                            request.setSale(saleCreateOrderResponse);

                            return iCommercialOperationType.reserveStock(request, saleCreateOrderResponse,
                                    flgCasi, flgFinanciamiento, sapidSimcard, currentCommercialOperationType)
                                    .flatMap(saleReserveStockResponse -> {
                                        request.setSale(saleReserveStockResponse);

                                        return iCommercialOperationType.createQuotation(request,
                                                saleReserveStockResponse, flgCasi, flgFinanciamiento,
                                                currentCommercialOperationType);
                                    });
                        });
                    } catch (ParseException e) {
                        return Mono.error(e);
                    }
                });
    }

    private boolean isNecessaryToSendVariablesInOrderCreation(CommercialOperationType commercialOperationType) {
        String reason = commercialOperationType.getReason();
        final boolean[] haveCaeqKeyInAdditionalData = {false};
        commercialOperationType.getAdditionalData().stream()
                .filter(kv -> kv.getKey().equalsIgnoreCase(Constants.CAEQ)
                        && kv.getValue().equalsIgnoreCase(Constants.STRING_TRUE))
                .findFirst().ifPresent(kv -> haveCaeqKeyInAdditionalData[0] = true);

        return reason.equalsIgnoreCase(Constants.CAEQ) || reason.equalsIgnoreCase(Constants.CAPL)
                || reason.equalsIgnoreCase(Constants.CASI) && haveCaeqKeyInAdditionalData[0];
    }

    private boolean setFinancingFlag(List<DeviceOffering> deviceOfferings) {
        return deviceOfferings != null && deviceOfferings.get(0).getOffers() != null
                && deviceOfferings.get(0).getOffers().get(0).getBillingOfferings() != null
                && deviceOfferings.get(0).getOffers().get(0).getBillingOfferings().get(0)
                .getCommitmentPeriods() != null
                && deviceOfferings.get(0).getOffers().get(0).getBillingOfferings().get(0)
                .getCommitmentPeriods().get(0).getFinancingInstalments() != null
                && !StringUtils.isEmpty(deviceOfferings.get(0).getOffers().get(0).getBillingOfferings()
                .get(0).getCommitmentPeriods().get(0).getFinancingInstalments().get(0)
                .getCodigo())
                && !deviceOfferings.get(0).getOffers().get(0).getBillingOfferings().get(0)
                .getCommitmentPeriods().get(0).getFinancingInstalments().get(0)
                .getCodigo().equals("TELEFCONT");
    }

    private String getCipCode(PaymentType paymentType, boolean sendIndicator, final boolean flgFinanciamiento) {
        if (!sendIndicator) {
            return null;
        } else if (!flgFinanciamiento) {
            return null;
        } else if (paymentType == null || paymentType.getAdditionalData() == null
                || paymentType.getAdditionalData().isEmpty()) {
            throw Commons.buildGenesisError(Constants.BAD_REQUEST_EXCEPTION_ID,
                    "Campos sale.paymentType o sale.paymentType.additional data está vació o nulo");
        }

        String paymentMediumLabelValue = Commons.getStringValueByKeyFromAdditionalDataList(
                paymentType.getAdditionalData(), "paymentMediumLabel");
        if (paymentMediumLabelValue.equalsIgnoreCase("Pago Efectivo")) {

            if (StringUtils.isEmpty(paymentType.getCid())) {
                throw Commons.buildGenesisError(Constants.BAD_REQUEST_EXCEPTION_ID,
                        "Falta campo sale.paymentType.cid");
            } else {
                return paymentType.getCid();
            }
        }
        return null;
    }

    private String getStringValueFromBusinessParameterDataListByKeyAndActiveTrue(
            List<BusinessParameterDataObjectExt> businessParameterDataList, String key) {

        final String[] stringValue = {""};

        if (businessParameterDataList != null && !businessParameterDataList.isEmpty()) {
            businessParameterDataList.forEach(kv -> {
                if (kv.getKey().equalsIgnoreCase(key) && kv.getActive()) {
                    stringValue[0] = kv.getValue();
                }
            });
        }

        return stringValue[0];
    }

    private boolean deviceOfferingIsNullOrEmpty(CommercialOperationType currentCommercialOperationType) {
        if (currentCommercialOperationType.getDeviceOffering() == null
                || currentCommercialOperationType.getDeviceOffering().isEmpty()) {
            return true;
        } else if (currentCommercialOperationType.getDeviceOffering().get(0).getStock() == null) {
            return true;
        } else return StringUtils.isEmpty(currentCommercialOperationType.getDeviceOffering().get(0)
                .getStock().getReservationId());
    }

    private String retrieveDomain(List<ContactMedium> prospectContact) {
        // Get domain from email
        String email = prospectContact.stream().filter(p -> p.getMediumType().equalsIgnoreCase(Constants.EMAIL))
                .map(p -> p.getCharacteristic().getEmailAddress()).collect(Collectors.joining());

        if (!email.isEmpty()) {
            int pos = email.indexOf('@');
            return email.substring(++pos);
        }
        return null;
    }

    private Mono<Sale> validationsAndBuildings(Sale saleRequest, PostSalesRequest request,
                                               CommercialOperationType currentCommercialOperationType, boolean isRetail,
                                               CreateProductOrderGeneralRequest mainRequestProductOrder)
            throws ParseException {

        if (currentCommercialOperationType.getDeviceOffering() != null
                && !currentCommercialOperationType.getDeviceOffering().isEmpty() && isRetail
                && saleRequest.getStatus().equalsIgnoreCase(Constants.NEGOCIACION)) {
            LOG.info("Sales flowSale Retail and Status NEGOCIACION, executing Create Order Validation");
            // FEMS-1514 Validación de creación Orden -> solo cuando es flujo retail, status
            // negociacion y la venta involucra un equipo, se debe hacer validación
            return salesMovistarTotalService.creationOrderValidation(saleRequest, mainRequestProductOrder,
                    currentCommercialOperationType, request.getHeadersMap(), productOrderWebClient, getSkuWebClient)
                    .flatMap(salesRepository::save);
        } else {
            LOG.info("Executing Create Order Service");
            return productOrderWebClient.createProductOrder(mainRequestProductOrder,
                    request.getHeadersMap(), saleRequest).flatMap(createOrderResponse -> {
                LOG.info("Create order response: "
                        .concat(new Gson().toJson(createOrderResponse)));
                currentCommercialOperationType.setOrder(createOrderResponse
                        .getCreateProductOrderResponse());

                if (salesMovistarTotalService.validateNegotiation(saleRequest.getAdditionalData(),
                        saleRequest.getIdentityValidations())) {
                    saleRequest.setStatus(Constants.NEGOCIACION);
                } else if (!StringUtils.isEmpty(createOrderResponse
                        .getCreateProductOrderResponse().getProductOrderId())) {
                    saleRequest.setStatus(Constants.SALES_STATUS_NUEVO);
                } else {
                    saleRequest.setStatus(Constants.PENDIENTE);
                }
                saleRequest.setAudioStatus(Constants.PENDIENTE);

                // Ship Delivery logic (tambo) - SERGIO
                if (currentCommercialOperationType.getWorkOrDeliveryType() != null
                        && !StringUtils.isEmpty(currentCommercialOperationType.getWorkOrDeliveryType()
                        .getMediumDelivery())
                        && currentCommercialOperationType.getWorkOrDeliveryType().getMediumDelivery()
                        .equalsIgnoreCase("Tienda")) {
                    saleRequest.setAdditionalData(salesMovistarTotalService.additionalDataAssigments(
                            saleRequest.getAdditionalData(), saleRequest, currentCommercialOperationType));
                }
                return Mono.just(request.getSale());
            });
        }
    }
}
