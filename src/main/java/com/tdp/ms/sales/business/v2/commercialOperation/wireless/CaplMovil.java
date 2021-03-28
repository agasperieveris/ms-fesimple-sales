package com.tdp.ms.sales.business.v2.commercialOperation.wireless;

import com.tdp.ms.sales.business.v2.commercialOperation.factory.CommercialOperationTypeAbstract;
import com.tdp.ms.sales.business.v2.commercialOperation.factory.ICommercialOperationType;
import com.tdp.ms.sales.model.dto.CommercialOperationType;
import com.tdp.ms.sales.model.dto.DeviceOffering;
import com.tdp.ms.sales.model.dto.ShipmentDetailsType;
import com.tdp.ms.sales.model.dto.productorder.CreateProductOrderGeneralRequest;
import com.tdp.ms.sales.model.dto.productorder.FlexAttrType;
import com.tdp.ms.sales.model.dto.productorder.capl.CaplRequest;
import com.tdp.ms.sales.model.dto.productorder.capl.NewAssignedBillingOffers;
import com.tdp.ms.sales.model.dto.productorder.capl.NewProductCapl;
import com.tdp.ms.sales.model.dto.productorder.capl.ProductChangeCapl;
import com.tdp.ms.sales.model.dto.productorder.capl.ProductOrderCaplRequest;
import com.tdp.ms.sales.model.dto.productorder.capl.RemovedAssignedBillingOffers;
import com.tdp.ms.sales.model.entity.Sale;
import com.tdp.ms.sales.model.request.PostSalesRequest;
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
public class CaplMovil extends CommercialOperationTypeAbstract implements ICommercialOperationType {
    private static final Logger LOG = LoggerFactory.getLogger(CaplMovil.class);

    @Override
    public Mono<Sale> createProductOrder(PostSalesRequest request, final String cipCode, String customerIdRequest,
                                         final boolean flgFinanciamiento, String productOfferingIdRequest,
                                         String channelIdRequest, final boolean isRetail, final boolean flgCasi,
                                         final boolean sendIndicator,
                                         CommercialOperationType currentCommercialOperationType, String sapidSimcard,
                                         BusinessParametersResponseObjectExt getBonificacionSim,
                                         CreateProductOrderGeneralRequest mainRequestProductOrder) {

        this.caplCommercialOperation(request.getSale(), mainRequestProductOrder,
                channelIdRequest, customerIdRequest, productOfferingIdRequest, cipCode, getBonificacionSim,
                flgFinanciamiento, isRetail, flgCasi, sendIndicator, currentCommercialOperationType);

        return Mono.just(request.getSale());
    }

