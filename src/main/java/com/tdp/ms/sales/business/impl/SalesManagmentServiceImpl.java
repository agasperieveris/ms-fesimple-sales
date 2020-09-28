package com.tdp.ms.sales.business.impl;

import com.tdp.ms.sales.business.SalesManagmentService;
import com.tdp.ms.sales.client.BusinessParameterWebClient;
import com.tdp.ms.sales.client.WebClientBusinessParameters;
import com.tdp.ms.sales.model.dto.BusinessParameterExt;
import com.tdp.ms.sales.model.dto.productorder.caeq.ChangedCharacteristic;
import com.tdp.ms.sales.model.dto.productorder.caeq.ChangedContainedProduct;
import com.tdp.ms.sales.model.dto.productorder.caeq.NewProductCaeq;
import com.tdp.ms.sales.model.dto.productorder.caeq.ProductChangeCaeq;
import com.tdp.ms.sales.model.dto.productorder.caeq.ProductOrderCaeqRequest;
import com.tdp.ms.sales.model.dto.productorder.caeqcapl.NewProductCaeqCapl;
import com.tdp.ms.sales.model.dto.productorder.caeqcapl.ProductChangeCaeqCapl;
import com.tdp.ms.sales.model.dto.productorder.caeqcapl.ProductOrderCaeqCaplRequest;
import com.tdp.ms.sales.model.dto.productorder.capl.NewAssignedBillingOffers;
import com.tdp.ms.sales.model.dto.productorder.capl.NewProductCapl;
import com.tdp.ms.sales.model.dto.productorder.capl.ProductChangeCapl;
import com.tdp.ms.sales.model.dto.productorder.capl.ProductOrderCaplRequest;
import com.tdp.ms.sales.model.dto.productorder.capl.RemovedAssignedBillingOffers;
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

        return businessParameterWebClient.getSalesCharacteristicsByCommercialOperationType(
                GetSalesCharacteristicsRequest
                        .builder()
                        .commercialOperationType("CAEQ")
                        .headersMap(request.getHeadersMap())
                        .build())
                .map(this::retrieveCharacteristics)
                .flatMap(salesCharacteristicsList -> {

                    // Getting CommercialTypeOperation value
                    String commercialOperationType = request.getSale().getCommercialOperation().get(0).getReason();

                    if (commercialOperationType.equals("CAPL")) {
                        // Building request for CAPL CommercialTypeOperation

                        RemovedAssignedBillingOffers caplBoRemoved = RemovedAssignedBillingOffers
                                .builder()
                                .productSpecPricingID("2253558")
                                .build();

                        NewAssignedBillingOffers caplNewBo = NewAssignedBillingOffers
                                .builder()
                                .productSpecPricingID("8091631427")
                                .parentProductCatalogID("7491")
                                .build();

                        ProductChangeCapl caplProductChanges = ProductChangeCapl
                                .builder()
                                .newAssignedBillingOffers(caplNewBo)
                                .removedAssignedBillingOffers(caplBoRemoved)
                                .build();

                        NewProductCapl newProductCapl = NewProductCapl
                                .builder()
                                .productID("8091631427 ")
                                .productChanges(caplProductChanges)
                                .build();

                        ProductOrderCaplRequest caplRequest = ProductOrderCaplRequest
                                .builder()
                                .productOfferingID("2196188")
                                .newProducts(newProductCapl)
                                .build();

                        // CALL TO CREATE PRODUCT ORDER SERVICE
                        ProductOrderRequest createProductOrderRequest = new ProductOrderRequest();
                        createProductOrderRequest.setActionType("CW");

                    } else if (commercialOperationType.equals("CAEQ")) {
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

                        ProductOrderCaeqRequest caeqRequest = ProductOrderCaeqRequest
                                .builder()
                                .productOfferingID("3232618")
                                .newProducts(newProductCaeq)
                                .build();

                        // CALL TO CREATE PRODUCT ORDER SERVICE
                        ProductOrderRequest createProductOrderRequest = new ProductOrderRequest();
                        createProductOrderRequest.setActionType("CW");

                    } else if (commercialOperationType.equals("CAEQ+CAPL")) {
                        // Building request for CAEQ+CAPL CommercialTypeOperation

                        RemovedAssignedBillingOffers caplBoRemoved = RemovedAssignedBillingOffers
                                .builder()
                                .productSpecPricingID("2253558")
                                .build();

                        NewAssignedBillingOffers caplNewBo = NewAssignedBillingOffers
                                .builder()
                                .productSpecPricingID("8091631427")
                                .parentProductCatalogID("7491")
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

                        ProductOrderCaeqCaplRequest caeqCaplRequest = ProductOrderCaeqCaplRequest
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
                    request.getSale().setId(uuid);


                    // Se obtiene el secuencial de businessParameters
                    Mono<BusinessParametersResponse> saleSequential = webClient.getNewSaleSequential("SEQ001", request.getHeadersMap());

                    return saleSequential.flatMap(saleSequentialItem -> {
                        request.getSale().setSalesId(saleSequentialItem.getData().get(0).getValue());

                        // asignar fecha de creación
                        Date todayDate = Calendar.getInstance().getTime();
                        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy'T'HH:mm:ss");
                        String todayDateString = dateFormatter.format(todayDate);
                        request.getSale().setSaleCreationDate(todayDateString);

                        return salesRepository.save(request.getSale())
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
