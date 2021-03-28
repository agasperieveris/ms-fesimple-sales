package com.tdp.ms.sales.business.v2.services.impl;

import com.tdp.ms.commons.util.DateUtils;
import com.tdp.ms.sales.business.v2.parrillaMt.factory.IParrillaMt;
import com.tdp.ms.sales.business.v2.parrillaMt.factory.ParrillaMtFactory;
import com.tdp.ms.sales.business.v2.services.SalesMovistarTotalService;
import com.tdp.ms.sales.client.GetSkuWebClient;
import com.tdp.ms.sales.client.ProductOrderWebClient;
import com.tdp.ms.sales.client.WebClientReceptor;
import com.tdp.ms.sales.model.dto.CommercialOperationType;
import com.tdp.ms.sales.model.dto.CreateProductOrderResponseType;
import com.tdp.ms.sales.model.dto.DeviceOffering;
import com.tdp.ms.sales.model.dto.FinancingInstalment;
import com.tdp.ms.sales.model.dto.IdentityValidationType;
import com.tdp.ms.sales.model.dto.KeyValueType;
import com.tdp.ms.sales.model.dto.OfferingType;
import com.tdp.ms.sales.model.dto.ProductRefInfoType;
import com.tdp.ms.sales.model.dto.RelatedProductType;
import com.tdp.ms.sales.model.dto.ShipmentDetailsType;
import com.tdp.ms.sales.model.dto.SiteRefType;
import com.tdp.ms.sales.model.dto.StockType;
import com.tdp.ms.sales.model.dto.productorder.CreateProductOrderGeneralRequest;
import com.tdp.ms.sales.model.dto.productorder.FlexAttrType;
import com.tdp.ms.sales.model.dto.productorder.FlexAttrValueType;
import com.tdp.ms.sales.model.dto.productorder.altafija.CharacteristicOfferType;
import com.tdp.ms.sales.model.dto.productorder.altafija.CommercialZoneType;
import com.tdp.ms.sales.model.dto.productorder.altafija.ProductLineType;
import com.tdp.ms.sales.model.dto.productorder.altafija.ProductOrderAltaFijaRequest;
import com.tdp.ms.sales.model.dto.productorder.altafija.ServiceabilityInfoType;
import com.tdp.ms.sales.model.dto.productorder.altafija.ServiceabilityOfferType;
import com.tdp.ms.sales.model.dto.productorder.altamobile.ProductOrderAltaMobileRequest;
import com.tdp.ms.sales.model.dto.productorder.caeq.ChangedCharacteristic;
import com.tdp.ms.sales.model.dto.productorder.caeq.ChangedContainedProduct;
import com.tdp.ms.sales.model.dto.productorder.caeq.ProductOrderCaeqRequest;
import com.tdp.ms.sales.model.dto.productorder.caeqcapl.ProductOrderCaeqCaplRequest;
import com.tdp.ms.sales.model.dto.productorder.capl.NewAssignedBillingOffers;
import com.tdp.ms.sales.model.dto.productorder.capl.ProductOrderCaplRequest;
import com.tdp.ms.sales.model.dto.quotation.Channel;
import com.tdp.ms.sales.model.dto.quotation.CreateQuotationRequestBody;
import com.tdp.ms.sales.model.dto.quotation.LegalId;
import com.tdp.ms.sales.model.dto.quotation.MoneyAmount;
import com.tdp.ms.sales.model.dto.quotation.Site;
import com.tdp.ms.sales.model.dto.reservestock.Destination;
import com.tdp.ms.sales.model.dto.reservestock.Item;
import com.tdp.ms.sales.model.dto.reservestock.Order;
import com.tdp.ms.sales.model.dto.reservestock.StockItem;
import com.tdp.ms.sales.model.entity.Sale;
import com.tdp.ms.sales.model.request.CreateQuotationRequest;
import com.tdp.ms.sales.model.request.PostSalesRequest;
import com.tdp.ms.sales.model.request.ReceptorRequest;
import com.tdp.ms.sales.model.request.ReserveStockRequest;
import com.tdp.ms.sales.model.response.BusinessParametersResponseObjectExt;
import com.tdp.ms.sales.model.response.CreateQuotationResponse;
import com.tdp.ms.sales.model.response.GetSkuResponse;
import com.tdp.ms.sales.model.response.ProductorderResponse;
import com.tdp.ms.sales.model.response.ReserveStockResponse;
import com.tdp.ms.sales.utils.Commons;
import com.tdp.ms.sales.utils.Constants;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import static java.lang.Math.round;

@Service
@RequiredArgsConstructor
public class SalesMovistarTotalServiceImpl extends SalesMovistarTotalAbstract implements SalesMovistarTotalService {
    private static final Logger LOG = LoggerFactory.getLogger(SalesMovistarTotalServiceImpl.class);

    @Autowired
    private ParrillaMtFactory parrillaMtFactory;

    @Autowired
    private WebClientReceptor webClientReceptor;

    @Override
    public Mono<Sale> postSaleMovistarTotal(PostSalesRequest request) {

        boolean isStatusValidado = request.getSale().getStatus().equalsIgnoreCase(Constants.STATUS_VALIDADO);

        String flowSaleValue = request.getSale().getAdditionalData().stream()
                .filter(keyValueType -> keyValueType.getKey().equalsIgnoreCase(Constants.FLOWSALE))
                .findFirst().orElse(KeyValueType.builder().value(null).build()).getValue();
        boolean isRetail = flowSaleValue.equalsIgnoreCase(Constants.RETAIL);

        IParrillaMt iParrillaMt = parrillaMtFactory.getParrillaMT(request);
        return iParrillaMt.processParrillaMT(request, isStatusValidado, isRetail);
    }

    @Override
    public ServiceabilityInfoType buildServiceabilityInfoType(PostSalesRequest request,
                                                              CommercialOperationType currentCommercialOperationType) {
        List<ServiceabilityOfferType> serviceabilityOffersList = new ArrayList<>();
        this.buildServiceAvailabilityAltaFija(currentCommercialOperationType, serviceabilityOffersList);

        // CommercialZoneType
        CommercialZoneType commercialZone = CommercialZoneType.builder()
                .commercialZoneId(currentCommercialOperationType.getServiceAvailability().getCommercialAreaId())
                .commercialZoneName(currentCommercialOperationType.getServiceAvailability()
                        .getCommercialAreaDescription())
                .build();

        return ServiceabilityInfoType.builder()
                .serviceabilityId(currentCommercialOperationType.getServiceAvailability().getId())
                .offers(serviceabilityOffersList.isEmpty() ? null : serviceabilityOffersList)
                .commercialZone(commercialZone).build();
    }

