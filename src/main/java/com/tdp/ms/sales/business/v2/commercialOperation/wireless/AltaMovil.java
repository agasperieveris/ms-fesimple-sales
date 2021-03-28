package com.tdp.ms.sales.business.v2.commercialOperation.wireless;

import com.tdp.ms.sales.business.v2.commercialOperation.factory.CommercialOperationTypeAbstract;
import com.tdp.ms.sales.business.v2.commercialOperation.factory.ICommercialOperationType;
import com.tdp.ms.sales.model.dto.CommercialOperationType;
import com.tdp.ms.sales.model.dto.ContactMedium;
import com.tdp.ms.sales.model.dto.DeviceOffering;
import com.tdp.ms.sales.model.dto.KeyValueType;
import com.tdp.ms.sales.model.dto.MediumCharacteristic;
import com.tdp.ms.sales.model.dto.PortabilityType;
import com.tdp.ms.sales.model.dto.productorder.CreateProductOrderGeneralRequest;
import com.tdp.ms.sales.model.dto.productorder.FlexAttrType;
import com.tdp.ms.sales.model.dto.productorder.FlexAttrValueType;
import com.tdp.ms.sales.model.dto.productorder.altamobile.AltaMobileRequest;
import com.tdp.ms.sales.model.dto.productorder.altamobile.NewProductAltaMobile;
import com.tdp.ms.sales.model.dto.productorder.altamobile.ProductChangeAltaMobile;
import com.tdp.ms.sales.model.dto.productorder.altamobile.ProductOrderAltaMobileRequest;
import com.tdp.ms.sales.model.dto.productorder.caeq.ChangedCharacteristic;
import com.tdp.ms.sales.model.dto.productorder.caeq.ChangedContainedProduct;
import com.tdp.ms.sales.model.dto.productorder.capl.NewAssignedBillingOffers;
import com.tdp.ms.sales.model.dto.productorder.portability.PortabilityDetailsType;
import com.tdp.ms.sales.model.entity.Sale;
import com.tdp.ms.sales.model.request.PostSalesRequest;
import com.tdp.ms.sales.model.response.BusinessParametersResponseObjectExt;
import com.tdp.ms.sales.utils.Commons;
import com.tdp.ms.sales.utils.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AltaMovil extends CommercialOperationTypeAbstract implements ICommercialOperationType {
    private static final Logger LOG = LoggerFactory.getLogger(AltaMovil.class);

    @Override
    public Mono<Sale> createProductOrder(PostSalesRequest request, final String cipCode, String customerIdRequest,
                                         final boolean flgFinanciamiento, String productOfferingIdRequest,
                                         String channelIdRequest, final boolean isRetail, final boolean flgCasi,
                                         final boolean sendIndicator,
                                         CommercialOperationType currentCommercialOperationType, String sapidSimcard,
                                         BusinessParametersResponseObjectExt getBonificacionSim,
                                         CreateProductOrderGeneralRequest mainRequestProductOrder) {

        // Recognizing Mobile Portability
        boolean isMobilePortability = currentCommercialOperationType.getReason()
                .equalsIgnoreCase(Constants.PORTABILIDAD);
        LOG.info("Sales contain Mobile Portability: " + isMobilePortability);

        this.altaCommercialOperation(request.getSale(), mainRequestProductOrder, channelIdRequest,
                customerIdRequest, productOfferingIdRequest, cipCode, getBonificacionSim, sapidSimcard,
                isMobilePortability, flgCasi, flgFinanciamiento, currentCommercialOperationType,
                isRetail, sendIndicator);

        return Mono.just(request.getSale());
    }

    private CreateProductOrderGeneralRequest altaCommercialOperation(Sale saleRequest,
                                 CreateProductOrderGeneralRequest mainRequestProductOrder, String channelIdRequest,
                                 String customerIdRequest, String productOfferingIdRequest, String cipCode,
                                 BusinessParametersResponseObjectExt bonificacionSimcardResponse, String sapidSimcardBp,
                                 Boolean isMobilePortability, Boolean flagCasi, final Boolean flgFinanciamiento,
                                 CommercialOperationType currentCommercialOperationType, boolean isRetail,
                                 final boolean sendIndicator) {

        // Building request for ALTA CommercialTypeOperation
        ProductOrderAltaMobileRequest altaRequestProductOrder = new ProductOrderAltaMobileRequest();
        altaRequestProductOrder.setSalesChannel(channelIdRequest);
        com.tdp.ms.sales.model.dto.productorder.Customer customer = com.tdp.ms.sales.model.dto.productorder.Customer
                .builder().customerId(customerIdRequest).build();
        altaRequestProductOrder.setCustomer(customer);
        altaRequestProductOrder.setProductOfferingId(productOfferingIdRequest);
        altaRequestProductOrder.setOnlyValidationIndicator(Constants.STRING_FALSE);
        altaRequestProductOrder.setActionType("PR");

        // Identifying if is Alta Only Simcard or Alta Combo (Equipment + Simcard)
        boolean altaCombo = currentCommercialOperationType.getDeviceOffering() != null
                && currentCommercialOperationType.getDeviceOffering().size() > 1;

        // ALTA Product Changes
        ProductChangeAltaMobile altaProductChanges = new ProductChangeAltaMobile();

        // ALTA NewAssignedBillingOffers
        List<NewAssignedBillingOffers> altaNewBoList = new ArrayList<>();

        // NewAssignedBillingOffer Plan
        NewAssignedBillingOffers altaNewBo1 = NewAssignedBillingOffers.builder()
                .productSpecPricingId(currentCommercialOperationType.getProductOfferings().get(0)
                        .getProductOfferingPrice().get(0).getPricePlanSpecContainmentId())
                .parentProductCatalogId(currentCommercialOperationType.getProductOfferings().get(0)
                        .getProductOfferingPrice().get(0).getProductSpecContainmentId())
                .build();
        altaNewBoList.add(altaNewBo1);

        // Simcard bonus conditional
        salesMovistarTotalService.validationToAddSimcardBonus(saleRequest, bonificacionSimcardResponse, altaNewBoList,
                currentCommercialOperationType);

        altaProductChanges.setNewAssignedBillingOffers(altaNewBoList);

        // ALTA ChangeContainedProducts
        List<ChangedContainedProduct> altaChangedContainedProductList = new ArrayList<>();

        if (altaCombo) {
            // ChangeContainedProduct Equipment
            altaChangedContainedProductList = salesMovistarTotalService.changedContainedCaeqList(saleRequest,
                    Constants.TEMP2, sapidSimcardBp, flagCasi, currentCommercialOperationType, true, isRetail);
            // altaChangedContainedProductList.get(0).setProductId(""); // Doesnt sent it in Alta
        }

        // ChangeContainedProduct SIM
        List<ChangedCharacteristic> changedCharacteristicList = new ArrayList<>();

        // SIM TYPE SKU Characteristic
        ChangedCharacteristic changedCharacteristic1 = ChangedCharacteristic.builder().characteristicId("9751")
                .characteristicValue(sapidSimcardBp) // SAPID PARAMETRIZADO EN BP
                .build();
        changedCharacteristicList.add(changedCharacteristic1);

        // ICCID Characteristic
        if (isRetail && saleRequest.getStatus().equalsIgnoreCase(Constants.STATUS_VALIDADO)) {
            String iccidSim = Commons.getStringValueByKeyFromAdditionalDataList(saleRequest.getAdditionalData(),
                    Constants.SIM_ICCID);
            ChangedCharacteristic changedCharacteristic2 = ChangedCharacteristic.builder()
                    .characteristicId("799244").characteristicValue(iccidSim) // 8958080008100067567
                    .build();
            changedCharacteristicList.add(changedCharacteristic2);
        }

        ChangedContainedProduct changedContainedProduct2 = ChangedContainedProduct.builder()
                .temporaryId("temp3").productCatalogId("7431")
                .changedCharacteristics(changedCharacteristicList).build();
        altaChangedContainedProductList.add(changedContainedProduct2);

        altaProductChanges.setChangedContainedProducts(altaChangedContainedProductList);

        if (isMobilePortability) {
            // Portability Characteristic
            List<ChangedCharacteristic> changedCharacteristicPortabilityList = new ArrayList<>();
            ChangedCharacteristic changedCharacteristicPortability1 = ChangedCharacteristic.builder()
                    .characteristicId("7601").characteristicValue(currentCommercialOperationType.getPortability()
                            .getPublicId())
                    .build();
            changedCharacteristicPortabilityList.add(changedCharacteristicPortability1);
            ChangedCharacteristic changedCharacteristicPortability2 = ChangedCharacteristic.builder()
                    .characteristicId("9211").characteristicValue("Y").build();
            changedCharacteristicPortabilityList.add(changedCharacteristicPortability2);
            ChangedContainedProduct changedContainedProductPortability = ChangedContainedProduct.builder()
                    .temporaryId("tempPortingNumber").productCatalogId("7101")
                    .changedCharacteristics(changedCharacteristicPortabilityList).build();
            altaProductChanges.getChangedContainedProducts().add(changedContainedProductPortability);

            PortabilityDetailsType portabilityDetailsType = this.buildMobilePortabilityType(saleRequest,
                    currentCommercialOperationType);

            altaProductChanges.setPortabilityDetails(portabilityDetailsType);
        }

        NewProductAltaMobile newProductAlta1 = new NewProductAltaMobile();
        newProductAlta1.setProductCatalogId(currentCommercialOperationType.getProductOfferings().get(0)
                .getProductOfferingProductSpecId());
        newProductAlta1.setTemporaryId(Constants.TEMP1);
        newProductAlta1.setBaId(saleRequest.getRelatedParty().get(0).getBillingArragmentId());
        newProductAlta1.setAccountId(saleRequest.getRelatedParty().get(0).getAccountId());
        newProductAlta1.setInvoiceCompany("TEF");
        newProductAlta1.setProductChanges(altaProductChanges);

        List<NewProductAltaMobile> altaNewProductsList = new ArrayList<>();
        altaNewProductsList.add(newProductAlta1);

        // Building Order Attributes
        List<FlexAttrType> altaOrderAttributesList = salesMovistarTotalService.commonOrderAttributes(saleRequest,
                flgFinanciamiento, sendIndicator, isRetail, currentCommercialOperationType);

        // Order Attributes when channel is retail
        if (isRetail && saleRequest.getStatus().equalsIgnoreCase(Constants.STATUS_VALIDADO)) {
            // RETAIL PAYMENT NUMBER ATTRIBUTE
            String paymentNumber = Commons.getStringValueByKeyFromAdditionalDataList(saleRequest.getAdditionalData(),
                    "NUMERO_TICKET");

            FlexAttrValueType paymentRegisterAttrValue = FlexAttrValueType.builder()
                    .stringValue(paymentNumber).valueType(Constants.STRING).build();
            FlexAttrType paymentRegisterAttr = FlexAttrType.builder().attrName("PAYMENT_REGISTER_NUMBER")
                    .flexAttrValue(paymentRegisterAttrValue).build();
            altaOrderAttributesList.add(paymentRegisterAttr);

            // RETAIL DEVICE SKU ATTRIBUTE
            DeviceOffering deviceOfferingSmartphone = currentCommercialOperationType.getDeviceOffering().stream()
                    .filter(item -> !item.getDeviceType()
                            .equalsIgnoreCase(Constants.DEVICE_TYPE_SIM))
                    .findFirst().orElse(null);
            if (deviceOfferingSmartphone != null) {
                String deviceSku = Commons.getStringValueByKeyFromAdditionalDataList(saleRequest.getAdditionalData(),
                        Constants.DEVICE_SKU);

                FlexAttrValueType deviceSkuAttrValue = FlexAttrValueType.builder()
                        .stringValue(deviceSku).valueType(Constants.STRING).build();
                FlexAttrType deviceSkuAttr = FlexAttrType.builder().attrName(Constants.DEVICE_SKU)
                        .flexAttrValue(deviceSkuAttrValue).build();
                altaOrderAttributesList.add(deviceSkuAttr);
            }

            // RETAIL SIM SKU ATTRIBUTE
            DeviceOffering deviceOfferingSim = currentCommercialOperationType.getDeviceOffering().stream()
                    .filter(item -> item.getDeviceType().equalsIgnoreCase(Constants.DEVICE_TYPE_SIM))
                    .findFirst()
                    .orElse(null);
            if (deviceOfferingSim != null) {
                String simSku = Commons.getStringValueByKeyFromAdditionalDataList(saleRequest.getAdditionalData(),
                        Constants.SIM_SKU);

                FlexAttrValueType simSkuAttrValue = FlexAttrValueType.builder().stringValue(simSku)
                        .valueType(Constants.STRING).build();
                FlexAttrType simSkuAttr = FlexAttrType.builder().attrName(Constants.SIM_SKU)
                        .flexAttrValue(simSkuAttrValue).build();
                altaOrderAttributesList.add(simSkuAttr);
            }

            // RETAIL CASHIER REGISTER NUMBER ATTRIBUTE
            String cashierRegisterNumber = Commons.getStringValueByKeyFromAdditionalDataList(
                    saleRequest.getAdditionalData(), Constants.NUMERO_CAJA);

            FlexAttrValueType cashierRegisterAttrValue = FlexAttrValueType.builder()
                    .stringValue(cashierRegisterNumber).valueType(Constants.STRING).build();
            FlexAttrType cashierRegisterAttr = FlexAttrType.builder().attrName("CASHIER_REGISTER_NUMBER")
                    .flexAttrValue(cashierRegisterAttrValue).build();
            altaOrderAttributesList.add(cashierRegisterAttr);
        }

        AltaMobileRequest altaRequest = AltaMobileRequest.builder().newProducts(altaNewProductsList)
                .sourceApp("FE").orderAttributes(altaOrderAttributesList)
                .shipmentDetails(currentCommercialOperationType.getWorkOrDeliveryType() != null
                        ? salesMovistarTotalService.createShipmentDetail(saleRequest, currentCommercialOperationType)
                        : null)
                .cip(cipCode)
                .upfrontIndicator(sendIndicator ? setUpFrontIndicator(saleRequest.getProductType()) : null)
                .build();

        // Building Main Alta Request
        altaRequestProductOrder.setRequest(altaRequest);

        // Setting Alta request into main request to send to create product order
        // service
        mainRequestProductOrder.setCreateProductOrderRequest(altaRequestProductOrder);

        return mainRequestProductOrder;
    }

    private PortabilityDetailsType buildMobilePortabilityType(Sale saleRequest,
                                      CommercialOperationType currentCommercialOperationType) {
        PortabilityDetailsType portabilityDetailsType = new PortabilityDetailsType();
        PortabilityType portabilityType = currentCommercialOperationType.getPortability();

        // Changing format date for donorActivationDate
        String donorActivationDate = portabilityType.getDonorActivationDate();
        // Original date format from donorActivationDate yyyy-MM-dd-HH:mm, example given
        // 2021-02-19-05:00
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd-HH:mm");
        Date dateValue = null;
        try {
            dateValue = input.parse(donorActivationDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // Format changed to yyyy-MM-dd to send into portability details to create order
        SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd");
        String donorActivationDateWithNewFormat = output.format(dateValue);

        // Throw 400 status for mandatory parameters
        portabilityDetailsType.setSourceOperator(portabilityType.getReceipt());
        portabilityDetailsType.setServiceType("01");
        portabilityDetailsType.setPlanType(portabilityType.getPlanType());
        portabilityDetailsType.setActivationDate(donorActivationDateWithNewFormat);
        portabilityDetailsType
                .setEquipmentCommitmentEndDate(portabilityType.getDonorEquipmentContractEndDate());
        portabilityDetailsType.setSalesDepartment("15");
        portabilityDetailsType.setConsultationId(portabilityType.getIdProcess());
        portabilityDetailsType.setConsultationGroup(portabilityType.getIdProcessGroup());
        portabilityDetailsType.setDocumentType(saleRequest.getRelatedParty().get(0).getNationalIdType());
        portabilityDetailsType.setDocumentNumber(saleRequest.getRelatedParty().get(0).getNationalId());
        portabilityDetailsType.setCustomerName(saleRequest.getRelatedParty().get(0).getFullName());

        String customerEmail = saleRequest.getProspectContact().stream()
                .filter(item -> item.getMediumType().equalsIgnoreCase("email address"))
                .findFirst()
                .orElse(ContactMedium.builder()
                        .characteristic(MediumCharacteristic.builder().emailAddress(null)
                                .build())
                        .build())
                .getCharacteristic().getEmailAddress();
        portabilityDetailsType.setCustomerEmail(customerEmail);
        portabilityDetailsType.setCustomerContactPhone(StringUtils.isEmpty(portabilityType.getCustomerContactPhone())
                ? null : portabilityType.getCustomerContactPhone());

        return portabilityDetailsType;
    }

    private String setUpFrontIndicator(String productType) {
        return productType.equalsIgnoreCase(Constants.WIRELESS) ? "N" : "Y";
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
