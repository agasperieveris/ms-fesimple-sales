package com.tdp.ms.sales.business.impl;

import com.tdp.genesis.core.exception.GenesisException;
import com.tdp.ms.commons.util.DateUtils;
import com.tdp.ms.sales.business.SalesManagmentService;
import com.tdp.ms.sales.client.BusinessParameterWebClient;
import com.tdp.ms.sales.client.GetSkuWebClient;
import com.tdp.ms.sales.client.ProductOrderWebClient;
import com.tdp.ms.sales.client.QuotationWebClient;
import com.tdp.ms.sales.client.StockWebClient;
import com.tdp.ms.sales.client.WebClientReceptor;
import com.tdp.ms.sales.model.dto.*;
import com.tdp.ms.sales.model.dto.businessparameter.BusinessParameterFinanciamientoFijaData;
import com.tdp.ms.sales.model.dto.businessparameter.BusinessParameterFinanciamientoFijaExt;
import com.tdp.ms.sales.model.dto.productorder.CreateProductOrderGeneralRequest;
import com.tdp.ms.sales.model.dto.productorder.FlexAttrType;
import com.tdp.ms.sales.model.dto.productorder.FlexAttrValueType;
import com.tdp.ms.sales.model.dto.productorder.altafija.AltaFijaRequest;
import com.tdp.ms.sales.model.dto.productorder.altafija.CharacteristicOfferType;
import com.tdp.ms.sales.model.dto.productorder.altafija.CommercialZoneType;
import com.tdp.ms.sales.model.dto.productorder.altafija.NewProductAltaFija;
import com.tdp.ms.sales.model.dto.productorder.altafija.ProductChangeAltaFija;
import com.tdp.ms.sales.model.dto.productorder.altafija.ProductLineType;
import com.tdp.ms.sales.model.dto.productorder.altafija.ProductOrderAltaFijaRequest;
import com.tdp.ms.sales.model.dto.productorder.altafija.ServiceabilityInfoType;
import com.tdp.ms.sales.model.dto.productorder.altafija.ServiceabilityOfferType;
import com.tdp.ms.sales.model.dto.productorder.altamobile.AltaMobileRequest;
import com.tdp.ms.sales.model.dto.productorder.altamobile.NewProductAltaMobile;
import com.tdp.ms.sales.model.dto.productorder.altamobile.ProductChangeAltaMobile;
import com.tdp.ms.sales.model.dto.productorder.altamobile.ProductOrderAltaMobileRequest;
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
import com.tdp.ms.sales.model.dto.productorder.migracionfija.MigracionFijaRequest;
import com.tdp.ms.sales.model.dto.productorder.migracionfija.NewProductMigracionFija;
import com.tdp.ms.sales.model.dto.productorder.migracionfija.ProductOrderMigracionFijaRequest;
import com.tdp.ms.sales.model.dto.productorder.portability.PortabilityDetailsType;
import com.tdp.ms.sales.model.dto.quotation.Address;
import com.tdp.ms.sales.model.dto.quotation.Channel;
import com.tdp.ms.sales.model.dto.quotation.CreateQuotationRequestBody;
import com.tdp.ms.sales.model.dto.quotation.Customer;
import com.tdp.ms.sales.model.dto.quotation.LegalId;
import com.tdp.ms.sales.model.dto.quotation.MoneyAmount;
import com.tdp.ms.sales.model.dto.quotation.Site;
import com.tdp.ms.sales.model.dto.reservestock.Destination;
import com.tdp.ms.sales.model.dto.reservestock.Item;
import com.tdp.ms.sales.model.dto.reservestock.Order;
import com.tdp.ms.sales.model.dto.reservestock.StockItem;
import com.tdp.ms.sales.model.entity.Sale;
import com.tdp.ms.sales.model.request.CreateQuotationRequest;
import com.tdp.ms.sales.model.request.GetSalesCharacteristicsRequest;
import com.tdp.ms.sales.model.request.PostSalesRequest;
import com.tdp.ms.sales.model.request.ReceptorRequest;
import com.tdp.ms.sales.model.request.ReserveStockRequest;
import com.tdp.ms.sales.model.response.BusinessParametersFinanciamientoFijaResponse;
import com.tdp.ms.sales.model.response.BusinessParametersResponse;
import com.tdp.ms.sales.model.response.BusinessParametersResponseObjectExt;
import com.tdp.ms.sales.model.response.CreateQuotationResponse;
import com.tdp.ms.sales.model.response.GetSalesCharacteristicsResponse;
import com.tdp.ms.sales.model.response.GetSkuResponse;
import com.tdp.ms.sales.model.response.ProductorderResponse;
import com.tdp.ms.sales.model.response.ReserveStockResponse;
import com.tdp.ms.sales.repository.SalesRepository;
import com.tdp.ms.sales.utils.Commons;
import com.tdp.ms.sales.utils.Constants;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