    private void buildServiceAvailabilityAltaFija(CommercialOperationType currentCommercialOperationType,
                                                  List<ServiceabilityOfferType> serviceabilityOffersList) {

        OfferingType mainOffering = currentCommercialOperationType.getProductOfferings().stream()
                .filter(item -> item.getType() != null
                        && !item.getType().equalsIgnoreCase(Constants.PRODUCT_TYPE_SVA))
                .findFirst().orElse(null);

        final boolean[] flgProductLandline = { false };
        final boolean[] flgProductBroadband = { false };
        final boolean[] flgProductCabletv = { false };
        if (mainOffering != null) {
            mainOffering.getProductSpecification().forEach(item -> {
                if (item.getProductType().equalsIgnoreCase(Constants.PRODUCT_TYPE_LANDLINE)) {
                    flgProductLandline[0] = true;
                } else if (item.getProductType().equalsIgnoreCase(Constants.PRODUCT_TYPE_BROADBAND)) {
                    flgProductBroadband[0] = true;
                } else if (item.getProductType().equalsIgnoreCase(Constants.PRODUCT_TYPE_CABLE_TV)
                        || item.getProductType()
                        .equalsIgnoreCase(Constants.PRODUCT_TYPE_CHANNEL_TV)) {
                    flgProductCabletv[0] = true;
                }
            });
        }
        LOG.info("Sales has landline product: " + flgProductLandline[0]);
        LOG.info("Sales has broadband product: " + flgProductBroadband[0]);
        LOG.info("Sales has cableTv product: " + flgProductCabletv[0]);

        Number offerPriority = currentCommercialOperationType.getServiceAvailability().getOffers().get(0).getPriority();
        currentCommercialOperationType.getServiceAvailability().getOffers().get(0).getServices()
                .forEach(serviceOffer -> {
            String serviceAbilityType = serviceOffer.getType();

            if (serviceAbilityType.equalsIgnoreCase(Constants.PRODUCT_TYPE_LANDLINE)
                    && flgProductLandline[0]) {

                // Serviceability Landline
                CharacteristicOfferType describeByLandline1 = CharacteristicOfferType
                        .builder().characteristicName("ALLOCATION_ID")
                        .characteristicValue(serviceOffer.getAllocationId())
                        .build();

                List<CharacteristicOfferType> describeByLandlineList = new ArrayList<>();
                describeByLandlineList.add(describeByLandline1);

                ProductLineType productOfferLandline1 = ProductLineType.builder()
                        .type("VOICE").description("Servicio de Voz")
                        .networkTechnology(Commons.getStringValueByKeyFromAdditionalDataList(
                                currentCommercialOperationType.getServiceAvailability().getAdditionalData(),
                                "networkAccessTechnologyLandline"))
                        .serviceTechnology(Commons.getStringValueByKeyFromAdditionalDataList(
                                currentCommercialOperationType.getServiceAvailability().getAdditionalData(),
                                "serviceTechnologyLandline"))
                        .describeByList(describeByLandlineList).build();
                List<ProductLineType> productOfferLandlineList = new ArrayList<>();
                productOfferLandlineList.add(productOfferLandline1);

                ServiceabilityOfferType serviceabilityOfferLandline = ServiceabilityOfferType
                        .builder().idOfferPriority(offerPriority)
                        .productOffer(productOfferLandlineList).build();
                serviceabilityOffersList.add(serviceabilityOfferLandline);

            } else if (serviceAbilityType.equalsIgnoreCase("broadband")
                    && flgProductBroadband[0]) {

                // Serviceability Broadband
                CharacteristicOfferType describeByBroadband1 = CharacteristicOfferType
                        .builder().characteristicName("MaxTheoricalSpeed")
                        .characteristicValue(Commons.getStringValueByKeyFromAdditionalDataList(
                                currentCommercialOperationType.getServiceAvailability().getAdditionalData(),
                                Constants.MAXSPEED))
                        .build();

                List<CharacteristicOfferType> describeByBroadbandList = new ArrayList<>();
                describeByBroadbandList.add(describeByBroadband1);

                ProductLineType productOfferBroadband1 = ProductLineType.builder()
                        .type("BB").description("Servicio de banda ancha")
                        .networkTechnology(Commons
                                .getStringValueByKeyFromAdditionalDataList(
                                        currentCommercialOperationType.getServiceAvailability().getAdditionalData(),
                                        "networkAccessTechnologyBroadband"))
                        .serviceTechnology(Commons
                                .getStringValueByKeyFromAdditionalDataList(
                                        currentCommercialOperationType.getServiceAvailability().getAdditionalData(),
                                        "serviceTechnologyBroadband"))
                        .describeByList(describeByBroadbandList).build();
                List<ProductLineType> productOfferBroadbandList = new ArrayList<>();
                productOfferBroadbandList.add(productOfferBroadband1);

                ServiceabilityOfferType serviceabilityOfferBroadband = ServiceabilityOfferType
                        .builder().idOfferPriority(offerPriority)
                        .productOffer(productOfferBroadbandList).build();
                serviceabilityOffersList.add(serviceabilityOfferBroadband);

            } else if (serviceAbilityType.equalsIgnoreCase("tv") && flgProductCabletv[0]) {

                // Serviceability CableTv
                ProductLineType productOfferCableTv1 = ProductLineType.builder()
                        .type("TV").description("Servicio de Television")
                        .networkTechnology(Commons
                                .getStringValueByKeyFromAdditionalDataList(
                                        currentCommercialOperationType.getServiceAvailability().getAdditionalData(),
                                        "networkAccessTechnologyTv"))
                        .serviceTechnology(Commons
                                .getStringValueByKeyFromAdditionalDataList(
                                        currentCommercialOperationType.getServiceAvailability().getAdditionalData(),
                                        "serviceTechnologyTv"))
                        .build();
                List<ProductLineType> productOfferCableTvList = new ArrayList<>();
                productOfferCableTvList.add(productOfferCableTv1);

                ServiceabilityOfferType serviceabilityOfferCableTv = ServiceabilityOfferType
                        .builder().idOfferPriority(offerPriority)
                        .productOffer(productOfferCableTvList).build();
                serviceabilityOffersList.add(serviceabilityOfferCableTv);
            }
        });
    }

    @Override
    public String setUpFrontIndicator(String productType) {
        return productType.equalsIgnoreCase(Constants.WIRELESS) ? "N" : "Y";
    }

    @Override
    public boolean validateNegotiation(List<KeyValueType> additionalData,
                                       List<IdentityValidationType> identityValidationTypes) {
        final Boolean[] isPresencial = { false };
        final Boolean[] isRetail = { false };
        final Boolean[] isBiometric = { true };

        additionalData.forEach(kv -> {
            if (kv.getKey().equalsIgnoreCase(Constants.FLOWSALE)) {
                if (kv.getValue().equalsIgnoreCase(Constants.PRESENCIAL)) {
                    isPresencial[0] = true;
                } else if (kv.getValue().equalsIgnoreCase(Constants.RETAIL)) {
                    isRetail[0] = true;
                }
            }
        });

        // Sort identityValidationType by date field
        final Date[] latestDate = { null };
        final int[] cont = { 0 };
        if (identityValidationTypes != null && !identityValidationTypes.isEmpty()) {
            identityValidationTypes.forEach(ivt -> {
                // convert String date to Date
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSZ");
                try {
                    Date currentDate = format.parse(ivt.getDate());
                    if (latestDate[0] == null || latestDate[0].before(currentDate)) {
                        latestDate[0] = currentDate;
                        cont[0]++;
                    }
                } catch (ParseException ex) {
                    LOG.error("Post Sales Validate Negotiation Exception: " + ex);
                }
            });
        }
        // validate validationType
        if (identityValidationTypes != null && !identityValidationTypes.get(cont[0]).getValidationType()
                .equalsIgnoreCase("Biometric")) {
            isBiometric[0] = false;
        }

        final boolean[] isRemotoAndContigenciaReniec = { false };
        additionalData.stream()
                .filter(kv -> kv.getKey().equalsIgnoreCase(Constants.FLOWSALE)
                        && kv.getValue().equalsIgnoreCase(Constants.REMOTO))
                .findFirst()
                .flatMap(kv -> identityValidationTypes.stream()
                        .filter(item -> item.getValidationType()
                                .equalsIgnoreCase("NoBiometric"))
                        .findFirst())
                .flatMap(item -> item.getAdditionalData().stream()
                        .filter(ad -> ad.getKey().equalsIgnoreCase("noBiometricIdType")
                                && ad.getValue().equalsIgnoreCase("3"))
                        .findFirst())
                .ifPresent(ad -> isRemotoAndContigenciaReniec[0] = true);

        return ((isPresencial[0] || isRetail[0]) && !isBiometric[0]) || isRemotoAndContigenciaReniec[0];
    }

    @Override
    public void postSalesEventFlow(PostSalesRequest request) {
        if (request.getSale().getAdditionalData() == null) {
            request.getSale().setAdditionalData(new ArrayList<>());
        }
        request.getSale().getAdditionalData().add(KeyValueType.builder().key("initialProcessDate")
                .value(DateUtils.getDatetimeNowCosmosDbFormat()).build());

        callReceptors(request);
    }

