package com.tdp.ms.sales.business.v2.commercialOperation.wireless;

import com.tdp.ms.sales.business.v2.commercialOperation.factory.CommercialOperationTypeAbstract;
import com.tdp.ms.sales.business.v2.commercialOperation.factory.ICommercialOperationType;
import com.tdp.ms.sales.model.dto.CommercialOperationType;
import com.tdp.ms.sales.model.dto.productorder.CreateProductOrderGeneralRequest;
import com.tdp.ms.sales.model.dto.productorder.FlexAttrType;
import com.tdp.ms.sales.model.dto.productorder.caeq.ChangedContainedProduct;
import com.tdp.ms.sales.model.dto.productorder.caeqcapl.CaeqCaplRequest;
import com.tdp.ms.sales.model.dto.productorder.caeqcapl.NewProductCaeqCapl;
import com.tdp.ms.sales.model.dto.productorder.caeqcapl.ProductChangeCaeqCapl;
import com.tdp.ms.sales.model.dto.productorder.caeqcapl.ProductOrderCaeqCaplRequest;
import com.tdp.ms.sales.model.dto.productorder.capl.NewAssignedBillingOffers;
import com.tdp.ms.sales.model.dto.productorder.capl.RemovedAssignedBillingOffers;
import com.tdp.ms.sales.model.entity.Sale;
import com.tdp.ms.sales.model.request.PostSalesRequest;
import com.tdp.ms.sales.model.response.BusinessParametersReasonCode;
import com.tdp.ms.sales.model.response.BusinessParametersResponseObjectExt;
import com.tdp.ms.sales.utils.Commons;
import com.tdp.ms.sales.utils.Constants;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class CaeqCaplMovil extends CommercialOperationTypeAbstract implements ICommercialOperationType {
    private static final Logger LOG = LoggerFactory.getLogger(CaeqCaplMovil.class);

    @Override
    public Mono<Sale> createProductOrder(PostSalesRequest request, final String cipCode, String customerIdRequest,
                                         final boolean flgFinanciamiento, String productOfferingIdRequest,
                                         String channelIdRequest, final boolean isRetail, final boolean flgCasi,
                                         final boolean sendIndicator,
                                         CommercialOperationType currentCommercialOperationType, String sapidSimcard,
                                         BusinessParametersResponseObjectExt getBonificacionSim,
                                         CreateProductOrderGeneralRequest mainRequestProductOrder) {
        Mono<BusinessParametersReasonCode> getParametersReasonCode = businessParameterWebClient
                .getParametersReasonCode(request.getHeadersMap());

        return getParametersReasonCode.flatMap(businessParametersReasonCode -> {
            this.caeqCaplCommercialOperation(request.getSale(), mainRequestProductOrder, flgCasi, channelIdRequest,
                    customerIdRequest, productOfferingIdRequest, cipCode, sapidSimcard, businessParametersReasonCode,
                    getBonificacionSim, flgFinanciamiento, currentCommercialOperationType, isRetail, sendIndicator);
            return Mono.just(request.getSale());
        });
    }

    private CreateProductOrderGeneralRequest caeqCaplCommercialOperation(Sale saleRequest,
                            CreateProductOrderGeneralRequest mainRequestProductOrder, boolean flgCasi,
                            String channelIdRequest, String customerIdRequest, String productOfferingIdRequest,
                            String cipCode, String sapidSimcardBp, BusinessParametersReasonCode getParameterReasonCode,
                            BusinessParametersResponseObjectExt bonificacionSimcardResponse,
                            final boolean flgFinanciamiento, CommercialOperationType currentCommercialOperationType,
                            final boolean isRetail, final boolean sendIndicator) {
        // Building request for CAEQ+CAPL CommercialTypeOperation

        boolean flgOnlyCapl = true;

        // Recognizing Capl into same plan or Capl with new plan
        if (currentCommercialOperationType.getProduct().getProductOffering() == null
                || !currentCommercialOperationType.getProduct().getProductOffering().getId()
                .equals(currentCommercialOperationType.getProductOfferings().get(0).getId())) {
            flgOnlyCapl = false;
        }
        LOG.info("Flag is only CAPL: " + flgOnlyCapl);

        // Code from CAPL
        ProductOrderCaeqCaplRequest caeqCaplRequestProductOrder = new ProductOrderCaeqCaplRequest();
        caeqCaplRequestProductOrder.setSalesChannel(channelIdRequest);
        caeqCaplRequestProductOrder.getCustomer().setCustomerId(customerIdRequest);
        caeqCaplRequestProductOrder.setProductOfferingId(productOfferingIdRequest);
        caeqCaplRequestProductOrder.setOnlyValidationIndicator(Constants.STRING_FALSE);
        getParameterReasonCode.getData().get(0).getExt().stream()
                .filter(reasonCodeExt -> reasonCodeExt.getCapl() && reasonCodeExt.getCaeq()
                        && reasonCodeExt.getCasi() == flgCasi)
                .findFirst().ifPresent(reasonCodeExt -> {
            caeqCaplRequestProductOrder.setReasonCode(reasonCodeExt.getReasonId());
        });

        RemovedAssignedBillingOffers caeqCaplBoRemoved1 = new RemovedAssignedBillingOffers();
        List<RemovedAssignedBillingOffers> caeqCaplBoRemovedList = new ArrayList<>();
        if (flgOnlyCapl) {
            // Recognizing Capl Fija
            if (saleRequest.getProductType().equalsIgnoreCase(Constants.WIRELESS)) {
                caeqCaplRequestProductOrder.setActionType("CW");
            } else {
                caeqCaplRequestProductOrder.setActionType("CH"); // landline, cableTv, broadband, bundle
            }

            caeqCaplBoRemoved1.setBillingOfferId(Commons.getStringValueByKeyFromAdditionalDataList(
                    currentCommercialOperationType.getProduct().getAdditionalData(), "billingOfferId"));
            caeqCaplBoRemovedList.add(caeqCaplBoRemoved1);
        } else {
            caeqCaplRequestProductOrder.setActionType("CH");
        }
        List<NewAssignedBillingOffers> caeqCaplNewBoList = new ArrayList<>();

        NewAssignedBillingOffers caplNewBo1 = NewAssignedBillingOffers.builder()
                .productSpecPricingId(currentCommercialOperationType.getProductOfferings().get(0)
                        .getProductOfferingPrice().get(0).getPricePlanSpecContainmentId())
                .parentProductCatalogId(currentCommercialOperationType.getProductOfferings().get(0)
                        .getProductOfferingPrice().get(0).getProductSpecContainmentId())
                .build();
        caeqCaplNewBoList.add(caplNewBo1);

        // Simcard bonus validation
        salesMovistarTotalService.validationToAddSimcardBonus(saleRequest, bonificacionSimcardResponse,
                caeqCaplNewBoList, currentCommercialOperationType);

        // Setting RemoveAssignedBillingOffers if commercial operation type is Capl into
        // same plan
        ProductChangeCaeqCapl caeqCaplProductChanges = new ProductChangeCaeqCapl();
        caeqCaplProductChanges.setNewAssignedBillingOffers(caeqCaplNewBoList);

        NewProductCaeqCapl newProductCaeqCapl1 = new NewProductCaeqCapl();
        newProductCaeqCapl1.setProductId(currentCommercialOperationType.getProduct().getId());
        if (flgOnlyCapl) {
            caeqCaplProductChanges.setRemovedAssignedBillingOffers(caeqCaplBoRemovedList);
        } else {
            newProductCaeqCapl1.setProductCatalogId(currentCommercialOperationType.getProductOfferings().get(0)
                    .getProductOfferingProductSpecId());
        }

        // Refactored Code from CAEQ
        List<ChangedContainedProduct> changedContainedProductList =
                salesMovistarTotalService.changedContainedCaeqList(saleRequest, Constants.TEMP1, sapidSimcardBp,
                        flgCasi, currentCommercialOperationType, false, isRetail);

        caeqCaplProductChanges.setChangedContainedProducts(changedContainedProductList);
        newProductCaeqCapl1.setProductChanges(caeqCaplProductChanges);

        List<NewProductCaeqCapl> caeqCaplNewProductList = new ArrayList<>();
        caeqCaplNewProductList.add(newProductCaeqCapl1);

        // Refactored Code from CAPL
        List<FlexAttrType> caeqCaplOrderAttributes =
                salesMovistarTotalService.commonOrderAttributes(saleRequest, flgFinanciamiento, sendIndicator, isRetail,
                        currentCommercialOperationType);
        // Adding Caeq Order Attributes
        salesMovistarTotalService.addCaeqOderAttributes(caeqCaplOrderAttributes, saleRequest, flgCasi, isRetail);

        String deliveryMethod = Commons.getStringValueByKeyFromAdditionalDataList(saleRequest.getAdditionalData(),
                Constants.KEY_DELIVERY_METHOD);
        CaeqCaplRequest caeqCaplRequest = CaeqCaplRequest.builder().newProducts(caeqCaplNewProductList)
                .sourceApp("FE").orderAttributes(caeqCaplOrderAttributes)
                .shipmentDetails(!StringUtils.isEmpty(deliveryMethod) && !deliveryMethod.equals("IS")
                        && currentCommercialOperationType.getWorkOrDeliveryType() != null
                        ? salesMovistarTotalService.createShipmentDetail(saleRequest, currentCommercialOperationType)
                        : null)
                .cip(cipCode)
                .upfrontIndicator(sendIndicator ?
                        salesMovistarTotalService.setUpFrontIndicator(saleRequest.getProductType()) : null)
                .build();

        caeqCaplRequestProductOrder.setRequest(caeqCaplRequest);

        // Setting capl request into main request to send to create product order
        // service
        mainRequestProductOrder.setCreateProductOrderRequest(caeqCaplRequestProductOrder);

        return mainRequestProductOrder;
    }

    @Override
    public Mono<Sale> reserveStock(PostSalesRequest request, Sale saleRequest, boolean flgCasi,
                                   boolean flgFinanciamiento, String sapidSimcard,
                                   CommercialOperationType currentCommercialOperationType) {
        return salesMovistarTotalService.reserveStockMobile(request, saleRequest, flgCasi, flgFinanciamiento,
                sapidSimcard, currentCommercialOperationType);
    }

    @Override
    public Mono<Sale> createQuotation(PostSalesRequest request, Sale sale, boolean flgCasi, boolean flgFinanciamiento,
                                      CommercialOperationType currentCommercialOperationType) {
        return salesMovistarTotalService.createQuotationMobile(request, sale, flgCasi, flgFinanciamiento,
                currentCommercialOperationType);
    }
}