/**
 * Class: SalesManagmentServiceImpl. <br/>
 * <b>Copyright</b>: &copy; 2020 Telef&oacute;nica del Per&uacute;<br/>
 * <b>Company</b>: Telef&oacute;nica del Per&uacute;<br/>
 *
 * @author Telef&oacute;nica del Per&uacute; (TDP) <br/>
 *         <u>Service Provider</u>: Everis Per&uacute; SAC (EVE) <br/>
 *         <u>Developed by</u>: <br/>
 *         <ul>
 *         <li>Cesar Gomez</li>
 *         <li>Sergio Rivas</li>
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
    private QuotationWebClient quotationWebClient;

    @Autowired
    private GetSkuWebClient getSkuWebClient;

    @Autowired
    private WebClientReceptor webClientReceptor;

    private static final String FLOW_SALE_POST = "01";
    private static final String FLOW_SALE_INVITATION = "03";

    private static final String SHIPPING_LOCALITY = "shippingLocality";
    private static final String PROVINCE_OF_SHIPPING_ADDRESS = "provinceOfShippingAddress";
    private static final String SHOP_ADDRESS = "shopAddress";

    private static final Logger LOG = LoggerFactory.getLogger(SalesManagmentServiceImpl.class);

    private List<BusinessParameterExt> retrieveCharacteristics(GetSalesCharacteristicsResponse response) {
        return response.getData().get(0).getExt();
    }

    public String retrieveDomain(List<ContactMedium> prospectContact) {
        // Get domain from email
        String email = prospectContact.stream()
                .filter(p -> p.getMediumType().equalsIgnoreCase(Constants.EMAIL))
                .map(p -> p.getCharacteristic().getEmailAddress())
                .collect(Collectors.joining());

        if (email != null && !email.isEmpty()) {
            int pos = email.indexOf('@');
            return email.substring(++pos);
        }
        return null;
    }

    private String getStringValueByKeyFromAdditionalDataList(List<KeyValueType> additionalData, String key) {
        final String[] stringValue = {""};

        if (additionalData != null && !additionalData.isEmpty()) {
            additionalData.forEach(kv -> {
                if (kv.getKey().equalsIgnoreCase(key)) {
                    stringValue[0] = kv.getValue();
                }
            });
        }

        return stringValue[0];
    }

    private void postSalesEventFlow(PostSalesRequest request) {
        if (request.getSale().getAdditionalData() == null) {
            request.getSale().setAdditionalData(new ArrayList<>());
        }
        request.getSale().getAdditionalData().add(
                KeyValueType
                        .builder()
                        .key("initialProcessDate")
                        .value(DateUtils.getDatetimeNowCosmosDbFormat())
                        .build());

        callReceptors(request);
    }

    private void callReceptors(PostSalesRequest request) {
        callWebClientReceptor(request, FLOW_SALE_POST);

        String reason = request.getSale().getCommercialOperation().get(0).getReason();
        if (reason.equalsIgnoreCase("CAPL") || reason.equalsIgnoreCase("CAEQ")) {
            callWebClientReceptor(request, FLOW_SALE_INVITATION);
        }
    }

    private void callWebClientReceptor(PostSalesRequest request, String eventFlowCode) {
        // Llamada a receptor
        webClientReceptor
                .register(
                        ReceptorRequest
                                .builder()
                                .businessId(request.getSale().getSalesId())
                                .typeEventFlow(eventFlowCode)
                                .message(request.getSale())
                                .build(),
                        request.getHeadersMap()
                )
                .subscribe();
    }

    private void buildOrderAttributesListAltaFija(List<FlexAttrType> altaFijaOrderAttributesList, Sale saleRequest,
                                                  CreateQuotationRequest createQuotationFijaRequest,
                                                  Boolean flgFinanciamiento) {

        FlexAttrValueType externalFinancialAttrValue =  FlexAttrValueType
                .builder()
                .stringValue(Boolean.TRUE.equals(flgFinanciamiento) ? "Y" : "N")
                .valueType(Constants.STRING)
                .build();
        FlexAttrType externalFinancialAttr = FlexAttrType
                .builder()
                .attrName("IS_EXTERNAL_FINANCING")
                .flexAttrValue(externalFinancialAttrValue)
                .build();

        FlexAttrValueType upFrontIndAttrValue =  FlexAttrValueType
                .builder()
                .stringValue(saleRequest.getCommercialOperation().get(0).getProductOfferings().get(0)
                        .getUpFront().getIndicator())
                .valueType(Constants.STRING)
                .build();
        FlexAttrType upFrontIndAttr = FlexAttrType
                .builder()
                .attrName("UPFRONT_IND")
                .flexAttrValue(upFrontIndAttrValue)
                .build();

        FlexAttrValueType paymentMethodAttrValue =  FlexAttrValueType
                .builder()
                .stringValue("EX")
                .valueType(Constants.STRING)
                .build();
        FlexAttrType paymentMethodAttr = FlexAttrType
                .builder()
                .attrName("PAYMENT_METHOD")
                .flexAttrValue(paymentMethodAttrValue)
                .build();

        altaFijaOrderAttributesList.add(externalFinancialAttr);
        altaFijaOrderAttributesList.add(upFrontIndAttr);
        altaFijaOrderAttributesList.add(paymentMethodAttr);

        // Order Attributes if is Financing
        if (Boolean.TRUE.equals(flgFinanciamiento)) {
            FlexAttrValueType downPaymentAttrValue = FlexAttrValueType
                    .builder()
                    .stringValue(createQuotationFijaRequest.getBody().getDownPayment().getAmount())
                    .valueType(Constants.STRING)
                    .build();
            FlexAttrType downPaymentAttr = FlexAttrType
                    .builder()
                    .attrName("DOWN_PAYMENT_AMOUNT")
                    .flexAttrValue(downPaymentAttrValue)
                    .build();
            altaFijaOrderAttributesList.add(downPaymentAttr);

            FlexAttrValueType financingAmountAttrValue = FlexAttrValueType
                    .builder()
                    .stringValue(createQuotationFijaRequest.getBody().getTotalAmount().getAmount())
                    .valueType(Constants.STRING)
                    .build();
            FlexAttrType financingAmountAttr = FlexAttrType
                    .builder()
                    .attrName("FINANCING_AMOUNT")
                    .flexAttrValue(financingAmountAttrValue)
                    .build();
            altaFijaOrderAttributesList.add(financingAmountAttr);

            FlexAttrValueType financingPlanAttrValue = FlexAttrValueType
                    .builder()
                    .stringValue(createQuotationFijaRequest.getBody().getFinancialEntity())
                    .valueType(Constants.STRING)
                    .build();
            FlexAttrType financingPlanAttr = FlexAttrType
                    .builder()
                    .attrName("FINANCING_PLAN")
                    .flexAttrValue(financingPlanAttrValue)
                    .build();
            altaFijaOrderAttributesList.add(financingPlanAttr);
        }

        // Order Attributes if is Scheduling
        if (flgFinanciamiento && saleRequest.getCommercialOperation() != null
                && saleRequest.getCommercialOperation().get(0).getWorkOrDeliveryType() != null
                && saleRequest.getCommercialOperation().get(0).getWorkOrDeliveryType().getScheduleDelivery() != null
                && !saleRequest.getCommercialOperation().get(0).getWorkOrDeliveryType().getScheduleDelivery()
                .equalsIgnoreCase("SLA")) {

            FlexAttrValueType downPaymentAttrValue = FlexAttrValueType
                    .builder()
                    .stringValue(createQuotationFijaRequest.getBody().getDownPayment().getAmount())
                    .valueType("TC")
                    .build();
            FlexAttrType downPaymentAttr = FlexAttrType
                    .builder()
                    .attrName("DELIVERY_METHOD")
                    .flexAttrValue(downPaymentAttrValue)
                    .build();
            altaFijaOrderAttributesList.add(downPaymentAttr);
        }
    }

    private void buildServiceAvailabilityAltaFija(Sale saleRequest,
                                                  List<ServiceabilityOfferType> serviceabilityOffersList) {
        saleRequest.getCommercialOperation().get(0).getServiceAvailability().getOffers().stream()
                .forEach(availabilityOffer -> {
                    String serviceAbilityType = availabilityOffer.getServices().get(0).getType();

                    if (serviceAbilityType.equalsIgnoreCase("VOICE")) {

                        // Serviceability Landline
                        CharacteristicOfferType describeByLandline1 =  CharacteristicOfferType
                                .builder()
                                .characteristicName("ALLOCATION_ID")
                                .characteristicValue(availabilityOffer.getServices().get(0).getAllocationId())
                                .build();

                        List<CharacteristicOfferType> describeByLandlineList = new ArrayList<>();
                        describeByLandlineList.add(describeByLandline1);

                        ProductLineType productOfferLandline1 = ProductLineType
                                .builder()
                                .type(serviceAbilityType)
                                .description("Servicio de Voz")
                                .networkTechnology(this.getStringValueByKeyFromAdditionalDataList(saleRequest
                                        .getCommercialOperation().get(0).getServiceAvailability()
                                        .getAdditionalData(),"networkAccessTechnologyLandline"))
                                .serviceTechnology(this.getStringValueByKeyFromAdditionalDataList(saleRequest
                                        .getCommercialOperation().get(0).getServiceAvailability()
                                        .getAdditionalData(),"serviceTechnologyLandline"))
                                .describeByList(describeByLandlineList)
                                .build();
                        List<ProductLineType> productOfferLandlineList = new ArrayList<>();
                        productOfferLandlineList.add(productOfferLandline1);

                        ServiceabilityOfferType serviceabilityOfferLandline = ServiceabilityOfferType
                                .builder()
                                .idOfferPriority(availabilityOffer.getPriority().toString())
                                .productOffer(productOfferLandlineList)
                                .build();
                        serviceabilityOffersList.add(serviceabilityOfferLandline);

                    } else if (serviceAbilityType.equalsIgnoreCase("BB")) {

                        // Serviceability Broadband
                        CharacteristicOfferType describeByBroadband1 =  CharacteristicOfferType
                                .builder()
                                .characteristicName("MaxTheoricalSpeed")
                                .characteristicValue(this.getStringValueByKeyFromAdditionalDataList(saleRequest
                                        .getCommercialOperation().get(0).getServiceAvailability()
                                        .getAdditionalData(), "maxSpeed"))
                                .build();

                        List<CharacteristicOfferType> describeByBroadbandList = new ArrayList<>();
                        describeByBroadbandList.add(describeByBroadband1);

                        ProductLineType productOfferBroadband1 = ProductLineType
                                .builder()
                                .type(serviceAbilityType)
                                .description("Servicio de banda ancha")
                                .networkTechnology(this.getStringValueByKeyFromAdditionalDataList(saleRequest
                                        .getCommercialOperation().get(0).getServiceAvailability()
                                        .getAdditionalData(),"networkAccessTechnologyBroadband"))
                                .serviceTechnology(this.getStringValueByKeyFromAdditionalDataList(saleRequest
                                        .getCommercialOperation().get(0).getServiceAvailability()
                                        .getAdditionalData(),"serviceTechnologyBroadband"))
                                .describeByList(describeByBroadbandList)
                                .build();
                        List<ProductLineType> productOfferBroadbandList = new ArrayList<>();
                        productOfferBroadbandList.add(productOfferBroadband1);

                        ServiceabilityOfferType serviceabilityOfferBroadband = ServiceabilityOfferType
                                .builder()
                                .idOfferPriority(availabilityOffer.getPriority().toString())
                                .productOffer(productOfferBroadbandList)
                                .build();
                        serviceabilityOffersList.add(serviceabilityOfferBroadband);

                    } else if (serviceAbilityType.equalsIgnoreCase("TV")) {

                        // Serviceability CableTv
                        ProductLineType productOfferCableTv1 = ProductLineType
                                .builder()
                                .type(serviceAbilityType)
                                .description("Servicio de Television")
                                .networkTechnology(this.getStringValueByKeyFromAdditionalDataList(saleRequest
                                        .getCommercialOperation().get(0).getServiceAvailability()
                                        .getAdditionalData(),"networkAccessTechnologyTv"))
                                .serviceTechnology(this.getStringValueByKeyFromAdditionalDataList(saleRequest
                                        .getCommercialOperation().get(0).getServiceAvailability()
                                        .getAdditionalData(),"serviceTechnologyTv"))
                                .build();
                        List<ProductLineType> productOfferCableTvList = new ArrayList<>();
                        productOfferCableTvList.add(productOfferCableTv1);

                        ServiceabilityOfferType serviceabilityOfferCableTv = ServiceabilityOfferType
                                .builder()
                                .idOfferPriority(availabilityOffer.getPriority().toString())
                                .productOffer(productOfferCableTvList)
                                .build();
                        serviceabilityOffersList.add(serviceabilityOfferCableTv);
                    }
                });
    }

    private List<NewProductAltaFija> buildNewProductsAltaFijaList(Sale saleRequest,
                                                  List<NewAssignedBillingOffers> newAssignedBillingOffersLandlineList,
                                                  List<NewAssignedBillingOffers> newAssignedBillingOffersBroadbandList,
                                                  List<NewAssignedBillingOffers> newAssignedBillingOffersCableTvList) {
        List<NewProductAltaFija> newProductsAltaFijaList = new ArrayList<>();

        String baId = saleRequest.getRelatedParty().get(0).getBillingArragmentId();
        String accountId = saleRequest.getRelatedParty().get(0).getAccountId();

        saleRequest.getCommercialOperation().get(0).getProductOfferings().get(0).getProductSpecification()
                .stream()
                .forEach(productSpecification -> {

                    String productType = productSpecification.getProductType();
                    if (productType.equalsIgnoreCase(Constants.PRODUCT_TYPE_LANDLINE)) {

                        NewProductAltaFija newProductAltaFijaLandline = NewProductAltaFija
                                .builder()
                                .productCatalogId(productSpecification.getRefinedProduct()
                                        .getProductCharacteristics().get(0).getId())
                                .temporaryId("temp")
                                .baId(baId)
                                .accountId(accountId)
                                .invoiceCompany("TDP")
                                .build();

                        //Adding Landline SVAs
                        if (!newAssignedBillingOffersLandlineList.isEmpty()) {

                            ProductChangeAltaFija productChangesLandline = ProductChangeAltaFija
                                    .builder()
                                    .newAssignedBillingOffers(newAssignedBillingOffersLandlineList)
                                    .build();
                            newProductAltaFijaLandline.setProductChanges(productChangesLandline);
                        }

                        newProductsAltaFijaList.add(newProductAltaFijaLandline);

                    } else if (productType.equalsIgnoreCase("broadband")) {

                        List<ChangedCharacteristic> changedCharacteristicsBroadbandList = new ArrayList<>();

                        ChangedCharacteristic changedCharacteristicBroadband1 = ChangedCharacteristic
                                .builder()
                                .characteristicId("3241482")
                                .characteristicValue(productSpecification.getProductPrice().get(2)
                                        .getAdditionalData().stream()
                                        .filter(item -> item.getKey().equalsIgnoreCase("downloadSpeed"))
                                        .findFirst()
                                        .orElse(KeyValueType.builder().value(null).build())
                                        .getValue())
                                .build();
                        changedCharacteristicsBroadbandList.add(changedCharacteristicBroadband1);

                        try {
                            ChangedCharacteristic changedCharacteristicBroadband2 = ChangedCharacteristic
                                    .builder()
                                    .characteristicId("3241532")
                                    .characteristicValue(Commons.getTimeNowInMillis())
                                    .build();
                            changedCharacteristicsBroadbandList.add(changedCharacteristicBroadband2);
                        } catch (ParseException e) {
                            LOG.error("Post Sales Exception Getting Time at Now in Milliseconds");
                        }

                        ChangedContainedProduct changedContainedProductBroadband1 = ChangedContainedProduct
                                .builder()
                                .temporaryId("temp")
                                .productCatalogId("3241312")
                                .changedCharacteristics(changedCharacteristicsBroadbandList)
                                .build();

                        List<ChangedContainedProduct> changedContainedProductsBroadbandList =
                                new ArrayList<>();
                        changedContainedProductsBroadbandList.add(changedContainedProductBroadband1);

                        ProductChangeAltaFija productChangesBroadband = ProductChangeAltaFija
                                .builder()
                                .changedContainedProducts(changedContainedProductsBroadbandList)
                                .build();
                        //Adding Broadband SVAs
                        if (!newAssignedBillingOffersBroadbandList.isEmpty()) {
                            productChangesBroadband.setNewAssignedBillingOffers(
                                    newAssignedBillingOffersBroadbandList);
                        }

                        NewProductAltaFija newProductAltaFijaBroadband = NewProductAltaFija
                                .builder()
                                .productCatalogId(productSpecification.getRefinedProduct()
                                        .getProductCharacteristics().get(0).getId())
                                .temporaryId("temp")
                                .baId(baId)
                                .accountId(accountId)
                                .invoiceCompany("TDP")
                                .productChanges(productChangesBroadband)
                                .build();
                        newProductsAltaFijaList.add(newProductAltaFijaBroadband);

                    } else if (productType.equalsIgnoreCase(Constants.PRODUCT_TYPE_CABLE_TV)) {

                        NewProductAltaFija newProductAltaFijaCableTv = NewProductAltaFija
                                .builder()
                                .productCatalogId(productSpecification.getRefinedProduct()
                                        .getProductCharacteristics().get(0).getId())
                                .temporaryId("temp")
                                .baId(baId)
                                .accountId(accountId)
                                .invoiceCompany("TDP")
                                .build();

                        //Adding CableTv SVAs
                        if (!newAssignedBillingOffersCableTvList.isEmpty()) {

                            ProductChangeAltaFija productChangesCableTv = ProductChangeAltaFija
                                    .builder()
                                    .newAssignedBillingOffers(newAssignedBillingOffersCableTvList)
                                    .build();
                            newProductAltaFijaCableTv.setProductChanges(productChangesCableTv);
                        }

                        newProductsAltaFijaList.add(newProductAltaFijaCableTv);

                    } else if (productType.equalsIgnoreCase("ShEq")) {

                        NewProductAltaFija newProductAltaFijaShareEquipment = NewProductAltaFija
                                .builder()
                                .productCatalogId(productSpecification.getRefinedProduct()
                                        .getProductCharacteristics().get(0).getId())
                                .temporaryId("temp")
                                .baId(baId)
                                .accountId(accountId)
                                .invoiceCompany("TDP")
                                .build();
                        newProductsAltaFijaList.add(newProductAltaFijaShareEquipment);

                    } else if (productType.equalsIgnoreCase("Accessories")) {

                        ChangedCharacteristic changedCharacteristicAccesories1 = ChangedCharacteristic
                                .builder()
                                .characteristicId("15734")
                                .characteristicValue("34203411")
                                .build();

                        List<ChangedCharacteristic> changedCharacteristicsAccesoriesList =
                                new ArrayList<>();
                        changedCharacteristicsAccesoriesList.add(changedCharacteristicAccesories1);

                        ChangedContainedProduct changedContainedProductAccesories1 = ChangedContainedProduct
                                .builder()
                                .temporaryId("temp")
                                .productCatalogId("34134811")
                                .changedCharacteristics(changedCharacteristicsAccesoriesList)
                                .build();

                        List<ChangedContainedProduct> changedContainedProductsAccesoriesList =
                                new ArrayList<>();
                        changedContainedProductsAccesoriesList.add(changedContainedProductAccesories1);

                        ProductChangeAltaFija productChangesAccesories = ProductChangeAltaFija
                                .builder()
                                .changedContainedProducts(changedContainedProductsAccesoriesList)
                                .build();

                        NewProductAltaFija newProductAltaFijaAccesories = NewProductAltaFija
                                .builder()
                                .productCatalogId(productSpecification.getRefinedProduct()
                                        .getProductCharacteristics().get(0).getId())
                                .temporaryId("temp")
                                .baId(baId)
                                .accountId(accountId)
                                .invoiceCompany("TDP")
                                .productChanges(productChangesAccesories)
                                .build();
                        newProductsAltaFijaList.add(newProductAltaFijaAccesories);
                    }
                });

        return newProductsAltaFijaList;
    }

    private void commercialOperationInputValidations(Sale saleRequest) {
        CommercialOperationType commercialOperationType = saleRequest.getCommercialOperation().get(0);
        if (commercialOperationType.getProductOfferings() == null) {
            commercialOperationType.setProductOfferings(new ArrayList<>());
        }
        if (commercialOperationType.getAdditionalData() == null) {
            commercialOperationType.setAdditionalData(new ArrayList<>());
        }
    }

    private Boolean setFinancingFlag(List<DeviceOffering> deviceOfferings) {
        if (deviceOfferings != null && deviceOfferings.get(0).getOffers() != null
                && deviceOfferings.get(0).getOffers().get(0).getBillingOfferings() != null
                && deviceOfferings.get(0).getOffers().get(0).getBillingOfferings().get(0).getCommitmentPeriods() != null
                && deviceOfferings.get(0).getOffers().get(0).getBillingOfferings().get(0).getCommitmentPeriods().get(0)
                .getFinancingInstalments() != null
                && !StringUtils.isEmpty(deviceOfferings.get(0).getOffers().get(0).getBillingOfferings().get(0)
                .getCommitmentPeriods().get(0).getFinancingInstalments().get(0).getCodigo())
                && !deviceOfferings.get(0).getOffers().get(0).getBillingOfferings().get(0).getCommitmentPeriods().get(0)
                .getFinancingInstalments().get(0).getCodigo().equals("TELEFCONT")) {
            return true;
        }
        return false;
    }

    @Override
    public Mono<Sale> post(PostSalesRequest request) {

        // Getting Sale object
        Sale saleRequest = request.getSale();
        commercialOperationInputValidations(saleRequest);

        if (StringUtils.isEmpty(saleRequest.getId())) {
            return Mono.error(GenesisException
                    .builder()
                    .exceptionId(Constants.BAD_REQUEST_EXCEPTION_ID)
                    .wildcards(new String[]{"id is mandatory."})
                    .build());
        }
        if (StringUtils.isEmpty(saleRequest.getSalesId())) {
            return Mono.error(GenesisException
                    .builder()
                    .exceptionId(Constants.BAD_REQUEST_EXCEPTION_ID)
                    .wildcards(new String[]{"salesId is mandatory."})
                    .build());
        }

        // Getting token Mcss, request header to create product order service
        String tokenMcss = "";
        for (KeyValueType kv : saleRequest.getAdditionalData()) {
            if (kv.getKey().equals(Constants.UFX_AUTHORIZATION)) {
                tokenMcss = kv.getValue();
            }
        }
        if (tokenMcss == null || tokenMcss.equals("")) {
            request.getHeadersMap().put(Constants.UFX_AUTHORIZATION, "");
        } else {
            request.getHeadersMap().put(Constants.UFX_AUTHORIZATION, tokenMcss);
        }

        // Validation if is retail
        String flowSaleValue = saleRequest.getAdditionalData().stream()
                .filter(keyValueType -> keyValueType.getKey().equalsIgnoreCase("flowSale"))
                .findFirst()
                .orElse(KeyValueType.builder().value(null).build())
                .getValue();
        Boolean isRetail = flowSaleValue.equalsIgnoreCase("Retail");
        Boolean statusValidado = saleRequest.getStatus().equalsIgnoreCase("VALIDADO");
        if (Boolean.TRUE.equals(isRetail) && statusValidado) {
            if (StringUtils.isEmpty(this.getStringValueByKeyFromAdditionalDataList(saleRequest.getAdditionalData(),
                    "MOVILE_IMEI"))) {
                return Mono.error(GenesisException
                        .builder()
                        .exceptionId(Constants.BAD_REQUEST_EXCEPTION_ID)
                        .wildcards(new String[]{"MOVILE_IMEI is mandatory. Must be sent into Additional Data Property "
                                + "with 'MOVILE_IMEI' key value."})
                        .build());
            } else if (StringUtils.isEmpty(this.getStringValueByKeyFromAdditionalDataList(saleRequest
                            .getAdditionalData(),"SIM_ICCID"))) {
                return Mono.error(GenesisException
                        .builder()
                        .exceptionId(Constants.BAD_REQUEST_EXCEPTION_ID)
                        .wildcards(new String[]{"SIM_ICCID is mandatory. Must be sent into Additional Data Property "
                                + "with 'SIM_ICCID' key value."})
                        .build());
            } else if (StringUtils.isEmpty(this.getStringValueByKeyFromAdditionalDataList(saleRequest
                            .getAdditionalData(),"NUMERO_CAJA"))) {
                return Mono.error(GenesisException
                        .builder()
                        .exceptionId(Constants.BAD_REQUEST_EXCEPTION_ID)
                        .wildcards(new String[]{"NUMERO_CAJA is mandatory. Must be sent into Additional Data Property "
                                + "with 'NUMERO_CAJA' key value."})
                        .build());
            } else if (StringUtils.isEmpty(this.getStringValueByKeyFromAdditionalDataList(saleRequest
                            .getAdditionalData(),"NUMERO_TICKET"))) {
                return Mono.error(GenesisException
                        .builder()
                        .exceptionId(Constants.BAD_REQUEST_EXCEPTION_ID)
                        .wildcards(new String[]{"NUMERO_TICKET is mandatory. Must be sent into Additional Data Property"
                                + " with 'NUMERO_TICKET' key value."})
                        .build());
            }
        }

        // Commercial Operations Types Flags
        final Boolean[] flgCapl = {false};
        final Boolean[] flgCaeq = {false};
        final Boolean[] flgCasi = {false};
        final Boolean[] flgAlta = {false};
        final Boolean[] flgFinanciamiento = {false};
        final String[] sapidSimcard = {""};

        // Getting Commercial Operation Types from Additional Data
        for (KeyValueType kv : saleRequest.getCommercialOperation().get(0).getAdditionalData()) {
            String stringKey = kv.getKey();
            Boolean booleanValue = kv.getValue().equalsIgnoreCase("true");

            if (stringKey.equalsIgnoreCase("CAPL")) {
                flgCapl[0] = booleanValue;
            } else if (stringKey.equalsIgnoreCase("CAEQ")) {
                flgCaeq[0] = booleanValue;
            } else if (stringKey.equalsIgnoreCase("CASI")) {
                flgCasi[0] = booleanValue;
            } else if (stringKey.equalsIgnoreCase("ALTA")) {
                flgAlta[0] = booleanValue;
            }
        }

        flgFinanciamiento[0] = setFinancingFlag(saleRequest.getCommercialOperation().get(0).getDeviceOffering());

        // Validar si es que es un reintento
        final Sale[] saleRetry = {null};
        salesRepository.findBySalesId(saleRequest.getSalesId()).map(saleItem -> saleRetry[0] = saleItem);

        if (saleRetry[0] != null && saleRetry[0].getCommercialOperation().get(0).getOrder() != null
                && saleRetry[0].getCommercialOperation().get(0).getDeviceOffering() == null
                && saleRetry[0].getCommercialOperation().get(0).getDeviceOffering().get(0).getStock() == null
                && StringUtils.isEmpty(saleRetry[0].getCommercialOperation().get(0).getDeviceOffering().get(0)
                .getStock().getReservationId())) { // Retry from Reservation

            // Call to Reserve Stock Service When Commercial Operation include CAEQ
            if (flgCaeq[0] || flgAlta[0]) {

                return this.callToReserveStockAndCreateQuotation(PostSalesRequest.builder()
                                .sale(saleRetry[0]).headersMap(request.getHeadersMap())
                                .build(), saleRetry[0], flgCasi[0],
                        flgFinanciamiento[0], sapidSimcard[0]);
            } else {
                if (Boolean.TRUE.equals(flgCasi[0])) {

                    // Call to Create Quotation Service When CommercialOperation Contains CASI
                    return this.callToCreateQuotation(PostSalesRequest.builder()
                            .sale(saleRetry[0]).headersMap(request.getHeadersMap())
                            .build(), saleRetry[0], true, flgFinanciamiento[0]);
                } else {
                    // Case when is Only CAPL
                    return salesRepository.save(saleRetry[0])
                            .map(r -> {
                                this.postSalesEventFlow(PostSalesRequest.builder()
                                        .sale(saleRetry[0]).headersMap(request.getHeadersMap())
                                        .build());
                                return r;
                            });
                }
            }

        } else if (saleRetry[0] != null && saleRetry[0].getCommercialOperation().get(0).getOrder() != null
                && saleRetry[0].getCommercialOperation().get(0).getDeviceOffering() != null
                && saleRetry[0].getCommercialOperation().get(0).getDeviceOffering().get(0).getStock() != null
                && !StringUtils.isEmpty(saleRetry[0].getCommercialOperation().get(0).getDeviceOffering().get(0)
                .getStock().getReservationId())) { // Retry from Create Quotation

            // Call to Create Quotation Service When CommercialOperation Contains CAEQ
            return this.callToCreateQuotation(PostSalesRequest.builder()
                            .sale(saleRetry[0]).headersMap(request.getHeadersMap()).build(),
                    saleRetry[0], flgCasi[0], flgFinanciamiento[0]);
        }

        // Getting Main CommercialTypeOperation value
        String commercialOperationReason = saleRequest.getCommercialOperation().get(0).getReason();
        String mainProductType = saleRequest.getProductType();

        // ALTA FIJA
        if (commercialOperationReason.equalsIgnoreCase("ALTA")
                && mainProductType.equalsIgnoreCase("WIRELINE")
                && saleRequest.getCommercialOperation().get(0).getAction().equalsIgnoreCase("PROVIDE"))
        {
            // Fija Commercial Operations

            return businessParameterWebClient
                    .getParametersFinanciamientoFija(request.getHeadersMap())
                    .map(BusinessParametersFinanciamientoFijaResponse::getData)
                    .map(bpFinanciamientoFijaData -> bpFinanciamientoFijaData.get(0))
                    .map(BusinessParameterFinanciamientoFijaData::getExt)
                    .flatMap(parametersFinanciamientoFija -> processFija(parametersFinanciamientoFija, saleRequest,
                            request, flgFinanciamiento, isRetail));

        } else if ((commercialOperationReason.equalsIgnoreCase("CAPL")
                || commercialOperationReason.equalsIgnoreCase("REPLACEOFFER"))
                && mainProductType.equalsIgnoreCase("WIRELINE")
                && saleRequest.getCommercialOperation().get(0).getAction().equalsIgnoreCase("MODIFY")) {

            String actionType = commercialOperationReason.equalsIgnoreCase("CAPL") ? "CW" : "CH";
            return businessParameterWebClient
                    .getParametersFinanciamientoFija(request.getHeadersMap())
                    .map(BusinessParametersFinanciamientoFijaResponse::getData)
                    .map(bpFinanciamientoFijaData -> bpFinanciamientoFijaData.get(0))
                    .map(BusinessParameterFinanciamientoFijaData::getExt)
                    .flatMap(parametersFinanciamientoFija -> wirelineMigrations(parametersFinanciamientoFija, request,
                            flgFinanciamiento, actionType, isRetail));

        } else if (mainProductType.equalsIgnoreCase(Constants.WIRELESS)) {
            // Mobile Commercial Operations
            Boolean deviceOfferingIsNullOrEmpty = deviceOfferingIsNullOrEmpty(saleRequest);

            if ((saleRequest.getCommercialOperation().get(0).getOrder() == null ||
                    StringUtils.isEmpty(saleRequest.getCommercialOperation().get(0).getOrder().getProductOrderId()))
                    && deviceOfferingIsNullOrEmpty) {

                // Get mail Validation, dominio de riesgo - SERGIO
                Mono<BusinessParametersResponse> getRiskDomain = businessParameterWebClient
                        .getRiskDomain(retrieveDomain(saleRequest.getProspectContact()), request.getHeadersMap());

                // Getting commons request properties
                String channelIdRequest = saleRequest.getChannel().getId();
                String customerIdRequest = saleRequest.getRelatedParty().get(0).getCustomerId();
                String productOfferingIdRequest = saleRequest.getCommercialOperation()
                        .get(0).getProductOfferings().get(0).getId();

                // Getting Characteristics By Main Commercial Operation - Check if is used it to remove
                Mono<List<BusinessParameterExt>> salesCharsByCot = businessParameterWebClient
                        .getSalesCharacteristicsByCommercialOperationType(
                                GetSalesCharacteristicsRequest
                                        .builder()
                                        .commercialOperationType(commercialOperationReason)
                                        .headersMap(request.getHeadersMap())
                                        .build())
                        .map(this::retrieveCharacteristics);

                // Get Bonificacion Simcard
                Mono<BusinessParametersResponseObjectExt> getBonificacionSim = businessParameterWebClient
                        .getBonificacionSimcard(saleRequest.getChannel().getId(), request.getHeadersMap());

                // Get Parameters Simcard
                Mono<BusinessParametersResponseObjectExt> getParametersSimCard = businessParameterWebClient
                        .getParametersSimcard(request.getHeadersMap());

                return Mono.zip(getRiskDomain, salesCharsByCot, getBonificacionSim, getParametersSimCard)
                        .flatMap(tuple -> validationsAndBuildings(tuple.getT1(), tuple.getT2(), tuple.getT3(),
                                tuple.getT4(), saleRequest, request, sapidSimcard, commercialOperationReason, flgCapl,
                                flgCaeq, flgCasi, flgAlta, flgFinanciamiento, channelIdRequest, customerIdRequest,
                                productOfferingIdRequest, isRetail));

            } else {
                return salesRepository.save(saleRequest)
                        .map(r -> {
                            this.postSalesEventFlow(request);
                            return r;
                        });
            }

        }

        return salesRepository.save(saleRequest)
                .map(r -> {
                    this.postSalesEventFlow(request);
                    return r;
                });
    }

    private Boolean deviceOfferingIsNullOrEmpty(Sale saleRequest) {
        if (saleRequest.getCommercialOperation().get(0).getDeviceOffering() == null
                || saleRequest.getCommercialOperation().get(0).getDeviceOffering().isEmpty()){
            return true;
        } else if (saleRequest.getCommercialOperation().get(0).getDeviceOffering().get(0)
                .getStock() == null) {
            return true;
        } else if (StringUtils.isEmpty(saleRequest.getCommercialOperation().get(0).getDeviceOffering().get(0)
                .getStock().getReservationId())) {
            return true;
        }
        return false;
    }

    private Mono<Sale> validationsAndBuildings(BusinessParametersResponse getRiskDomain,
                                               List<BusinessParameterExt> salesCharsByCot,
                                               BusinessParametersResponseObjectExt getBonificacionSim,
                                               BusinessParametersResponseObjectExt getParametersSimCard,
                                               Sale saleRequest,
                                               PostSalesRequest request, final String[] sapidSimcard,
                                               String commercialOperationReason, final Boolean[] flgCapl,
                                               final Boolean[] flgCaeq, final Boolean[] flgCasi,
                                               final Boolean[] flgAlta, final Boolean[] flgFinanciamiento,
                                               String channelIdRequest, String customerIdRequest,
                                               String productOfferingIdRequest, Boolean isRetail) {

        if (!getRiskDomain.getData().isEmpty() && getRiskDomain.getData().get(0).getActive()) {
            // if it is a risk domain, cancel operation
            return Mono.error(GenesisException
                    .builder()
                    .exceptionId("SVR1000")
                    .wildcards(new String[]{"Dominio de riesgo, se canceló la operación"})
                    .build());
        }

        // Getting simcard sapid from bussiness parameter
        sapidSimcard[0] = getStringValueFromBusinessParameterDataListByKeyAndActiveTrue(getParametersSimCard.getData(),
                "sapid");

        // Getting CIP Code
        String cipCode = "";
        if (saleRequest.getCommercialOperation().get(0).getWorkOrDeliveryType() != null
                && !StringUtils.isEmpty(saleRequest.getCommercialOperation().get(0).getWorkOrDeliveryType()
                .getMediumDelivery())
                && saleRequest.getCommercialOperation().get(0).getWorkOrDeliveryType().getMediumDelivery()
                .equalsIgnoreCase("DELIVERY")
                && saleRequest.getPaymenType().getPaymentType().equalsIgnoreCase("EX")
                && this.getStringValueByKeyFromAdditionalDataList(saleRequest.getAdditionalData(),
                "paymentTypeLabel").equals("PAGO EFECTIVO")) {

            cipCode = saleRequest.getPaymenType().getCid(); // Validate if cipCode is empty
        }

        // Building Main Request to send to Create Product Order Service
        CreateProductOrderGeneralRequest mainRequestProductOrder = new CreateProductOrderGeneralRequest();

        // Recognizing Mobile Portability
        Boolean isMobilePortability = commercialOperationReason.equalsIgnoreCase("PORTABILIDAD");

        // Recognizing CAPL Commercial Operation Type
        if (flgCapl[0] && !flgCaeq[0] && !flgCasi[0] && !flgAlta[0]) {

            mainRequestProductOrder = this.caplCommercialOperation(saleRequest, mainRequestProductOrder,
                    channelIdRequest, customerIdRequest, productOfferingIdRequest, cipCode);

        } else if (!flgCapl[0] && flgCaeq[0] && !flgCasi[0] && !flgAlta[0]) { // Recognizing CAEQ Commercial Operation Type

            mainRequestProductOrder = this.caeqCommercialOperation(saleRequest, mainRequestProductOrder,
                    channelIdRequest, customerIdRequest, productOfferingIdRequest, cipCode);

        } else if (flgCapl[0] && flgCaeq[0] && !flgCasi[0] && !flgAlta[0]) { // Recognizing CAEQ+CAPL Commercial Operation Type

            mainRequestProductOrder = this.caeqCaplCommercialOperation(saleRequest, mainRequestProductOrder,
                    channelIdRequest, customerIdRequest, productOfferingIdRequest, cipCode);
        } else if (!flgCapl[0] && !flgCaeq[0] && !flgCasi[0] && flgAlta[0] || isMobilePortability) {

            mainRequestProductOrder = this.altaCommercialOperation(saleRequest, mainRequestProductOrder,
                    channelIdRequest, customerIdRequest, productOfferingIdRequest, cipCode, getBonificacionSim,
                    sapidSimcard[0], isMobilePortability);
        }

        // FEMS-1514 Validación de creación Orden -> solo cuando es flujo retail se debe hacer validación
        Mono<Sale> saleRequestValidation = creationOrderValidation(saleRequest, mainRequestProductOrder,
                request.getHeadersMap());

        if (isRetail && saleRequest.getStatus().equalsIgnoreCase("NEGOCIACION")) {
            return saleRequestValidation.flatMap(salesRepository::save);
        } else {
            CreateProductOrderGeneralRequest finalMainRequestProdOrder = mainRequestProductOrder;
            return productOrderWebClient.createProductOrder(finalMainRequestProdOrder, request.getHeadersMap(),
                    saleRequest).flatMap(createOrderResponse -> {
                        saleRequest.getCommercialOperation().get(0).setOrder(createOrderResponse
                                .getCreateProductOrderResponse());

                        if (validateNegotiation(saleRequest.getAdditionalData(),
                                saleRequest.getIdentityValidations())) {
                            saleRequest.setStatus(Constants.NEGOCIACION);
                        } else if (!StringUtils.isEmpty(createOrderResponse.getCreateProductOrderResponse()
                                .getProductOrderId())) {
                            saleRequest.setStatus("NUEVO");
                        } else {
                            saleRequest.setStatus("PENDIENTE");
                        }
                        saleRequest.setAudioStatus("PENDIENTE");

                        // Ship Delivery logic (tambo) - SERGIO
                        if (saleRequest.getCommercialOperation().get(0).getWorkOrDeliveryType() != null
                                && saleRequest.getCommercialOperation().get(0).getWorkOrDeliveryType()
                                .getMediumDelivery().equalsIgnoreCase("Tienda")) {
                            saleRequest.setAdditionalData(additionalDataAssigments(saleRequest
                                    .getAdditionalData(), saleRequest));
                        }

                        // Call to Reserve Stock Service When Commercial Operation include CAEQ
                        if (flgCaeq[0] || flgAlta[0] || isMobilePortability) {

                            return this.callToReserveStockAndCreateQuotation(PostSalesRequest.builder()
                                            .sale(saleRequest).headersMap(request.getHeadersMap()).build(), saleRequest,
                                    flgCasi[0], flgFinanciamiento[0], sapidSimcard[0]);
                        } else {
                            if (Boolean.TRUE.equals(flgCasi[0])) {
                                // Call to Create Quotation Service When CommercialOperation Contains CASI
                                return this.callToCreateQuotation(PostSalesRequest.builder()
                                                .sale(saleRequest).headersMap(request.getHeadersMap()).build(),
                                        saleRequest, flgCasi[0], flgFinanciamiento[0]);
                            } else {
                                // Case when is Only CAPL
                                return salesRepository.save(saleRequest)
                                        .map(r -> {
                                            this.postSalesEventFlow(PostSalesRequest.builder()
                                                    .sale(saleRequest).headersMap(request.getHeadersMap()).build());
                                            return r;
                                        });
                            }
                        }
                    });
        }
    }

    private void assignBillingOffers(List<OfferingType> productOfferings,
                                     List<NewAssignedBillingOffers> newAssignedBillingOffersCableTvList,
                                     List<NewAssignedBillingOffers> newAssignedBillingOffersBroadbandList,
                                     List<NewAssignedBillingOffers> newAssignedBillingOffersLandlineList) {
        for (int i = 1; i < productOfferings.size(); i++) {
            String productTypeSva = productOfferings.get(i).getProductSpecification().get(0)
                    .getProductType();
            String productTypeComponent = this.getStringValueByKeyFromAdditionalDataList(
                    productOfferings.get(i).getAdditionalData(), "productType"); // Pendiente confirmación de la ruta de referencia del Additional Data

            if (productTypeSva.equalsIgnoreCase("sva")) {

                if (productTypeComponent.equalsIgnoreCase(Constants.PRODUCT_TYPE_CABLE_TV)
                        || productTypeComponent.equalsIgnoreCase(Constants.PRODUCT_TYPE_BROADBAND)
                        || productTypeComponent.equalsIgnoreCase(Constants.PRODUCT_TYPE_LANDLINE)) {

                    NewAssignedBillingOffers newAssignedBillingOffers = NewAssignedBillingOffers
                            .builder()
                            .productSpecPricingId(productOfferings.get(i).getId())
                            .parentProductCatalogId(this.getStringValueByKeyFromAdditionalDataList(
                                    productOfferings.get(i).getAdditionalData(),
                                    "parentProductCatalogID"))
                            .build();

                    if (productTypeComponent.equalsIgnoreCase(Constants.PRODUCT_TYPE_CABLE_TV)) {
                        newAssignedBillingOffersCableTvList.add(newAssignedBillingOffers);
                    } else if (productTypeComponent.equalsIgnoreCase(Constants.PRODUCT_TYPE_BROADBAND)) {
                        newAssignedBillingOffersBroadbandList.add(newAssignedBillingOffers);
                    } else if (productTypeComponent.equalsIgnoreCase(Constants.PRODUCT_TYPE_LANDLINE)) {
                        newAssignedBillingOffersLandlineList.add(newAssignedBillingOffers);
                    }
                }
            }
        }
    }

    private Mono<Sale> processFija(List<BusinessParameterFinanciamientoFijaExt> parametersFinanciamientoFija,
                                   Sale saleRequest, PostSalesRequest request, final Boolean[] flgFinanciamiento,
                                   Boolean isRetail) {
        // Building Create Quotation Request to use into Create Order Request
        CreateQuotationRequest createQuotationFijaRequest = new CreateQuotationRequest();
        if (flgFinanciamiento[0]) {
            this.buildCreateQuotationFijaRequest(createQuotationFijaRequest, request,
                    parametersFinanciamientoFija);
        }

        // Identifying New Assigned Billing Offers SVAs
        List<NewAssignedBillingOffers> newAssignedBillingOffersCableTvList = new ArrayList<>();
        List<NewAssignedBillingOffers> newAssignedBillingOffersBroadbandList = new ArrayList<>();
        List<NewAssignedBillingOffers> newAssignedBillingOffersLandlineList = new ArrayList<>();

        List<OfferingType> productOfferings = saleRequest.getCommercialOperation().get(0)
                .getProductOfferings();

        assignBillingOffers(productOfferings, newAssignedBillingOffersCableTvList,
                newAssignedBillingOffersBroadbandList, newAssignedBillingOffersLandlineList);

        // New Products Alta Fija
        List<NewProductAltaFija> newProductsAltaFijaList = this.buildNewProductsAltaFijaList(saleRequest,
                newAssignedBillingOffersLandlineList,
                newAssignedBillingOffersBroadbandList,
                newAssignedBillingOffersCableTvList);

        // Building ServiceAvailability
        ServiceabilityInfoType serviceabilityInfo = buildServiceabilityInfoType(request);

        // Order Attributes Alta Fija
        List<FlexAttrType> altaFijaOrderAttributesList = new ArrayList<>();
        this.buildOrderAttributesListAltaFija(altaFijaOrderAttributesList, saleRequest,
                createQuotationFijaRequest, flgFinanciamiento[0]);

        AltaFijaRequest altaFijaRequest = new AltaFijaRequest();
        altaFijaRequest.setNewProducts(newProductsAltaFijaList);
        altaFijaRequest.setAppointmentId(saleRequest.getCommercialOperation().get(0).getWorkOrDeliveryType() != null
                && saleRequest.getCommercialOperation().get(0).getWorkOrDeliveryType().getWorkOrder() != null
                && saleRequest.getCommercialOperation().get(0).getWorkOrDeliveryType().getWorkOrder()
                .getWorkForceTeams() != null
                ? saleRequest.getCommercialOperation().get(0).getWorkOrDeliveryType().getWorkOrder().getWorkForceTeams()
                .get(0).getId() : null);
        altaFijaRequest.setAppointmentNumber(saleRequest.getSalesId());
        altaFijaRequest.setServiceabilityInfo(serviceabilityInfo);
        altaFijaRequest.setSourceApp(saleRequest.getSalesId());
        altaFijaRequest.setOrderAttributes(altaFijaOrderAttributesList);
        if (saleRequest.getPaymenType() != null && !StringUtils.isEmpty(saleRequest.getPaymenType().getCid())) {
            altaFijaRequest.setCip(saleRequest.getPaymenType().getCid());
        }
        altaFijaRequest.setUpfrontIndicator(saleRequest.getCommercialOperation().get(0)
                .getProductOfferings().get(0).getUpFront().getIndicator());

        // Alta Fija Customize Request
        ProductOrderAltaFijaRequest productOrderAltaFijaRequest = ProductOrderAltaFijaRequest
                .builder()
                .salesChannel(saleRequest.getChannel().getId())
                .request(altaFijaRequest)
                .customerId(saleRequest.getRelatedParty().get(0).getCustomerId())
                .productOfferingId(saleRequest.getCommercialOperation().get(0)
                        .getProductOfferings().get(0).getId())
                .onlyValidationIndicator(Constants.STRING_FALSE)
                .actionType("PR")
                .build();

        // Building Main Request to send to Create Product Order Service
        CreateProductOrderGeneralRequest mainRequestProductOrder = new CreateProductOrderGeneralRequest();
        mainRequestProductOrder.setCreateProductOrderRequest(productOrderAltaFijaRequest);

        return createOrderFija(mainRequestProductOrder, request, saleRequest, flgFinanciamiento,
                createQuotationFijaRequest, isRetail);
    }

    private Mono<Sale> createOrderFija(CreateProductOrderGeneralRequest mainRequestProductOrder,
                                       PostSalesRequest request, Sale saleRequest, final Boolean[] flgFinanciamiento,
                                       CreateQuotationRequest createQuotationFijaRequest, Boolean isRetail) {

        // FEMS-1514 Validación de creación Orden
        Mono<Sale> saleRequestValidation = creationOrderValidation(saleRequest, mainRequestProductOrder,
                request.getHeadersMap());
        if (isRetail && saleRequest.getStatus().equalsIgnoreCase("NEGOCIACION")) {
            return saleRequestValidation.flatMap(salesRepository::save);
        } else {
            // Call de Create Alta Fija Order
            return productOrderWebClient.createProductOrder(mainRequestProductOrder, request.getHeadersMap(),
                    saleRequest).flatMap(createOrderResponse -> addOrderIntoSale(PostSalesRequest.builder()
                                    .sale(saleRequest).headersMap(request.getHeadersMap()).build(), saleRequest,
                            flgFinanciamiento, createQuotationFijaRequest, createOrderResponse));
        }
    }

    private Mono<Sale> addOrderIntoSale(PostSalesRequest request, Sale saleRequest, final Boolean[] flgFinanciamiento,
                                        CreateQuotationRequest createQuotationFijaRequest,
                                        ProductorderResponse createOrderResponse) {
        // Adding Order info to sales
        saleRequest.getCommercialOperation().get(0)
                .setOrder(createOrderResponse.getCreateProductOrderResponse());

        if (validateNegotiation(saleRequest.getAdditionalData(),
                saleRequest.getIdentityValidations())) {
            saleRequest.setStatus(Constants.NEGOCIACION);
        } else if (!StringUtils.isEmpty(createOrderResponse.getCreateProductOrderResponse()
                .getProductOrderId())) {
            // When All is OK
            saleRequest.setStatus("NUEVO");
        } else {
            // When Create Product Order Service fail or doesnt respond with an Order Id
            saleRequest.setStatus("PENDIENTE");
        }
        saleRequest.setAudioStatus("PENDIENTE");

        if (flgFinanciamiento[0]) {
            return quotationWebClient.createQuotation(createQuotationFijaRequest,
                    saleRequest)
                    .flatMap(createQuotationResponse -> salesRepository.save(saleRequest)
                            .map(r -> {
                                this.postSalesEventFlow(request);
                                return r;
                            }));
        } else {
            return salesRepository.save(saleRequest)
                    .map(r -> {
                        this.postSalesEventFlow(request);
                        return r;
                    });
        }
    }

    private String getStringValueFromBpExtListByParameterName(String parameterName,
                                                              List<BusinessParameterFinanciamientoFijaExt> ext) {
        final String[] stringValue = {""};

        if (!ext.isEmpty()) {
            ext.stream().forEach(bpExt -> {
                if (bpExt.getNomParameter().equals(parameterName)) {
                    stringValue[0] = bpExt.getCodParameterValue();
                }
            });
        }

        return stringValue[0];
    }
    private void buildCreateQuotationFijaRequest(CreateQuotationRequest createQuotationRequest,
                                                 PostSalesRequest salesRequest,
                                                 List<BusinessParameterFinanciamientoFijaExt> bpFinanciamiento) {
        createQuotationRequest.setHeadersMap(salesRequest.getHeadersMap());

        Sale sale = salesRequest.getSale();

        TimePeriod validFor = TimePeriod
                .builder()
                .endDateTime(Commons.getDatetimeNow())
                .startDateTime(Commons.getDatetimeNow())
                .build();
        com.tdp.ms.sales.model.dto.quotation.ContactMedium contactMedium1 = com.tdp.ms.sales.model.dto.quotation
                .ContactMedium
                .builder()
                .type(Constants.EMAIL)
                .name(sale.getProspectContact().get(0).getCharacteristic().getEmailAddress())
                .preferred("true")
                .isActive("true")
                .validFor(validFor)
                .build();
        List<com.tdp.ms.sales.model.dto.quotation.ContactMedium> contactMediumList = new ArrayList<>();
        contactMediumList.add(contactMedium1);

        LegalId legalId = LegalId
                .builder()
                .country("PE")
                .isPrimary("true")
                .nationalId(sale.getRelatedParty().get(0).getNationalId())
                .nationalIdType(sale.getRelatedParty().get(0).getNationalIdType())
                .build();

        Address address = Address
                .builder()
                .streetNr(sale.getCommercialOperation().get(0).getWorkOrDeliveryType().getPlace().get(0).getAddress()
                        .getStreetNr())
                .streetName(sale.getCommercialOperation().get(0).getWorkOrDeliveryType().getPlace().get(0).getAddress()
                        .getStreetName())
                .streetType(sale.getCommercialOperation().get(0).getWorkOrDeliveryType().getPlace().get(0).getAddress()
                        .getStreetType())
                .locality(this.getStringValueByKeyFromAdditionalDataList(sale.getCommercialOperation().get(0)
                        .getWorkOrDeliveryType().getPlace().get(0).getAdditionalData(), "locality"))
                .city(sale.getCommercialOperation().get(0).getWorkOrDeliveryType().getPlace().get(0).getAddress()
                        .getCity())
                .stateOrProvince(sale.getCommercialOperation().get(0).getWorkOrDeliveryType().getPlace().get(0)
                        .getAddress().getStateOrProvince())
                .region(sale.getCommercialOperation().get(0).getWorkOrDeliveryType().getPlace().get(0)
                        .getAddress().getRegion())
                .country(sale.getCommercialOperation().get(0).getWorkOrDeliveryType().getPlace().get(0)
                        .getAddress().getCountry())
                .build();

        Customer customerQuotation = Customer
                .builder()
                .id(sale.getRelatedParty().get(0).getCustomerId())
                .creditScore(sale.getRelatedParty().get(0).getScore().getScore())
                .name(sale.getRelatedParty().get(0).getFirstName())
                .surname(sale.getRelatedParty().get(0).getLastName())
                .segment(this.getStringValueByKeyFromAdditionalDataList(sale.getAdditionalData(),
                        "customerTypeCode"))
                .subsegment(this.getStringValueByKeyFromAdditionalDataList(sale.getAdditionalData(),
                        "customerSubTypeCode"))
                .contactMedia(contactMediumList)
                .legalId(legalId)
                .address(address)
                .creditLimit(sale.getRelatedParty().get(0).getScore().getFinancingCapacity())
                .build();

        Number amountTotalAmount = sale.getCommercialOperation().get(0).getDeviceOffering().get(0).getOffers().get(0)
                .getBillingOfferings().get(0).getCommitmentPeriods().get(0).getFinancingInstalments().get(0)
                .getInstalments().getTotalAmount().getValue().doubleValue() - sale.getCommercialOperation().get(0)
                .getDeviceOffering().get(1).getSimSpecifications().get(0)
                .getPrice().get(0).getValue().doubleValue();

        MoneyAmount totalAmount = MoneyAmount
                .builder()
                .amount(amountTotalAmount.toString())
                .units("")
                .build();

        MoneyAmount associatedPlanRecurrentCost = MoneyAmount
                .builder()
                .amount("0.00")
                .units("PEN")
                .build();

        MoneyAmount totalCustomerRecurrentCost = MoneyAmount
                .builder()
                .amount(sale.getCommercialOperation().get(0).getProductOfferings().get(0).getProductOfferingPrice()
                        .get(0).getMaxPrice().getAmount().toString())
                .units("PEN").build();

        MoneyAmount downPayment = MoneyAmount
                .builder()
                .amount(sale.getCommercialOperation().get(0).getDeviceOffering().get(0).getOffers().get(0)
                        .getBillingOfferings().get(0).getCommitmentPeriods().get(0).getFinancingInstalments()
                        .get(0).getInstalments().getOpeningQuota().getValue().toString())
                .units("PEN")
                .build();

        Site site = Site
                .builder()
                .id(sale.getChannel().getStoreId())
                .build();

        Channel channel = Channel
                .builder()
                .name(sale.getChannel().getId())
                .build();

        // Financiamiento de Instalación
        List<com.tdp.ms.sales.model.dto.quotation.Item> itemsList = new ArrayList<>();
        String salesId = sale.getSalesId();

        if (sale.getCommercialOperation().get(0).getProductOfferings().get(0).getUpFront().getIndicator()
                                                                                .equalsIgnoreCase("N")) {
            String offeringId = this.getStringValueByKeyFromAdditionalDataList(sale.getCommercialOperation().get(0)
                    .getProductOfferings().get(0).getAdditionalData(), "PRODUCT_FOR_INST_FEE")
                    .concat("_")
                    .concat(salesId);

            com.tdp.ms.sales.model.dto.quotation.Item itemInstallation = com.tdp.ms.sales.model.dto.quotation.Item
                    .builder()
                    .offeringId(offeringId)
                    .orderActionId(sale.getCommercialOperation().get(0).getOrder().getProductOrderReferenceNumber())
                    .itemChargeCode(this.getStringValueFromBpExtListByParameterName(
                                                            "chargeCodeInstallation", bpFinanciamiento))
                    .build();
            itemsList.add(itemInstallation);
        }
        // Upgrade de Modem Premium
        if (this.getStringValueByKeyFromAdditionalDataList(sale.getCommercialOperation().get(0).getProductOfferings()
                            .get(0).getAdditionalData(), "modemPremium").equalsIgnoreCase("true")) {
            String offeringId = "EQUP"
                    .concat("_")
                    .concat(salesId);

            com.tdp.ms.sales.model.dto.quotation.Item itemModemPremium = com.tdp.ms.sales.model.dto.quotation.Item
                    .builder()
                    .offeringId(offeringId)
                    .orderActionId(sale.getCommercialOperation().get(0).getOrder().getProductOrderReferenceNumber())
                    .itemChargeCode(this.getStringValueFromBpExtListByParameterName(
                                                            "chargeCodeDevicePremium", bpFinanciamiento))
                    .build();
            itemsList.add(itemModemPremium);
        }
        // Ultra Wifi
        if (this.getStringValueByKeyFromAdditionalDataList(sale.getCommercialOperation().get(0).getProductOfferings()
                .get(0).getAdditionalData(), "ultraWifi").equalsIgnoreCase("true")) {
            String offeringId = "BB"
                    .concat("_")
                    .concat(salesId);

            com.tdp.ms.sales.model.dto.quotation.Item itemUltraWifi = com.tdp.ms.sales.model.dto.quotation.Item
                    .builder()
                    .offeringId(offeringId)
                    .orderActionId(sale.getCommercialOperation().get(0).getOrder().getProductOrderReferenceNumber())
                    .itemChargeCode(this.getStringValueFromBpExtListByParameterName(
                                                            "chargeCodeUltraWifi", bpFinanciamiento))
                    .build();
            itemsList.add(itemUltraWifi);
        }

        // Attributes only to Fija
        final String[] serviceIdLobConcat = {""};

        sale.getCommercialOperation().get(0).getProductOfferings().stream()
                .forEach(productOffering -> {
                    String productSpecificationName = productOffering.getProductSpecification().get(0).getName();
                    if (productSpecificationName.equalsIgnoreCase("TV")) {
                        serviceIdLobConcat[0] = serviceIdLobConcat[0].concat("TV=").concat(productOffering
                                .getProductSpecification().get(0).getRefinedProduct()
                                .getProductCharacteristics().get(0).getId()).concat(";");
                    } else if (productSpecificationName.equalsIgnoreCase(Constants.PRODUCT_TYPE_BROADBAND)) {
                        serviceIdLobConcat[0] = serviceIdLobConcat[0].concat("INT=").concat(productOffering
                                .getProductSpecification().get(0).getRefinedProduct()
                                .getProductCharacteristics().get(0).getId()).concat(";");
                    } else if (productSpecificationName.equalsIgnoreCase("ShEq")) {
                        serviceIdLobConcat[0] = serviceIdLobConcat[0].concat("EQUP=").concat(productOffering
                                .getProductSpecification().get(0).getRefinedProduct()
                                .getProductCharacteristics().get(0).getId());
                    }
                });

        String financialEntity = this.getStringValueFromBpExtListByParameterName("financialEntity",
                                                                                                    bpFinanciamiento);

        CreateQuotationRequestBody body = CreateQuotationRequestBody
                .builder()
                .orderId(sale.getCommercialOperation().get(0).getOrder().getProductOrderId())
                .accountId(sale.getRelatedParty().get(0).getAccountId())
                .billingAgreement(sale.getRelatedParty().get(0).getBillingArragmentId())
                .commercialAgreement("N")
                .serviceIdLobConcat(serviceIdLobConcat[0])
                .customer(customerQuotation)
                .operationType(sale.getCommercialOperation().get(0).getReason())
                .totalAmount(totalAmount)
                .associatedPlanRecurrentCost(associatedPlanRecurrentCost)
                .totalCustomerRecurrentCost(totalCustomerRecurrentCost)
                .downPayment(downPayment)
                .site(site)
                .financialEntity(financialEntity)
                .items(itemsList)
                .channel(channel)
                .build();

        createQuotationRequest.setBody(body);
    }

    private Mono<Sale> creationOrderValidation(Sale saleRequest, CreateProductOrderGeneralRequest productOrderRequest,
                                         HashMap<String, String> headersMap) {
        KeyValueType keyValueType = saleRequest.getAdditionalData().stream()
                .filter(item -> item.getKey().equalsIgnoreCase("flowSale"))
                .findFirst()
                .orElse(null);

        String operationType =
                saleRequest.getCommercialOperation().get(0).getReason().equals("ALTA") ? "Provide" : "Change";

        if (keyValueType != null && keyValueType.getValue().equalsIgnoreCase("Retail")
                && saleRequest.getStatus().equalsIgnoreCase(Constants.NEGOCIACION)) {

            DeviceOffering saleDeviceOffering = saleRequest.getCommercialOperation().get(0).getDeviceOffering().stream()
                    .filter(deviceOffering -> !deviceOffering.getDisplayName().equalsIgnoreCase("simcard")
                            && !deviceOffering.getDisplayName().equalsIgnoreCase("simdevice"))
                    .findFirst()
                    .orElseThrow(() -> buildGenesisError(Constants.BAD_REQUEST_EXCEPTION_ID,
                            "commercialOperation[].deviceOffering[].simSpecifications[0].sapid is missing."));

            Mono<List<GetSkuResponse>> getSku = getSkuWebClient.createSku(saleRequest.getChannel().getId(),
                    "default", saleDeviceOffering.getSapid(),
                    Double.parseDouble(saleDeviceOffering.getCostoPromedioSinIgvSoles()),
                    operationType, "", saleRequest.getChannel().getStoreId(), "2",
                    saleRequest.getChannel().getDealerId(), saleDeviceOffering.getSapid(),
                    saleDeviceOffering.getCostoPromedioSinIgvSoles(), headersMap).collectList();

            // set onlyValidatonIndicator == true
            String classObjectName = productOrderRequest.getCreateProductOrderRequest().getClass().getName();
            int index = classObjectName.lastIndexOf(".");
            classObjectName = classObjectName.substring(index + 1);
            if (classObjectName.equalsIgnoreCase("ProductOrderCaplRequest")) {
                ProductOrderCaplRequest productOrderCaplRequest =
                        (ProductOrderCaplRequest) productOrderRequest.getCreateProductOrderRequest();
                productOrderCaplRequest.setOnlyValidationIndicator("true");
                productOrderRequest.setCreateProductOrderRequest(productOrderCaplRequest);
            } else if (classObjectName.equalsIgnoreCase("ProductOrderCaeqRequest")) {
                ProductOrderCaeqRequest productOrderCaeqRequest =
                        (ProductOrderCaeqRequest) productOrderRequest.getCreateProductOrderRequest();
                productOrderCaeqRequest.setOnlyValidationIndicator("true");
                productOrderRequest.setCreateProductOrderRequest(productOrderCaeqRequest);
            } else if (classObjectName.equalsIgnoreCase("ProductOrderCaeqCaplRequest")) {
                ProductOrderCaeqCaplRequest productOrderCaeqCaplRequest =
                        (ProductOrderCaeqCaplRequest) productOrderRequest.getCreateProductOrderRequest();
                productOrderCaeqCaplRequest.setOnlyValidationIndicator("true");
                productOrderRequest.setCreateProductOrderRequest(productOrderCaeqCaplRequest);
            } else if (classObjectName.equalsIgnoreCase("ProductOrderAltaFijaRequest")) {
                ProductOrderAltaFijaRequest productOrderAltaFijaRequest =
                        (ProductOrderAltaFijaRequest) productOrderRequest.getCreateProductOrderRequest();
                productOrderAltaFijaRequest.setOnlyValidationIndicator("true");
                productOrderRequest.setCreateProductOrderRequest(productOrderAltaFijaRequest);
            } else if (classObjectName.equalsIgnoreCase("ProductOrderAltaMobileRequest")) {
                ProductOrderAltaMobileRequest productOrderAltaMobileRequest =
                        (ProductOrderAltaMobileRequest) productOrderRequest.getCreateProductOrderRequest();
                productOrderAltaMobileRequest.setOnlyValidationIndicator("true");
                productOrderRequest.setCreateProductOrderRequest(productOrderAltaMobileRequest);
            }

            Mono<ProductorderResponse> productOrderResponse =
                    productOrderWebClient.createProductOrder(productOrderRequest, headersMap, saleRequest);

            // Creación del sku
            return Mono.zip(getSku, productOrderResponse).map(tuple -> {
                // añadir respuesta a sale.additionalData y hacer validación de la orden
                saleRequest.getAdditionalData().add(KeyValueType.builder()
                        .key(Constants.DEVICE_SKU)
                        .value(tuple.getT1().get(0).getDeviceType().equals("mobile_phone")
                                ? tuple.getT1().get(0).getSku() : tuple.getT1().get(1).getSku())
                        .build());
                saleRequest.getAdditionalData().add(KeyValueType.builder()
                        .key(Constants.SIM_SKU)
                        .value(tuple.getT1().get(0).getDeviceType().equals("sim")
                                ? tuple.getT1().get(0).getSku() : tuple.getT1().get(1).getSku())
                        .build());

                // cambiar status a "VALIDADO"
                saleRequest.setStatus("VALIDADO");
                return saleRequest;
            });
        } else {
            return Mono.just(saleRequest);
        }
    }

    private Mono<Sale> callToReserveStockAndCreateQuotation(PostSalesRequest request, Sale saleRequest, Boolean flgCasi,
                                             Boolean flgFinanciamiento, String sapidSimcard) {
        ReserveStockRequest reserveStockRequest = new ReserveStockRequest();
        reserveStockRequest = this.buildReserveStockRequest(reserveStockRequest,
                saleRequest, saleRequest.getCommercialOperation().get(0).getOrder(), sapidSimcard);

        return stockWebClient.reserveStock(reserveStockRequest,
                request.getHeadersMap(), saleRequest)
                .flatMap(reserveStockResponse -> {

                    this.setReserveReponseInSales(reserveStockResponse, saleRequest);

                    // Call to Create Quotation Service When CommercialOperation Contains CAEQ
                    return this.callToCreateQuotation(request, saleRequest, flgCasi, flgFinanciamiento);
                });
    }

    private void setReserveReponseInSales(ReserveStockResponse reserveStockResponse, Sale saleRequest) {
        KeyValueType dateKv = KeyValueType
                .builder()
                .key("reservationDate")
                .value(Commons.getDatetimeNow())
                .build();

        if (saleRequest.getCommercialOperation().get(0).getDeviceOffering().get(0).getAdditionalData() == null) {
            saleRequest.getCommercialOperation().get(0).getDeviceOffering().get(0).setAdditionalData(new ArrayList<>());
        }

        saleRequest.getCommercialOperation().get(0).getDeviceOffering()
                .get(0).getAdditionalData().add(dateKv);

        saleRequest.getCommercialOperation().get(0).getDeviceOffering()
                .forEach(deviceOffering -> {
                    if (deviceOffering.getStock() == null) {
                        deviceOffering.setStock(StockType.builder()
                                .reservationId(reserveStockResponse.getId()).build());
                    } else {
                        deviceOffering.getStock().setReservationId(reserveStockResponse.getId());
                    }
                });

        saleRequest.getCommercialOperation().get(0).getDeviceOffering()
                .get(0).getStock()
                .setAmount(reserveStockResponse.getItems()
                        .get(0).getAmount());

        saleRequest.getCommercialOperation().get(0).getDeviceOffering()
                .get(0).getStock()
                .setSite(reserveStockResponse.getItems()
                        .get(0).getSite());
    }

    private Mono<Sale> callToCreateQuotation(PostSalesRequest request, Sale sale, Boolean flgCasi,
                                                                                    Boolean flgFinanciamiento) {

        if (flgFinanciamiento) {
            CreateQuotationRequest createQuotationRequest = new CreateQuotationRequest();
            this.buildCreateQuotationRequest(createQuotationRequest, request, flgCasi);
            return quotationWebClient.createQuotation(createQuotationRequest, sale)
                    .flatMap(createQuotationResponse -> {
                        this.setQuotationResponseInSales(createQuotationResponse,
                                sale);
                        return salesRepository.save(sale)
                                .map(r -> {
                                    this.postSalesEventFlow(request);
                                    return r;
                                });
                    });
        } else {
            return salesRepository.save(sale)
                    .map(r -> {
                        this.postSalesEventFlow(request);
                        return r;
                    });
        }
    }

    private void setQuotationResponseInSales(CreateQuotationResponse quotationResponse, Sale sale) {
        KeyValueType keyValueDateQuotation = KeyValueType
                .builder()
                .key("financingDate")
                .value(Commons.getDatetimeNow())
                .build();
        sale.getAdditionalData().add(keyValueDateQuotation);
        KeyValueType keyValueAmountQuotation = KeyValueType
                .builder()
                .key("amountPerInstalment")
                .value(quotationResponse.getAmountPerInstalment().toString())
                .build();
        sale.getAdditionalData().add(keyValueAmountQuotation);
    }

    private void buildCreateQuotationRequest(CreateQuotationRequest createQuotationRequest,
                                                               PostSalesRequest salesRequest, Boolean flgCasi) {
        createQuotationRequest.setHeadersMap(salesRequest.getHeadersMap());

        Sale sale = salesRequest.getSale();

        TimePeriod validFor = TimePeriod
                .builder()
                .startDateTime(Commons.getDatetimeNow())
                .endDateTime(Commons.getDatetimeNow())
                .build();

        com.tdp.ms.sales.model.dto.quotation.ContactMedium contactMedium1 = com.tdp.ms.sales.model.dto.quotation
                .ContactMedium
                .builder()
                .validFor(validFor)
                .preferred("true")
                .name(sale.getProspectContact().get(0).getCharacteristic().getEmailAddress())
                .isActive("true")
                .type(Constants.EMAIL)
                .build();
        List<com.tdp.ms.sales.model.dto.quotation.ContactMedium> contactMediumList = new ArrayList<>();
        contactMediumList.add(contactMedium1);

        LegalId legalId = LegalId
                .builder()
                .isPrimary("true")
                .country("PE")
                .nationalIdType(sale.getRelatedParty().get(0).getNationalIdType())
                .nationalId(sale.getRelatedParty().get(0).getNationalId())
                .build();

        Address address = Address
                .builder()
                .streetName(sale.getCommercialOperation().get(0).getWorkOrDeliveryType().getPlace().get(0).getAddress()
                                                                                                    .getStreetName())
                .streetNr(sale.getCommercialOperation().get(0).getWorkOrDeliveryType().getPlace().get(0).getAddress()
                                                                                                        .getStreetNr())


                .locality(this.getStringValueByKeyFromAdditionalDataList(sale.getCommercialOperation().get(0)
                                    .getWorkOrDeliveryType().getPlace().get(0).getAdditionalData(), "locality"))
                .streetType(sale.getCommercialOperation().get(0).getWorkOrDeliveryType().getPlace().get(0).getAddress()
                                                                                                    .getStreetType())
                .city(sale.getCommercialOperation().get(0).getWorkOrDeliveryType().getPlace().get(0).getAddress()
                                                                                                            .getCity())
                .stateOrProvince(sale.getCommercialOperation().get(0).getWorkOrDeliveryType().getPlace().get(0)
                                                                                    .getAddress().getStateOrProvince())
                .country(sale.getCommercialOperation().get(0).getWorkOrDeliveryType().getPlace().get(0)
                                                                                            .getAddress().getCountry())
                .region(sale.getCommercialOperation().get(0).getWorkOrDeliveryType().getPlace().get(0)
                                                                                            .getAddress().getRegion())
                .build();

        Customer customerQuotation = Customer
                .builder()
                .creditScore(sale.getRelatedParty().get(0).getScore().getScore())
                .id(sale.getRelatedParty().get(0).getCustomerId())
                .name(sale.getRelatedParty().get(0).getFirstName())
                .surname(sale.getRelatedParty().get(0).getLastName())
                .subsegment(this.getStringValueByKeyFromAdditionalDataList(sale.getAdditionalData(),
                                                                                        "releatedPartySubSegment"))
                .segment(this.getStringValueByKeyFromAdditionalDataList(sale.getAdditionalData(),
                                                                                        "releatedPartySegment"))
                .address(address)
                .legalId(legalId)
                .creditLimit(sale.getRelatedParty().get(0).getScore().getFinancingCapacity())
                .contactMedia(contactMediumList).build();

        Double simAmount = sale.getCommercialOperation().get(0).getDeviceOffering().size() > 1 ?
                sale.getCommercialOperation().get(0).getDeviceOffering().get(1).getSimSpecifications().get(0).getPrice()
                        .get(0).getValue().doubleValue() : 0.0;

        Number amountTotalAmount = sale.getCommercialOperation().get(0).getDeviceOffering().get(0).getOffers().get(0)
                .getBillingOfferings().get(0).getCommitmentPeriods().get(0).getFinancingInstalments().get(0)
                .getInstalments().getTotalAmount().getValue().doubleValue() - simAmount;

        MoneyAmount totalAmount = MoneyAmount
                .builder()
                .units("PEN")
                .amount(amountTotalAmount.toString())
                .build();

        MoneyAmount associatedPlanRecurrentCost = MoneyAmount
                .builder()
                .units("PEN")
                .amount("0.00")
                .build();

        MoneyAmount totalCustomerRecurrentCost = MoneyAmount.builder()
                .units("PEN").amount(sale.getCommercialOperation().get(0).getProductOfferings().get(0)
                                                .getProductOfferingPrice().get(0).getMaxPrice().getAmount().toString())
                .build();

        MoneyAmount downPayment = MoneyAmount
                .builder()
                .amount(sale.getCommercialOperation().get(0).getDeviceOffering().get(0).getOffers().get(0)
                        .getBillingOfferings().get(0).getCommitmentPeriods().get(0).getFinancingInstalments()
                        .get(0).getInstalments().getOpeningQuota().getValue().toString())
                .units("PEN")
                .build();

        Site site = Site
                .builder()
                .id(sale.getChannel().getStoreId())
                .build();

        Channel channel = Channel.builder().name(sale.getChannel().getName()).build();

        MoneyAmount totalCost = MoneyAmount
                .builder().units("PEN")
                .amount(sale.getCommercialOperation().get(0).getDeviceOffering().get(0).getOffers().get(0)
                        .getBillingOfferings().get(0).getCommitmentPeriods().get(0).getFinancingInstalments().get(0)
                        .getInstalments().getTotalAmount().getValue().toString()).build();

        MoneyAmount taxExcludedAmount = MoneyAmount.builder()
                .amount(sale.getCommercialOperation().get(0).getDeviceOffering().get(0).getOffers().get(0)
                        .getBillingOfferings().get(0).getCommitmentPeriods().get(0).getFinancingInstalments().get(0)
                        .getInstalments().getTotalAmount().getValue().toString())
                .units("PEN")
                .build();

        List<com.tdp.ms.sales.model.dto.quotation.Item> itemsList = new ArrayList<>();
        com.tdp.ms.sales.model.dto.quotation.Item itemEquipment = com.tdp.ms.sales.model.dto.quotation.Item
                .builder()
                .taxExcludedAmount(taxExcludedAmount)
                .type("mobile phone")
                .offeringId("EQUIP_FE".concat(sale.getCommercialOperation().get(0).getProduct().getPublicId()))
                .totalCost(totalCost)
                .orderActionId(sale.getCommercialOperation().get(0).getOrder().getProductOrderReferenceNumber())
                .build();
        itemsList.add(itemEquipment);

        if (flgCasi) { // sale.getCommercialOperation().get(0).getDeviceOffering().size() == 2
            MoneyAmount totalCostSim = MoneyAmount
                    .builder()
                    .amount(sale.getCommercialOperation().get(0).getDeviceOffering().get(1).getSimSpecifications()
                                                                        .get(0).getPrice().get(0).getValue().toString())
                    .units("PEN")
                    .build();

            com.tdp.ms.sales.model.dto.quotation.Item itemSim = com.tdp.ms.sales.model.dto.quotation.Item
                    .builder()
                    .offeringId("SIM_FE".concat(sale.getCommercialOperation().get(0).getProduct().getPublicId()))
                    .type("simcard")
                    .publicId(sale.getCommercialOperation().get(0).getProduct().getPublicId())
                    .totalCost(totalCostSim)
                    .build();
            itemsList.add(itemSim);
        }

        CreateQuotationRequestBody body = CreateQuotationRequestBody
                .builder()
                .items(itemsList)
                .billingAgreement(sale.getRelatedParty().get(0).getBillingArragmentId())
                .orderId(sale.getCommercialOperation().get(0).getOrder().getProductOrderId())
                .accountId(sale.getRelatedParty().get(0).getAccountId())
                .commercialAgreement("N")
                .customer(customerQuotation)
                .operationType(sale.getCommercialOperation().get(0).getReason()) // Debe llegar para Alta reason = ALTA y para Porta reason = PORTA
                .totalAmount(totalAmount)
                .downPayment(downPayment)
                .totalCustomerRecurrentCost(totalCustomerRecurrentCost)
                .associatedPlanRecurrentCost(associatedPlanRecurrentCost)
                .site(site)
                .channel(channel)
                .financialEntity(sale.getCommercialOperation().get(0).getDeviceOffering().get(0).getOffers().get(0)
                        .getBillingOfferings().get(0).getCommitmentPeriods().get(0).getFinancingInstalments().get(0)
                                                                                                        .getCodigo())
                .build();

        createQuotationRequest.setBody(body);
    }

    public List<KeyValueType> additionalDataAssigments(List<KeyValueType> input, Sale saleRequest) {
        // add shipmentDetails structure to additionalData
        List<KeyValueType> additionalDataAux = input;
        if (additionalDataAux == null) {
            additionalDataAux = new ArrayList<>();
        }
        // assignments
        KeyValueType mediumDeliveryLabel = KeyValueType.builder()
                .key("mediumDeliveryLabel").value("Chip Tienda").build();
        KeyValueType collectStoreId = KeyValueType.builder()
                .key("collectStoreId").value(saleRequest.getChannel().getStoreId()).build();
        KeyValueType shipmentAddressId = KeyValueType.builder()
                .key("shipmentAddressId").value("").build();
        KeyValueType shipmentSiteId = KeyValueType.builder()
                .key("shipmentSiteId").value("NA").build();
        KeyValueType shipmentInstructions = KeyValueType.builder()
                .key("shipmentInstructions").value("No se registró instrucciones").build();
        additionalDataAux.add(mediumDeliveryLabel);
        additionalDataAux.add(collectStoreId);
        additionalDataAux.add(shipmentAddressId);
        additionalDataAux.add(shipmentSiteId);
        additionalDataAux.add(shipmentInstructions);

        KeyValueType shippingLocality;
        KeyValueType provinceOfShippingAddress;
        KeyValueType shopAddress;

        if (saleRequest.getCommercialOperation() != null && !saleRequest.getCommercialOperation().isEmpty()
                && saleRequest.getCommercialOperation().get(0).getWorkOrDeliveryType().getPlace() != null
                && !saleRequest.getCommercialOperation().get(0).getWorkOrDeliveryType().getPlace().isEmpty()
                && saleRequest.getCommercialOperation().get(0).getWorkOrDeliveryType().getPlace().get(0).getAddress()
                                                                    .getRegion().equalsIgnoreCase("LIMA")) {
            // case when is Lima
            shippingLocality = KeyValueType.builder()
                    .key(SHIPPING_LOCALITY).value("PUEBLO LIBRE").build();
            provinceOfShippingAddress = KeyValueType.builder()
                    .key(PROVINCE_OF_SHIPPING_ADDRESS).value("15").build();
            shopAddress = KeyValueType.builder()
                    .key(SHOP_ADDRESS).value("AV. SUCRE NRO 1183 LIMA-LIMA-PUEBLO").build();

        } else if (saleRequest.getCommercialOperation() != null
                && saleRequest.getCommercialOperation().get(0).getWorkOrDeliveryType().getPlace() != null
                && !saleRequest.getCommercialOperation().isEmpty()
                && !saleRequest.getCommercialOperation().get(0).getWorkOrDeliveryType().getPlace().isEmpty()
                && saleRequest.getCommercialOperation().get(0).getWorkOrDeliveryType().getPlace().get(0).getAddress()
                                                                .getRegion().equalsIgnoreCase("CALLAO")) {
            // case when is Callao
            shippingLocality = KeyValueType.builder()
                    .key(SHIPPING_LOCALITY).value("PUEBLO LIBRE").build();
            provinceOfShippingAddress = KeyValueType.builder()
                    .key(PROVINCE_OF_SHIPPING_ADDRESS).value("07").build();
            shopAddress = KeyValueType.builder()
                    .key(SHOP_ADDRESS).value("AV. SUCRE NRO 1183 LIMA-LIMA-PUEBLO").build();

        } else {
            // case when is not Lima and is not Callao
            shippingLocality = KeyValueType.builder()
                    .key(SHIPPING_LOCALITY).value("TRUJILLO").build();
            provinceOfShippingAddress = KeyValueType.builder()
                    .key(PROVINCE_OF_SHIPPING_ADDRESS).value("13").build();
            shopAddress = KeyValueType.builder()
                    .key(SHOP_ADDRESS).value("AV. AMERICA NORTE 1245 URB. LOS JARDINES - TRUJILLO").build();
        }

        additionalDataAux.add(shippingLocality);
        additionalDataAux.add(provinceOfShippingAddress);
        additionalDataAux.add(shopAddress);


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
        if (identityValidationTypes != null && !identityValidationTypes.isEmpty()) {
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
                    LOG.error("Post Sales Validate Negotiation Exception: " + ex);
                }
            });
        }
        // validate validationType
        if (identityValidationTypes != null && !identityValidationTypes.get(cont[0])
                .getValidationType().equalsIgnoreCase("Biometric")) {
            isBiometric[0] = false;
        }

        return isPresencial[0] && !isBiometric[0];
    }

    private String getStringValueFromBusinessParameterDataListByKeyAndActiveTrue(
            List<BusinessParameterDataObjectExt> businessParameterDataList,
            String key) {

        final String[] stringValue = {""};

        if (businessParameterDataList != null && !businessParameterDataList.isEmpty()) {
            businessParameterDataList.stream().forEach(kv -> {
                if (kv.getKey().equalsIgnoreCase(key) && kv.getActive()) {
                    stringValue[0] = kv.getValue();
                }
            });
        }

        return stringValue[0];
    }

    private PortabilityDetailsType buildMobilePortabilityType(Sale saleRequest) {
        PortabilityDetailsType portabilityDetailsType =  new PortabilityDetailsType();

        // Throw 400 status for mandatory parameters
        PortabilityType portabilityType = saleRequest.getCommercialOperation().get(0).getPortability();
        portabilityDetailsType.setSourceOperator(portabilityType.getReceipt());
        portabilityDetailsType.setServiceType(portabilityType.getProductType());
        portabilityDetailsType.setPlanType(portabilityType.getPlanType());
        portabilityDetailsType.setActivationDate(portabilityType.getDonorActivationDate());
        portabilityDetailsType.setEquipmentCommitmentEndDate(portabilityType.getDonorEquipmentContractEndDate());
        portabilityDetailsType.setSalesDepartment("15");
        portabilityDetailsType.setConsultationId(portabilityType.getIdProcess());
        portabilityDetailsType.setConsultationGroup(portabilityType.getIdProcessGroup());
        portabilityDetailsType.setDocumentType(saleRequest.getRelatedParty().get(0).getNationalIdType());
        portabilityDetailsType.setDocumentNumber(saleRequest.getRelatedParty().get(0).getNationalId());
        portabilityDetailsType.setCustomerName(saleRequest.getRelatedParty().get(0).getFullName());

        String customerEmail = StringUtils.isEmpty(saleRequest.getProspectContact().get(0).getCharacteristic()
                .getEmailAddress()) ? "" : saleRequest.getProspectContact().get(0).getCharacteristic()
                .getEmailAddress();
        portabilityDetailsType.setCustomerEmail(customerEmail);
        portabilityDetailsType.setCustomerContactPhone(portabilityType.getCustomerContactPhone());

        return portabilityDetailsType;
    }

    private CreateProductOrderGeneralRequest altaCommercialOperation(Sale saleRequest,
                                    CreateProductOrderGeneralRequest mainRequestProductOrder, String channelIdRequest,
                                    String customerIdRequest, String productOfferingIdRequest, String cipCode,
                                    BusinessParametersResponseObjectExt bonificacionSimcardResponse,
                                    String sapidSimcardBp, Boolean isMobilePortability) {

        // Building request for ALTA CommercialTypeOperation
        ProductOrderAltaMobileRequest altaRequestProductOrder = new ProductOrderAltaMobileRequest();
        altaRequestProductOrder.setSalesChannel(channelIdRequest);
        com.tdp.ms.sales.model.dto.productorder.Customer customer = com.tdp.ms.sales.model.dto.productorder.Customer
                .builder()
                .customerId(customerIdRequest)
                .build();
        altaRequestProductOrder.setCustomer(customer);
        altaRequestProductOrder.setProductOfferingId(productOfferingIdRequest);
        altaRequestProductOrder.setOnlyValidationIndicator(Constants.STRING_FALSE);
        altaRequestProductOrder.setActionType("PR");

        // Identifying if is Alta Only Simcard or Alta Combo (Equipment + Simcard)
        Boolean altaCombo = saleRequest.getCommercialOperation().get(0).getDeviceOffering() != null
                && saleRequest.getCommercialOperation().get(0).getDeviceOffering().size() > 1;

        // ALTA Product Changes
        ProductChangeAltaMobile altaProductChanges = new ProductChangeAltaMobile();

        // ALTA NewAssignedBillingOffers
        List<NewAssignedBillingOffers> altaNewBoList = new ArrayList<>();

        // NewAssignedBillingOffer Plan
        NewAssignedBillingOffers altaNewBo1 = NewAssignedBillingOffers
                .builder()
                .productSpecPricingId(saleRequest.getCommercialOperation().get(0).getProductOfferings().get(0)
                        .getProductOfferingPrice().get(0).getPricePlanSpecContainmentId())
                .parentProductCatalogId(saleRequest.getCommercialOperation().get(0).getProductOfferings().get(0)
                        .getProductOfferingPrice().get(0).getProductSpecContainmentId())
                .build();
        altaNewBoList.add(altaNewBo1);

        if (saleRequest.getChannel().getId().equalsIgnoreCase("CC")) {
            // NewAssignedBillingOffer SIM
            String productSpecPricingId = bonificacionSimcardResponse.getData().get(0).getValue(); // "34572615"
            String parentProductCatalogId = bonificacionSimcardResponse.getData().get(0).getExt().toString(); // "7431"

            NewAssignedBillingOffers altaNewBo2 = NewAssignedBillingOffers
                    .builder()
                    .productSpecPricingId(productSpecPricingId)
                    .parentProductCatalogId(parentProductCatalogId)
                    .build();
            altaNewBoList.add(altaNewBo2);
        }

        altaProductChanges.setNewAssignedBillingOffers(altaNewBoList);

        // ALTA ChangeContainedProducts
        List<ChangedContainedProduct> altaChangedContainedProductList = new ArrayList<>();

        if (altaCombo) {
            // ChangeContainedProduct Equipment
            altaChangedContainedProductList = this.changedContainedCaeqList(saleRequest, "temp2");
            //altaChangedContainedProductList.get(0).setProductId(""); // Doesnt sent it in Alta
        }

        // ChangeContainedProduct SIM
        List<ChangedCharacteristic> changedCharacteristicList = new ArrayList<>();

        // SIM TYPE SKU Characteristic
        ChangedCharacteristic changedCharacteristic1 = ChangedCharacteristic
                .builder()
                .characteristicId("9751")
                .characteristicValue(sapidSimcardBp) // SAPID PARAMETRIZADO EN BP
                .build();
        changedCharacteristicList.add(changedCharacteristic1);

        // ICCID Characteristic
        String flowSaleValue = saleRequest.getAdditionalData().stream()
                .filter(keyValueType -> keyValueType.getKey().equalsIgnoreCase("flowSale"))
                .findFirst()
                .orElse(KeyValueType.builder().value(null).build())
                .getValue();
        Boolean isRetail = flowSaleValue.equalsIgnoreCase("Retail");
        if (Boolean.TRUE.equals(isRetail) && saleRequest.getStatus().equalsIgnoreCase("VALIDADO")) {
            String iccidSim = this.getStringValueByKeyFromAdditionalDataList(saleRequest.getAdditionalData(),
                    "SIM_ICCID");
            ChangedCharacteristic changedCharacteristic2 = ChangedCharacteristic
                    .builder()
                    .characteristicId("799244")
                    .characteristicValue(iccidSim) // 8958080008100067567
                    .build();
            changedCharacteristicList.add(changedCharacteristic2);
        }

        ChangedContainedProduct changedContainedProduct2 = ChangedContainedProduct
                .builder()
                .temporaryId("temp3")
                .productCatalogId("7431")
                .changedCharacteristics(changedCharacteristicList)
                .build();
        altaChangedContainedProductList.add(changedContainedProduct2);

        altaProductChanges.setChangedContainedProducts(altaChangedContainedProductList);

        if (isMobilePortability) {
            PortabilityDetailsType portabilityDetailsType = this.buildMobilePortabilityType(saleRequest);

            altaProductChanges.setPortabilityDetails(portabilityDetailsType);
        }


        NewProductAltaMobile newProductAlta1 = new NewProductAltaMobile();
        newProductAlta1.setProductCatalogId(saleRequest.getCommercialOperation().get(0)
                    .getProductOfferings().get(0).getProductOfferingProductSpecId());
        newProductAlta1.setTemporaryId(Constants.TEMP1);
        newProductAlta1.setBaId(saleRequest.getRelatedParty().get(0).getBillingArragmentId());
        newProductAlta1.setAccountId(saleRequest.getRelatedParty().get(0).getAccountId());
        newProductAlta1.setInvoiceCompany("TEF");
        newProductAlta1.setProductChanges(altaProductChanges);

        List<NewProductAltaMobile> altaNewProductsList = new ArrayList<>();
        altaNewProductsList.add(newProductAlta1);

        // Building Order Attributes
        List<FlexAttrType> altaOrderAttributesList = this.commonOrderAttributes(saleRequest);

        // Order Attributes when channel is retail
        if (Boolean.TRUE.equals(isRetail) && saleRequest.getStatus().equalsIgnoreCase("VALIDADO")) {
            //  RETAIL PAYMENT NUMBER ATTRIBUTE
            String paymentNumber = this.getStringValueByKeyFromAdditionalDataList(saleRequest.getAdditionalData(),
                    "NUMERO_TICKET");

            FlexAttrValueType paymentRegisterAttrValue =  FlexAttrValueType
                    .builder()
                    .stringValue(paymentNumber)
                    .valueType(Constants.STRING)
                    .build();
            FlexAttrType paymentRegisterAttr = FlexAttrType
                    .builder()
                    .attrName("PAYMENT_REGISTER_NUMBER")
                    .flexAttrValue(paymentRegisterAttrValue)
                    .build();

            //  RETAIL DEVICE SKU ATTRIBUTE
            String deviceSku = this.getStringValueByKeyFromAdditionalDataList(saleRequest.getAdditionalData(),
                    Constants.DEVICE_SKU);

            FlexAttrValueType deviceSkuAttrValue =  FlexAttrValueType
                    .builder()
                    .stringValue(deviceSku)
                    .valueType(Constants.STRING)
                    .build();
            FlexAttrType deviceSkuAttr = FlexAttrType
                    .builder()
                    .attrName(Constants.DEVICE_SKU)
                    .flexAttrValue(deviceSkuAttrValue)
                    .build();

            //  RETAIL SIM SKU ATTRIBUTE
            String simSku = this.getStringValueByKeyFromAdditionalDataList(saleRequest.getAdditionalData(),
                                                                                                    Constants.SIM_SKU);

            FlexAttrValueType simSkuAttrValue =  FlexAttrValueType
                    .builder()
                    .stringValue(simSku)
                    .valueType(Constants.STRING)
                    .build();
            FlexAttrType simSkuAttr = FlexAttrType
                    .builder()
                    .attrName(Constants.SIM_SKU)
                    .flexAttrValue(simSkuAttrValue)
                    .build();

            //  RETAIL CASHIER REGISTER NUMBER ATTRIBUTE
            String cashierRegisterNumber = this.getStringValueByKeyFromAdditionalDataList(saleRequest
                                                                            .getAdditionalData(),"NUMERO_CAJA");

            FlexAttrValueType cashierRegisterAttrValue =  FlexAttrValueType
                    .builder()
                    .stringValue(cashierRegisterNumber)
                    .valueType(Constants.STRING)
                    .build();
            FlexAttrType cashierRegisterAttr = FlexAttrType
                    .builder()
                    .attrName(Constants.SIM_SKU)
                    .flexAttrValue(cashierRegisterAttrValue)
                    .build();

            altaOrderAttributesList.add(paymentRegisterAttr);
            altaOrderAttributesList.add(deviceSkuAttr);
            altaOrderAttributesList.add(simSkuAttr);
            altaOrderAttributesList.add(cashierRegisterAttr);
        }

        String deliveryMethod = this.getStringValueByKeyFromAdditionalDataList(saleRequest.getAdditionalData(),
                "deliveryMethod");
        AltaMobileRequest altaRequest = AltaMobileRequest
                .builder()
                .newProducts(altaNewProductsList)
                .sourceApp("FE")
                .orderAttributes(altaOrderAttributesList)
                .shipmentDetails(!StringUtils.isEmpty(deliveryMethod) && !deliveryMethod.equals("IS") && saleRequest
                        .getCommercialOperation().get(0).getWorkOrDeliveryType() != null ?
                        createShipmentDetail(saleRequest): null)
                .build();

        // Building Main Alta Request
        altaRequestProductOrder.setRequest(altaRequest);

        // Setting Alta request into main request to send to create product order service
        mainRequestProductOrder.setCreateProductOrderRequest(altaRequestProductOrder);

        return mainRequestProductOrder;
    }

    public CreateProductOrderGeneralRequest caplCommercialOperation(Sale saleRequest,
                                    CreateProductOrderGeneralRequest mainRequestProductOrder, String channelIdRequest,
                                    String customerIdRequest, String productOfferingIdRequest, String cipCode) {
        Boolean flgOnlyCapl = true;

        // Recognizing Capl into same plan or Capl with new plan
        if (!saleRequest.getCommercialOperation().get(0).getProduct().getProductOffering().getId().equals(saleRequest
                .getCommercialOperation().get(0).getProductOfferings().get(0).getId())
        ) {
            flgOnlyCapl = false;
        }

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

            caplBoRemoved1.setBillingOfferId(this.getStringValueByKeyFromAdditionalDataList(saleRequest
                    .getCommercialOperation().get(0).getProduct().getAdditionalData(), "billingOfferId"));
            caplBoRemovedList.add(caplBoRemoved1);
        } else {
            caplRequestProductOrder.setActionType("CH");
        }

        NewAssignedBillingOffers caplNewBo1 = NewAssignedBillingOffers
                .builder()
                .productSpecPricingId(saleRequest.getCommercialOperation().get(0)
                        .getProductOfferings().get(0).getProductOfferingPrice().get(0).getPricePlanSpecContainmentId())
                .parentProductCatalogId(saleRequest.getCommercialOperation().get(0)
                        .getProductOfferings().get(0).getProductOfferingPrice().get(0).getProductSpecContainmentId())
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
                    .getProductOfferings().get(0).getProductOfferingProductSpecId());
        }
        newProductCapl1.setProductChanges(caplProductChanges);

        // Refactored Code from CAPL
        List<FlexAttrType> caplOrderAttributes = this.commonOrderAttributes(saleRequest);

        List<NewProductCapl> caplNewProductsList = new ArrayList<>();
        caplNewProductsList.add(newProductCapl1);

        String deliveryMethod = this.getStringValueByKeyFromAdditionalDataList(saleRequest.getAdditionalData(),
                "deliveryMethod");
        CaplRequest caplRequest = CaplRequest
                .builder()
                .newProducts(caplNewProductsList)
                .sourceApp("FE")
                .orderAttributes(caplOrderAttributes)
                .shipmentDetails(!StringUtils.isEmpty(deliveryMethod) && !deliveryMethod.equals("IS") && saleRequest
                        .getCommercialOperation().get(0).getWorkOrDeliveryType() != null ?
                        createShipmentDetail(saleRequest): null)
                .build();
        //if (!StringUtils.isEmpty(cipCode)) caplRequest.setCip(cipCode);

        // Building Main Capl Request
        caplRequestProductOrder.setRequest(caplRequest);

        // Setting capl request into main request to send to create product order service
        mainRequestProductOrder.setCreateProductOrderRequest(caplRequestProductOrder);

        return mainRequestProductOrder;
    }

    public ShipmentDetailsType createShipmentDetail(Sale saleRequest) {
        ShipmentDetailsType shipmentDetailsType = ShipmentDetailsType.builder()
                .recipientFirstName(saleRequest.getRelatedParty().get(0).getFirstName())
                .recipientLastName(saleRequest.getRelatedParty().get(0).getLastName()).build();
        shipmentDetailsType.setRecipientTelephoneNumber(saleRequest.getCommercialOperation().get(0).getWorkOrDeliveryType().getContact().getPhoneNumber());
        shipmentDetailsType.setShippingLocality(saleRequest.getCommercialOperation().get(0).getWorkOrDeliveryType().getPlace().get(0).getAddress().getStateOrProvince());
        shipmentDetailsType.setShipmentAddressId(saleRequest.getCommercialOperation().get(0).getWorkOrDeliveryType().getPlace().get(0).getId());
        shipmentDetailsType.setShipmentSiteId("NA");
        shipmentDetailsType.setRecipientEmail(saleRequest.getCommercialOperation().get(0).getWorkOrDeliveryType().getContact().getEmailAddress());
        // additional Datas
        saleRequest.getAdditionalData().stream().forEach(item -> {
            if (item.getKey().equalsIgnoreCase("shipmentInstructions")) {
                shipmentDetailsType.setShipmentInstructions(item.getValue());
            } else if (item.getKey().equalsIgnoreCase("shipmentOption")) {
                shipmentDetailsType.setShipmentOption(item.getValue());
            }
        });

        CollectionUtils.emptyIfNull(saleRequest.getCommercialOperation().get(0).getWorkOrDeliveryType()
                .getAdditionalData())
                .stream().forEach(item -> {
            if (item.getKey().equalsIgnoreCase(SHOP_ADDRESS)) {
                shipmentDetailsType.setShopAddress(item.getValue());
            } else if (item.getKey().equalsIgnoreCase("shopName")) {
                shipmentDetailsType.setShopName(item.getValue());
            } else if (item.getKey().equalsIgnoreCase("collectStoreId")) {
                shipmentDetailsType.setCollectStoreId(item.getValue());
            }
        });

        CollectionUtils.emptyIfNull(saleRequest.getCommercialOperation().get(0).getWorkOrDeliveryType().getPlace()
                .get(0).getAdditionalData())
                .stream().forEach(item -> {
                    if (item.getKey().equalsIgnoreCase("stateOrProvinceCode")) {
                        shipmentDetailsType.setProvinceOfShippingAddress(item.getValue());
                    }
                });

        return shipmentDetailsType;
    }

    public CreateProductOrderGeneralRequest caeqCommercialOperation(Sale saleRequest,
                                    CreateProductOrderGeneralRequest mainRequestProductOrder, String channelIdRequest,
                                    String customerIdRequest, String productOfferingIdRequest, String cipCode) {
        // Building request for CAEQ CommercialTypeOperation

        // Refactored Code from CAEQ
        List<ChangedContainedProduct> changedContainedProductList = this.changedContainedCaeqList(saleRequest, Constants.TEMP1);

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

        // Refactored Code from CAPL
        List<FlexAttrType> caeqCaplOrderAttributes = this.commonOrderAttributes(saleRequest);

        // Order Attributes
        //List<FlexAttrType> caeqOrderAttributes = new ArrayList<>();
        this.addCaeqOderAttributes(caeqCaplOrderAttributes, saleRequest);

        String deliveryMethod = this.getStringValueByKeyFromAdditionalDataList(saleRequest.getAdditionalData(),
                "deliveryMethod");
        CaeqRequest caeqRequest = CaeqRequest
                .builder()
                .sourceApp("FE")
                .newProducts(newProductCaeqList)
                .orderAttributes(caeqCaplOrderAttributes.isEmpty() ? null : caeqCaplOrderAttributes)
                .shipmentDetails(!StringUtils.isEmpty(deliveryMethod) && !deliveryMethod.equals("IS") && saleRequest
                        .getCommercialOperation().get(0).getWorkOrDeliveryType() != null ?
                        createShipmentDetail(saleRequest): null)
                .build();
        if (!StringUtils.isEmpty(cipCode)) {
            caeqRequest.setCip(cipCode);
        }

        ProductOrderCaeqRequest caeqProductOrderRequest = new ProductOrderCaeqRequest();
        caeqProductOrderRequest.setSalesChannel(channelIdRequest);
        caeqProductOrderRequest.getCustomer().setCustomerId(customerIdRequest);
        caeqProductOrderRequest.setProductOfferingId(productOfferingIdRequest);
        caeqProductOrderRequest.setOnlyValidationIndicator(Constants.STRING_FALSE);
        caeqProductOrderRequest.setActionType("CW");
        caeqProductOrderRequest.setRequest(caeqRequest);

        // Setting capl request into main request to send to create product order service
        mainRequestProductOrder.setCreateProductOrderRequest(caeqProductOrderRequest);

        return mainRequestProductOrder;
    }

    public CreateProductOrderGeneralRequest caeqCaplCommercialOperation(Sale saleRequest,
                                    CreateProductOrderGeneralRequest mainRequestProductOrder, String channelIdRequest,
                                    String customerIdRequest, String productOfferingIdRequest, String cipCode) {
        // Building request for CAEQ+CAPL CommercialTypeOperation

        Boolean flgOnlyCapl = true;

        // Recognizing Capl into same plan or Capl with new plan
        if (!saleRequest.getCommercialOperation().get(0).getProduct().getProductOffering().getId().equals(saleRequest
                .getCommercialOperation().get(0).getProductOfferings().get(0).getId())
        ) {
            flgOnlyCapl = false;
        }

        // Code from CAPL
        ProductOrderCaeqCaplRequest caeqCaplRequestProductOrder = new ProductOrderCaeqCaplRequest();
        caeqCaplRequestProductOrder.setSalesChannel(channelIdRequest);
        caeqCaplRequestProductOrder.getCustomer().setCustomerId(customerIdRequest);
        caeqCaplRequestProductOrder.setProductOfferingId(productOfferingIdRequest);
        caeqCaplRequestProductOrder.setOnlyValidationIndicator(Constants.STRING_FALSE);

        RemovedAssignedBillingOffers caeqCaplBoRemoved1 = new RemovedAssignedBillingOffers();
        List<RemovedAssignedBillingOffers> caeqCaplBoRemovedList = new ArrayList<>();
        if (flgOnlyCapl) {
            // Recognizing Capl Fija
            if (saleRequest.getProductType().equalsIgnoreCase(Constants.WIRELESS)) {
                caeqCaplRequestProductOrder.setActionType("CW");
            } else {
                caeqCaplRequestProductOrder.setActionType("CH"); // landline, cableTv, broadband, bundle
            }

            caeqCaplBoRemoved1.setBillingOfferId(this.getStringValueByKeyFromAdditionalDataList(saleRequest
                    .getCommercialOperation().get(0).getProduct().getAdditionalData(), "billingOfferId"));
            caeqCaplBoRemovedList.add(caeqCaplBoRemoved1);
        } else {
            caeqCaplRequestProductOrder.setActionType("CH");
        }

        NewAssignedBillingOffers caplNewBo1 = NewAssignedBillingOffers
                .builder()
                .productSpecPricingId(saleRequest.getCommercialOperation().get(0)
                        .getProductOfferings().get(0).getProductOfferingPrice().get(0).getPricePlanSpecContainmentId())
                .parentProductCatalogId(saleRequest.getCommercialOperation().get(0)
                        .getProductOfferings().get(0).getProductOfferingPrice().get(0).getProductSpecContainmentId())
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
        List<ChangedContainedProduct> changedContainedProductList = this.changedContainedCaeqList(saleRequest,
                                                                                                    Constants.TEMP1);

        caeqCaplProductChanges.setChangedContainedProducts(changedContainedProductList);
        newProductCaeqCapl1.setProductChanges(caeqCaplProductChanges);

        List<NewProductCaeqCapl> caeqCaplNewProductList = new ArrayList<>();
        caeqCaplNewProductList.add(newProductCaeqCapl1);

        // Refactored Code from CAPL
        List<FlexAttrType> caeqCaplOrderAttributes = this.commonOrderAttributes(saleRequest);
        // Adding Caeq Order Attributes
        this.addCaeqOderAttributes(caeqCaplOrderAttributes, saleRequest);

        String deliveryMethod = this.getStringValueByKeyFromAdditionalDataList(saleRequest.getAdditionalData(),
                "deliveryMethod");
        CaeqCaplRequest caeqCaplRequest = CaeqCaplRequest
                .builder()
                .newProducts(caeqCaplNewProductList)
                .sourceApp("FE")
                .orderAttributes(caeqCaplOrderAttributes)
                .shipmentDetails(!StringUtils.isEmpty(deliveryMethod) && !deliveryMethod.equals("IS") && saleRequest
                        .getCommercialOperation().get(0).getWorkOrDeliveryType() != null ?
                        createShipmentDetail(saleRequest): null)
                .build();
        //if (!StringUtils.isEmpty(cipCode)) caeqCaplRequest.setCip(cipCode);

        caeqCaplRequestProductOrder.setRequest(caeqCaplRequest);

        // Setting capl request into main request to send to create product order service
        mainRequestProductOrder.setCreateProductOrderRequest(caeqCaplRequestProductOrder);

        return mainRequestProductOrder;
    }

    public List<FlexAttrType> commonOrderAttributes(Sale saleRequest) {
        // Building Common Order Attributes
        List<FlexAttrType> commonOrderAttributes = new ArrayList<>();

        // Delivery Method Attribute
        String deliveryCode = "";
        for (KeyValueType kv : saleRequest.getAdditionalData()) {
            if (kv.getKey().equals("deliveryMethod")) {
                deliveryCode = kv.getValue();
            }
        }
        String flowSaleValue = saleRequest.getAdditionalData().stream()
                .filter(keyValueType -> keyValueType.getKey().equalsIgnoreCase("flowSale"))
                .findFirst()
                .orElse(KeyValueType.builder().value(null).build())
                .getValue();
        Boolean isRetail = flowSaleValue.equalsIgnoreCase("Retail");
        if (isRetail) {
            deliveryCode = "IS";
        }
        if (!StringUtils.isEmpty(deliveryCode)) {
            FlexAttrValueType deliveryAttrValue =  FlexAttrValueType
                    .builder()
                    .stringValue(deliveryCode)
                    .valueType(Constants.STRING)
                    .build();
            FlexAttrType deliveryAttr = FlexAttrType
                    .builder()
                    .attrName("DELIVERY_METHOD")
                    .flexAttrValue(deliveryAttrValue)
                    .build();
            commonOrderAttributes.add(deliveryAttr);
        }

        // Payment Method Attribute - Conditional
        if (!isRetail && saleRequest.getPaymenType() != null && !StringUtils.isEmpty(saleRequest.getPaymenType()
                .getPaymentType())) {
            FlexAttrValueType paymentAttrValue =  FlexAttrValueType
                    .builder()
                    .stringValue(saleRequest.getPaymenType().getPaymentType())
                    .valueType(Constants.STRING)
                    .build();
            FlexAttrType paymentAttr = FlexAttrType
                    .builder()
                    .attrName("PAYMENT_METHOD")
                    .flexAttrValue(paymentAttrValue)
                    .build();
            commonOrderAttributes.add(paymentAttr);
        }

        return commonOrderAttributes;
    }

    public void addCaeqOderAttributes(List<FlexAttrType> caeqOrderAttributes, Sale saleRequest) {
        // Adding CAEQ Attributes
        String documentTypeValue = "";

        documentTypeValue = this.getStringValueByKeyFromAdditionalDataList(saleRequest.getPaymenType()
                                                                        .getAdditionalData(), "paymentDocument");
        if (!documentTypeValue.isEmpty()) {
            if (documentTypeValue.equalsIgnoreCase("Boleta")) {
                documentTypeValue = "BO";
            } else if (documentTypeValue.equalsIgnoreCase("Factura")) {
                documentTypeValue = "FA";
            }
            FlexAttrValueType deliveryAttrValue =  FlexAttrValueType
                    .builder()
                    .stringValue(documentTypeValue)
                    .valueType(Constants.STRING)
                    .build();
            FlexAttrType documentTypeAttr = FlexAttrType
                    .builder()
                    .attrName("DOCUMENT_TYPE")
                    .flexAttrValue(deliveryAttrValue)
                    .build();
            caeqOrderAttributes.add(documentTypeAttr);
        }

        String customerRuc = saleRequest.getRelatedParty().size() < 2
                                    || StringUtils.isEmpty(saleRequest.getRelatedParty().get(1).getNationalId()) ? ""
                                                                : saleRequest.getRelatedParty().get(1).getNationalId();

        if (!customerRuc.isEmpty()) {
            FlexAttrValueType paymentAttrValue =  FlexAttrValueType
                    .builder()
                    .stringValue(customerRuc)
                    .valueType(Constants.STRING)
                    .build();
            FlexAttrType customerRucAttr = FlexAttrType
                    .builder()
                    .attrName("CUSTOMER_RUC")
                    .flexAttrValue(paymentAttrValue)
                    .build();
            caeqOrderAttributes.add(customerRucAttr);
        }
    }

    public List<ChangedContainedProduct> changedContainedCaeqList(Sale saleRequest, String tempNum) {
        String acquisitionType = "";
        acquisitionType = getAcquisitionTypeValue(saleRequest);

        // AcquisitionType Characteristic
        ChangedCharacteristic changedCharacteristic1 = ChangedCharacteristic
                .builder()
                .characteristicId("9941")
                .characteristicValue(acquisitionType)
                .build();

        // EquipmentCID Characteristic
        ChangedCharacteristic changedCharacteristic2 = ChangedCharacteristic
                .builder()
                .characteristicId("15734")
                .characteristicValue(saleRequest.getCommercialOperation().get(0).getDeviceOffering().stream()
                        .filter(item -> !item.getDeviceType().equalsIgnoreCase("SIM"))
                        .findFirst()
                        .orElse(null)
                        .getId())
                .build();

        // EquipmentIMEI Characteristic
        String deviceImei = "000000000000000";
        Boolean isRetail = getRetailFlag(saleRequest);
        if (isRetail && saleRequest.getStatus().equalsIgnoreCase(Constants.VALIDADO)) {
            deviceImei = this.getStringValueByKeyFromAdditionalDataList(saleRequest.getAdditionalData(),
                                                                                                    "MOVILE_IMEI");
        }
        ChangedCharacteristic changedCharacteristic3 = ChangedCharacteristic
                .builder()
                .characteristicId("9871")
                .characteristicValue(deviceImei)
                .build();

        List<ChangedCharacteristic> changedCharacteristicList = new ArrayList<>();
        changedCharacteristicList.add(changedCharacteristic1);
        changedCharacteristicList.add(changedCharacteristic2);
        changedCharacteristicList.add(changedCharacteristic3);

        // SIMGROUP Characteristic (Conditional)
        if (saleRequest.getCommercialOperation().get(0).getDeviceOffering().size() == 1) {
            ChangedCharacteristic changedCharacteristic4 = ChangedCharacteristic
                    .builder()
                    .characteristicId("16524")
                    .characteristicValue(saleRequest.getCommercialOperation().get(0).getDeviceOffering().get(0)
                            .getSimSpecifications().get(0).getType())
                    .build();
            changedCharacteristicList.add(changedCharacteristic4);

        } else if (saleRequest.getCommercialOperation().get(0).getDeviceOffering().size() > 1) {
            String simGroup = saleRequest.getCommercialOperation().get(0).getDeviceOffering().get(1)
                                                                            .getSimSpecifications().get(0).getType();
            ChangedCharacteristic changedCharacteristic4 = ChangedCharacteristic
                    .builder()
                    .characteristicId("16524")
                    .characteristicValue(simGroup)
                    .build();
            changedCharacteristicList.add(changedCharacteristic4);
        }

        ChangedContainedProduct changedContainedProduct1 = ChangedContainedProduct
                .builder()
                .temporaryId(tempNum)
                .productCatalogId("7411")
                .changedCharacteristics(changedCharacteristicList)
                .build();

        if (!saleRequest.getCommercialOperation().get(0).getReason().equalsIgnoreCase("PORTA")
                && !saleRequest.getCommercialOperation().get(0).getReason().equalsIgnoreCase("ALTA")
                && saleRequest.getProductType().equalsIgnoreCase(Constants.WIRELESS)
                && saleRequest.getCommercialOperation().get(0).getProduct().getProductRelationShip() != null) {
            // FEMS-3799 (CR)
            setChangedContainedProductProductId(changedContainedProduct1, saleRequest.getCommercialOperation().get(0)
                    .getProduct().getProductRelationShip());
        }

        List<ChangedContainedProduct> changedContainedProductList = new ArrayList<>();
        changedContainedProductList.add(changedContainedProduct1);

        return changedContainedProductList;
    }

    private void setChangedContainedProductProductId(ChangedContainedProduct changedContainedProduct,
                                                     List<RelatedProductType> productRelationShip) {
        // FEMS-3799 (CR)
        productRelationShip.stream()
                .filter(item -> item.getProduct().getDescription().equalsIgnoreCase("SimDevice"))
                .findFirst()
                .ifPresent(item -> item.getProduct().getProductRelationship().stream()
                        .filter(pr -> pr.getProduct().getDescription().equalsIgnoreCase("Device"))
                        .findFirst()
                        .ifPresent(pr -> changedContainedProduct.setProductId(pr.getProduct().getId()))
                );
    }

    private String getAcquisitionTypeValue(Sale saleRequest) {
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

        // Getting Commercial Operation Types from Additional Data
        final Boolean[] flgAlta = {false};
        saleRequest.getCommercialOperation().get(0).getAdditionalData().stream()
                .forEach(additionalData -> {
                    String stringKey = additionalData.getKey();
                    Boolean booleanValue = additionalData.getValue().equalsIgnoreCase("true");
                    if (stringKey.equalsIgnoreCase("ALTA")) {
                        flgAlta[0] = booleanValue;
                    }
                });

        // Logic for Set Acquisition Type Value
        if (flgAlta[0] && saleRequest.getCommercialOperation().get(0).getDeviceOffering().size() == 1) { // Identifying if is Alta only Sim
            acquisitionType = "Private";
        } else if (saleChannelId.equalsIgnoreCase("CC") && deliveryType.equalsIgnoreCase("SP")
                || saleChannelId.equalsIgnoreCase("CEC")
                || saleChannelId.equalsIgnoreCase("ST") && deliveryType.equalsIgnoreCase("SP")
                || saleChannelId.equalsIgnoreCase("DLS")
        ) {
            acquisitionType = "ConsessionPurchased";
        } else if (saleChannelId.equalsIgnoreCase("ST") && deliveryType.equalsIgnoreCase("IS")
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

    private ReserveStockRequest buildReserveStockRequest(ReserveStockRequest request, Sale sale,
                                                        CreateProductOrderResponseType createOrderResponse,
                                                        String sapidSimcardBp) {
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

        List<StockItem> itemsList =  new ArrayList<>();

        // Equipment Item
        Item item1 = Item
                .builder()
                .id(sale.getCommercialOperation().get(0).getDeviceOffering().stream()
                        .filter(item -> !item.getDeviceType().equalsIgnoreCase("SIM"))
                        .findFirst()
                        .orElse(DeviceOffering.builder().sapid(null).build())
                        .getSapid())
                .type("IMEI")
                .build();
        StockItem stockItem1 = StockItem
                .builder()
                .item(item1)
                .build();
        itemsList.add(stockItem1);

        if (sale.getCommercialOperation().get(0).getDeviceOffering().size() > 1) {
            // SIM Item
            Item item2 = Item
                    .builder()
                    .id(sapidSimcardBp)
                    .type("ICCID")
                    .build();
            StockItem stockItem2 = StockItem
                    .builder()
                    .item(item2)
                    .build();
            itemsList.add(stockItem2);
        }
        request.setItems(itemsList);

        request.setOrderAction(createOrderResponse.getProductOrderReferenceNumber());

        Order order = Order
                .builder()
                .id(createOrderResponse.getProductOrderId())
                .build();
        request.setOrder(order);

        return  request;
    }

    private Boolean compareComponents(String productTypeName) {
        return productTypeName.equalsIgnoreCase(Constants.PRODUCT_TYPE_BROADBAND)
                || productTypeName.equalsIgnoreCase("cableTv")
                || productTypeName.equalsIgnoreCase("device")
                || productTypeName.equalsIgnoreCase("landline")
                || productTypeName.equalsIgnoreCase("accessories");
    }

    private Mono<Sale> wirelineMigrations(List<BusinessParameterFinanciamientoFijaExt> parametersFinanciamientoFija,
                                          PostSalesRequest request, final Boolean[] flgFinanciamiento,
                                          String actionType, Boolean isRetail) {

        // Building Create Quotation Request to use into Create Order Request
        CreateQuotationRequest createQuotationFijaRequest = new CreateQuotationRequest();
        if (flgFinanciamiento[0]) {
            this.buildCreateQuotationFijaRequest(createQuotationFijaRequest, request,
                    parametersFinanciamientoFija);
        }

        List<CommercialOperationType> commercialOperationTypeList = request.getSale().getCommercialOperation();

        List<RelatedProductType> productRelationShip = commercialOperationTypeList.get(0).getProduct().getProductRelationShip();
        List<ComposingProductType> productSpecification = commercialOperationTypeList.get(0).getProductOfferings()
                .get(0).getProductSpecification();

        List<MigrationComponent> migrationComponentList = new ArrayList<>();
        // Paso 1: Obtener en Id de componente de parque
        productRelationShip.forEach(item -> {
            if (compareComponents(item.getProduct().getProductType())) {

                String productId = item.getProduct().getId();
                migrationComponentList.add(MigrationComponent.builder()
                        .componentName(item.getProduct().getProductType())
                        .productId(StringUtils.isEmpty(productId) ? null : productId)
                        .build());
            }
        });

        // Paso 2: Obtener ID de plan de Oferta por cada componente
        fillProductOfferingProductSpecId(migrationComponentList, productSpecification);

        // Paso 3 y 4: Se debe enviar PLAN BAF en el componente broadband
        List<NewProductMigracionFija> newProducts = broadbandPlanBAF(migrationComponentList, productRelationShip,
                commercialOperationTypeList.get(0).getProductOfferings());

        ServiceabilityInfoType serviceabilityInfo = null;
        if (actionType.equalsIgnoreCase("CH")) {
            // Sí se envía sección service availability
            serviceabilityInfo = buildServiceabilityInfoType(request);
        }

        // Order Attributes para Fija
        List<FlexAttrType> migracionFijaOrderAttributesList = new ArrayList<>();
        this.buildOrderAttributesListAltaFija(migracionFijaOrderAttributesList, request.getSale(),
                createQuotationFijaRequest, flgFinanciamiento[0]);

        MigracionFijaRequest migracionFijaRequest = new MigracionFijaRequest();
        migracionFijaRequest.setNewProducts(newProducts);
        migracionFijaRequest.setAppointmentId(request.getSale().getCommercialOperation().get(0)
                .getWorkOrDeliveryType() != null
                ? request.getSale().getCommercialOperation().get(0).getWorkOrDeliveryType().getWorkOrder()
                .getWorkForceTeams().get(0).getId() : null);
        migracionFijaRequest.setAppointmentNumber(request.getSale().getSalesId());
        migracionFijaRequest.setServiceabilityInfo(serviceabilityInfo);
        migracionFijaRequest.setSourceApp(request.getSale().getSalesId());
        migracionFijaRequest.setOrderAttributes(migracionFijaOrderAttributesList);
        migracionFijaRequest.setCip(!StringUtils.isEmpty(request.getSale().getPaymenType().getCid()) ?
                request.getSale().getPaymenType().getCid() : null);
        migracionFijaRequest.setUpfrontIndicator(request.getSale().getCommercialOperation().get(0).getProductOfferings()
                .get(0).getUpFront().getIndicator());

        ProductOrderMigracionFijaRequest productOrderMigracionFijaRequest = ProductOrderMigracionFijaRequest.builder()
                .actionType(actionType)
                .onlyValidationIndicator(Constants.STRING_FALSE)
                .request(migracionFijaRequest)
                .salesChannel(request.getSale().getChannel().getId())
                .customer(com.tdp.ms.sales.model.dto.productorder.Customer.builder()
                        .customerId(request.getSale().getRelatedParty().get(0).getCustomerId())
                        .build())
                .productOfferingId(request.getSale().getCommercialOperation().get(0).getProductOfferings().get(0)
                        .getId())
                .build();

        CreateProductOrderGeneralRequest mainRequestProductOrder = new CreateProductOrderGeneralRequest();
        mainRequestProductOrder.setCreateProductOrderRequest(productOrderMigracionFijaRequest);

        return createOrderFija(mainRequestProductOrder, request, request.getSale(), flgFinanciamiento,
                createQuotationFijaRequest, isRetail);
    }

    private ServiceabilityInfoType buildServiceabilityInfoType(PostSalesRequest request) {
        List<ServiceabilityOfferType> serviceabilityOffersList = new ArrayList<>();
        this.buildServiceAvailabilityAltaFija(request.getSale(), serviceabilityOffersList);

        // CommercialZoneType
        CommercialZoneType commercialZone = CommercialZoneType
                .builder()
                .commercialZoneId(request.getSale().getCommercialOperation().get(0).getServiceAvailability()
                        .getCommercialAreaId())
                .commercialZoneName(request.getSale().getCommercialOperation().get(0).getServiceAvailability()
                        .getCommercialAreaDescription())
                .build();

        return ServiceabilityInfoType
                .builder()
                .serviceabilityId(request.getSale().getCommercialOperation().get(0)
                        .getServiceAvailability().getId())
                .offers(serviceabilityOffersList.isEmpty() ? null : serviceabilityOffersList)
                .commercialZone(commercialZone)
                .build();
    }

    private List<NewProductMigracionFija> broadbandPlanBAF(List<MigrationComponent> migrationComponentList,
                                                           List<RelatedProductType> productRelationShip,
                                                           List<OfferingType> productOfferings) {
        final String[] productId = {null};
        productRelationShip.stream()
                .filter(item -> item.getProduct().getProductType().equalsIgnoreCase(Constants.PRODUCT_TYPE_BROADBAND)
                        && item.getProduct().getName().equalsIgnoreCase("Internet_Plan"))
                .findFirst()
                .ifPresent(item -> productId[0] = item.getProduct().getId());

        final String[] characteristicValue = {null};
        productOfferings.get(0).getProductSpecification().stream().filter(item -> item.getProductType()
                .equalsIgnoreCase(Constants.PRODUCT_TYPE_BROADBAND))
                .findFirst()
                .ifPresent(item -> {
                    characteristicValue[0] = item.getProductPrice().get(3).getAdditionalData().stream()
                            .filter(keyValueType -> keyValueType.getKey().equalsIgnoreCase("downloadSpeed"))
                            .findFirst()
                            .orElseThrow(() -> buildGenesisError(Constants.BAD_REQUEST_EXCEPTION_ID,
                                    "Cannot get time in milliseconds."))
                            .getValue();
                });

        // Asignar variables a migrationComponentList[].productChanges (broadband)
        migrationComponentList.stream()
                .filter(migrationComponent ->
                        migrationComponent.getComponentName().equalsIgnoreCase(Constants.PRODUCT_TYPE_BROADBAND))
                .findFirst()
                .ifPresent(migrationComponent -> {
                    try {
                        migrationComponent.setProductChanges(ProductChangeAltaFija
                                .builder()
                                .changedContainedProducts(Collections.singletonList(ChangedContainedProduct
                                        .builder()
                                        .productId(productId[0]).temporaryId("temp")
                                        .changedCharacteristics(Arrays.asList(ChangedCharacteristic.builder()
                                                        .characteristicId("3241482")
                                                        .characteristicValue(characteristicValue[0])
                                                        .build(),
                                                ChangedCharacteristic.builder()
                                                        .characteristicId("3241532")
                                                        .characteristicValue(Commons.getTimeNowInMillis())
                                                        .build()))
                                        .build()))
                                .build());
                    } catch (ParseException e) {
                        throw buildGenesisError(Constants.BAD_REQUEST_EXCEPTION_ID,
                                "Cannot get time in milliseconds.");
                    }
                });

        // Paso 6: Envío de SVA
        productOfferings.forEach(productOffering -> {
            if (productOffering.getProductSpecification().get(0).getProductType().equalsIgnoreCase("sva")) {
                String productType = getStringValueByKeyFromAdditionalDataList(productOffering.getAdditionalData(),
                        "productType");
                if (productType.equalsIgnoreCase(Constants.PRODUCT_TYPE_CABLE_TV)
                        || productType.equalsIgnoreCase(Constants.PRODUCT_TYPE_CHANNEL_TV)
                        || productType.equalsIgnoreCase(Constants.PRODUCT_TYPE_DEVICE)
                        || productType.equalsIgnoreCase(Constants.PRODUCT_TYPE_SHEQ)
                        || productType.equalsIgnoreCase(Constants.PRODUCT_TYPE_BROADBAND)
                        || productType.equalsIgnoreCase(Constants.PRODUCT_TYPE_LANDLINE)) {

                    NewAssignedBillingOffers newAssignedBillingOffers = NewAssignedBillingOffers.builder()
                            .productSpecPricingId(productOffering.getId())
                            .parentProductCatalogId(this.getStringValueByKeyFromAdditionalDataList(
                                    productOffering.getAdditionalData(), "parentProductCatalogID"))
                            .build();

                    if (productType.equalsIgnoreCase(Constants.PRODUCT_TYPE_CHANNEL_TV)) {
                        productType = Constants.PRODUCT_TYPE_CABLE_TV;
                    }

                    String finalProductType = productType;
                    migrationComponentList.stream()
                            .filter(item -> item.getComponentName().equalsIgnoreCase(finalProductType))
                            .findFirst()
                            .ifPresent(item -> {
                                if (item.getProductChanges().getNewAssignedBillingOffers() == null) {
                                    item.getProductChanges().setNewAssignedBillingOffers(new ArrayList<>());
                                }
                                item.getProductChanges().getNewAssignedBillingOffers().add(newAssignedBillingOffers);
                            });
                }
            }
        });

        // Paso 3: relacionar el plan de oferta con ID de parque según sea cada componente
        return matchingMigrationOffers(migrationComponentList);
    }

    private GenesisException buildGenesisError(String exceptionId, String description) {
        return GenesisException
                .builder()
                .exceptionId(exceptionId)
                .wildcards(new String[]{description})
                .build();
    }

    private List<NewProductMigracionFija> matchingMigrationOffers(List<MigrationComponent> migrationComponentList) {
        List<NewProductMigracionFija> newProducts = new ArrayList<>();
        migrationComponentList.forEach(migrationComponent -> {
            newProducts.add(NewProductMigracionFija.builder()
                    .productCatalogId(migrationComponent.getProductOfferingProductSpecId())
                    .productId(migrationComponent.getProductId())
                    .productChanges(migrationComponent.getProductChanges())
                    .build());
        });
        return newProducts;
    }

    private void fillProductOfferingProductSpecId(List<MigrationComponent> migrationComponentList,
                                                  List<ComposingProductType> productSpecificationList) {
        productSpecificationList.forEach(productSpecification -> {
            String productTypeName = productSpecification.getProductType();
            if (Boolean.TRUE.equals(compareComponents(productTypeName))) {

                ProductSpecCharacteristicType productCharacteristic = productSpecification.getRefinedProduct()
                        .getProductCharacteristics().stream()
                        .filter(item -> item.getName().equalsIgnoreCase("productOfferingProductSpecID"))
                        .findFirst()
                        .orElse(null);

                MigrationComponent migrationComponent = migrationComponentList.stream()
                        .filter(migrationComponentItem ->
                                migrationComponentItem.getComponentName().equalsIgnoreCase(productTypeName))
                        .findFirst()
                        .orElse(null);

                if (migrationComponent != null) {
                    // TODO: validar qué campo es productCharacteristics.productSpecCharacteristicValue.value. Por ahora se usa roductCharacteristics.id
                    migrationComponent.setProductOfferingProductSpecId(productCharacteristic.getId());
                } else {
                    migrationComponentList.add(MigrationComponent.builder()
                            .componentName(productTypeName)
                            .productId(null)
                            .productOfferingProductSpecId(productCharacteristic.getId())
                            .build());
                }

            }
        });
    }
}