    private void callReceptors(PostSalesRequest request) {
        String flowSale = Commons.getStringValueByKeyFromAdditionalDataList(request.getSale().getAdditionalData(),
                Constants.FLOWSALE);
        boolean isFlow5 = flowSale.equalsIgnoreCase(Constants.REMOTO)
                && request.getSale().getStatus().equalsIgnoreCase(Constants.SALES_STATUS_NUEVO)
                && request.getSale().getAudioStatus().equalsIgnoreCase(Constants.PENDIENTE);
        boolean isFlow4 = !isFlow5 && (flowSale.equalsIgnoreCase(Constants.TIENDAS)
                || flowSale.equalsIgnoreCase(Constants.PROACTIVO) || flowSale.equalsIgnoreCase(Constants.RETAIL)
                || (flowSale.equalsIgnoreCase(Constants.REMOTO)));

        if (!request.getSale().getProductType().equalsIgnoreCase(Constants.MT)) {
            if (isFlow4) {
                callWebClientReceptor(request, Constants.FLOW_SALE_PRESENCIAL);
            } else if (isFlow5) {
                callWebClientReceptor(request, Constants.FLOW_SALE_NO_PRESENCIAL);
            }
        }
    }

    private void callWebClientReceptor(PostSalesRequest request, String eventFlowCode) {
        // Llamada a receptor
        webClientReceptor.register(ReceptorRequest.builder()
                        .businessId(request.getSale().getSalesId())
                        .typeEventFlow(eventFlowCode).message(request.getSale()).build(),
                request.getHeadersMap()).subscribe();
    }

    @Override
    public List<ChangedContainedProduct> changedContainedCaeqList(Sale saleRequest, String tempNum,
                                                              String sapidSimcardBp, boolean flgCasi,
                                                              CommercialOperationType currentCommercialOperationType,
                                                              boolean flgAlta, boolean isRetail) {
        String acquisitionType = "";
        acquisitionType = getAcquisitionTypeValue(saleRequest, flgAlta, currentCommercialOperationType);

        // AcquisitionType Characteristic
        ChangedCharacteristic changedCharacteristic1 = ChangedCharacteristic.builder().characteristicId("9941")
                .characteristicValue(acquisitionType).build();

        // EquipmentCID Characteristic
        ChangedCharacteristic changedCharacteristic2 = ChangedCharacteristic.builder().characteristicId("15734")
                .characteristicValue(currentCommercialOperationType.getDeviceOffering().stream()
                        .filter(item -> !item.getDeviceType().equalsIgnoreCase("SIM"))
                        .findFirst()
                        .orElse(DeviceOffering.builder().id(null).build()).getId())
                .build();

        List<ChangedCharacteristic> changedCharacteristicList = new ArrayList<>();

        // EquipmentIMEI Characteristic
        if (isRetail && saleRequest.getStatus().equalsIgnoreCase(Constants.VALIDADO)) {
            String deviceImei = Commons.getStringValueByKeyFromAdditionalDataList(
                    saleRequest.getAdditionalData(), Constants.MOVILE_IMEI);
            ChangedCharacteristic changedCharacteristic3 = ChangedCharacteristic.builder()
                    .characteristicId("9871").characteristicValue(deviceImei).build();
            changedCharacteristicList.add(changedCharacteristic3);
        }

        changedCharacteristicList.add(changedCharacteristic1);
        changedCharacteristicList.add(changedCharacteristic2);

        // SIMGROUP Characteristic (Conditional)
        if (currentCommercialOperationType.getDeviceOffering().size() == 1) {
            ChangedCharacteristic changedCharacteristic4 = ChangedCharacteristic.builder()
                    .characteristicId("16524")
                    .characteristicValue(
                            currentCommercialOperationType.getDeviceOffering().get(0).getSimSpecifications().get(0)
                                    .getType())
                    .build();
            changedCharacteristicList.add(changedCharacteristic4);

        } else if (currentCommercialOperationType.getDeviceOffering().size() > 1) {
            String simGroup = currentCommercialOperationType.getDeviceOffering().get(1).getSimSpecifications().get(0)
                    .getType();
            ChangedCharacteristic changedCharacteristic4 = ChangedCharacteristic.builder()
                    .characteristicId("16524").characteristicValue(simGroup).build();
            changedCharacteristicList.add(changedCharacteristic4);
        }

        ChangedContainedProduct changedContainedProduct1 = ChangedContainedProduct.builder()
                .temporaryId(tempNum).productCatalogId("7411")
                .changedCharacteristics(changedCharacteristicList).build();

        if (!currentCommercialOperationType.getReason().equalsIgnoreCase(Constants.PORTABILIDAD)
                && !currentCommercialOperationType.getReason().equalsIgnoreCase(Constants.ALTA)
                && saleRequest.getProductType().equalsIgnoreCase(Constants.WIRELESS)
                && currentCommercialOperationType.getProduct().getProductRelationShip() != null) {
            // FEMS-3799 (CR)
            setChangedContainedProductProductId(changedContainedProduct1, currentCommercialOperationType
                    .getProduct().getProductRelationShip());
        }

        List<ChangedContainedProduct> changedContainedProductList = new ArrayList<>();
        // FEMS3873 - CASI attributes
        return casiAttributes(sapidSimcardBp, changedContainedProduct1, changedContainedProductList, flgCasi,
                currentCommercialOperationType);
    }

    private String getAcquisitionTypeValue(Sale saleRequest, boolean flgAlta,
                                           CommercialOperationType currentCommercialOperationType) {
        String acquisitionType = "";
        String saleChannelId = "";
        String deliveryType = "";

        // Getting Sale Channel
        saleChannelId = saleRequest.getChannel().getId();

        // Getting Delivery Method (IS, SP)
        for (KeyValueType kv : saleRequest.getAdditionalData()) {
            if (kv.getKey().equals(Constants.KEY_DELIVERY_METHOD)) {
                deliveryType = kv.getValue();
            }
        }

        // Logic for Set Acquisition Type Value
        if (flgAlta && currentCommercialOperationType.getDeviceOffering().size() == 1) { // Identifying if is Alta only Sim
            acquisitionType = "Private";
        } else if (saleChannelId.equalsIgnoreCase("CC") && deliveryType.equalsIgnoreCase("SP")
                || saleChannelId.equalsIgnoreCase("CEC")
                || saleChannelId.equalsIgnoreCase("ST") && deliveryType.equalsIgnoreCase("SP")
                || saleChannelId.equalsIgnoreCase("DLS")) {
            acquisitionType = "ConsessionPurchased";
        } else if (saleChannelId.equalsIgnoreCase("ST") && deliveryType.equalsIgnoreCase("IS")
                || saleChannelId.equalsIgnoreCase("DLV")) {
            acquisitionType = "Sale";
        } else if (saleChannelId.equalsIgnoreCase("DLC")) {
            acquisitionType = "Consignation";
        } else {
            acquisitionType = "Private";
        }

        return acquisitionType;
    }

    private void setChangedContainedProductProductId(ChangedContainedProduct changedContainedProduct,
                                                     List<RelatedProductType> productRelationShip) {
        // FEMS-3799 (CR)
        productRelationShip.stream()
                .filter(item -> item.getProduct().getDescription().equalsIgnoreCase(Constants.SIM_DEVICE))
                .findFirst()
                .ifPresent(item -> item.getProduct().getProductRelationship().stream()
                        .filter(pr -> pr.getProduct().getDescription().equalsIgnoreCase(Constants.PRODUCT_TYPE_DEVICE))
                        .findFirst()
                        .ifPresent(pr -> changedContainedProduct.setProductId(pr.getProduct().getId())));
    }

