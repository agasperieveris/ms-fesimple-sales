package com.tdp.ms.sales.business.v2.commercialOperation.wireless;

import com.tdp.ms.sales.business.v2.commercialOperation.factory.CommercialOperationTypeAbstract;
import com.tdp.ms.sales.business.v2.commercialOperation.factory.ICommercialOperationType;
import com.tdp.ms.sales.model.dto.CommercialOperationType;
import com.tdp.ms.sales.model.dto.productorder.CreateProductOrderGeneralRequest;
import com.tdp.ms.sales.model.dto.productorder.FlexAttrType;
import com.tdp.ms.sales.model.dto.productorder.caeq.CaeqRequest;
import com.tdp.ms.sales.model.dto.productorder.caeq.ChangedContainedProduct;
import com.tdp.ms.sales.model.dto.productorder.caeq.NewProductCaeq;
import com.tdp.ms.sales.model.dto.productorder.caeq.ProductChangeCaeq;
import com.tdp.ms.sales.model.dto.productorder.caeq.ProductOrderCaeqRequest;
import com.tdp.ms.sales.model.dto.productorder.capl.NewAssignedBillingOffers;
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
public class CaeqMovil extends CommercialOperationTypeAbstract implements ICommercialOperationType {
    private static final Logger LOG = LoggerFactory.getLogger(CaeqMovil.class);

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
            this.caeqCommercialOperation(request.getSale(), mainRequestProductOrder, flgCasi, channelIdRequest,
                    customerIdRequest, productOfferingIdRequest, cipCode, sapidSimcard, businessParametersReasonCode,
                    getBonificacionSim, flgFinanciamiento, currentCommercialOperationType, isRetail, sendIndicator);
            return Mono.just(request.getSale());
        });
    }

    private CreateProductOrderGeneralRequest caeqCommercialOperation(Sale saleRequest,
                            CreateProductOrderGeneralRequest mainRequestProductOrder, boolean flgCasi,
                            String channelIdRequest, String customerIdRequest, String productOfferingIdRequest,
                            String cipCode, String sapidSimcardBp, BusinessParametersReasonCode getParameterReasonCode,
                            BusinessParametersResponseObjectExt bonificacionSimcardResponse,
                            final boolean flgFinanciamiento, CommercialOperationType currentCommercialOperationType,
                            final boolean isRetail, final boolean sendIndicator) {
        // Building request for CAEQ CommercialTypeOperation

        List<NewAssignedBillingOffers> caeqNewBoList = new ArrayList<>();
        // Simcard bonus conditional
        salesMovistarTotalService.validationToAddSimcardBonus(saleRequest, bonificacionSimcardResponse, caeqNewBoList,
                currentCommercialOperationType);

        // Refactored Code from CAEQ
        List<ChangedContainedProduct> changedContainedProductList =
                salesMovistarTotalService.changedContainedCaeqList(saleRequest, Constants.TEMP1, sapidSimcardBp,
                        flgCasi, currentCommercialOperationType, false, isRetail);

        ProductChangeCaeq productChangeCaeq = ProductChangeCaeq.builder()
                .changedContainedProducts(changedContainedProductList)
                .newAssignedBillingOffers(caeqNewBoList.isEmpty() ? null : caeqNewBoList).build();

        NewProductCaeq newProductCaeq1 = NewProductCaeq.builder()
                .productId(currentCommercialOperationType.getProduct().getId())
                .productChanges(productChangeCaeq).build();
        if (flgCasi) {
            newProductCaeq1.setProductCatalogId(currentCommercialOperationType.getProductOfferings().get(0)
                    .getProductOfferingProductSpecId());
        }
        List<NewProductCaeq> newProductCaeqList = new ArrayList<>();
        newProductCaeqList.add(newProductCaeq1);

        // Refactored Code from CAPL
        List<FlexAttrType> caeqCaplOrderAttributes =
                salesMovistarTotalService.commonOrderAttributes(saleRequest, flgFinanciamiento, sendIndicator, isRetail,
                        currentCommercialOperationType);

        // Order Attributes
        salesMovistarTotalService.addCaeqOderAttributes(caeqCaplOrderAttributes, saleRequest, flgCasi, isRetail);

        String deliveryMethod = Commons.getStringValueByKeyFromAdditionalDataList(saleRequest.getAdditionalData(),
                Constants.KEY_DELIVERY_METHOD);
        CaeqRequest caeqRequest = CaeqRequest.builder().sourceApp("FE").newProducts(newProductCaeqList)
                .orderAttributes(caeqCaplOrderAttributes.isEmpty() ? null : caeqCaplOrderAttributes)
                .shipmentDetails(!StringUtils.isEmpty(deliveryMethod) && !deliveryMethod.equals("IS")
                        && currentCommercialOperationType.getWorkOrDeliveryType() != null
                        ? salesMovistarTotalService.createShipmentDetail(saleRequest, currentCommercialOperationType)
                        : null)
                .upfrontIndicator(sendIndicator ?
                        salesMovistarTotalService.setUpFrontIndicator(saleRequest.getProductType()) : null)
                .cip(cipCode).build();

        ProductOrderCaeqRequest caeqProductOrderRequest = new ProductOrderCaeqRequest();
        caeqProductOrderRequest.setSalesChannel(channelIdRequest);
        caeqProductOrderRequest.getCustomer().setCustomerId(customerIdRequest);
        caeqProductOrderRequest.setProductOfferingId(productOfferingIdRequest);
        caeqProductOrderRequest.setOnlyValidationIndicator(Constants.STRING_FALSE);
        caeqProductOrderRequest.setActionType("CW");
        caeqProductOrderRequest.setRequest(caeqRequest);
        getParameterReasonCode.getData().get(0).getExt().stream()
                .filter(reasonCodeExt -> !reasonCodeExt.getCapl() && reasonCodeExt.getCaeq()
                        && reasonCodeExt.getCasi() == flgCasi)
                .findFirst().ifPresent(reasonCodeExt -> {
            caeqProductOrderRequest.setReasonCode(reasonCodeExt.getReasonId());
        });

        // Setting capl request into main request to send to create product order
        // service
        mainRequestProductOrder.setCreateProductOrderRequest(caeqProductOrderRequest);

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
