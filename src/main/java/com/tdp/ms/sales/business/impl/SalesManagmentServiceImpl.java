package com.tdp.ms.sales.business.impl;

import com.tdp.genesis.core.exception.GenesisException;
import com.tdp.ms.sales.business.SalesManagmentService;
import com.tdp.ms.sales.client.BusinessParameterWebClient;
import com.tdp.ms.sales.client.PaymentWebClient;
import com.tdp.ms.sales.client.ProductOrderWebClient;
import com.tdp.ms.sales.client.StockWebClient;
import com.tdp.ms.sales.model.dto.BusinessParameterExt;
import com.tdp.ms.sales.model.dto.ContactMedium;
import com.tdp.ms.sales.model.dto.CreateProductOrderResponseType;
import com.tdp.ms.sales.model.dto.FinancingInstalment;
import com.tdp.ms.sales.model.dto.IdentityValidationType;
import com.tdp.ms.sales.model.dto.KeyValueType;
import com.tdp.ms.sales.model.dto.MoneyAmount;
import com.tdp.ms.sales.model.dto.SiteRefType;
import com.tdp.ms.sales.model.dto.productorder.CreateProductOrderGeneralRequest;
import com.tdp.ms.sales.model.dto.productorder.FlexAttrType;
import com.tdp.ms.sales.model.dto.productorder.FlexAttrValueType;
import com.tdp.ms.sales.model.dto.productorder.caeq.CaeqRequest;
import com.tdp.ms.sales.model.dto.productorder.caeq.ChangedCharacteristic;
import com.tdp.ms.sales.model.dto.productorder.caeq.ChangedContainedProduct;
import com.tdp.ms.sales.model.dto.productorder.caeq.NewProductCaeq;
import com.tdp.ms.sales.model.dto.productorder.caeq.ProductChangeCaeq;
import com.tdp.ms.sales.model.dto.productorder.caeq.ProductOrderCaeqRequest;
import com.tdp.ms.sales.model.dto.productorder.caeqcapl.CaeqCaplRequest;
import com.tdp.ms.sales.model.dto.productorder.caeqcapl.NewProductCaeqCapl;
import com.tdp.ms.sales.model.dto.productorder.caeqcapl.ProductChangeCaeqCapl;
import com.tdp.ms.sales.model.dto.productorder.caeqcapl.ProductOrderCaeqCaplRequest;
import com.tdp.ms.sales.model.dto.productorder.capl.CaplRequest;
import com.tdp.ms.sales.model.dto.productorder.capl.NewAssignedBillingOffers;
import com.tdp.ms.sales.model.dto.productorder.capl.NewProductCapl;
import com.tdp.ms.sales.model.dto.productorder.capl.ProductChangeCapl;
import com.tdp.ms.sales.model.dto.productorder.capl.ProductOrderCaplRequest;
import com.tdp.ms.sales.model.dto.productorder.capl.RemovedAssignedBillingOffers;
import com.tdp.ms.sales.model.dto.reservestock.Destination;
import com.tdp.ms.sales.model.dto.reservestock.Item;
import com.tdp.ms.sales.model.dto.reservestock.Order;
import com.tdp.ms.sales.model.dto.reservestock.StockItem;
import com.tdp.ms.sales.model.entity.Sale;
import com.tdp.ms.sales.model.request.GenerateCipRequest;
import com.tdp.ms.sales.model.request.GetSalesCharacteristicsRequest;
import com.tdp.ms.sales.model.request.PostSalesRequest;
import com.tdp.ms.sales.model.request.ReserveStockRequest;
import com.tdp.ms.sales.model.response.BusinessParametersResponse;
import com.tdp.ms.sales.model.response.GenerateCipResponse;
import com.tdp.ms.sales.model.response.GetSalesCharacteristicsResponse;
import com.tdp.ms.sales.repository.SalesRepository;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Class: SalesManagmentServiceImpl. <br/>
 * <b>Copyright</b>: &copy; 2019 Telef&oacute;nica del Per&uacute;<br/>
 * <b>Company</b>: Telef&oacute;nica del Per&uacute;<br/>
 *
 * @author Telef&oacute;nica del Per&uacute; (TDP) <br/>
 *         <u>Service Provider</u>: Everis Per&uacute; SAC (EVE) <br/>
 *         <u>Developed by</u>: <br/>
 *         <ul>
 *         <li>Cesar Gomez</li>
 *         </ul>
 *         <u>Changes</u>:<br/>
 *         <ul>
 *         <li>2020-09-24 Creaci&oacute;n del proyecto.</li>
 *         </ul>
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class SalesManagmentServiceImpl implements SalesManagmentService {

    @Autowired
    private SalesRepository salesRepository;

    @Autowired
    private BusinessParameterWebClient businessParameterWebClient;

    @Autowired
    private ProductOrderWebClient productOrderWebClient;

    @Autowired
    private StockWebClient stockWebClient;

    @Autowired
    private PaymentWebClient paymentWebClient;

    public List<BusinessParameterExt> retrieveCharacteristics(GetSalesCharacteristicsResponse response) {
        return response.getData().get(0).getExt();
    }

    public String retrieveDomain(List<ContactMedium> prospectContact) {
        // Get domain from email
        String email = prospectContact.stream()
                .filter(p -> p.getMediumType().equalsIgnoreCase("email"))
                .map(p -> p.getCharacteristic().getEmailAddress())
                .collect(Collectors.joining());

        if (email != null && !email.isEmpty()) {
            int pos = email.indexOf("@");
            return email.substring(++pos);
        }
        return null;
    }

    public String getStringValueByKeyFromAdditionalDataList(List<KeyValueType> additionalData, String key) {
        final String[] stringValue = {""};

        additionalData.stream().forEach(kv -> {
            if (kv.getKey().equalsIgnoreCase(key)) {
                stringValue[0] = kv.getValue();
            }
        });

        return stringValue[0];
    }

    @Override
    public Mono<Sale> post(PostSalesRequest request) {

        // Getting Sale object
        Sale saleRequest = request.getSale();

        // Getting token Mcss, request header to create product order service
        String tokenMcss = "";
        for (KeyValueType kv : saleRequest.getAdditionalData()) {
            if (kv.getKey().equals("ufxauthorization")) {
                tokenMcss = kv.getValue();
            }
        }
        if (tokenMcss == null || tokenMcss.equals("")) {
            return Mono.error(GenesisException
                    .builder()
                    .exceptionId("SVC1000")
                    .wildcards(new String[]{"Token MCSS is mandatory. Must be sent into Additional Data Property "
                            + "with 'ufxauthorization' key value."})
                    .build());
        }
        request.getHeadersMap().put("ufxauthorization", tokenMcss);

        // Get mail Validation, dominio de riesgo - SERGIO
        Mono<BusinessParametersResponse> getRiskDomain = businessParameterWebClient
                        .getRiskDomain(retrieveDomain(saleRequest.getProspectContact()), request.getHeadersMap());

        // Getting commons request properties
        String channelIdRequest = saleRequest.getChannel().getId();
        String customerIdRequest = saleRequest.getRelatedParty().get(0).getCustomerId();
        String productOfferingIdRequest = saleRequest.getCommercialOperation()
                .get(0).getProductOfferings().get(0).getId();

        // Getting Main CommercialTypeOperation value
        String commercialOperationType = saleRequest.getCommercialOperation().get(0).getReason();
        Mono<List<BusinessParameterExt>> salesCharsByCOT = businessParameterWebClient.getSalesCharacteristicsByCommercialOperationType(
                GetSalesCharacteristicsRequest
                        .builder()
                        .commercialOperationType(commercialOperationType)
                        .headersMap(request.getHeadersMap())
                        .build())
                .map(this::retrieveCharacteristics);

        return Mono.zip(getRiskDomain, salesCharsByCOT)
                .flatMap(tuple -> {
                    if (tuple.getT1().getData().get(0).getActive().equals("true")) {
                        // if it is a risk domain, cancel operation
                        return Mono.error(GenesisException
                                .builder()
                                .exceptionId("SVR1000")
                                .wildcards(new String[]{"Dominio de riesgo, se canceló la operación"})
                                .build());
                    }

                    // Generating CIP Code
                    if (saleRequest.getCommercialOperation().get(0).getWorkOrDeliveryType().getMediumDelivery().equalsIgnoreCase("DELIVERY")
                            && saleRequest.getPaymenType().getPaymentType().equalsIgnoreCase("EX")
                            && this.getStringValueByKeyFromAdditionalDataList(saleRequest.getAdditionalData(), "paymentTypeLabel").equals("PAGO EFECTIVO")
                    ) {
                        GenerateCipRequest generateCipRequest = new GenerateCipRequest();
                        generateCipRequest.setHeadersMap(request.getHeadersMap());
                        generateCipRequest = buildGenerateCipRequestFromSale(generateCipRequest, saleRequest);
                        Mono<GenerateCipResponse> generateCip = paymentWebClient.generateCip(generateCipRequest);
                    }

                    // Building Main Request to send to Create Product Order Service
                    CreateProductOrderGeneralRequest mainRequestProductOrder = new CreateProductOrderGeneralRequest();

                    // Getting Commercial Operation Types from Additional Data
                    Boolean flgCapl = false;
                    Boolean flgCaeq = false;
                    Boolean flgCasi = false;
                    for (KeyValueType kv : saleRequest.getCommercialOperation().get(0).getAdditionalData()) {
                        String stringKey = kv.getKey();
                        Boolean booleanValue = kv.getValue().equalsIgnoreCase("true");

                        if (stringKey.equalsIgnoreCase("CAPL")) {
                            flgCapl = booleanValue;
                        } else if (stringKey.equalsIgnoreCase("CAEQ")) {
                            flgCaeq = booleanValue;
                        } else if (stringKey.equalsIgnoreCase("CASI")) {
                            flgCasi = booleanValue;
                        }
                    }

                    // Recognizing CAPL Commercial Operation Type
                    if (flgCapl && !flgCaeq && !flgCasi) {

                        mainRequestProductOrder = this.caplCommercialOperation(saleRequest,
                                mainRequestProductOrder, channelIdRequest, customerIdRequest, productOfferingIdRequest);

                    } else if (!flgCapl && flgCaeq && !flgCasi) { // Recognizing CAEQ Commercial Operation Type

                        mainRequestProductOrder = this.caeqCommercialOperation(saleRequest,
                                mainRequestProductOrder, channelIdRequest, customerIdRequest, productOfferingIdRequest);

                    } else if (flgCapl && flgCaeq && !flgCasi) { // Recognizing CAEQ+CAPL Commercial Operation Type

                        mainRequestProductOrder = this.caeqCaplCommercialOperation(saleRequest,
                                mainRequestProductOrder, channelIdRequest, customerIdRequest, productOfferingIdRequest);
                    }

                    return productOrderWebClient.createProductOrder(mainRequestProductOrder, request.getHeadersMap())
                            .flatMap(createOrderResponse -> {

                                return salesRepository.findBySalesId(saleRequest.getSalesId())
                                        .flatMap(saleFinded -> {

                                            // Adding Order info to sales
                                            saleFinded.getCommercialOperation().get(0)
                                                    .setOrder(createOrderResponse.getCreateProductOrderResponse());

                                            if (validateNegotiation(saleRequest.getAdditionalData(),
                                                    saleRequest.getIdentityValidations())) {
                                                saleFinded.setStatus("NEGOCIACION");
                                            } else {
                                                saleFinded.setStatus("NUEVO");
                                            }

                                            // Ship Delivery logic (tambo) - SERGIO
                                            if (saleRequest.getCommercialOperation().get(0).getWorkOrDeliveryType().getMediumDelivery().equalsIgnoreCase("Tienda")) {

                                                saleFinded.setAdditionalData(additionalDataAssigments(saleRequest.getAdditionalData()));
                                            }

                                            ReserveStockRequest reserveStockRequest = new ReserveStockRequest();
                                            reserveStockRequest = this.buildReserveStockRequest(reserveStockRequest,
                                                    saleRequest, createOrderResponse.getCreateProductOrderResponse());

                                            return stockWebClient.reserveStock(reserveStockRequest,
                                                    request.getHeadersMap())
                                                    .flatMap(reserveStockResponse -> {
                                                        DateFormat dateFormat = new SimpleDateFormat(
                                                                "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSZ");
                                                        LocalDateTime nowDateTime = LocalDateTime.now();

                                                        KeyValueType dateKv = KeyValueType
                                                                .builder()
                                                                .key("reservationDate")
                                                                .value(dateFormat.format(nowDateTime))
                                                                .build();
                                                        saleFinded.getCommercialOperation().get(0).getDeviceOffering()
                                                                .get(0).getAdditionalData().add(dateKv);


                                                        saleFinded.getCommercialOperation().get(0).getDeviceOffering()
                                                                .get(0).getStock()
                                                                .setReservationId(reserveStockResponse.getId());


                                                        saleFinded.getCommercialOperation().get(0).getDeviceOffering()
                                                                .get(0).getStock()
                                                                .setAmount(reserveStockResponse.getItems()
                                                                        .get(0).getAmount());


                                                        saleFinded.getCommercialOperation().get(0).getDeviceOffering()
                                                                .get(0).getStock()
                                                                .setSite(reserveStockResponse.getItems()
                                                                        .get(0).getSite());

                                                        return salesRepository.save(saleFinded);
                                                    });

                                        });
                            });
                });
    }

    public List<KeyValueType> additionalDataAssigments(List<KeyValueType> input) {
        // add shipmentDetails structure to additionalData
        List<KeyValueType> additionalDataAux = input;
        if (additionalDataAux == null) {
            additionalDataAux = new ArrayList<>();
        }
        // assignments
        KeyValueType mediumDeliveryLabel = KeyValueType.builder()
                .key("mediumDeliveryLabel").value("Chip Tienda").build();
        KeyValueType collectStoreId = KeyValueType.builder()
                .key("collectStoreId").value("Validar campo").build();
        KeyValueType shipmentAddressId = KeyValueType.builder()
                .key("shipmentAddressId").value("").build();
        KeyValueType shipmentSiteId = KeyValueType.builder()
                .key("shipmentSiteId").value("NA").build();
        KeyValueType shippingLocality = KeyValueType.builder()
                .key("shippingLocality").value("Pendiente").build();
        KeyValueType provinceOfShippingAddress = KeyValueType.builder()
                .key("provinceOfShippingAddress").value("Pendiente").build();
        KeyValueType shopAddress = KeyValueType.builder()
                .key("shopAddress").value("Pendiente").build();
        KeyValueType shipmentInstructions = KeyValueType.builder()
                .key("shipmentInstructions").value("No se registró instrucciones").build();
        additionalDataAux.add(mediumDeliveryLabel);
        additionalDataAux.add(collectStoreId);
        additionalDataAux.add(shipmentAddressId);
        additionalDataAux.add(shipmentSiteId);
        additionalDataAux.add(shippingLocality);
        additionalDataAux.add(provinceOfShippingAddress);
        additionalDataAux.add(shopAddress);
        additionalDataAux.add(shipmentInstructions);

        return additionalDataAux;
    }

    public Boolean validateNegotiation(List<KeyValueType> additionalData,
                                       List<IdentityValidationType> identityValidationTypes) {
        final Boolean[] isPresencial = {false};
        final Boolean[] isBiometric = {true};

        additionalData.stream().forEach(kv -> {
            if (kv.getKey().equalsIgnoreCase("flowSale")
                    && kv.getValue().equalsIgnoreCase("Presencial")) {
                isPresencial[0] = true;
            }
        });

        // Sort identityValidationType by date field
        final Date[] latestDate = {null};
        final int[] cont = {0};
        identityValidationTypes.stream().forEach(ivt -> {
            // convert String date to Date
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSZ");
            try {
                Date currentDate = format.parse(ivt.getDate());
                if (latestDate[0] == null || latestDate[0].before(currentDate)) {
                    latestDate[0] = currentDate;
                    cont[0]++;
                }
            } catch (ParseException ex) {
                System.out.println(ex);
            }

        });

        // validate validationType
        if (!identityValidationTypes.get(cont[0]).getValidationType().equalsIgnoreCase("Biometric")) {
            isBiometric[0] = false;
        }

        return isPresencial[0] && !isBiometric[0];
    }

    public CreateProductOrderGeneralRequest caplCommercialOperation(Sale saleRequest,
                                    CreateProductOrderGeneralRequest mainRequestProductOrder, String channelIdRequest,
                                    String customerIdRequest, String productOfferingIdRequest) {

        // Building request for CAPL CommercialTypeOperation

        ProductOrderCaplRequest caplRequestProductOrder = new ProductOrderCaplRequest();
        caplRequestProductOrder.setSalesChannel(channelIdRequest);
        caplRequestProductOrder.setCustomerId(customerIdRequest);
        caplRequestProductOrder.setProductOfferingId(productOfferingIdRequest);
        caplRequestProductOrder.setOnlyValidationIndicator(false);

        // Recognizing Capl into same plan or Capl with new plan
        Boolean flgOnlyCapl = true;
        RemovedAssignedBillingOffers caplBoRemoved1 = new RemovedAssignedBillingOffers();
        List<RemovedAssignedBillingOffers> caplBoRemovedList = new ArrayList<>();
        if (saleRequest.getCommercialOperation().get(0).getProduct().getProductSpec().getId() == null
                || saleRequest.getCommercialOperation().get(0).getProduct().getProductSpec().getId().equals("")
        ) {
            flgOnlyCapl = false;
            caplRequestProductOrder.setActionType("CH");
        } else {
            // Recognizing Capl Fija
            String productType = saleRequest.getProductType();
            if (productType.equals("landline") || productType.equals("cableTv")
                    || productType.equals("broadband") || productType.equals("bundle")) {
                caplRequestProductOrder.setActionType("CH");
            } else {
                caplRequestProductOrder.setActionType("CW");
            }

            caplBoRemoved1.setProductSpecPricingId(saleRequest.getCommercialOperation().get(0)
                    .getProduct().getProductSpec().getId());
            caplBoRemovedList.add(caplBoRemoved1);
        }

        NewAssignedBillingOffers caplNewBo1 = NewAssignedBillingOffers
                .builder()
                .productSpecPricingId(saleRequest.getCommercialOperation().get(0)
                        .getProductOfferings().get(0).getProductOfferingProductSpecId())
                .parentProductCatalogId("7491")
                .build();
        List<NewAssignedBillingOffers> caplNewBoList = new ArrayList<>();
        caplNewBoList.add(caplNewBo1);

        // Setting RemoveAssignedBillingOffers if commercial operation type is Capl into same plan
        ProductChangeCapl caplProductChanges = new ProductChangeCapl();
        caplProductChanges.setNewAssignedBillingOffers(caplNewBoList);

        NewProductCapl newProductCapl1 = new NewProductCapl();
        newProductCapl1.setProductId(saleRequest.getCommercialOperation().get(0).getProduct().getId());
        if (flgOnlyCapl) {
            caplProductChanges.setRemovedAssignedBillingOffers(caplBoRemovedList);
        } else {
            newProductCapl1.setProductCatalogId(saleRequest.getCommercialOperation().get(0)
                    .getProductOfferings().get(0).getProductOfferingProductSpecId()); // Consultar si el id del catalogo es = al id del nuevo plan
        }
        newProductCapl1.setProductChanges(caplProductChanges);

        // Refactored Code from CAPL
        List<FlexAttrType> caplOrderAttributes = this.commonOrderAttributes(saleRequest);

        List<NewProductCapl> caplNewProductsList = new ArrayList<>();
        caplNewProductsList.add(newProductCapl1);

        CaplRequest caplRequest = CaplRequest
                .builder()
                .newProducts(caplNewProductsList)
                .sourceApp("FE")
                .orderAttributes(caplOrderAttributes)
                .build();

        // Building Main Capl Request
        caplRequestProductOrder.setRequest(caplRequest);

        // Setting capl request into main request to send to create product order service
        mainRequestProductOrder.setCreateProductOrderRequest(caplRequestProductOrder);

        return mainRequestProductOrder;
    }

    public CreateProductOrderGeneralRequest caeqCommercialOperation(Sale saleRequest,
                                    CreateProductOrderGeneralRequest mainRequestProductOrder, String channelIdRequest,
                                    String customerIdRequest, String productOfferingIdRequest) {
        // Building request for CAEQ CommercialTypeOperation

        // Refactored Code from CAEQ
        List<ChangedContainedProduct> changedContainedProductList = this.changedContainedCaeqList(saleRequest);

        ProductChangeCaeq productChangeCaeq = ProductChangeCaeq
                .builder()
                .changedContainedProducts(changedContainedProductList)
                .build();

        NewProductCaeq newProductCaeq1 = NewProductCaeq
                .builder()
                .productId(saleRequest.getCommercialOperation().get(0).getProduct().getId()) // Consultar porque hay 2 product ids
                .productChanges(productChangeCaeq)
                .build();
        List<NewProductCaeq> newProductCaeqList = new ArrayList<>();
        newProductCaeqList.add(newProductCaeq1);

        CaeqRequest caeqRequest = CaeqRequest
                .builder()
                .sourceApp("FE")
                .newProducts(newProductCaeqList)
                .build();

        ProductOrderCaeqRequest caeqProductOrderRequest = new ProductOrderCaeqRequest();
        caeqProductOrderRequest.setSalesChannel(channelIdRequest);
        caeqProductOrderRequest.setCustomerId(customerIdRequest);
        caeqProductOrderRequest.setProductOfferingId(productOfferingIdRequest);
        caeqProductOrderRequest.setOnlyValidationIndicator(false);
        caeqProductOrderRequest.setActionType("CW");
        caeqProductOrderRequest.setRequest(caeqRequest);

        // Setting capl request into main request to send to create product order service
        mainRequestProductOrder.setCreateProductOrderRequest(caeqProductOrderRequest);

        return mainRequestProductOrder;
    }

    public CreateProductOrderGeneralRequest caeqCaplCommercialOperation(Sale saleRequest,
                                    CreateProductOrderGeneralRequest mainRequestProductOrder, String channelIdRequest,
                                    String customerIdRequest, String productOfferingIdRequest) {
        // Building request for CAEQ+CAPL CommercialTypeOperation

        // Code from CAPL
        ProductOrderCaeqCaplRequest caeqCaplRequestProductOrder = new ProductOrderCaeqCaplRequest();
        caeqCaplRequestProductOrder.setSalesChannel(channelIdRequest);
        caeqCaplRequestProductOrder.setCustomerId(customerIdRequest);
        caeqCaplRequestProductOrder.setProductOfferingId(productOfferingIdRequest);
        caeqCaplRequestProductOrder.setOnlyValidationIndicator(false);

        // Recognizing Capl into same plan or Capl with new plan
        Boolean flgOnlyCapl = true;
        RemovedAssignedBillingOffers caeqCaplBoRemoved1 = new RemovedAssignedBillingOffers();
        List<RemovedAssignedBillingOffers> caeqCaplBoRemovedList = new ArrayList<>();
        if (saleRequest.getCommercialOperation().get(0).getProduct().getProductSpec().getId() == null
                || saleRequest.getCommercialOperation().get(0).getProduct().getProductSpec().getId().equals("")
        ) {
            flgOnlyCapl = false;
            caeqCaplRequestProductOrder.setActionType("CH");
        } else {
            // Recognizing Capl Fija
            String productType = saleRequest.getProductType();
            if (productType.equals("landline") || productType.equals("cableTv") || productType.equals("broadband")
                    || productType.equals("bundle")) {
                caeqCaplRequestProductOrder.setActionType("CH");
            } else {
                caeqCaplRequestProductOrder.setActionType("CW");
            }

            caeqCaplBoRemoved1.setProductSpecPricingId(saleRequest.getCommercialOperation().get(0)
                    .getProduct().getProductSpec().getId());
            caeqCaplBoRemovedList.add(caeqCaplBoRemoved1);
        }

        NewAssignedBillingOffers caplNewBo1 = NewAssignedBillingOffers
                .builder()
                .productSpecPricingId(saleRequest.getCommercialOperation().get(0)
                        .getProductOfferings().get(0).getProductOfferingProductSpecId())
                .parentProductCatalogId("7491")
                .build();
        List<NewAssignedBillingOffers> caeqCaplNewBoList = new ArrayList<>();
        caeqCaplNewBoList.add(caplNewBo1);

        // Setting RemoveAssignedBillingOffers if commercial operation type is Capl into same plan
        ProductChangeCaeqCapl caeqCaplProductChanges = new ProductChangeCaeqCapl();
        caeqCaplProductChanges.setNewAssignedBillingOffers(caeqCaplNewBoList);

        NewProductCaeqCapl newProductCaeqCapl1 = new NewProductCaeqCapl();
        newProductCaeqCapl1.setProductId(saleRequest.getCommercialOperation().get(0).getProduct().getId());
        if (flgOnlyCapl) {
            caeqCaplProductChanges.setRemovedAssignedBillingOffers(caeqCaplBoRemovedList);
        } else {
            newProductCaeqCapl1.setProductCatalogId(saleRequest.getCommercialOperation().get(0)
                    .getProductOfferings().get(0).getProductOfferingProductSpecId());
        }

        // Refactored Code from CAEQ
        List<ChangedContainedProduct> changedContainedProductList = this.changedContainedCaeqList(saleRequest);

        caeqCaplProductChanges.setChangedContainedProducts(changedContainedProductList);
        newProductCaeqCapl1.setProductChanges(caeqCaplProductChanges);

        List<NewProductCaeqCapl> caeqCaplNewProductList = new ArrayList<>();
        caeqCaplNewProductList.add(newProductCaeqCapl1);

        // Refactored Code from CAPL
        List<FlexAttrType> caeqCaplOrderAttributes = this.commonOrderAttributes(saleRequest);

        CaeqCaplRequest caeqCaplRequest = CaeqCaplRequest
                .builder()
                .newProducts(caeqCaplNewProductList)
                .sourceApp("FE")
                .orderAttributes(caeqCaplOrderAttributes)
                .build();

        caeqCaplRequestProductOrder.setRequest(caeqCaplRequest);

        // Setting capl request into main request to send to create product order service
        mainRequestProductOrder.setCreateProductOrderRequest(caeqCaplRequestProductOrder);

        return mainRequestProductOrder;
    }

    public List<FlexAttrType> commonOrderAttributes(Sale saleRequest) {
        // Building Attributes
        String deliveryCode = "";
        for (KeyValueType kv : saleRequest.getAdditionalData()) {
            if (kv.getKey().equals("deliveryMethod")) {
                deliveryCode = kv.getValue();
            }
        }
        FlexAttrValueType deliveryAttrValue =  FlexAttrValueType
                .builder()
                .stringValue(deliveryCode)
                .valueType("STRING")
                .build();
        FlexAttrType deliveryAttr = FlexAttrType
                .builder()
                .attrName("DELIVERY_METHOD")
                .flexAttrValue(deliveryAttrValue)
                .build();

        FlexAttrValueType paymentAttrValue =  FlexAttrValueType
                .builder()
                .stringValue(saleRequest.getPaymenType().getPaymentType())
                .valueType("STRING")
                .build();
        FlexAttrType paymentAttr = FlexAttrType
                .builder()
                .attrName("PAYMENT_METHOD")
                .flexAttrValue(paymentAttrValue)
                .build();

        List<FlexAttrType> caplOrderAttributes = new ArrayList<>();
        caplOrderAttributes.add(deliveryAttr);
        caplOrderAttributes.add(paymentAttr);

        return caplOrderAttributes;
    }

    public List<ChangedContainedProduct> changedContainedCaeqList(Sale saleRequest) {
        String acquisitionType = "";
        acquisitionType = getAcquisitionTypeValue(saleRequest);
        ChangedCharacteristic changedCharacteristic1 = ChangedCharacteristic
                .builder()
                .characteristicId("9941")
                .characteristicValue(acquisitionType)
                .build();

        ChangedCharacteristic changedCharacteristic2 = ChangedCharacteristic
                .builder()
                .characteristicId("15734")
                .characteristicValue(saleRequest.getCommercialOperation().get(0).getDeviceOffering().get(0).getId()) // Consultar si esta caracteristica se agrega solamente cuando el device_type es simcard
                .build();

        ChangedCharacteristic changedCharacteristic3 = ChangedCharacteristic
                .builder()
                .characteristicId("9871")
                .characteristicValue("000000000000000") // IMEI, Consultar si se debe enviar valor real cuando viene de dealer
                .build();

        ChangedCharacteristic changedCharacteristic4 = ChangedCharacteristic
                .builder()
                .characteristicId("16524") // SIMGROUP, Pendiente revisar con Abraham y Ivonne, otro código 9871
                .characteristicValue("Estandar")
                .build();

        List<ChangedCharacteristic> changedCharacteristicList = new ArrayList<>();
        changedCharacteristicList.add(changedCharacteristic1);
        changedCharacteristicList.add(changedCharacteristic2);
        changedCharacteristicList.add(changedCharacteristic3);
        changedCharacteristicList.add(changedCharacteristic4);

        ChangedContainedProduct changedContainedProduct1 = ChangedContainedProduct
                .builder()
                .productId(saleRequest.getCommercialOperation().get(0).getProduct().getId()) // Consultar porque hay 2 product ids
                .temporaryId("temp1")
                .productCatalogId(saleRequest.getCommercialOperation().get(0)
                        .getProductOfferings().get(0).getProductOfferingProductSpecId()) // Consultar
                .changedCharacteristics(changedCharacteristicList)
                .build();

        List<ChangedContainedProduct> changedContainedProductList = new ArrayList<>();
        changedContainedProductList.add(changedContainedProduct1);

        return changedContainedProductList;
    }

    public String getAcquisitionTypeValue(Sale saleRequest) {
        String acquisitionType = "";
        String saleChannelId = "";
        String deliveryType = "";

        // Getting Sale Channel
        saleChannelId = saleRequest.getChannel().getId();

        // Getting Delivery Method (IS, SP)
        for (KeyValueType kv : saleRequest.getAdditionalData()) {
            if (kv.getKey().equals("deliveryMethod")) {
                deliveryType = kv.getValue();
            }
        }

        // Logic for Set Acquisition Type Value
        if (saleChannelId.equalsIgnoreCase("CC") && deliveryType.equalsIgnoreCase("SP")
                || saleChannelId.equalsIgnoreCase("CEC")
                || (saleChannelId.equalsIgnoreCase("ST") && deliveryType.equalsIgnoreCase("SP"))
                || saleChannelId.equalsIgnoreCase("DLS")
        ) {
            acquisitionType = "ConsessionPurchased";
        } else if ((saleChannelId.equalsIgnoreCase("ST") && deliveryType.equalsIgnoreCase("IS"))
                || saleChannelId.equalsIgnoreCase("DLV")
        ) {
            acquisitionType = "Sale";
        } else if (saleChannelId.equalsIgnoreCase("DLC")) {
            acquisitionType = "Consignation";
        } else {
            acquisitionType = "Private";
        }

        return acquisitionType;
    }

    public ReserveStockRequest buildReserveStockRequest(ReserveStockRequest request, Sale sale, CreateProductOrderResponseType createOrderResponse) {
        request.setReason("PRAEL");

        List<String> requiredActionList =  new ArrayList<>();
        requiredActionList.add("PR");
        request.setRequiredActions(requiredActionList);

        List<String> usageList =  new ArrayList<>();
        usageList.add("sale");
        request.setUsage(usageList);

        SiteRefType site = SiteRefType
                .builder()
                .id(sale.getChannel().getStoreId())
                .build();
        Destination destination = Destination
                .builder()
                .site(site)
                .type("store")
                .build();
        request.setDestination(destination);

        request.setChannel(sale.getChannel().getId());

        Item item = Item
                .builder()
                .id(sale.getCommercialOperation().get(0).getDeviceOffering().get(0).getSapid())
                .type("IMEI")
                .build();
        StockItem stockItem1 = StockItem
                .builder()
                .item(item)
                .build();
        List<StockItem> itemsList =  new ArrayList<>();
        itemsList.add(stockItem1);
        request.setItems(itemsList);

        request.setOrderAction(createOrderResponse.getProductOrderReferenceNumber());

        Order order = Order
                .builder()
                .id(createOrderResponse.getProductOrderId())
                .build();
        request.setOrder(order);

        return  request;
    }

    public GenerateCipRequest buildGenerateCipRequestFromSale(GenerateCipRequest request, Sale sale) {
        FinancingInstalment financingInstalment = sale.getCommercialOperation().get(0).getDeviceOffering().get(0).getOffers()
                .get(0).getBillingOfferings().get(0).getCommitmentPeriods().get(0).getFinancingInstalments().get(0);
        MoneyAmount montoCip;
        // Identificando cliente al contado
        if (financingInstalment.getDescription().equalsIgnoreCase("CONTADO")) {
            montoCip = financingInstalment.getInstalments().getAmount();
        } else {
            montoCip = financingInstalment.getInstalments().getOpeningQuota();
        }

        request.getBody().setAmount(montoCip.getValue());
        request.getBody().setCurrency(montoCip.getCurrency());

        return request;
    }

}