    private List<ChangedContainedProduct> casiAttributes(String sapidSimcardBp,
                                                         ChangedContainedProduct changedContainedProduct1,
                                                         List<ChangedContainedProduct> changedContainedProductList,
                                                         boolean flgCasi,
                                                         CommercialOperationType currentCommercialOperationType) {
        // Cuando viene activo el flag de CASI
        if (flgCasi) {
            List<RelatedProductType> productRelationShipList = currentCommercialOperationType.getProduct()
                    .getProductRelationShip();
            // Buscar el productId para simcard
            String simcardProductId = productRelationShipList.stream()
                    .filter(prs -> prs.getProduct().getDescription().equalsIgnoreCase(Constants.SIM_DEVICE)
                            || prs.getProduct().getDescription().equalsIgnoreCase("Simcard")).findFirst()
                    .orElse(RelatedProductType.builder()
                            .product(ProductRefInfoType.builder().id(null).build()).build())
                    .getProduct().getId();

            // Buscar el productId para device
            String deviceProductId = productRelationShipList.stream()
                    .filter(prs -> !prs.getProduct().getDescription().equalsIgnoreCase(Constants.SIM_DEVICE)
                            && !prs.getProduct().getDescription().equalsIgnoreCase("Simcard")).findFirst()
                    .orElse(RelatedProductType.builder()
                            .product(ProductRefInfoType.builder().id(null).build()).build())
                    .getProduct().getId();

            changedContainedProduct1.setProductId(deviceProductId);

            ChangedContainedProduct changedContainedProductCasi = ChangedContainedProduct.builder()
                    .productId(simcardProductId).temporaryId("changeSimcard")
                    .productCatalogId("7431")
                    .changedCharacteristics(Collections.singletonList(
                            ChangedCharacteristic.builder().characteristicId("9751")
                                    .characteristicValue(sapidSimcardBp).build()))
                    .build();
            changedContainedProductList.add(changedContainedProductCasi);
        }
        changedContainedProductList.add(changedContainedProduct1);
        return changedContainedProductList;
    }

    @Override
    public List<FlexAttrType> commonOrderAttributes(Sale saleRequest, final boolean flgFinanciamiento,
                                                    final boolean sendIndicator, final boolean isRetail,
                                                    CommercialOperationType currentCommercialOperationType) {
        // Building Common Order Attributes
        List<FlexAttrType> commonOrderAttributes = new ArrayList<>();

        // Delivery Method Attribute
        String deliveryCode = "";
        for (KeyValueType kv : saleRequest.getAdditionalData()) {
            if (kv.getKey().equals(Constants.KEY_DELIVERY_METHOD)) {
                deliveryCode = kv.getValue();
            }
        }

        if (isRetail) {
            deliveryCode = "IS";
        }
        if (!StringUtils.isEmpty(deliveryCode)) {
            FlexAttrValueType deliveryAttrValue = FlexAttrValueType.builder().stringValue(deliveryCode)
                    .valueType(Constants.STRING).build();
            FlexAttrType deliveryAttr = FlexAttrType.builder().attrName(Constants.DELIVERY_METHOD)
                    .flexAttrValue(deliveryAttrValue).build();
            commonOrderAttributes.add(deliveryAttr);
        }

        // Payment Method Attribute - Conditional
        if (!isRetail && saleRequest.getPaymenType() != null
                && !StringUtils.isEmpty(saleRequest.getPaymenType().getPaymentType())) {
            FlexAttrValueType paymentAttrValue = FlexAttrValueType.builder()
                    .stringValue(saleRequest.getPaymenType().getPaymentType())
                    .valueType(Constants.STRING).build();
            FlexAttrType paymentAttr = FlexAttrType.builder().attrName("PAYMENT_METHOD")
                    .flexAttrValue(paymentAttrValue).build();
            commonOrderAttributes.add(paymentAttr);
        }

        if (sendIndicator && flgFinanciamiento) {
            // FEMS-5497
            FinancingInstalment financingInstalment = currentCommercialOperationType.getDeviceOffering().get(0)
                    .getOffers().get(0).getBillingOfferings().get(0).getCommitmentPeriods().get(0)
                    .getFinancingInstalments().get(0);

            // FINANCING_AMOUNT
            Number financingAmount = financingInstalment.getInstalments().getTotalAmount().getValue()
                    .doubleValue()
                    - financingInstalment.getInstalments().getOpeningQuota().getValue()
                    .doubleValue();
            FlexAttrValueType financingAmountAttrValue = FlexAttrValueType.builder()
                    .stringValue(financingAmount.toString()).valueType(Constants.STRING).build();
            FlexAttrType financingAmountAttr = FlexAttrType.builder().attrName("FINANCING_AMOUNT")
                    .flexAttrValue(financingAmountAttrValue).build();
            commonOrderAttributes.add(financingAmountAttr);

            // IS_EXTERNAL_FINANCING -> true
            FlexAttrValueType externalFinancialAttrValue = FlexAttrValueType.builder().stringValue("true")
                    .valueType(Constants.STRING).build();
            FlexAttrType externalFinancialAttr = FlexAttrType.builder().attrName("IS_EXTERNAL_FINANCING")
                    .flexAttrValue(externalFinancialAttrValue).build();
            commonOrderAttributes.add(externalFinancialAttr);

            // FINANFING_PLAN
            String codigo = financingInstalment.getCodigo();
            FlexAttrValueType financingPlanAttrValue = FlexAttrValueType.builder().stringValue(codigo)
                    .valueType(Constants.STRING).build();
            FlexAttrType financingPlanAttr = FlexAttrType.builder().attrName("FINANCING_PLAN")
                    .flexAttrValue(financingPlanAttrValue).build();
            commonOrderAttributes.add(financingPlanAttr);

            // DOWN_PAYMENT_AMOUNT
            Number downPaymentAmount = financingInstalment.getInstalments().getOpeningQuota().getValue()
                    .doubleValue();
            FlexAttrValueType downPaymentAttrValue = FlexAttrValueType.builder()
                    .stringValue(downPaymentAmount.toString()).valueType(Constants.STRING).build();
            FlexAttrType downPaymentAttr = FlexAttrType.builder().attrName("DOWN_PAYMENT_AMOUNT")
                    .flexAttrValue(downPaymentAttrValue).build();
            commonOrderAttributes.add(downPaymentAttr);

        } else if (sendIndicator) {
            // IS_EXTERNAL_FINANCING -> false
            FlexAttrValueType externalFinancialAttrValue = FlexAttrValueType.builder().stringValue("false")
                    .valueType(Constants.STRING).build();
            FlexAttrType externalFinancialAttr = FlexAttrType.builder().attrName("IS_EXTERNAL_FINANCING")
                    .flexAttrValue(externalFinancialAttrValue).build();
            commonOrderAttributes.add(externalFinancialAttr);
        }

        return commonOrderAttributes;
    }

