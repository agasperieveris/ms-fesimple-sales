package com.tdp.ms.sales.business.impl;

import com.tdp.ms.sales.business.SalesManagmentService;
import com.tdp.ms.sales.client.BusinessParameterWebClient;
import com.tdp.ms.sales.client.WebClientBusinessParameters;
import com.tdp.ms.sales.model.dto.BusinessParameterExt;
import com.tdp.ms.sales.model.dto.KeyValueType;
import com.tdp.ms.sales.model.dto.productorder.CreateProductOrderGeneralRequest;
import com.tdp.ms.sales.model.dto.productorder.FlexAttrType;
import com.tdp.ms.sales.model.dto.productorder.FlexAttrValueType;
import com.tdp.ms.sales.model.dto.productorder.capl.ProductOrderCaplRequest;
import com.tdp.ms.sales.model.dto.productorder.caeq.ChangedCharacteristic;
import com.tdp.ms.sales.model.dto.productorder.caeq.ChangedContainedProduct;
import com.tdp.ms.sales.model.dto.productorder.caeq.NewProductCaeq;
import com.tdp.ms.sales.model.dto.productorder.caeq.ProductChangeCaeq;
import com.tdp.ms.sales.model.dto.productorder.caeq.CaeqRequest;
import com.tdp.ms.sales.model.dto.productorder.caeqcapl.NewProductCaeqCapl;
import com.tdp.ms.sales.model.dto.productorder.caeqcapl.ProductChangeCaeqCapl;
import com.tdp.ms.sales.model.dto.productorder.caeqcapl.CaeqCaplRequest;
import com.tdp.ms.sales.model.dto.productorder.capl.NewAssignedBillingOffers;
import com.tdp.ms.sales.model.dto.productorder.capl.NewProductCapl;
import com.tdp.ms.sales.model.dto.productorder.capl.ProductChangeCapl;
import com.tdp.ms.sales.model.dto.productorder.capl.CaplRequest;
import com.tdp.ms.sales.model.dto.productorder.capl.RemovedAssignedBillingOffers;
import com.tdp.ms.sales.model.entity.Sale;
import com.tdp.ms.sales.model.request.GetSalesCharacteristicsRequest;
import com.tdp.ms.sales.model.request.PostSalesRequest;
import com.tdp.ms.sales.model.request.ProductOrderRequest;
import com.tdp.ms.sales.model.response.BusinessParametersResponse;
import com.tdp.ms.sales.model.response.GetSalesCharacteristicsResponse;
import com.tdp.ms.sales.model.response.SalesResponse;
import com.tdp.ms.sales.repository.SalesRepository;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
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

    private final WebClientBusinessParameters webClient;

    private List<BusinessParameterExt> retrieveCharacteristics(GetSalesCharacteristicsResponse response) {
        return response.getData().get(0).getExt();
    }

    @Override
    public Mono<SalesResponse> post(PostSalesRequest request) {
        // Getting Sale object
        Sale saleRequest = request.getSale();

        // Getting commons request properties
        String channelIdRequest = saleRequest.getChannel().getId();
        String customerIdRequest = saleRequest.getRelatedParty().get(0).getCustomerId();
        String productOfferingIdRequest = saleRequest.getCommercialOperation().get(0).getProductOfferings().get(0).getId();

        // Getting Main CommercialTypeOperation value
        String commercialOperationType = saleRequest.getCommercialOperation().get(0).getReason();

        return businessParameterWebClient.getSalesCharacteristicsByCommercialOperationType(
                GetSalesCharacteristicsRequest
                        .builder()
                        .commercialOperationType(commercialOperationType)
                        .headersMap(request.getHeadersMap())
                        .build())
                .map(this::retrieveCharacteristics)
                .flatMap(salesCharacteristicsList -> {

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
                            if (productType.equals("landline") || productType.equals("cableTv") || productType.equals("broadband") || productType.equals("bundle") || productType.equals("mobile")) {
                                caplRequestProductOrder.setActionType("CH");
                            } else {
                                caplRequestProductOrder.setActionType("CW");
                            }

                            caplBoRemoved1.setProductSpecPricingId(saleRequest.getCommercialOperation().get(0).getProduct().getProductSpec().getId());
                            caplBoRemovedList.add(caplBoRemoved1);
                        }

                        NewAssignedBillingOffers caplNewBo1 = NewAssignedBillingOffers
                                .builder()
                                .productSpecPricingId(saleRequest.getCommercialOperation().get(0).getProductOfferings().get(0).getProductOfferingProductSpecId())
                                .parentProductCatalogId("7491")
                                .build();
                        List<NewAssignedBillingOffers> caplNewBoList = new ArrayList<>();
                        caplNewBoList.add(caplNewBo1);

                        // Setting RemoveAssignedBillingOffers if commercial operation type is Capl into same plan
                        ProductChangeCapl caplProductChanges = new ProductChangeCapl();
                        caplProductChanges.setNewAssignedBillingOffers(caplNewBoList);

                        NewProductCapl newProductCapl1 = new NewProductCapl();
                        newProductCapl1.setProductId(saleRequest.getCommercialOperation().get(0).getProduct().getId());
                        newProductCapl1.setProductChanges(caplProductChanges);
                        if (flgOnlyCapl) {
                            caplProductChanges.setRemovedAssignedBillingOffers(caplBoRemovedList);
                        } else {
                            newProductCapl1.setProductCatalogId(saleRequest.getCommercialOperation().get(0).getProductOfferings().get(0).getProductOfferingProductSpecId());
                        }

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
                        CreateProductOrderGeneralRequest mainCaplRequestProductOrder = CreateProductOrderGeneralRequest
                                .builder()
                                .createProductOrderRequest(caplRequestProductOrder)
                                .build();

                        // CALL TO CREATE PRODUCT ORDER SERVICE

                    } else if (!flgCapl && flgCaeq && !flgCasi) { // Recognizing CAEQ Commercial Operation Type
                        // Building request for CAEQ CommercialTypeOperation

                        ChangedCharacteristic changedCharacteristic1 = ChangedCharacteristic
                                .builder()
                                .characteristicId("9941")
                                .characteristicValue("Private")
                                .build();

                        List<ChangedCharacteristic> changedCharacteristicList = new ArrayList<>();
                        changedCharacteristicList.add(changedCharacteristic1);

                        ChangedContainedProduct changedContainedProduct = ChangedContainedProduct
                                .builder()
                                .productID("8091614432")
                                .temporaryId("temp1")
                                .productCatalogId("7411")
                                .changedCharacteristics(changedCharacteristicList)
                                .build();

                        ProductChangeCaeq productChangeCaeq = ProductChangeCaeq
                                .builder()
                                .changedContainedProducts(changedContainedProduct)
                                .build();

                        NewProductCaeq newProductCaeq = NewProductCaeq
                                .builder()
                                .productID("8091614409")
                                .productChanges(productChangeCaeq)
                                .build();

                        CaeqRequest caeqRequest = CaeqRequest
                                .builder()
                                .productOfferingID("3232618")
                                .newProducts(newProductCaeq)
                                .build();

                        // CALL TO CREATE PRODUCT ORDER SERVICE
                        ProductOrderRequest createProductOrderRequest = new ProductOrderRequest();
                        createProductOrderRequest.setActionType("CW");

                    } else if (flgCapl && flgCaeq && !flgCasi) { // Recognizing CAEQ+CAPL Commercial Operation Type
                        // Building request for CAEQ+CAPL CommercialTypeOperation

                        RemovedAssignedBillingOffers caplBoRemoved = RemovedAssignedBillingOffers
                                .builder()
                                .productSpecPricingId("2253558")
                                .build();

                        NewAssignedBillingOffers caplNewBo = NewAssignedBillingOffers
                                .builder()
                                .productSpecPricingId("8091631427")
                                .parentProductCatalogId("7491")
                                .build();

                        ChangedCharacteristic changedCharacteristic1 = ChangedCharacteristic
                                .builder()
                                .characteristicId("9941")
                                .characteristicValue("Private")
                                .build();

                        List<ChangedCharacteristic> changedCharacteristicList = new ArrayList<>();
                        changedCharacteristicList.add(changedCharacteristic1);

                        ChangedContainedProduct changedContainedProduct = ChangedContainedProduct
                                .builder()
                                .productID("8091614432")
                                .temporaryId("temp1")
                                .productCatalogId("7411")
                                .changedCharacteristics(changedCharacteristicList)
                                .build();

                        ProductChangeCaeqCapl productChangeCaeqCapl = ProductChangeCaeqCapl
                                .builder()
                                .changedContainedProducts(changedContainedProduct)
                                .newAssignedBillingOffers(caplNewBo)
                                .removedAssignedBillingOffers(caplBoRemoved)
                                .build();

                        NewProductCaeqCapl newProductCaeqCapl = NewProductCaeqCapl
                                .builder()
                                .productID("8091614409")
                                .productChanges(productChangeCaeqCapl)
                                .build();

                        CaeqCaplRequest caeqCaplRequest = CaeqCaplRequest
                                .builder()
                                .productOfferingID("3232618")
                                .newProducts(newProductCaeqCapl)
                                .build();

                        // CALL TO CREATE PRODUCT ORDER SERVICE
                        ProductOrderRequest createProductOrderRequest = new ProductOrderRequest();
                        createProductOrderRequest.setActionType("CW");
                    }




                    // START - POST SALES LEAD CODE
                    String uuid = UUID.randomUUID().toString();
                    while (salesRepository.existsById(uuid) == Mono.just(true)) {
                        uuid = UUID.randomUUID().toString();
                    }
                    saleRequest.setId(uuid);


                    // Se obtiene el secuencial de businessParameters
                    Mono<BusinessParametersResponse> saleSequential = webClient.getNewSaleSequential("SEQ001", request.getHeadersMap());

                    return saleSequential.flatMap(saleSequentialItem -> {
                        saleRequest.setSalesId(saleSequentialItem.getData().get(0).getValue());

                        // asignar fecha de creación
                        Date todayDate = Calendar.getInstance().getTime();
                        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy'T'HH:mm:ss");
                        String todayDateString = dateFormatter.format(todayDate);
                        saleRequest.setSaleCreationDate(todayDateString);

                        return salesRepository.save(saleRequest)
                                .flatMap(saleItem -> {
                                    String salesId;

                                    salesId = String.valueOf(saleItem.getSalesId());
                                    int len = salesId.length();
                                    if (len < 9) {
                                        for (int i = 0; i < 9 - len; i++) {
                                            salesId = "0" + salesId;
                                        }
                                    }

                                    SalesResponse salesResponse = SalesResponse
                                            .builder()
                                            .id(saleItem.getId())
                                            .salesId("FE-" + salesId)
                                            .description(saleItem.getDescription())
                                            .additionalData(saleItem.getAdditionalData())
                                            .channel(saleItem.getChannel())
                                            .agent(saleItem.getAgent())
                                            .commercialOperationType(saleItem.getCommercialOperation())
                                            .estimatedRevenue(saleItem.getEstimatedRevenue())
                                            .paymentType(saleItem.getPaymenType())
                                            .validFor(saleItem.getValidFor())
                                            .name(saleItem.getName())
                                            .priority(saleItem.getPriority())
                                            .productType(saleItem.getProductType())
                                            .prospectContact(saleItem.getProspectContact())
                                            .relatedParty(saleItem.getRelatedParty())
                                            .saleCreationDate(saleItem.getSaleCreationDate())
                                            .status(saleItem.getStatus())
                                            .statusChangeDate(saleItem.getStatusChangeDate())
                                            .statusChangeReason(saleItem.getStatusChangeReason())
                                            .audioStatus(saleItem.getAudioStatus())
                                            .identityValidations(saleItem.getIdentityValidations())
                                            .audioUrl(saleItem.getAudioUrl())
                                            .build();

                                    return Mono.just(salesResponse);
                                });
                    });
                    // END - POST SALES LEAD CODE
                });
    }

}
