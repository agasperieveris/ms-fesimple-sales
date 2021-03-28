package com.tdp.ms.sales.business.v2.commercialOperation.wireline;

import com.tdp.ms.sales.business.v2.commercialOperation.factory.CommercialOperationTypeAbstract;
import com.tdp.ms.sales.business.v2.commercialOperation.factory.ICommercialOperationType;
import com.tdp.ms.sales.model.dto.CommercialOperationType;
import com.tdp.ms.sales.model.dto.productorder.CreateProductOrderGeneralRequest;
import com.tdp.ms.sales.model.entity.Sale;
import com.tdp.ms.sales.model.request.PostSalesRequest;
import com.tdp.ms.sales.model.response.BusinessParametersResponseObjectExt;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AltaFija extends CommercialOperationTypeAbstract implements ICommercialOperationType {
    private static final Logger LOG = LoggerFactory.getLogger(AltaFija.class);

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


    // ESTO ES PARA CUANDO SE TENGA QUE HACER UN REFACTOR DE LA FIJA!!
    /*@Override
    public Mono<Sale> createProductOrder(PostSalesRequest request, final String cipCode, String customerIdRequest,
                                         final boolean flgFinanciamiento, String productOfferingIdRequest,
                                         String channelIdRequest, final boolean isRetail, final boolean flgCasi,
                                         final boolean sendIndicator,
                                         CommercialOperationType currentCommercialOperationType, String sapidSimcard,
                                         BusinessParametersResponseObjectExt getBonificacionSim,
                                         CreateProductOrderGeneralRequest mainRequestProductOrder) {
        // Fija Commercial Operations
        LOG.info("Alta Fija Sales Case");

        return businessParameterWebClient.getParametersFinanciamientoFija(request.getHeadersMap())
                .map(BusinessParametersFinanciamientoFijaResponse::getData)
                .map(bpFinanciamientoFijaData -> bpFinanciamientoFijaData.get(0))
                .map(BusinessParameterFinanciamientoFijaData::getExt)
                .flatMap(parametersFinanciamientoFija -> processAltaFija(request.getSale(), request, isRetail,
                        cipCode, flgFinanciamiento, sendIndicator, currentCommercialOperationType)); // TODO: parametersFinanciamientoFija ya no es necesario, seria para request de quotation
    }

    private Mono<Sale> processAltaFija(Sale saleRequest, PostSalesRequest request,
                                       final boolean isRetail, final String cipCode,
                                       final boolean flgFinanciamiento, final boolean sendIndicator,
                                       CommercialOperationType currentCommercialOperationType) {

        // Identifying New Assigned Billing Offers SVAs
        List<NewAssignedBillingOffers> newAssignedBillingOffersCableTvList = new ArrayList<>();
        List<NewAssignedBillingOffers> newAssignedBillingOffersBroadbandList = new ArrayList<>();
        List<NewAssignedBillingOffers> newAssignedBillingOffersLandlineList = new ArrayList<>();

        List<OfferingType> productOfferings = currentCommercialOperationType.getProductOfferings();

        assignBillingOffers(productOfferings, newAssignedBillingOffersCableTvList,
                newAssignedBillingOffersBroadbandList, newAssignedBillingOffersLandlineList);

        // New Products Alta Fija
        List<NewProductAltaFija> newProductsAltaFijaList = this.buildNewProductsAltaFijaList(saleRequest,
                newAssignedBillingOffersLandlineList, newAssignedBillingOffersBroadbandList,
                newAssignedBillingOffersCableTvList, currentCommercialOperationType);

        // Building ServiceAvailability
        ServiceabilityInfoType serviceabilityInfo = salesMovistarTotalService.buildServiceabilityInfoType(request,
                currentCommercialOperationType);
        LOG.info("serviceabilityInfo: " + new Gson().toJson(serviceabilityInfo));

        // Order Attributes Alta Fija
        List<FlexAttrType> altaFijaOrderAttributesList = new ArrayList<>();
        this.buildOrderAttributesListAltaFija(altaFijaOrderAttributesList, saleRequest, flgFinanciamiento,
                currentCommercialOperationType);

        AltaFijaRequest altaFijaRequest = new AltaFijaRequest();
        altaFijaRequest.setNewProducts(newProductsAltaFijaList);
        altaFijaRequest.setAppointmentId(currentCommercialOperationType.getWorkOrDeliveryType() != null
                && currentCommercialOperationType.getWorkOrDeliveryType().getWorkOrder() != null
                && currentCommercialOperationType.getWorkOrDeliveryType().getWorkOrder().getWorkforceTeams() != null
                ? currentCommercialOperationType.getWorkOrDeliveryType().getWorkOrder().getWorkforceTeams().get(0)
                .getId() : null);
        altaFijaRequest.setAppointmentNumber(saleRequest.getSalesId());
        altaFijaRequest.setServiceabilityInfo(serviceabilityInfo);
        altaFijaRequest.setSourceApp(saleRequest.getSalesId());
        altaFijaRequest.setOrderAttributes(altaFijaOrderAttributesList);

        com.tdp.ms.sales.model.dto.productorder.Customer altaFijaCustomer = com.tdp.ms.sales.model.dto.productorder.Customer
                .builder().customerId(saleRequest.getRelatedParty().get(0).getCustomerId()).build();

        String productOfferingId = currentCommercialOperationType.getProductOfferings() != null
                && !currentCommercialOperationType.getProductOfferings().isEmpty()
                && !StringUtils.isEmpty(currentCommercialOperationType.getProductOfferings().get(0).getId())
                ? currentCommercialOperationType.getProductOfferings().get(0).getId() : "";
        altaFijaRequest.setCip(cipCode);

        altaFijaRequest.setUpfrontIndicator(sendIndicator ? salesMovistarTotalService
                .setUpFrontIndicator(saleRequest.getProductType()) : null);

        // Alta Fija Customize Request
        ProductOrderAltaFijaRequest productOrderAltaFijaRequest = ProductOrderAltaFijaRequest.builder()
                .salesChannel(saleRequest.getChannel().getId()).request(altaFijaRequest)
                .customer(altaFijaCustomer).productOfferingId(productOfferingId)
                .onlyValidationIndicator(Constants.STRING_FALSE).actionType("PR").build();

        // Building Main Request to send to Create Product Order Service
        CreateProductOrderGeneralRequest mainRequestProductOrder = new CreateProductOrderGeneralRequest();
        mainRequestProductOrder.setCreateProductOrderRequest(productOrderAltaFijaRequest);

        // CreateOrderFija
        LOG.info("Create Order Request: ".concat(new Gson().toJson(mainRequestProductOrder)));

        if (currentCommercialOperationType.getDeviceOffering() != null
                && !currentCommercialOperationType.getDeviceOffering().isEmpty()
                && isRetail && saleRequest.getStatus().equalsIgnoreCase("NEGOCIACION")) {
            // FEMS-1514 Validación de creación Orden -> solo cuando es flujo retail, status
            // negociacion
            // y la venta involucra un equipo, se debe hacer validación
            return creationOrderValidation(saleRequest, mainRequestProductOrder, request.getHeadersMap())
                    .flatMap(salesRepository::save);
        } else {
            LOG.info("Executing Create Order Service");
            // Call de Create Alta Fija Order
            return productOrderWebClient
                    .createProductOrder(mainRequestProductOrder, request.getHeadersMap(),
                            saleRequest)
                    .flatMap(createOrderResponse -> addOrderIntoSale(PostSalesRequest.builder().sale(saleRequest)
                                    .headersMap(request.getHeadersMap()).build(),
                            saleRequest, createOrderResponse, currentCommercialOperationType));
        }
    }

    private void assignBillingOffers(List<OfferingType> productOfferings,
                                     List<NewAssignedBillingOffers> newAssignedBillingOffersCableTvList,
                                     List<NewAssignedBillingOffers> newAssignedBillingOffersBroadbandList,
                                     List<NewAssignedBillingOffers> newAssignedBillingOffersLandlineList) {
        for (int i = 1; i < productOfferings.size(); i++) {
            String productTypeSva = productOfferings.get(i).getProductSpecification().get(0)
                    .getProductType();
            String productTypeComponent = Commons.getStringValueByKeyFromAdditionalDataList(
                    productOfferings.get(i).getAdditionalData(), Constants.PRODUCT_TYPE);

            if (productTypeSva.equalsIgnoreCase(Constants.PRODUCT_TYPE_SVA)) {
                boolean isCableTvOrChannelTv = productTypeComponent
                        .equalsIgnoreCase(Constants.PRODUCT_TYPE_CABLE_TV)
                        || productTypeComponent
                        .equalsIgnoreCase(Constants.PRODUCT_TYPE_CHANNEL_TV);
                boolean isBroadbandOrLandline = productTypeComponent
                        .equalsIgnoreCase(Constants.PRODUCT_TYPE_BROADBAND)
                        || productTypeComponent
                        .equalsIgnoreCase(Constants.PRODUCT_TYPE_LANDLINE);

                if (isCableTvOrChannelTv || isBroadbandOrLandline) {

                    NewAssignedBillingOffers newAssignedBillingOffers = NewAssignedBillingOffers
                            .builder().productSpecPricingId(productOfferings.get(i).getId())
                            .parentProductCatalogId(productOfferings.get(i)
                                    .getProductSpecification().get(0)
                                    .getProductPrice().get(0)
                                    .getProductSpecContainmentId())
                            .build();

                    if (isCableTvOrChannelTv) {
                        newAssignedBillingOffersCableTvList.add(newAssignedBillingOffers);
                    } else if (productTypeComponent
                            .equalsIgnoreCase(Constants.PRODUCT_TYPE_BROADBAND)) {
                        newAssignedBillingOffersBroadbandList.add(newAssignedBillingOffers);
                    } else if (productTypeComponent
                            .equalsIgnoreCase(Constants.PRODUCT_TYPE_LANDLINE)) {
                        newAssignedBillingOffersLandlineList.add(newAssignedBillingOffers);
                    }
                }
            }
        }
    }

    private List<NewProductAltaFija> buildNewProductsAltaFijaList(Sale saleRequest,
                                                  List<NewAssignedBillingOffers> newAssignedBillingOffersLandlineList,
                                                  List<NewAssignedBillingOffers> newAssignedBillingOffersBroadbandList,
                                                  List<NewAssignedBillingOffers> newAssignedBillingOffersCableTvList,
                                                  CommercialOperationType currentCommercialOperationType) {

        LOG.info("SVAs Landline: ".concat(new Gson().toJson(newAssignedBillingOffersLandlineList)));
        LOG.info("SVAs Broadband: ".concat(new Gson().toJson(newAssignedBillingOffersBroadbandList)));
        LOG.info("SVAs CableTv: ".concat(new Gson().toJson(newAssignedBillingOffersCableTvList)));

        List<NewProductAltaFija> newProductsAltaFijaList = new ArrayList<>();
        final Integer[] cont = { 1 };

        String baId = saleRequest.getRelatedParty().get(0).getBillingArragmentId();
        String accountId = saleRequest.getRelatedParty().get(0).getAccountId();

        if (currentCommercialOperationType.getProductOfferings() != null
                && !currentCommercialOperationType.getProductOfferings().isEmpty()
                && currentCommercialOperationType.getProductOfferings().get(0).getProductSpecification() != null
                && !currentCommercialOperationType.getProductOfferings().get(0).getProductSpecification().isEmpty()) {

            currentCommercialOperationType.getProductOfferings().get(0).getProductSpecification()
                    .forEach(productSpecification -> {

                String productType = productSpecification.getProductType();
                if (productType.equalsIgnoreCase(Constants.PRODUCT_TYPE_LANDLINE)) {
                    NewProductAltaFija newProductAltaFijaLandline = NewProductAltaFija
                            .builder()
                            .productCatalogId(productSpecification
                                    .getRefinedProduct()
                                    .getProductCharacteristics()
                                    .get(0).getId())
                            .temporaryId("temp".concat(cont[0].toString()))
                            .baId(baId).accountId(accountId)
                            .invoiceCompany("TDP").build();
                    cont[0]++;

                    // Adding Landline SVAs
                    if (!newAssignedBillingOffersLandlineList.isEmpty()) {

                        ProductChangeAltaFija productChangesLandline = ProductChangeAltaFija
                                .builder()
                                .newAssignedBillingOffers(
                                        newAssignedBillingOffersLandlineList)
                                .build();
                        newProductAltaFijaLandline.setProductChanges(
                                productChangesLandline);
                    }

                    newProductsAltaFijaList.add(newProductAltaFijaLandline);

                } else if (productType.equalsIgnoreCase("broadband")) {
                    NewProductAltaFija newProductAltaFijaBroadband = NewProductAltaFija
                            .builder()
                            .productCatalogId(productSpecification
                                    .getRefinedProduct()
                                    .getProductCharacteristics()
                                    .get(0).getId())
                            .temporaryId("temp".concat(cont[0].toString()))
                            .baId(baId).accountId(accountId)
                            .invoiceCompany("TDP").build();
                    cont[0]++;

                    List<ChangedCharacteristic> changedCharacteristicsBroadbandList = new ArrayList<>();

                    ChangedCharacteristic changedCharacteristicBroadband1 = ChangedCharacteristic
                            .builder().characteristicId("3241482")
                            .characteristicValue(productSpecification
                                    .getProductPrice().get(2)
                                    .getAdditionalData().stream()
                                    .filter(item -> item.getKey()
                                            .equalsIgnoreCase(
                                                    "downloadSpeed"))
                                    .findFirst()
                                    .orElse(KeyValueType.builder()
                                            .value(null)
                                            .build())
                                    .getValue())
                            .build();
                    changedCharacteristicsBroadbandList
                            .add(changedCharacteristicBroadband1);

                    try {
                        ChangedCharacteristic changedCharacteristicBroadband2 = ChangedCharacteristic
                                .builder().characteristicId("3241532")
                                .characteristicValue(Commons
                                        .getTimeNowInMillis())
                                .build();
                        changedCharacteristicsBroadbandList
                                .add(changedCharacteristicBroadband2);
                    } catch (ParseException e) {
                        LOG.error("Post Sales Exception Getting Time at Now in Milliseconds");
                    }

                    ChangedContainedProduct changedContainedProductBroadband1 = ChangedContainedProduct
                            .builder()
                            .temporaryId("temp".concat(cont[0].toString()))
                            .productCatalogId("3241312")
                            .changedCharacteristics(
                                    changedCharacteristicsBroadbandList)
                            .build();
                    cont[0]++;

                    List<ChangedContainedProduct> changedContainedProductsBroadbandList = new ArrayList<>();
                    changedContainedProductsBroadbandList
                            .add(changedContainedProductBroadband1);

                    ProductChangeAltaFija productChangesBroadband = ProductChangeAltaFija
                            .builder()
                            .changedContainedProducts(
                                    changedContainedProductsBroadbandList)
                            .build();
                    // Adding Broadband SVAs
                    if (!newAssignedBillingOffersBroadbandList.isEmpty()) {
                        productChangesBroadband.setNewAssignedBillingOffers(
                                newAssignedBillingOffersBroadbandList);
                    }

                    newProductAltaFijaBroadband
                            .setProductChanges(productChangesBroadband);

                    newProductsAltaFijaList.add(newProductAltaFijaBroadband);

                } else if (productType
                        .equalsIgnoreCase(Constants.PRODUCT_TYPE_CABLE_TV)) {
                    NewProductAltaFija newProductAltaFijaCableTv = NewProductAltaFija
                            .builder()
                            .productCatalogId(productSpecification
                                    .getRefinedProduct()
                                    .getProductCharacteristics()
                                    .get(0).getId())
                            .temporaryId("temp".concat(cont[0].toString()))
                            .baId(baId).accountId(accountId)
                            .invoiceCompany("TDP").build();
                    cont[0]++;

                    // Adding CableTv SVAs
                    if (!newAssignedBillingOffersCableTvList.isEmpty()) {

                        ProductChangeAltaFija productChangesCableTv = ProductChangeAltaFija
                                .builder()
                                .newAssignedBillingOffers(
                                        newAssignedBillingOffersCableTvList)
                                .build();
                        newProductAltaFijaCableTv.setProductChanges(
                                productChangesCableTv);
                    }

                    newProductsAltaFijaList.add(newProductAltaFijaCableTv);

                } else if (productType
                        .equalsIgnoreCase(Constants.PRODUCT_TYPE_DEVICE)) {
                    NewProductAltaFija newProductAltaFijaShareEquipment = NewProductAltaFija
                            .builder()
                            .productCatalogId(productSpecification
                                    .getRefinedProduct()
                                    .getProductCharacteristics()
                                    .get(0).getId())
                            .temporaryId("temp".concat(cont[0].toString()))
                            .baId(baId).accountId(accountId)
                            .invoiceCompany("TDP").build();
                    newProductsAltaFijaList.add(newProductAltaFijaShareEquipment);
                    cont[0]++;

                } else if (productType
                        .equalsIgnoreCase(Constants.PRODUCT_TYPE_ACCESSORIES)) {
                    NewProductAltaFija newProductAltaFijaAccesories = NewProductAltaFija
                            .builder()
                            .productCatalogId(productSpecification
                                    .getRefinedProduct()
                                    .getProductCharacteristics()
                                    .get(0).getId())
                            .temporaryId("temp".concat(cont[0].toString()))
                            .baId(baId).accountId(accountId)
                            .invoiceCompany("TDP").build();
                    cont[0]++;

                    ChangedCharacteristic changedCharacteristicAccesories1 = ChangedCharacteristic
                            .builder().characteristicId("15734")
                            .characteristicValue("34203411").build();

                    List<ChangedCharacteristic> changedCharacteristicsAccesoriesList = new ArrayList<>();
                    changedCharacteristicsAccesoriesList
                            .add(changedCharacteristicAccesories1);

                    ChangedContainedProduct changedContainedProductAccesories1 = ChangedContainedProduct
                            .builder()
                            .temporaryId("temp".concat(cont[0].toString()))
                            .productCatalogId("34134811")
                            .changedCharacteristics(
                                    changedCharacteristicsAccesoriesList)
                            .build();
                    cont[0]++;

                    List<ChangedContainedProduct> changedContainedProductsAccesoriesList = new ArrayList<>();
                    changedContainedProductsAccesoriesList
                            .add(changedContainedProductAccesories1);

                    ProductChangeAltaFija productChangesAccesories = ProductChangeAltaFija
                            .builder()
                            .changedContainedProducts(
                                    changedContainedProductsAccesoriesList)
                            .build();

                    newProductAltaFijaAccesories
                            .setProductChanges(productChangesAccesories);
                    newProductsAltaFijaList.add(newProductAltaFijaAccesories);
                }
            });
        }

        return newProductsAltaFijaList;
    }

    private String getStringValueFromBpExtListByParameterName(String parameterName,
                                                              List<BusinessParameterFinanciamientoFijaExt> ext) {
        final String[] stringValue = { "" };

        if (!ext.isEmpty()) {
            ext.forEach(bpExt -> {
                if (bpExt.getNomParameter().equals(parameterName)) {
                    stringValue[0] = bpExt.getCodParameterValue();
                }
            });
        }

        return stringValue[0];
    }

    private void buildOrderAttributesListAltaFija(List<FlexAttrType> altaFijaOrderAttributesList, Sale saleRequest,
                                                  final boolean flgFinanciamiento,
                                                  CommercialOperationType currentCommercialOperationType) {

        FlexAttrValueType externalFinancialAttrValue = FlexAttrValueType.builder()
                .stringValue(Boolean.TRUE.equals(flgFinanciamiento) ? "Y" : "N")
                .valueType(Constants.STRING).build();
        FlexAttrType externalFinancialAttr = FlexAttrType.builder().attrName("IS_EXTERNAL_FINANCING")
                .flexAttrValue(externalFinancialAttrValue).build();

        String upFrontIndAttrStringValue = currentCommercialOperationType.getProductOfferings() != null
                && !StringUtils.isEmpty(currentCommercialOperationType.getProductOfferings().get(0).getUpFront()
                .getIndicator())
                ? currentCommercialOperationType.getProductOfferings().get(0).getUpFront().getIndicator() : "N";

        FlexAttrValueType upFrontIndAttrValue = FlexAttrValueType.builder()
                .stringValue(upFrontIndAttrStringValue).valueType(Constants.STRING).build();
        FlexAttrType upFrontIndAttr = FlexAttrType.builder().attrName("UPFRONT_IND")
                .flexAttrValue(upFrontIndAttrValue).build();

        FlexAttrValueType paymentMethodAttrValue = FlexAttrValueType.builder().stringValue("EX")
                .valueType(Constants.STRING).build();
        FlexAttrType paymentMethodAttr = FlexAttrType.builder().attrName("PAYMENT_METHOD")
                .flexAttrValue(paymentMethodAttrValue).build();

        altaFijaOrderAttributesList.add(externalFinancialAttr);
        altaFijaOrderAttributesList.add(upFrontIndAttr);
        altaFijaOrderAttributesList.add(paymentMethodAttr);

        // Order Attributes if is Financing
        if (Boolean.TRUE.equals(flgFinanciamiento)) {

            FinancingInstalment financingInstalment = currentCommercialOperationType.getDeviceOffering().get(0)
                    .getOffers().get(0).getBillingOfferings().get(0).getCommitmentPeriods().get(0)
                    .getFinancingInstalments().get(0);

            Number downPaymentAmount = financingInstalment.getInstalments().getOpeningQuota().getValue()
                    .doubleValue();
            FlexAttrValueType downPaymentAttrValue = FlexAttrValueType.builder()
                    .stringValue(downPaymentAmount.toString())
                    .valueType(Constants.STRING).build();
            FlexAttrType downPaymentAttr = FlexAttrType.builder().attrName("DOWN_PAYMENT_AMOUNT")
                    .flexAttrValue(downPaymentAttrValue).build();
            altaFijaOrderAttributesList.add(downPaymentAttr);

            Number financingAmount = financingInstalment.getInstalments().getTotalAmount().getValue().doubleValue()
                    - financingInstalment.getInstalments().getOpeningQuota().getValue().doubleValue();
            FlexAttrValueType financingAmountAttrValue = FlexAttrValueType.builder()
                    .stringValue(financingAmount.toString())
                    .valueType(Constants.STRING).build();
            FlexAttrType financingAmountAttr = FlexAttrType.builder().attrName("FINANCING_AMOUNT")
                    .flexAttrValue(financingAmountAttrValue).build();
            altaFijaOrderAttributesList.add(financingAmountAttr);

            FlexAttrValueType financingPlanAttrValue = FlexAttrValueType.builder()
                    .stringValue(financingInstalment.getCodigo())
                    .valueType(Constants.STRING).build();
            FlexAttrType financingPlanAttr = FlexAttrType.builder().attrName("FINANCING_PLAN")
                    .flexAttrValue(financingPlanAttrValue).build();
            altaFijaOrderAttributesList.add(financingPlanAttr);
        }

        // Order Attributes if is Scheduling
        if (flgFinanciamiento && saleRequest.getCommercialOperation() != null
                && currentCommercialOperationType.getWorkOrDeliveryType() != null
                && currentCommercialOperationType.getWorkOrDeliveryType()
                .getScheduleDelivery() != null
                && !saleRequest.getCommercialOperation().get(0).getWorkOrDeliveryType()
                .getScheduleDelivery().equalsIgnoreCase("SLA")) {

            FlexAttrValueType schedulingAttrValue = FlexAttrValueType.builder().stringValue("TC")
                    .valueType("STRING").build();
            FlexAttrType downPaymentAttr = FlexAttrType.builder().attrName(Constants.DELIVERY_METHOD)
                    .flexAttrValue(schedulingAttrValue).build();
            altaFijaOrderAttributesList.add(downPaymentAttr);
        }
    }

    private Mono<Sale> creationOrderValidation(Sale saleRequest,
                                               CreateProductOrderGeneralRequest productOrderRequest, HashMap<String, String> headersMap) {
        KeyValueType keyValueType = saleRequest.getAdditionalData().stream()
                .filter(item -> item.getKey().equalsIgnoreCase(Constants.FLOWSALE)).findFirst()
                .orElse(null);

        String operationType = saleRequest.getCommercialOperation().get(0).getReason().equals("ALTA")
                ? "Provide"
                : "Change";

        if (keyValueType != null && keyValueType.getValue().equalsIgnoreCase(Constants.RETAIL)
                && saleRequest.getStatus().equalsIgnoreCase(Constants.NEGOCIACION)) {

            DeviceOffering saleDeviceOfferingSim = saleRequest.getCommercialOperation().get(0)
                    .getDeviceOffering().stream()
                    .filter(deviceOffering -> deviceOffering.getDeviceType()
                            .equalsIgnoreCase(Constants.DEVICE_TYPE_SIM))
                    .findFirst().orElse(null);

            DeviceOffering saleDeviceOfferingSmartphone = saleRequest.getCommercialOperation().get(0)
                    .getDeviceOffering().stream()
                    .filter(deviceOffering -> deviceOffering.getDeviceType()
                            .equalsIgnoreCase(Constants.DEVICE_TYPE_SMARTPHONE))
                    .findFirst().orElse(null);

            if (saleDeviceOfferingSim == null && saleDeviceOfferingSmartphone == null) {
                throw Commons.buildGenesisError(Constants.BAD_REQUEST_EXCEPTION_ID,
                        "DeviceOffering property must contain at least 1 element with device type "
                                + "SIM or Smarthpone.");
            } else if (saleDeviceOfferingSim != null) {
                if (StringUtils.isEmpty(saleDeviceOfferingSim.getId())) {
                    throw Commons.buildGenesisError(Constants.BAD_REQUEST_EXCEPTION_ID,
                            "DeviceOffering id property value from Device Type Sim is mandatory.");
                } else if (StringUtils.isEmpty(saleDeviceOfferingSim.getCostoPromedioSinIgvSoles())) {
                    throw Commons.buildGenesisError(Constants.BAD_REQUEST_EXCEPTION_ID,
                            "DeviceOffering costoPromedioSinIGVSoles property value from "
                                    + "Device Type Sim is mandatory.");
                }
            } else {
                if (StringUtils.isEmpty(saleDeviceOfferingSmartphone.getId())) {
                    throw Commons.buildGenesisError(Constants.BAD_REQUEST_EXCEPTION_ID,
                            "DeviceOffering id property value from Device Type Smartphone is mandatory.");
                } else if (StringUtils
                        .isEmpty(saleDeviceOfferingSmartphone.getCostoPromedioSinIgvSoles())) {
                    throw Commons.buildGenesisError(Constants.BAD_REQUEST_EXCEPTION_ID,
                            "DeviceOffering costoPromedioSinIGVSoles property value from "
                                    + "Device Type Smartphone is mandatory.");
                }
            }

            String sapidDeviceOfferingSim = saleDeviceOfferingSim == null
                    || StringUtils.isEmpty(saleDeviceOfferingSim.getId()) ? ""
                    : saleDeviceOfferingSim.getId();
            String costoDeviceOfferingSim = saleDeviceOfferingSim == null
                    || StringUtils.isEmpty(saleDeviceOfferingSim.getCostoPromedioSinIgvSoles())
                    ? "0.00"
                    : saleDeviceOfferingSim.getCostoPromedioSinIgvSoles();

            String sapidDeviceOfferingSmartphone = saleDeviceOfferingSmartphone == null
                    || StringUtils.isEmpty(saleDeviceOfferingSmartphone.getId()) ? ""
                    : saleDeviceOfferingSmartphone.getId();
            String costoDeviceOfferingSmartphone = saleDeviceOfferingSmartphone == null || StringUtils
                    .isEmpty(saleDeviceOfferingSmartphone.getCostoPromedioSinIgvSoles()) ? "0.00"
                    : saleDeviceOfferingSmartphone.getCostoPromedioSinIgvSoles();

            Mono<List<GetSkuResponse>> getSku = getSkuWebClient.createSku(saleRequest.getChannel().getId(),
                    "default", sapidDeviceOfferingSim, Double.parseDouble(costoDeviceOfferingSim),
                    operationType, "", saleRequest.getChannel().getStoreId(), "2",
                    saleRequest.getChannel().getDealerId(), sapidDeviceOfferingSmartphone,
                    costoDeviceOfferingSmartphone, headersMap).collectList();

            // set onlyValidatonIndicator == true
            String classObjectName = productOrderRequest.getCreateProductOrderRequest().getClass()
                    .getName();
            int index = classObjectName.lastIndexOf(".");
            classObjectName = classObjectName.substring(index + 1);
            if (classObjectName.equalsIgnoreCase("ProductOrderCaplRequest")) {
                ProductOrderCaplRequest productOrderCaplRequest = (ProductOrderCaplRequest) productOrderRequest
                        .getCreateProductOrderRequest();
                productOrderCaplRequest.setOnlyValidationIndicator("true");
                productOrderRequest.setCreateProductOrderRequest(productOrderCaplRequest);
            } else if (classObjectName.equalsIgnoreCase("ProductOrderCaeqRequest")) {
                ProductOrderCaeqRequest productOrderCaeqRequest = (ProductOrderCaeqRequest) productOrderRequest
                        .getCreateProductOrderRequest();
                productOrderCaeqRequest.setOnlyValidationIndicator("true");
                productOrderRequest.setCreateProductOrderRequest(productOrderCaeqRequest);
            } else if (classObjectName.equalsIgnoreCase("ProductOrderCaeqCaplRequest")) {
                ProductOrderCaeqCaplRequest productOrderCaeqCaplRequest = (ProductOrderCaeqCaplRequest) productOrderRequest
                        .getCreateProductOrderRequest();
                productOrderCaeqCaplRequest.setOnlyValidationIndicator("true");
                productOrderRequest.setCreateProductOrderRequest(productOrderCaeqCaplRequest);
            } else if (classObjectName.equalsIgnoreCase("ProductOrderAltaFijaRequest")) {
                ProductOrderAltaFijaRequest productOrderAltaFijaRequest = (ProductOrderAltaFijaRequest) productOrderRequest
                        .getCreateProductOrderRequest();
                productOrderAltaFijaRequest.setOnlyValidationIndicator("true");
                productOrderRequest.setCreateProductOrderRequest(productOrderAltaFijaRequest);
            } else if (classObjectName.equalsIgnoreCase("ProductOrderAltaMobileRequest")) {
                ProductOrderAltaMobileRequest productOrderAltaMobileRequest = (ProductOrderAltaMobileRequest) productOrderRequest
                        .getCreateProductOrderRequest();
                productOrderAltaMobileRequest.setOnlyValidationIndicator("true");
                productOrderRequest.setCreateProductOrderRequest(productOrderAltaMobileRequest);
            }

            Mono<ProductorderResponse> productOrderResponse =
                    productOrderWebClient.createProductOrder(productOrderRequest, headersMap, saleRequest);

            // Creación del sku
            return Mono.zip(getSku, productOrderResponse).map(tuple -> {
                // añadir respuesta a sale.additionalData y hacer validación de la orden

                GetSkuResponse deviceSku = tuple.getT1().stream()
                        .filter(item -> item.getDeviceType().equalsIgnoreCase("mobile_phone"))
                        .findFirst().orElse(null);

                GetSkuResponse simSku = tuple.getT1().stream()
                        .filter(item -> item.getDeviceType().equalsIgnoreCase("sim"))
                        .findFirst().orElse(null);

                if (deviceSku != null) {
                    saleRequest.getAdditionalData().add(KeyValueType.builder()
                            .key(Constants.DEVICE_SKU).value(deviceSku.getSku()).build());
                }
                if (simSku != null) {
                    saleRequest.getAdditionalData().add(KeyValueType.builder()
                            .key(Constants.SIM_SKU).value(simSku.getSku()).build());
                }

                // cambiar status a "VALIDADO"
                saleRequest.setStatus(Constants.STATUS_VALIDADO);
                return saleRequest;
            });
        } else {
            return Mono.just(saleRequest);
        }
    }

    private Mono<Sale> addOrderIntoSale(PostSalesRequest request, Sale saleRequest,
                                        ProductorderResponse createOrderResponse,
                                        CommercialOperationType currentCommercialOperationType) {

        LOG.info("Create Order Response: " + new Gson().toJson(createOrderResponse));
        // Adding Order info to sales
        currentCommercialOperationType.setOrder(createOrderResponse.getCreateProductOrderResponse());

        if (salesMovistarTotalService.validateNegotiation(saleRequest.getAdditionalData(),
                saleRequest.getIdentityValidations())) {
            saleRequest.setStatus(Constants.NEGOCIACION);
        } else if (!StringUtils
                .isEmpty(createOrderResponse.getCreateProductOrderResponse().getProductOrderId())) {
            // When All is OK
            saleRequest.setStatus(Constants.SALES_STATUS_NUEVO);
        } else {
            // When Create Product Order Service fail or doesnt respond with an Order Id
            saleRequest.setStatus(Constants.PENDIENTE);
        }
        saleRequest.setAudioStatus(Constants.PENDIENTE);

        return salesRepository.save(saleRequest).map(r -> {
            salesMovistarTotalService.postSalesEventFlow(request);
            return r;
        });
    }*/
}