    @Override
    public ShipmentDetailsType createShipmentDetail(Sale saleRequest,
                                                    CommercialOperationType currentCommercialOperationType) {
        ShipmentDetailsType shipmentDetailsType = ShipmentDetailsType.builder()
                .recipientFirstName(saleRequest.getRelatedParty().get(0).getFirstName())
                .recipientLastName(saleRequest.getRelatedParty().get(0).getLastName()).build();
        shipmentDetailsType.setRecipientTelephoneNumber(currentCommercialOperationType.getWorkOrDeliveryType()
                .getContact().getPhoneNumber());
        shipmentDetailsType.setShippingLocality(currentCommercialOperationType.getWorkOrDeliveryType().getPlace().get(0)
                .getAddress().getStateOrProvince());
        shipmentDetailsType.setShipmentAddressId(currentCommercialOperationType.getWorkOrDeliveryType().getPlace()
                .get(0).getId());
        shipmentDetailsType.setShipmentSiteId("NA");
        saleRequest.getProspectContact().stream()
                .filter(item -> item.getMediumType().equalsIgnoreCase(Constants.MEDIUM_TYPE_EMAIL_ADDRESS))
                .findFirst()
                .ifPresent(contactMedium -> {
                    shipmentDetailsType.setRecipientEmail(contactMedium.getCharacteristic().getEmailAddress());
        });
        // additional Datas
        currentCommercialOperationType.getWorkOrDeliveryType().getAdditionalData().forEach(item -> {
                    if (item.getKey().equalsIgnoreCase("shipmentInstructions")) {
                        shipmentDetailsType.setShipmentInstructions(item.getValue());
                    } else if (item.getKey().equalsIgnoreCase("shipmentOption")) {
                        shipmentDetailsType.setShipmentOption(item.getValue());
                    }
                });

        CollectionUtils.emptyIfNull(currentCommercialOperationType.getWorkOrDeliveryType().getAdditionalData())
                .forEach(item -> {
                    if (item.getKey().equalsIgnoreCase(Constants.SHOP_ADDRESS)) {
                        shipmentDetailsType.setShopAddress(item.getValue());
                    } else if (item.getKey().equalsIgnoreCase("shopName")) {
                        shipmentDetailsType.setShopName(item.getValue());
                    } else if (item.getKey().equalsIgnoreCase("collectStoreId")) {
                        shipmentDetailsType.setCollectStoreId(item.getValue());
                    }
                });

        CollectionUtils.emptyIfNull(currentCommercialOperationType.getWorkOrDeliveryType().getPlace().get(0)
                .getAdditionalData()).forEach(item -> {
            if (item.getKey().equalsIgnoreCase("stateOrProvinceCode")) {
                shipmentDetailsType.setProvinceOfShippingAddress(item.getValue());
            }
        });

        return shipmentDetailsType;
    }