    private CreateProductOrderGeneralRequest caplCommercialOperation(Sale saleRequest,
                                    CreateProductOrderGeneralRequest mainRequestProductOrder, String channelIdRequest,
                                    String customerIdRequest, String productOfferingIdRequest, String cipCode,
                                    BusinessParametersResponseObjectExt bonificacionSimcardResponse,
                                    final boolean flgFinanciamiento, final boolean isRetail, final boolean flgCasi,
                                    final boolean sendIndicator,
                                    CommercialOperationType currentCommercialOperationType) {
        boolean flgOnlyCapl = true;

        // Recognizing Capl into same plan or Capl with new plan
        if (currentCommercialOperationType.getProduct().getProductOffering() != null
                || !currentCommercialOperationType.getProduct().getProductOffering().getId()
                .equals(currentCommercialOperationType.getProductOfferings().get(0).getId())) {
            flgOnlyCapl = false;
        }

        LOG.info("Flag is only CAPL: " + flgOnlyCapl);

        // Building request for CAPL CommercialTypeOperation
        ProductOrderCaplRequest caplRequestProductOrder = new ProductOrderCaplRequest();
        caplRequestProductOrder.setSalesChannel(channelIdRequest);
        caplRequestProductOrder.getCustomer().setCustomerId(customerIdRequest);
        caplRequestProductOrder.setProductOfferingId(productOfferingIdRequest);
        caplRequestProductOrder.setOnlyValidationIndicator(Constants.STRING_FALSE);

        RemovedAssignedBillingOffers caplBoRemoved1 = new RemovedAssignedBillingOffers();
        List<RemovedAssignedBillingOffers> caplBoRemovedList = new ArrayList<>();
        if (flgOnlyCapl) {
            // Recognizing Capl Mobile or Fija
            if (saleRequest.getProductType().equalsIgnoreCase(Constants.WIRELESS)) {
                caplRequestProductOrder.setActionType("CW");
            } else {
                caplRequestProductOrder.setActionType("CH"); // landline, cableTv, broadband, bundle
            }

            caplBoRemoved1.setBillingOfferId(Commons.getStringValueByKeyFromAdditionalDataList(
                    currentCommercialOperationType.getProduct().getAdditionalData(), "billingOfferId"));
            caplBoRemovedList.add(caplBoRemoved1);
        } else {
            caplRequestProductOrder.setActionType("CH");
        }

        List<NewAssignedBillingOffers> caplNewBoList = new ArrayList<>();
        NewAssignedBillingOffers caplNewBo1 = NewAssignedBillingOffers.builder()
                .productSpecPricingId(currentCommercialOperationType.getProductOfferings().get(0)
                        .getProductOfferingPrice().get(0).getPricePlanSpecContainmentId())
                .parentProductCatalogId(currentCommercialOperationType.getProductOfferings().get(0)
                        .getProductOfferingPrice().get(0).getProductSpecContainmentId())
                .build();
        caplNewBoList.add(caplNewBo1);

        // Simcard bonus conditional
        this.validationToAddSimcardBonus(saleRequest, bonificacionSimcardResponse, caplNewBoList,
                currentCommercialOperationType);

        // Setting RemoveAssignedBillingOffers if commercial operation type is Capl into
        // same plan
        ProductChangeCapl caplProductChanges = new ProductChangeCapl();
        caplProductChanges.setNewAssignedBillingOffers(caplNewBoList);

        NewProductCapl newProductCapl1 = new NewProductCapl();
        newProductCapl1.setProductId(currentCommercialOperationType.getProduct().getId());
        if (flgOnlyCapl) {
            caplProductChanges.setRemovedAssignedBillingOffers(caplBoRemovedList);
        } else {
            newProductCapl1.setProductCatalogId(currentCommercialOperationType.getProductOfferings().get(0)
                    .getProductOfferingProductSpecId());
        }
        newProductCapl1.setProductChanges(caplProductChanges);

        // Refactored Code from CAPL
        List<FlexAttrType> caplOrderAttributes =
                salesMovistarTotalService.commonOrderAttributes(saleRequest, flgFinanciamiento, sendIndicator, isRetail,
                        currentCommercialOperationType);

        List<NewProductCapl> caplNewProductsList = new ArrayList<>();
        caplNewProductsList.add(newProductCapl1);

        String deliveryMethod = Commons.getStringValueByKeyFromAdditionalDataList(saleRequest.getAdditionalData(),
                Constants.KEY_DELIVERY_METHOD);
        CaplRequest caplRequest = CaplRequest.builder().newProducts(caplNewProductsList).sourceApp("FE")
                .orderAttributes(caplOrderAttributes)
                .shipmentDetails(!StringUtils.isEmpty(deliveryMethod) && !deliveryMethod.equals("IS")
                        && currentCommercialOperationType.getWorkOrDeliveryType() != null
                        ? salesMovistarTotalService.createShipmentDetail(saleRequest, currentCommercialOperationType)
                        : null)
                .build();
        // if (!StringUtils.isEmpty(cipCode)) caplRequest.setCip(cipCode);

        // Building Main Capl Request
        caplRequestProductOrder.setRequest(caplRequest);

        // Setting capl request into main request to send to create product order
        // service
        mainRequestProductOrder.setCreateProductOrderRequest(caplRequestProductOrder);

        return mainRequestProductOrder;
    }

    public void validationToAddSimcardBonus(Sale sale,
                                            BusinessParametersResponseObjectExt bonificacionSimcardResponse,
                                            List<NewAssignedBillingOffers> altaNewBoList,
                                            CommercialOperationType currentCommercialOperationType) {
        if (currentCommercialOperationType.getDeviceOffering() != null) {
            // Simcard bonus conditional
            DeviceOffering deviceOfferingSimcard = currentCommercialOperationType.getDeviceOffering().stream()
                    .filter(item -> item.getDeviceType()
                            .equalsIgnoreCase(Constants.DEVICE_TYPE_SIM))
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

    @Override
    public Mono<Sale> reserveStock(PostSalesRequest request, Sale saleRequest, boolean flgCasi,
                                   boolean flgFinanciamiento, String sapidSimcard,
                                   CommercialOperationType currentCommercialOperationType) {
        // No se hace ninguna reserva, ya que es un cambio de plan
        return Mono.just(saleRequest);
    }

    @Override
    public Mono<Sale> createQuotation(PostSalesRequest request, Sale sale, boolean flgCasi, boolean flgFinanciamiento,
                                      CommercialOperationType currentCommercialOperationType) {
        return salesMovistarTotalService.createQuotationMobile(request, sale, flgCasi, flgFinanciamiento,
                currentCommercialOperationType);
    }
}