    @Override
    public Mono<Sale> creationOrderValidation(Sale saleRequest, CreateProductOrderGeneralRequest productOrderRequest,
                                              CommercialOperationType currentCommercialOperationType,
                                              HashMap<String, String> headersMap,
                                              ProductOrderWebClient productOrderWebClient,
                                              GetSkuWebClient getSkuWebClient) {

        KeyValueType keyValueType = saleRequest.getAdditionalData().stream()
                .filter(item -> item.getKey().equalsIgnoreCase(Constants.FLOWSALE)).findFirst()
                .orElse(null);

        String operationType = currentCommercialOperationType.getReason().equals("ALTA")
                ? "Provide"
                : "Change";

        if (keyValueType != null && keyValueType.getValue().equalsIgnoreCase(Constants.RETAIL)
                && saleRequest.getStatus().equalsIgnoreCase(Constants.NEGOCIACION)) {

            DeviceOffering saleDeviceOfferingSim = currentCommercialOperationType.getDeviceOffering().stream()
                    .filter(deviceOffering -> deviceOffering.getDeviceType()
                            .equalsIgnoreCase(Constants.DEVICE_TYPE_SIM))
                    .findFirst().orElse(null);

            DeviceOffering saleDeviceOfferingSmartphone = currentCommercialOperationType.getDeviceOffering().stream()
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
                    || StringUtils.isEmpty(saleDeviceOfferingSim.getId()) ? "" : saleDeviceOfferingSim.getId();
            String costoDeviceOfferingSim = saleDeviceOfferingSim == null
                    || StringUtils.isEmpty(saleDeviceOfferingSim.getCostoPromedioSinIgvSoles()) ? "0.00"
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
                productOrderCaplRequest.setOnlyValidationIndicator(Constants.STRING_TRUE);
                productOrderRequest.setCreateProductOrderRequest(productOrderCaplRequest);
            } else if (classObjectName.equalsIgnoreCase("ProductOrderCaeqRequest")) {
                ProductOrderCaeqRequest productOrderCaeqRequest = (ProductOrderCaeqRequest) productOrderRequest
                        .getCreateProductOrderRequest();
                productOrderCaeqRequest.setOnlyValidationIndicator(Constants.STRING_TRUE);
                productOrderRequest.setCreateProductOrderRequest(productOrderCaeqRequest);
            } else if (classObjectName.equalsIgnoreCase("ProductOrderCaeqCaplRequest")) {
                ProductOrderCaeqCaplRequest productOrderCaeqCaplRequest = (ProductOrderCaeqCaplRequest) productOrderRequest
                        .getCreateProductOrderRequest();
                productOrderCaeqCaplRequest.setOnlyValidationIndicator(Constants.STRING_TRUE);
                productOrderRequest.setCreateProductOrderRequest(productOrderCaeqCaplRequest);
            } else if (classObjectName.equalsIgnoreCase("ProductOrderAltaFijaRequest")) {
                ProductOrderAltaFijaRequest productOrderAltaFijaRequest = (ProductOrderAltaFijaRequest) productOrderRequest
                        .getCreateProductOrderRequest();
                productOrderAltaFijaRequest.setOnlyValidationIndicator(Constants.STRING_TRUE);
                productOrderRequest.setCreateProductOrderRequest(productOrderAltaFijaRequest);
            } else if (classObjectName.equalsIgnoreCase("ProductOrderAltaMobileRequest")) {
                ProductOrderAltaMobileRequest productOrderAltaMobileRequest = (ProductOrderAltaMobileRequest) productOrderRequest
                        .getCreateProductOrderRequest();
                productOrderAltaMobileRequest.setOnlyValidationIndicator(Constants.STRING_TRUE);
                productOrderRequest.setCreateProductOrderRequest(productOrderAltaMobileRequest);
            }

            Mono<ProductorderResponse> productOrderResponse = productOrderWebClient
                    .createProductOrder(productOrderRequest, headersMap, saleRequest);

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

    @Override
    public List<KeyValueType> additionalDataAssigments(List<KeyValueType> input, Sale saleRequest,
                                                        CommercialOperationType currentCommercialOperationType) {
        // add shipmentDetails structure to additionalData
        List<KeyValueType> additionalDataAux = input;
        if (additionalDataAux == null) {
            additionalDataAux = new ArrayList<>();
        }
        // assignments
        KeyValueType mediumDeliveryLabel = KeyValueType.builder().key("mediumDeliveryLabel")
                .value("Chip Tienda").build();
        KeyValueType collectStoreId = KeyValueType.builder().key("collectStoreId")
                .value(saleRequest.getChannel().getStoreId()).build();
        KeyValueType shipmentAddressId = KeyValueType.builder().key("shipmentAddressId").value("").build();
        KeyValueType shipmentSiteId = KeyValueType.builder().key("shipmentSiteId").value("NA").build();
        KeyValueType shipmentInstructions = KeyValueType.builder().key("shipmentInstructions")
                .value("No se registró instrucciones").build();
        additionalDataAux.add(mediumDeliveryLabel);
        additionalDataAux.add(collectStoreId);
        additionalDataAux.add(shipmentAddressId);
        additionalDataAux.add(shipmentSiteId);
        additionalDataAux.add(shipmentInstructions);

        KeyValueType shippingLocality;
        KeyValueType provinceOfShippingAddress;
        KeyValueType shopAddress;

        if (saleRequest.getCommercialOperation() != null && !saleRequest.getCommercialOperation().isEmpty()
                && currentCommercialOperationType.getWorkOrDeliveryType().getPlace() != null
                && !currentCommercialOperationType.getWorkOrDeliveryType().getPlace().isEmpty()
                && currentCommercialOperationType.getWorkOrDeliveryType().getPlace().get(0).getAddress().getRegion()
                .equalsIgnoreCase("LIMA")) {
            // case when is Lima
            shippingLocality = KeyValueType.builder().key(Constants.SHIPPING_LOCALITY).value("PUEBLO LIBRE").build();
            provinceOfShippingAddress = KeyValueType.builder().key(Constants.PROVINCE_OF_SHIPPING_ADDRESS).value("15")
                    .build();
            shopAddress = KeyValueType.builder().key(Constants.SHOP_ADDRESS)
                    .value("AV. SUCRE NRO 1183 LIMA-LIMA-PUEBLO").build();

        } else if (saleRequest.getCommercialOperation() != null
                && currentCommercialOperationType.getWorkOrDeliveryType().getPlace() != null
                && !saleRequest.getCommercialOperation().isEmpty()
                && !currentCommercialOperationType.getWorkOrDeliveryType().getPlace().isEmpty()
                && currentCommercialOperationType.getWorkOrDeliveryType().getPlace().get(0).getAddress().getRegion()
                .equalsIgnoreCase("CALLAO")) {
            // case when is Callao
            shippingLocality = KeyValueType.builder().key(Constants.SHIPPING_LOCALITY).value("PUEBLO LIBRE").build();
            provinceOfShippingAddress = KeyValueType.builder().key(Constants.PROVINCE_OF_SHIPPING_ADDRESS).value("07")
                    .build();
            shopAddress = KeyValueType.builder().key(Constants.SHOP_ADDRESS)
                    .value("AV. SUCRE NRO 1183 LIMA-LIMA-PUEBLO").build();

        } else {
            // case when is not Lima and is not Callao
            shippingLocality = KeyValueType.builder().key(Constants.SHIPPING_LOCALITY).value("TRUJILLO").build();
            provinceOfShippingAddress = KeyValueType.builder().key(Constants.PROVINCE_OF_SHIPPING_ADDRESS).value("13")
                    .build();
            shopAddress = KeyValueType.builder().key(Constants.SHOP_ADDRESS)
                    .value("AV. AMERICA NORTE 1245 URB. LOS JARDINES - TRUJILLO").build();
        }

        additionalDataAux.add(shippingLocality);
        additionalDataAux.add(provinceOfShippingAddress);
        additionalDataAux.add(shopAddress);

        return additionalDataAux;
    }

    @Override
    public Mono<Sale> reserveStockMobile(PostSalesRequest request, Sale saleRequest, boolean flgCasi,
                                         boolean flgFinanciamiento, String sapidSimcard,
                                         CommercialOperationType currentCommercialOperationType) {

        if (saleRequest.getProductType().equalsIgnoreCase(Constants.WIRELINE)) {
            return Mono.just(saleRequest);
        }

        ReserveStockRequest reserveStockRequest = new ReserveStockRequest();
        this.buildReserveStockRequest(reserveStockRequest, saleRequest, currentCommercialOperationType.getOrder(),
                sapidSimcard, currentCommercialOperationType);

        return stockWebClient.reserveStock(reserveStockRequest, request.getHeadersMap(), saleRequest)
                .flatMap(reserveStockResponse -> {
                    this.setReserveReponseInSales(reserveStockResponse, saleRequest,
                            currentCommercialOperationType);
                    return Mono.just(saleRequest);
                });
    }

    @Override
    public ReserveStockRequest buildReserveStockRequest(ReserveStockRequest request, Sale sale,
                                                        CreateProductOrderResponseType createOrderResponse,
                                                        String sapidSimcardBp,
                                                        CommercialOperationType currentCommercialOperationType) {
        request.setReason("PRAEL");

        List<String> requiredActionList = new ArrayList<>();
        requiredActionList.add(currentCommercialOperationType.getReason().equalsIgnoreCase(Constants.ALTA)
                || currentCommercialOperationType.getReason().equalsIgnoreCase(Constants.PORTABILIDAD) ? "PR"
                : "CH");
        request.setRequiredActions(requiredActionList);

        List<String> usageList = new ArrayList<>();
        usageList.add("sale");
        request.setUsage(usageList);

        SiteRefType site = SiteRefType.builder().id(sale.getChannel().getStoreId()).build();
        Destination destination = Destination.builder().site(site).type("store").build();
        request.setDestination(destination);

        request.setChannel(sale.getChannel().getId());

        List<StockItem> itemsList = new ArrayList<>();

        // Equipment Item
        DeviceOffering deviceOfferingSmartphone = currentCommercialOperationType.getDeviceOffering().stream()
                .filter(item -> item.getDeviceType().equalsIgnoreCase(Constants.DEVICE_TYPE_SMARTPHONE))
                .findFirst().orElse(null);
        if (deviceOfferingSmartphone != null) {
            Item item1 = Item.builder().id(deviceOfferingSmartphone.getSapid()).type("IMEI").build();
            StockItem stockItem1 = StockItem.builder().item(item1).build();
            itemsList.add(stockItem1);
        }

        DeviceOffering deviceOfferingSimcard = currentCommercialOperationType.getDeviceOffering().stream()
                .filter(item -> item.getDeviceType().equalsIgnoreCase(Constants.DEVICE_TYPE_SIM))
                .findFirst().orElse(null);

        if (deviceOfferingSimcard != null) {
            // SIM Item
            Item item2 = Item.builder().id(sapidSimcardBp).type("ICCID").build();
            StockItem stockItem2 = StockItem.builder().item(item2).build();
            itemsList.add(stockItem2);
        }
        request.setItems(itemsList);

        request.setOrderAction(createOrderResponse.getProductOrderId());

        Order order = Order.builder()
                // Quitar último caracter
                .id(org.apache.commons.lang3.StringUtils
                        .chop(createOrderResponse.getNewProductsInNewOfferings().get(0)
                                .getProductOrderItemReferenceNumber()))
                .build();
        request.setOrder(order);

        return request;
    }

    @Override
    public void setReserveReponseInSales(ReserveStockResponse reserveStockResponse, Sale saleRequest,
                                         CommercialOperationType commercialOperationType) {
        KeyValueType dateKv = KeyValueType.builder().key("reservationDate").value(Commons.getDatetimeNow())
                .build();

        if (commercialOperationType.getDeviceOffering().get(0).getAdditionalData() == null) {
            commercialOperationType.getDeviceOffering().get(0).setAdditionalData(new ArrayList<>());
        }

        commercialOperationType.getDeviceOffering().get(0).getAdditionalData().add(dateKv);

        commercialOperationType.getDeviceOffering().forEach(deviceOffering -> {
            if (deviceOffering.getStock() == null) {
                deviceOffering.setStock(StockType.builder().reservationId(reserveStockResponse.getId()).build());
            } else {
                deviceOffering.getStock().setReservationId(reserveStockResponse.getId());
            }
        });

        commercialOperationType.getDeviceOffering().get(0).getStock().setAmount(reserveStockResponse.getItems().get(0)
                .getAmount());

        commercialOperationType.getDeviceOffering().get(0).getStock().setSite(reserveStockResponse.getItems().get(0)
                .getSite());
    }

    @Override
    public Mono<Sale> createQuotationMobile(PostSalesRequest request, Sale sale, boolean flgCasi,
                                            boolean flgFinanciamiento,
                                            CommercialOperationType currentCommercialOperationType) {

        if (flgFinanciamiento) {
            CreateQuotationRequest createQuotationRequest = new CreateQuotationRequest();
            this.buildCreateQuotationRequest(createQuotationRequest, request, flgCasi, currentCommercialOperationType);
            if (createQuotationRequest.getBody() != null) {
                return quotationWebClient.createQuotation(createQuotationRequest, sale)
                        .flatMap(createQuotationResponse -> {
                            this.setQuotationResponseInSales(createQuotationResponse, sale);
                            return salesRepository.save(sale).map(r -> {
                                this.postSalesEventFlow(request);
                                return r;
                            });
                        });
            }
        }

        return salesRepository.save(sale).map(r -> {
            this.postSalesEventFlow(request);
            return r;
        });
    }

    private void buildCreateQuotationRequest(CreateQuotationRequest createQuotationRequest,
                                             PostSalesRequest salesRequest, boolean flgCasi,
                                             CommercialOperationType currentCommercialOperationType) {

        createQuotationRequest.setHeadersMap(salesRequest.getHeadersMap());
        Sale sale = salesRequest.getSale();

        final String[] email = { null };
        sale.getProspectContact().stream().filter(
                item -> item.getMediumType().equalsIgnoreCase(Constants.MEDIUM_TYPE_EMAIL_ADDRESS))
                .findFirst().ifPresent(item -> email[0] = item.getCharacteristic().getEmailAddress());
        com.tdp.ms.sales.model.dto.quotation.ContactMedium contactMedium1 = com.tdp.ms.sales.model.dto.quotation.ContactMedium
                .builder().type(Constants.EMAIL).name(email[0]).preferred("true").isActive("true")
                .build();
        List<com.tdp.ms.sales.model.dto.quotation.ContactMedium> contactMediumList = new ArrayList<>();
        contactMediumList.add(contactMedium1);

        LegalId legalId = LegalId.builder().isPrimary("true").country("PE")
                .nationalIdType(sale.getRelatedParty().get(0).getNationalIdType())
                .nationalId(sale.getRelatedParty().get(0).getNationalId()).build();

        com.tdp.ms.sales.model.dto.quotation.Customer customerQuotation = com.tdp.ms.sales.model.dto.quotation.Customer.builder()
                .creditScore(sale.getRelatedParty().get(0).getScore().getScore())
                .id(sale.getRelatedParty().get(0).getCustomerId())
                .name(sale.getRelatedParty().get(0).getFirstName())
                .surname(sale.getRelatedParty().get(0).getLastName())
                .subsegment(Commons.getStringValueByKeyFromAdditionalDataList(sale.getAdditionalData(),
                        "customerSubTypeCode"))
                .segment(Commons.getStringValueByKeyFromAdditionalDataList(sale.getAdditionalData(),
                        "customerTypeCode"))
                .legalId(legalId)
                .creditLimit(sale.getRelatedParty().get(0).getScore().getFinancingCapacity())
                .contactMedia(contactMediumList).build();

        Double simAmount = currentCommercialOperationType.getDeviceOffering().size() > 1
                ? currentCommercialOperationType.getDeviceOffering().get(1).getSimSpecifications()
                .get(0).getPrice().get(0).getValue().doubleValue() : 0.0;

        Number amountTotalAmount = currentCommercialOperationType.getDeviceOffering().get(0).getOffers().get(0)
                .getBillingOfferings().get(0).getCommitmentPeriods().get(0).getFinancingInstalments().get(0)
                .getInstalments().getTotalAmount().getValue().doubleValue() - simAmount;

        com.tdp.ms.sales.model.dto.quotation.MoneyAmount totalAmount =
                com.tdp.ms.sales.model.dto.quotation.MoneyAmount.builder().units("PEN")
                        .amount(amountTotalAmount.toString()).build();

        com.tdp.ms.sales.model.dto.quotation.MoneyAmount associatedPlanRecurrentCost =
                com.tdp.ms.sales.model.dto.quotation.MoneyAmount.builder().units("PEN").amount("0.00").build();

        com.tdp.ms.sales.model.dto.quotation.MoneyAmount totalCustomerRecurrentCost =
                com.tdp.ms.sales.model.dto.quotation.MoneyAmount.builder().units("PEN")
                .amount(currentCommercialOperationType.getProductOfferings().get(0).getProductOfferingPrice().get(0)
                        .getMaxPrice().getAmount() == null ? "0" : currentCommercialOperationType.getProductOfferings()
                        .get(0).getProductOfferingPrice().get(0).getMaxPrice().getAmount().toString())
                .build();

        com.tdp.ms.sales.model.dto.quotation.MoneyAmount downPayment =
                com.tdp.ms.sales.model.dto.quotation.MoneyAmount.builder()
                        .amount(currentCommercialOperationType.getDeviceOffering().get(0).getOffers()
                        .get(0).getBillingOfferings().get(0).getCommitmentPeriods().get(0)
                        .getFinancingInstalments().get(0).getInstalments().getOpeningQuota()
                        .getValue().toString())
                        .units("PEN").build();

        Site site = Site.builder().id(sale.getChannel().getStoreId()).build();

        Channel channel = Channel.builder().name(sale.getChannel().getId()).build();

        com.tdp.ms.sales.model.dto.quotation.MoneyAmount totalCost =
                com.tdp.ms.sales.model.dto.quotation.MoneyAmount.builder().units("PEN")
                        .amount(currentCommercialOperationType.getDeviceOffering().get(0).getOffers()
                        .get(0).getBillingOfferings().get(0).getCommitmentPeriods().get(0)
                        .getFinancingInstalments().get(0).getInstalments().getTotalAmount()
                        .getValue().toString()).build();

        double taxExcludedAmountDouble = currentCommercialOperationType.getDeviceOffering().get(0)
                .getOffers().get(0).getBillingOfferings().get(0).getCommitmentPeriods().get(0)
                .getFinancingInstalments().get(0).getInstalments().getTotalAmount().getValue()
                .doubleValue() / 1.18;
        com.tdp.ms.sales.model.dto.quotation.MoneyAmount taxExcludedAmount =
                com.tdp.ms.sales.model.dto.quotation.MoneyAmount.builder()
                        .amount(Double.toString(round(taxExcludedAmountDouble * 100.0) / 100.0)).units("PEN")
                        .build();

        List<com.tdp.ms.sales.model.dto.quotation.Item> itemsList = new ArrayList<>();
        com.tdp.ms.sales.model.dto.quotation.Item itemEquipment = com.tdp.ms.sales.model.dto.quotation.Item
                .builder().taxExcludedAmount(taxExcludedAmount).type("mobile phone")
                .offeringId("EQUIP_FE".concat(currentCommercialOperationType.getProduct().getPublicId()))
                .totalCost(totalCost)
                .orderActionId(org.apache.commons.lang3.StringUtils.chop(currentCommercialOperationType.getOrder()
                        .getProductOrderReferenceNumber()))
                .publicId(currentCommercialOperationType.getProduct().getPublicId()).build();
        itemsList.add(itemEquipment);

        if (flgCasi) { // currentCommercialOperationType.getDeviceOffering().size() == 2
            com.tdp.ms.sales.model.dto.quotation.MoneyAmount totalCostSim = MoneyAmount.builder()
                    .amount(currentCommercialOperationType.getDeviceOffering().get(1)
                            .getSimSpecifications().get(0).getPrice().get(0).getValue()
                            .toString())
                    .units("PEN").build();

            com.tdp.ms.sales.model.dto.quotation.Item itemSim = com.tdp.ms.sales.model.dto.quotation.Item
                    .builder()
                    .offeringId("SIM_FE".concat(currentCommercialOperationType.getProduct().getPublicId()))
                    .type("simcard")
                    .publicId(currentCommercialOperationType.getProduct().getPublicId())
                    .orderActionId(org.apache.commons.lang3.StringUtils
                            .chop(currentCommercialOperationType.getOrder()
                                    .getProductOrderReferenceNumber()))
                    .totalCost(totalCostSim).build();
            itemsList.add(itemSim);
        }

        String operationType = getOperationTypeForQuotationRequest(currentCommercialOperationType.getAdditionalData(),
                currentCommercialOperationType.getReason());

        CreateQuotationRequestBody body = CreateQuotationRequestBody.builder().items(itemsList)
                .billingAgreement(sale.getRelatedParty().get(0).getBillingArragmentId())
                .orderId("TEF" + String.format("%012d",
                        new BigInteger(currentCommercialOperationType.getOrder().getProductOrderId())))
                .accountId(sale.getRelatedParty().get(0).getAccountId()).commercialAgreement("N")
                .customer(customerQuotation).operationType(operationType).totalAmount(totalAmount)
                .downPayment(downPayment).totalCustomerRecurrentCost(totalCustomerRecurrentCost)
                .associatedPlanRecurrentCost(associatedPlanRecurrentCost).site(site).channel(channel)
                .financialEntity(currentCommercialOperationType.getDeviceOffering().get(0)
                        .getOffers().get(0).getBillingOfferings().get(0).getCommitmentPeriods()
                        .get(0).getFinancingInstalments().get(0).getCodigo())
                .build();

        if (operationType.equalsIgnoreCase(Constants.CAPL)) {
            createQuotationRequest.setBody(null);
        } else {
            createQuotationRequest.setBody(body);
        }
    }

    private String getOperationTypeForQuotationRequest(List<KeyValueType> additionalData, String reason) {
        String isCapl = additionalData.stream().filter(item -> item.getKey().equalsIgnoreCase(Constants.CAPL))
                .findFirst().orElse(KeyValueType.builder().value(Constants.STRING_FALSE).build()).getValue();

        String isCaeq = additionalData.stream().filter(item -> item.getKey().equalsIgnoreCase(Constants.CAEQ))
                .findFirst().orElse(KeyValueType.builder().value(Constants.STRING_FALSE).build()).getValue();

        return Boolean.parseBoolean(isCaeq) || (Boolean.parseBoolean(isCapl) && Boolean.parseBoolean(isCaeq))
                ? Constants.CAEQ
                : reason;
    }

    private void setQuotationResponseInSales(CreateQuotationResponse quotationResponse, Sale sale) {
        KeyValueType keyValueDateQuotation = KeyValueType.builder().key("financingDate")
                .value(Commons.getDatetimeNow()).build();
        sale.getAdditionalData().add(keyValueDateQuotation);
        KeyValueType keyValueAmountQuotation = KeyValueType.builder().key("amountPerInstalment")
                .value(quotationResponse.getAmountPerInstalment()).build();
        sale.getAdditionalData().add(keyValueAmountQuotation);
    }

    @Override
    public void addCaeqOderAttributes(List<FlexAttrType> caeqOrderAttributes, Sale saleRequest, boolean flgCasi,
                                      boolean isRetail) {
        // Adding CAEQ Attributes
        String documentTypeValue = "";

        documentTypeValue = Commons.getStringValueByKeyFromAdditionalDataList(
                saleRequest.getPaymenType().getAdditionalData(), "paymentDocument");
        if (!documentTypeValue.isEmpty()) {
            if (documentTypeValue.equalsIgnoreCase("Boleta")) {
                documentTypeValue = "BO";
            } else if (documentTypeValue.equalsIgnoreCase("Factura")) {
                documentTypeValue = "FA";
            }
            FlexAttrValueType deliveryAttrValue = FlexAttrValueType.builder().stringValue(documentTypeValue)
                    .valueType(Constants.STRING).build();
            FlexAttrType documentTypeAttr = FlexAttrType.builder().attrName("DOCUMENT_TYPE")
                    .flexAttrValue(deliveryAttrValue).build();
            caeqOrderAttributes.add(documentTypeAttr);
        }

        String customerRuc = saleRequest.getRelatedParty().size() < 2
                || StringUtils.isEmpty(saleRequest.getRelatedParty().get(1).getNationalId()) ? ""
                : saleRequest.getRelatedParty().get(1).getNationalId();

        if (!customerRuc.isEmpty()) {
            FlexAttrValueType paymentAttrValue = FlexAttrValueType.builder().stringValue(customerRuc)
                    .valueType(Constants.STRING).build();
            FlexAttrType customerRucAttr = FlexAttrType.builder().attrName("CUSTOMER_RUC")
                    .flexAttrValue(paymentAttrValue).build();
            caeqOrderAttributes.add(customerRucAttr);
        }

        // FEAT-2026 FEMS-3878: Creación de orden para CAEQ/CASI/CAPL O caeq /casi-
        // Flujo Retail
        this.casiAndRetailOrderAttributes(caeqOrderAttributes, saleRequest, flgCasi, isRetail);
    }

    private void casiAndRetailOrderAttributes(List<FlexAttrType> caeqOrderAttributes, Sale saleRequest,
                                              final boolean flgCasi, final boolean isRetail) {
        if (flgCasi && isRetail) {
            String deviceSkuValue = Commons.getStringValueByKeyFromAdditionalDataList(saleRequest.getAdditionalData(),
                    Constants.DEVICE_SKU);
            String simSkuValue = Commons.getStringValueByKeyFromAdditionalDataList(saleRequest.getAdditionalData(),
                    Constants.SIM_SKU);
            String cashierRegisterNumber = Commons.getStringValueByKeyFromAdditionalDataList(
                    saleRequest.getAdditionalData(), Constants.NUMERO_CAJA);

            FlexAttrValueType deviceSkuAttrValue = FlexAttrValueType.builder().stringValue(deviceSkuValue)
                    .valueType(Constants.STRING).build();
            FlexAttrType deviceSkuTypeAttr = FlexAttrType.builder().attrName(Constants.DEVICE_SKU)
                    .flexAttrValue(deviceSkuAttrValue).build();
            caeqOrderAttributes.add(deviceSkuTypeAttr);

            FlexAttrValueType simSkuAttrValue = FlexAttrValueType.builder().stringValue(simSkuValue)
                    .valueType(Constants.STRING).build();
            FlexAttrType simSkuTypeAttr = FlexAttrType.builder().attrName(Constants.SIM_SKU)
                    .flexAttrValue(simSkuAttrValue).build();
            caeqOrderAttributes.add(simSkuTypeAttr);

            FlexAttrValueType cachierRegisterNumberAttrValue = FlexAttrValueType.builder()
                    .stringValue(cashierRegisterNumber).valueType(Constants.STRING).build();
            FlexAttrType cashierRegisterNumberTypeAttr = FlexAttrType.builder()
                    .attrName("CASHIER_REGISTER_NUMBER")
                    .flexAttrValue(cachierRegisterNumberAttrValue).build();
            caeqOrderAttributes.add(cashierRegisterNumberTypeAttr);

            // En la HU dice: Para el caso de CAEQ= true, casi= true y capl=true /
            // caeq=true, casi=true y capl=false en
            // canal Retail, enviar como valor IS para el OrderAttribute de DELIVERY_METHOD
            FlexAttrValueType deliveryMethodAttrValue = FlexAttrValueType.builder().stringValue("IS")
                    .valueType(Constants.STRING).build();
            FlexAttrType deliveryMethodTypeAttr = FlexAttrType.builder().attrName(Constants.DELIVERY_METHOD)
                    .flexAttrValue(deliveryMethodAttrValue).build();
            caeqOrderAttributes.add(deliveryMethodTypeAttr);
        }
    }

    @Override
    public void validationToAddSimcardBonus(Sale sale, BusinessParametersResponseObjectExt bonificacionSimcardResponse,
                                             List<NewAssignedBillingOffers> altaNewBoList,
                                             CommercialOperationType currentCommercialOperationType) {
        if (currentCommercialOperationType.getDeviceOffering() != null) {
            // Simcard bonus conditional
            DeviceOffering deviceOfferingSimcard = currentCommercialOperationType.getDeviceOffering().stream()
                    .filter(item -> item.getDeviceType().equalsIgnoreCase(Constants.DEVICE_TYPE_SIM))
                    .findFirst().orElse(null);
            String deliveryMethod = Commons.getStringValueByKeyFromAdditionalDataList(sale.getAdditionalData(),
                    Constants.KEY_DELIVERY_METHOD);

            if (deviceOfferingSimcard != null && deliveryMethod.equalsIgnoreCase("SP")) {
                // FEMS-5081 new conditional only simcard and delivery NewAssignedBillingOffer SIM
                String productSpecPricingId = bonificacionSimcardResponse.getData().get(0).getValue();
                // Old "34572615", New "4442848" FEMS-5081
                String parentProductCatalogId = bonificacionSimcardResponse.getData().get(0).getExt()
                        .toString(); // Old "7431", New "7491" FEMS-5081

                NewAssignedBillingOffers altaNewBo2 = NewAssignedBillingOffers.builder()
                        .productSpecPricingId(productSpecPricingId)
                        .parentProductCatalogId(parentProductCatalogId).build();
                altaNewBoList.add(altaNewBo2);
            }
        }
    }
}
