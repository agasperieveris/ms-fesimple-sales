package com.tdp.ms.sales.business.impl;

import com.tdp.genesis.core.exception.GenesisException;
import com.tdp.ms.commons.util.DateUtils;
import com.tdp.ms.sales.business.SalesManagmentService;
import com.tdp.ms.sales.client.*;
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
import com.tdp.ms.sales.model.response.*;
import com.tdp.ms.sales.repository.SalesRepository;
import com.tdp.ms.sales.utils.Commons;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
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
    private QuotationWebClient quotationWebClient;

    @Autowired
    private GetSkuWebClient getSkuWebClient;

    @Autowired
    private WebClientReceptor webClientReceptor;

    private static final String FLOW_SALE_POST = "01";

    private final static String SHIPPING_LOCALITY = "shippingLocality";
    private final static String PROVINCE_OF_SHIPPING_ADDRESS = "provinceOfShippingAddress";
    private final static String SHOP_ADDRESS = "shopAddress";

    private static final Logger LOG = LoggerFactory.getLogger(SalesManagmentServiceImpl.class);

    public List<BusinessParameterExt> retrieveCharacteristics(GetSalesCharacteristicsResponse response) {
        System.out.println("retrieveCharacteristics: " + response.getData().get(0).getExt());
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

    private String getStringValueByKeyFromAdditionalDataList(List<KeyValueType> additionalData, String key) {
        final String[] stringValue = {""};

        if (additionalData != null && !additionalData.isEmpty()) {
            additionalData.stream().forEach(kv -> {
                if (kv.getKey().equalsIgnoreCase(key)) {
                    stringValue[0] = kv.getValue();
                }
            });
        }

        return stringValue[0];
    }

    private void postSalesEventFlow(PostSalesRequest request) {
        if(request.getSale().getAdditionalData()!=null) {
            request.getSale().getAdditionalData().add(
                    KeyValueType
                            .builder()
                            .key("initialProcessDate")
                            .value(DateUtils.getDatetimeNowCosmosDbFormat())
                            .build()
            );
        } else {
            request.getSale().setAdditionalData(new ArrayList<>());
            request.getSale().getAdditionalData().add(
                    KeyValueType
                            .builder()
                            .key("initialProcessDate")
                            .value(DateUtils.getDatetimeNowCosmosDbFormat())
                            .build()
            );
        }

        // Llamada a receptor
        webClientReceptor
                .register(
                        ReceptorRequest
                                .builder()
                                .businessId(request.getSale().getSalesId())
                                .typeEventFlow(FLOW_SALE_POST)
                                .message(request)
                                .build(),
                        request.getHeadersMap()
                )
                .subscribe();
    }

    @Override
    public Mono<Sale> post(PostSalesRequest request) {

        // Getting Sale object
        Sale saleRequest = request.getSale();

        if (StringUtils.isEmpty(saleRequest.getId())) {
            return Mono.error(GenesisException
                    .builder()
                    .exceptionId("SVC1000")
                    .wildcards(new String[]{"id is mandatory."})
                    .build());
        }
        if (StringUtils.isEmpty(saleRequest.getSalesId())) {
            return Mono.error(GenesisException
                    .builder()
                    .exceptionId("SVC1000")
                    .wildcards(new String[]{"salesId is mandatory."})
                    .build());
        }

        // Getting token Mcss, request header to create product order service
        String tokenMcss = "";
        for (KeyValueType kv : saleRequest.getAdditionalData()) {
            if (kv.getKey().equals("ufxauthorization")) {
                System.out.println("ufxauthorization VALUE: " + kv.getValue());
                tokenMcss = kv.getValue();
            }
        }
        System.out.println("TOKEEEN: " + tokenMcss);
        if (tokenMcss == null || tokenMcss.equals("")) {
            return Mono.error(GenesisException
                    .builder()
                    .exceptionId("SVC1000")
                    .wildcards(new String[]{"Token MCSS is mandatory. Must be sent into Additional Data Property "
                            + "with 'ufxauthorization' key value."})
                    .build());
        }
        request.getHeadersMap().put("ufxauthorization", tokenMcss);

        // Validation if is retail
        String channelId = saleRequest.getChannel().getId();
        Boolean isRetail = channelId.equalsIgnoreCase("DLC") || channelId.equalsIgnoreCase("DLV")
                || channelId.equalsIgnoreCase("DLS");
        if (isRetail) {
            if (StringUtils.isEmpty(this.getStringValueByKeyFromAdditionalDataList(saleRequest.getAdditionalData(),
                    "MOVILE_IMEI"))) {
                return Mono.error(GenesisException
                        .builder()
                        .exceptionId("SVC1000")
                        .wildcards(new String[]{"MOVILE_IMEI is mandatory. Must be sent into Additional Data Property "
                                + "with 'MOVILE_IMEI' key value."})
                        .build());
            } else if (StringUtils.isEmpty(this.getStringValueByKeyFromAdditionalDataList(saleRequest.getAdditionalData(),
                    "SIM_ICCID"))) {
                return Mono.error(GenesisException
                        .builder()
                        .exceptionId("SVC1000")
                        .wildcards(new String[]{"SIM_ICCID is mandatory. Must be sent into Additional Data Property "
                                + "with 'SIM_ICCID' key value."})
                        .build());
            } else if (StringUtils.isEmpty(this.getStringValueByKeyFromAdditionalDataList(saleRequest.getAdditionalData(),
                    "NUMERO_CAJA"))) {
                return Mono.error(GenesisException
                        .builder()
                        .exceptionId("SVC1000")
                        .wildcards(new String[]{"NUMERO_CAJA is mandatory. Must be sent into Additional Data Property "
                                + "with 'NUMERO_CAJA' key value."})
                        .build());
            } else if (StringUtils.isEmpty(this.getStringValueByKeyFromAdditionalDataList(saleRequest.getAdditionalData(),
                    "NUMERO_TICKET"))) {
                return Mono.error(GenesisException
                        .builder()
                        .exceptionId("SVC1000")
                        .wildcards(new String[]{"NUMERO_TICKET is mandatory. Must be sent into Additional Data Property "
                                + "with 'NUMERO_TICKET' key value."})
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

        flgFinanciamiento[0] = !StringUtils.isEmpty(saleRequest.getCommercialOperation().get(0).getDeviceOffering()
                .get(0).getOffers().get(0).getBillingOfferings().get(0).getCommitmentPeriods().get(0)
                .getFinancingInstalments().get(0).getDescription()) && !saleRequest.getCommercialOperation().get(0)
                .getDeviceOffering().get(0).getOffers().get(0).getBillingOfferings().get(0).getCommitmentPeriods()
                .get(0).getFinancingInstalments().get(0).getDescription().equals("CONTADO");

        // Getting Main CommercialTypeOperation value
        String commercialOperationReason = saleRequest.getCommercialOperation().get(0).getReason();
        String mainProductType = saleRequest.getProductType();
        System.out.println("MAIN COMMERCIAL OPERATION TYPE: " + commercialOperationReason);

        // ALTA FIJA
        if (commercialOperationReason.equalsIgnoreCase("ALTA")
                && mainProductType.equalsIgnoreCase("WIRELINE")
                && saleRequest.getCommercialOperation().get(0).getAction().equalsIgnoreCase("PROVIDE"))
        {
            // Fija Commercial Operations

            // Building Create Quotation Request to use into Create Order Request
            CreateQuotationRequest createQuotationFijaRequest = new CreateQuotationRequest();
            if (flgFinanciamiento[0]) this.buildCreateQuotationFijaRequest(createQuotationFijaRequest, request);

            // Identifying New Assigned Billing Offers SVAs
            List<NewAssignedBillingOffers> newAssignedBillingOffersCableTvList = new ArrayList<>();
            List<NewAssignedBillingOffers> newAssignedBillingOffersBroadbandList = new ArrayList<>();
            List<NewAssignedBillingOffers> newAssignedBillingOffersLandlineList = new ArrayList<>();

            List<OfferingType> productOfferings = saleRequest.getCommercialOperation().get(0).getProductOfferings();
            for (int i = 1; i < productOfferings.size(); i++) {
                String productTypeSva = productOfferings.get(i).getProductSpecification().get(0).getProductType();
                String productTypeComponent = this.getStringValueByKeyFromAdditionalDataList(productOfferings.get(i)
                        .getAdditionalData(), "productType"); // Pendiente confirmación de la ruta de referencia del Additional Data

                if (productTypeSva.equalsIgnoreCase("sva")) {

                    if (productTypeComponent.equalsIgnoreCase("cableTv")
                            || productTypeComponent.equalsIgnoreCase("broadband")
                            || productTypeComponent.equalsIgnoreCase("landline")) {

                        NewAssignedBillingOffers newAssignedBillingOffers = NewAssignedBillingOffers
                                .builder()
                                .productSpecPricingId(productOfferings.get(i).getId())
                                .parentProductCatalogId(this.getStringValueByKeyFromAdditionalDataList(productOfferings
                                        .get(i).getAdditionalData(), "parentProductCatalogID"))
                                .build();
                        if (productTypeComponent.equalsIgnoreCase("cableTv")) newAssignedBillingOffersCableTvList.add(newAssignedBillingOffers);
                        if (productTypeComponent.equalsIgnoreCase("broadband")) newAssignedBillingOffersBroadbandList.add(newAssignedBillingOffers);
                        if (productTypeComponent.equalsIgnoreCase("landline")) newAssignedBillingOffersLandlineList.add(newAssignedBillingOffers);
                    }
                }
            }

            // New Products Alta Fija
            List<NewProductAltaFija> newProductsAltaFijaList = new ArrayList<>();
            String baId = saleRequest.getRelatedParty().get(0).getBillingArragmentId();
            String accountId = saleRequest.getRelatedParty().get(0).getAccountId();

            saleRequest.getCommercialOperation().get(0).getProductOfferings().get(0).getProductSpecification().stream()
                    .forEach(productSpecification -> {

                        String productType = productSpecification.getProductType();
                        if (productType.equalsIgnoreCase("landline")) {

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
                                    .characteristicValue(saleRequest.getCommercialOperation().get(0)
                                            .getProductOfferings().get(0).getProductOfferingPrice().get(0)
                                                                            .getBenefits().get(0).getDownloadSpeed())
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
                                    .productId("") // Empty when is alta fija
                                    .build();

                            List<ChangedContainedProduct> changedContainedProductsBroadbandList = new ArrayList<>();
                            changedContainedProductsBroadbandList.add(changedContainedProductBroadband1);

                            ProductChangeAltaFija productChangesBroadband = ProductChangeAltaFija
                                    .builder()
                                    .changedContainedProducts(changedContainedProductsBroadbandList)
                                    .build();
                            //Adding Broadband SVAs
                            if (!newAssignedBillingOffersBroadbandList.isEmpty()) {
                                productChangesBroadband.setNewAssignedBillingOffers(newAssignedBillingOffersBroadbandList);
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

                        } else if (productType.equalsIgnoreCase("cableTv")) {

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

                        } else if (productType.equalsIgnoreCase("Accesories")) {

                            ChangedCharacteristic changedCharacteristicAccesories1 = ChangedCharacteristic
                                    .builder()
                                    .characteristicId("15734")
                                    .characteristicValue("34203411")
                                    .build();

                            List<ChangedCharacteristic> changedCharacteristicsAccesoriesList = new ArrayList<>();
                            changedCharacteristicsAccesoriesList.add(changedCharacteristicAccesories1);

                            ChangedContainedProduct changedContainedProductAccesories1 = ChangedContainedProduct
                                    .builder()
                                    .temporaryId("temp")
                                    .productCatalogId("34134811")
                                    .changedCharacteristics(changedCharacteristicsAccesoriesList)
                                    .productId("") // Empty when is alta fija
                                    .build();

                            List<ChangedContainedProduct> changedContainedProductsAccesoriesList = new ArrayList<>();
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

            // Building ServiceAvailability

            // ServiceAvailability Offers
            List<ServiceabilityOfferType> serviceabilityOffersList = new ArrayList<>();
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
                                    .networkTechnology(this.getStringValueByKeyFromAdditionalDataList(saleRequest.
                                            getCommercialOperation().get(0).getServiceAvailability()
                                            .getAdditionalData(),"networkAccessTechnologyLandline"))
                                    .serviceTechnology(this.getStringValueByKeyFromAdditionalDataList(saleRequest.
                                            getCommercialOperation().get(0).getServiceAvailability()
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
                                    .networkTechnology(this.getStringValueByKeyFromAdditionalDataList(saleRequest.
                                            getCommercialOperation().get(0).getServiceAvailability()
                                            .getAdditionalData(),"networkAccessTechnologyBroadband"))
                                    .serviceTechnology(this.getStringValueByKeyFromAdditionalDataList(saleRequest.
                                            getCommercialOperation().get(0).getServiceAvailability()
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
                                    .networkTechnology(this.getStringValueByKeyFromAdditionalDataList(saleRequest.
                                            getCommercialOperation().get(0).getServiceAvailability()
                                            .getAdditionalData(),"networkAccessTechnologyTv"))
                                    .serviceTechnology(this.getStringValueByKeyFromAdditionalDataList(saleRequest.
                                            getCommercialOperation().get(0).getServiceAvailability()
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

            // CommercialZoneType
            CommercialZoneType commercialZone = CommercialZoneType
                    .builder()
                    .commercialZoneId(saleRequest.getCommercialOperation().get(0).getServiceAvailability()
                                                                                                .getCommercialAreaId())
                    .commercialZoneName(saleRequest.getCommercialOperation().get(0).getServiceAvailability()
                                                                                        .getCommercialAreaDescription())
                    .build();

            ServiceabilityInfoType serviceabilityInfo = ServiceabilityInfoType
                    .builder()
                    .serviceabilityId(saleRequest.getCommercialOperation().get(0).getServiceAvailability().getId())
                    .offers(serviceabilityOffersList)
                    .commercialZone(commercialZone)
                    .build();

            // Order Attributes Alta Fija
            FlexAttrValueType externalFinancialAttrValue =  FlexAttrValueType
                    .builder()
                    .stringValue(flgFinanciamiento[0]? "Y" : "N")
                    .valueType("STRING")
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
                    .valueType("STRING")
                    .build();
            FlexAttrType upFrontIndAttr = FlexAttrType
                    .builder()
                    .attrName("UPFRONT_IND")
                    .flexAttrValue(upFrontIndAttrValue)
                    .build();

            FlexAttrValueType paymentMethodAttrValue =  FlexAttrValueType
                    .builder()
                    .stringValue("EX")
                    .valueType("STRING")
                    .build();
            FlexAttrType paymentMethodAttr = FlexAttrType
                    .builder()
                    .attrName("PAYMENT_METHOD")
                    .flexAttrValue(paymentMethodAttrValue)
                    .build();

            List<FlexAttrType> altaFijaOrderAttributesList = new ArrayList<>();
            altaFijaOrderAttributesList.add(externalFinancialAttr);
            altaFijaOrderAttributesList.add(upFrontIndAttr);
            altaFijaOrderAttributesList.add(paymentMethodAttr);

            // Order Attributes if is Financing
            if (flgFinanciamiento[0]) {
                FlexAttrValueType downPaymentAttrValue = FlexAttrValueType
                        .builder()
                        .stringValue(createQuotationFijaRequest.getBody().getDownPayment().getAmount())
                        .valueType("STRING")
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
                        .valueType("STRING")
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
                        .valueType("STRING")
                        .build();
                FlexAttrType financingPlanAttr = FlexAttrType
                        .builder()
                        .attrName("FINANCING_PLAN")
                        .flexAttrValue(financingPlanAttrValue)
                        .build();
                altaFijaOrderAttributesList.add(financingPlanAttr);
            }

            // Order Attributes if is Scheduling
            if (!saleRequest.getCommercialOperation().get(0).getWorkOrDeliveryType().getScheduleDelivery()
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

            AltaFijaRequest altaFijaRequest = new AltaFijaRequest();
            altaFijaRequest.setNewProducts(newProductsAltaFijaList);
            altaFijaRequest.setAppointmentId(saleRequest.getCommercialOperation().get(0).getWorkOrDeliveryType()
                                                                    .getWorkOrder().getWorkForceTeams().get(0).getId());
            altaFijaRequest.setAppointmentNumber(saleRequest.getSalesId());
            altaFijaRequest.setServiceabilityInfo(serviceabilityInfo);
            altaFijaRequest.setSourceApp(saleRequest.getSalesId());
            altaFijaRequest.setOrderAttributes(altaFijaOrderAttributesList);
            if (!StringUtils.isEmpty(saleRequest.getPaymenType().getCid())) {
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
                    .productOfferingId(saleRequest.getCommercialOperation().get(0).getProductOfferings().get(0).getId())
                    .onlyValidationIndicator(false)
                    .actionType("PR")
                    .build();

            // Building Main Request to send to Create Product Order Service
            CreateProductOrderGeneralRequest mainRequestProductOrder = new CreateProductOrderGeneralRequest();
            mainRequestProductOrder.setCreateProductOrderRequest(productOrderAltaFijaRequest);

            // Call de Create Alta Fija Order
            return productOrderWebClient.createProductOrder(mainRequestProductOrder, request.getHeadersMap(),
                    saleRequest)
                    .flatMap(createOrderResponse -> {
                        System.out.println("CREATE PRODUCT ORDER RESPONSE: " + createOrderResponse);
                        // Adding Order info to sales
                        saleRequest.getCommercialOperation().get(0)
                                .setOrder(createOrderResponse.getCreateProductOrderResponse());

                        if (validateNegotiation(saleRequest.getAdditionalData(),
                                saleRequest.getIdentityValidations())) {
                            saleRequest.setStatus("NEGOCIACION");
                        } else if (!StringUtils.isEmpty(createOrderResponse.getCreateProductOrderResponse()
                                .getProductOrderId())) {
                            // When All is OK
                            saleRequest.setStatus("NUEVO");
                        } else {
                            // When Create Product Order Service fail or doesnt respond with an Order Id
                            saleRequest.setStatus("PENDIENTE");
                        }

                        // FEMS-1514 Validación de creación Orden
                        Mono<Sale> saleRequestUpdated = creationOrderValidation(saleRequest, mainRequestProductOrder,
                                                                                            request.getHeadersMap());
                        return saleRequestUpdated.flatMap(saleItem -> {

                            if (flgFinanciamiento[0]) {
                                return businessParameterWebClient
                                        .getParametersFinanciamientoFija(request.getHeadersMap())
                                        .map(BusinessParametersFinanciamientoFijaResponse::getData)
                                        .map(bpFinanciamientoFijaData -> bpFinanciamientoFijaData.get(0))
                                        .map(BusinessParameterFinanciamientoFijaData::getExt)
                                        .flatMap(parametersFinanciamientoFija -> {
                                            
                                            createQuotationFijaRequest.getBody()
                                                    .setFinancialEntity(this.getStringValueFromBpExtListByParameterName(
                                                            "financialEntity",
                                                                                        parametersFinanciamientoFija));

                                            return quotationWebClient.createQuotation(createQuotationFijaRequest,
                                                    saleRequest)
                                                    .flatMap(createQuotationResponse -> {

                                                        return salesRepository.save(saleItem)
                                                                .map(r -> {
                                                                  this.postSalesEventFlow(request);
                                                                  return r;
                                                                });
                                                    });
                                        });
                            } else {
                                return salesRepository.save(saleItem)
                                        .map(r -> {
                                            this.postSalesEventFlow(request);
                                            return r;
                                        });
                            }
                        });
                    });
        } else if (mainProductType.equalsIgnoreCase("WIRELESS")) {
            // Mobile Commercial Operations

            if (StringUtils.isEmpty(saleRequest.getCommercialOperation().get(0).getOrder().getProductOrderId())
                    && StringUtils.isEmpty(saleRequest.getCommercialOperation().get(0).getDeviceOffering().get(0)
                    .getStock().getReservationId())) {

                // Get mail Validation, dominio de riesgo - SERGIO
                Mono<BusinessParametersResponse> getRiskDomain = businessParameterWebClient
                        .getRiskDomain(retrieveDomain(saleRequest.getProspectContact()), request.getHeadersMap());

                // Getting commons request properties
                String channelIdRequest = saleRequest.getChannel().getId();
                String customerIdRequest = saleRequest.getRelatedParty().get(0).getCustomerId();
                String productOfferingIdRequest = saleRequest.getCommercialOperation()
                        .get(0).getProductOfferings().get(0).getId();

                // Getting Characteristics By Main Commercial Operation - Check if is used it to remove
                Mono<List<BusinessParameterExt>> salesCharsByCOT = businessParameterWebClient
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

                return Mono.zip(getRiskDomain, salesCharsByCOT, getBonificacionSim, getParametersSimCard)
                        .flatMap(tuple -> {
                            System.out.println("RESPONSEEEE T1: " + tuple.getT1());
                            System.out.println("RESPONSEEEE T2: " + tuple.getT2());
                            if (!tuple.getT1().getData().isEmpty()
                                    && tuple.getT1().getData().get(0).getActive()
                            ) {
                                // if it is a risk domain, cancel operation
                                return Mono.error(GenesisException
                                        .builder()
                                        .exceptionId("SVR1000")
                                        .wildcards(new String[]{"Dominio de riesgo, se canceló la operación"})
                                        .build());
                            }

                            // Getting simcard sapid from bussiness parameter
                            sapidSimcard[0] = getStringValueFromBusinessParameterDataListByKeyAndActiveTrue(
                                    tuple.getT4().getData(), "sapid");

                            // Getting CIP Code
                            String cipCode = "";
                            if (saleRequest.getCommercialOperation().get(0).getWorkOrDeliveryType().getMediumDelivery()
                                    .equalsIgnoreCase("DELIVERY")
                                    && saleRequest.getPaymenType().getPaymentType().equalsIgnoreCase("EX")
                                    && this.getStringValueByKeyFromAdditionalDataList(saleRequest.getAdditionalData(),
                                    "paymentTypeLabel").equals("PAGO EFECTIVO")
                            ) {
                                cipCode = saleRequest.getPaymenType().getCid(); // Validate if cipCode is empty
                            }

                            // Building Main Request to send to Create Product Order Service
                            CreateProductOrderGeneralRequest mainRequestProductOrder = new CreateProductOrderGeneralRequest();

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
                            } else if (!flgCapl[0] && !flgCaeq[0] && !flgCasi[0] && flgAlta[0]) {
                                mainRequestProductOrder = this.altaCommercialOperation(saleRequest, mainRequestProductOrder,
                                        channelIdRequest, customerIdRequest, productOfferingIdRequest, cipCode,
                                        tuple.getT3(), sapidSimcard[0]);
                            }
                            System.out.println("BOOLEAN flgCapl: " + flgCapl[0]);
                            System.out.println("BOOLEAN flgCaeq: " + flgCaeq[0]);
                            System.out.println("BOOLEAN flgCasi: " + flgCasi[0]);

                            System.out.println("REQUESTTT PRODUCT ORDER: " + mainRequestProductOrder);
                            CreateProductOrderGeneralRequest finalMainRequestProductOrder = mainRequestProductOrder;
                            return productOrderWebClient.createProductOrder(mainRequestProductOrder, request.getHeadersMap(),
                                    saleRequest)
                                    .flatMap(createOrderResponse -> {
                                        saleRequest.getCommercialOperation().get(0)
                                                .setOrder(createOrderResponse.getCreateProductOrderResponse());

                                        if (validateNegotiation(saleRequest.getAdditionalData(),
                                                saleRequest.getIdentityValidations())) {
                                            saleRequest.setStatus("NEGOCIACION");
                                        } else if (!StringUtils.isEmpty(createOrderResponse.getCreateProductOrderResponse()
                                                .getProductOrderId())) {
                                            saleRequest.setStatus("NUEVO");
                                        } else {
                                            saleRequest.setStatus("PENDIENTE");
                                        }

                                        // Ship Delivery logic (tambo) - SERGIO
                                        if (saleRequest.getCommercialOperation().get(0).getWorkOrDeliveryType()
                                                .getMediumDelivery().equalsIgnoreCase("Tienda")) {
                                            saleRequest.setAdditionalData(additionalDataAssigments(saleRequest
                                                    .getAdditionalData(), saleRequest));
                                        }

                                        // FEMS-1514 Validación de creación Orden
                                        Mono<Sale> saleRequestUpdated = creationOrderValidation(saleRequest, finalMainRequestProductOrder, request.getHeadersMap());
                                        return saleRequestUpdated.flatMap(saleItem -> {
                                            System.out.println("BOOLEAN CAEQ: " + flgCaeq[0]);
                                            // Call to Reserve Stock Service When Commercial Operation include CAEQ
                                            if (flgCaeq[0] || flgAlta[0]) {

                                                return this.callToReserveStockAndCreateQuotation(request, saleItem, flgCasi[0], flgFinanciamiento[0],
                                                        sapidSimcard[0]);
                                            } else {
                                                if (flgCasi[0]) {
                                                    // Call to Create Quotation Service When CommercialOperation Contains CASI
                                                    return this.callToCreateQuotation(request, saleItem, flgCasi[0],
                                                            flgFinanciamiento[0]);
                                                } else {
                                                    // Case when is Only CAPL
                                                    return salesRepository.save(saleItem)
                                                            .map(r -> {
                                                                this.postSalesEventFlow(request);
                                                                return r;
                                                            });
                                                }
                                            }
                                        });
                                    });
                        });

            } else if (!StringUtils.isEmpty(saleRequest.getCommercialOperation().get(0).getOrder().getProductOrderId())
                    && StringUtils.isEmpty(saleRequest.getCommercialOperation().get(0).getDeviceOffering().get(0)
                    .getStock())
                    && StringUtils.isEmpty(saleRequest.getCommercialOperation().get(0).getDeviceOffering().get(0)
                    .getStock().getReservationId())) { // Retry from Reservation

                // Call to Reserve Stock Service When Commercial Operation include CAEQ
                if (flgCaeq[0] || flgAlta[0]) {

                    return this.callToReserveStockAndCreateQuotation(request, saleRequest, flgCasi[0], flgFinanciamiento[0],
                            sapidSimcard[0]);
                } else {
                    if (flgCasi[0]) {

                        // Call to Create Quotation Service When CommercialOperation Contains CASI
                        return this.callToCreateQuotation(request, saleRequest, flgCasi[0], flgFinanciamiento[0]);
                    } else {
                        // Case when is Only CAPL
                        return salesRepository.save(saleRequest)
                                .map(r -> {
                                    this.postSalesEventFlow(request);
                                    return r;
                                });
                    }
                }

            } else if (!StringUtils.isEmpty(saleRequest.getCommercialOperation().get(0).getOrder().getProductOrderId())
                    && !StringUtils.isEmpty(saleRequest.getCommercialOperation().get(0).getDeviceOffering().get(0)
                    .getStock().getReservationId())) { // Retry from Create Quotation

                // Call to Create Quotation Service When CommercialOperation Contains CAEQ
                return this.callToCreateQuotation(request, saleRequest, flgCasi[0], flgFinanciamiento[0]);
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
                                             PostSalesRequest salesRequest) {
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
                .type("email")
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
                        "releatedPartySegment"))
                .subsegment(this.getStringValueByKeyFromAdditionalDataList(sale.getAdditionalData(),
                        "releatedPartySubSegment"))
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
                .name(sale.getChannel().getName())
                .build();

        MoneyAmount totalCost = MoneyAmount
                .builder()
                .amount(sale.getCommercialOperation().get(0).getDeviceOffering().get(0).getOffers().get(0)
                        .getBillingOfferings().get(0).getCommitmentPeriods().get(0).getFinancingInstalments().get(0)
                        .getInstalments().getTotalAmount().getValue().toString())
                .units("").build();

        MoneyAmount taxExcludedAmount = MoneyAmount
                .builder()
                .amount(sale.getCommercialOperation().get(0).getDeviceOffering().get(0).getOffers().get(0)
                        .getBillingOfferings().get(0).getCommitmentPeriods().get(0).getFinancingInstalments().get(0)
                        .getInstalments().getTotalAmount().getValue().toString())
                .units("")
                .build();

        List<com.tdp.ms.sales.model.dto.quotation.Item> itemsList = new ArrayList<>();
        com.tdp.ms.sales.model.dto.quotation.Item itemEquipment = com.tdp.ms.sales.model.dto.quotation.Item
                .builder()
                .offeringId("EQUIP_FE".concat(sale.getCommercialOperation().get(0).getProduct().getPublicId()))
                .type("mobile phone")
                .orderActionId(sale.getCommercialOperation().get(0).getOrder().getProductOrderReferenceNumber())
                .totalCost(totalCost)
                .taxExcludedAmount(taxExcludedAmount)
                .build();
        itemsList.add(itemEquipment);

        // Attribute only to Fija
        final String[] commercialAgreement = {""};

        sale.getCommercialOperation().get(0).getProductOfferings().get(0).getProductSpecification().stream()
                .forEach(productSpecification -> {
                    String productSpecificationName = productSpecification.getName();
                    if (productSpecificationName.equalsIgnoreCase("TV")) {
                        commercialAgreement[0] = commercialAgreement[0].concat("TV=").concat(productSpecification
                                        .getRefinedProduct().getProductCharacteristics().get(0).getId()).concat(";");
                    } else if (productSpecificationName.equalsIgnoreCase("Broadband")) {
                        commercialAgreement[0] = commercialAgreement[0].concat("INT=").concat(productSpecification
                                        .getRefinedProduct().getProductCharacteristics().get(0).getId()).concat(";");
                    } else if (productSpecificationName.equalsIgnoreCase("ShEq")) {
                        commercialAgreement[0] = commercialAgreement[0].concat("EQUP=").concat(productSpecification
                                                    .getRefinedProduct().getProductCharacteristics().get(0).getId());
                    }
                });


        CreateQuotationRequestBody body = CreateQuotationRequestBody
                .builder()
                .orderId(sale.getCommercialOperation().get(0).getOrder().getProductOrderId())
                .accountId(sale.getRelatedParty().get(0).getAccountId())
                .billingAgreement(sale.getRelatedParty().get(0).getBillingArragmentId())
                .commercialAgreement("N")
                .commercialAgreement(commercialAgreement[0])
                .customer(customerQuotation)
                .operationType(sale.getCommercialOperation().get(0).getReason()) // Pendiente confirmación para alta, reason = ALTA
                .totalAmount(totalAmount)
                .associatedPlanRecurrentCost(associatedPlanRecurrentCost)
                .totalCustomerRecurrentCost(totalCustomerRecurrentCost)
                .downPayment(downPayment)
                .site(site)
                .financialEntity(sale.getCommercialOperation().get(0).getDeviceOffering().get(0).getOffers().get(0)
                        .getBillingOfferings().get(0).getCommitmentPeriods().get(0).getFinancingInstalments().get(0)
                        .getCodigo())
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

        if (keyValueType.getValue().equalsIgnoreCase("Retail")
                && saleRequest.getStatus().equalsIgnoreCase("NEGOCIACION")) {

            DeviceOffering saleDeviceOffering = saleRequest.getCommercialOperation().get(0).getDeviceOffering().get(0);

            Mono<List<GetSkuResponse>> getSku = getSkuWebClient.createSku(saleRequest.getChannel().getId(),
                    "default", saleDeviceOffering.getSimSpecifications().get(0).getSapid(),
                    saleDeviceOffering.getSimSpecifications().get(0).getPrice().get(0).getValue().doubleValue(),
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
                productOrderCaplRequest.setOnlyValidationIndicator(true);
                productOrderRequest.setCreateProductOrderRequest(productOrderCaplRequest);
            } else if (classObjectName.equalsIgnoreCase("ProductOrderCaeqRequest")) {
                ProductOrderCaeqRequest productOrderCaeqRequest =
                        (ProductOrderCaeqRequest) productOrderRequest.getCreateProductOrderRequest();
                productOrderCaeqRequest.setOnlyValidationIndicator(true);
                productOrderRequest.setCreateProductOrderRequest(productOrderCaeqRequest);
            } else if (classObjectName.equalsIgnoreCase("ProductOrderCaeqCaplRequest")) {
                ProductOrderCaeqCaplRequest productOrderCaeqCaplRequest =
                        (ProductOrderCaeqCaplRequest) productOrderRequest.getCreateProductOrderRequest();
                productOrderCaeqCaplRequest.setOnlyValidationIndicator(true);
                productOrderRequest.setCreateProductOrderRequest(productOrderCaeqCaplRequest);
            } else if (classObjectName.equalsIgnoreCase("ProductOrderAltaFijaRequest")) {
                ProductOrderAltaFijaRequest productOrderAltaFijaRequest =
                        (ProductOrderAltaFijaRequest) productOrderRequest.getCreateProductOrderRequest();
                productOrderAltaFijaRequest.setOnlyValidationIndicator(true);
                productOrderRequest.setCreateProductOrderRequest(productOrderAltaFijaRequest);
            } else if (classObjectName.equalsIgnoreCase("ProductOrderAltaMobileRequest")) {
                ProductOrderAltaMobileRequest productOrderAltaMobileRequest =
                        (ProductOrderAltaMobileRequest) productOrderRequest.getCreateProductOrderRequest();
                productOrderAltaMobileRequest.setOnlyValidationIndicator(true);
                productOrderRequest.setCreateProductOrderRequest(productOrderAltaMobileRequest);
            }

            Mono<ProductorderResponse> productOrderResponse =
                    productOrderWebClient.createProductOrder(productOrderRequest, headersMap, saleRequest);

            // Creación del sku
            return Mono.zip(getSku, productOrderResponse).map(tuple -> {
                // añadir respuesta a sale.additionalData y hacer validación de la orden
                saleRequest.getAdditionalData().add(KeyValueType.builder()
                        .key("DEVICE_SKU")
                        .value(tuple.getT1().get(0).getDeviceType().equals("mobile_phone")
                                ? tuple.getT1().get(0).getSku() : tuple.getT1().get(1).getSku())
                        .build());
                saleRequest.getAdditionalData().add(KeyValueType.builder()
                        .key("SIM_SKU")
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
        saleRequest.getCommercialOperation().get(0).getDeviceOffering()
                .get(0).getAdditionalData().add(dateKv);

        saleRequest.getCommercialOperation().get(0).getDeviceOffering()
                .forEach(deviceOffering -> deviceOffering.getStock()
                        .setReservationId(reserveStockResponse.getId()));

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
        CreateQuotationRequest createQuotationRequest = new CreateQuotationRequest();
        this.buildCreateQuotationRequest(createQuotationRequest, request, flgCasi);

        if (flgFinanciamiento) {
            return quotationWebClient.createQuotation(createQuotationRequest,
                    sale)
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
                .type("email")
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

        Number amountTotalAmount = sale.getCommercialOperation().get(0).getDeviceOffering().get(0).getOffers().get(0)
                .getBillingOfferings().get(0).getCommitmentPeriods().get(0).getFinancingInstalments().get(0)
                .getInstalments().getTotalAmount().getValue().doubleValue() - sale.getCommercialOperation().get(0)
                .getDeviceOffering().get(1).getSimSpecifications().get(0).getPrice().get(0).getValue().doubleValue();

        MoneyAmount totalAmount = MoneyAmount
                .builder()
                .units("")
                .amount(amountTotalAmount.toString())
                .build();

        MoneyAmount associatedPlanRecurrentCost = MoneyAmount
                .builder()
                .units("PEN")
                .amount("0.00")
                .build();

        MoneyAmount totalCustomerRecurrentCost = MoneyAmount.builder()
                .units("PEN")
                .amount(sale.getCommercialOperation().get(0).getProductOfferings().get(0).getProductOfferingPrice()
                                                                        .get(0).getMaxPrice().getAmount().toString())
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

        Channel channel = Channel
                .builder()
                .name(sale.getChannel().getName())
                .build();

        MoneyAmount totalCost = MoneyAmount
                .builder()
                .units("")
                .amount(sale.getCommercialOperation().get(0).getDeviceOffering().get(0).getOffers().get(0)
                        .getBillingOfferings().get(0).getCommitmentPeriods().get(0).getFinancingInstalments().get(0)
                        .getInstalments().getTotalAmount().getValue().toString())
                .build();

        MoneyAmount taxExcludedAmount = MoneyAmount.builder()
                .amount(sale.getCommercialOperation().get(0).getDeviceOffering().get(0).getOffers().get(0)
                        .getBillingOfferings().get(0).getCommitmentPeriods().get(0).getFinancingInstalments().get(0)
                        .getInstalments().getTotalAmount().getValue().toString())
                .units("")
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
                    .units("")
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
                .operationType(sale.getCommercialOperation().get(0).getReason()) // Pendiente confirmación para alta, reason = ALTA
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
                && saleRequest.getCommercialOperation().get(0).getWorkOrDeliveryType().getPlace().get(0).getAddress().getRegion().equalsIgnoreCase("LIMA")) {
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
                && saleRequest.getCommercialOperation().get(0).getWorkOrDeliveryType().getPlace().get(0).getAddress().getRegion().equalsIgnoreCase("CALLAO")) {
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

    private CreateProductOrderGeneralRequest altaCommercialOperation(Sale saleRequest,
                                    CreateProductOrderGeneralRequest mainRequestProductOrder, String channelIdRequest,
                                    String customerIdRequest, String productOfferingIdRequest, String cipCode,
                             BusinessParametersResponseObjectExt bonificacionSimcardResponse, String sapidSimcardBp) {

        // Building request for ALTA CommercialTypeOperation
        ProductOrderAltaMobileRequest altaRequestProductOrder = new ProductOrderAltaMobileRequest();
        altaRequestProductOrder.setSalesChannel(channelIdRequest);
        altaRequestProductOrder.setCustomerId(customerIdRequest);
        altaRequestProductOrder.setProductOfferingId(productOfferingIdRequest);
        altaRequestProductOrder.setOnlyValidationIndicator(false);
        altaRequestProductOrder.setActionType("PR");

        // Identifying if is Alta Only Simcard or Alta Combo (Equipment + Simcard)
        Boolean altaCombo = saleRequest.getCommercialOperation().get(0).getDeviceOffering().size() > 1;

        // ALTA Product Changes
        ProductChangeAltaMobile altaProductChanges = new ProductChangeAltaMobile();

        // ALTA NewAssignedBillingOffers
        List<NewAssignedBillingOffers> altaNewBoList = new ArrayList<>();

        // NewAssignedBillingOffer Plan
        NewAssignedBillingOffers altaNewBo1 = NewAssignedBillingOffers
                .builder()
                .productSpecPricingId(saleRequest.getCommercialOperation().get(0)
                        .getProductOfferings().get(0).getProductOfferingPrice().get(0)
                                                                                .getPricePlanSpecContainmentId())
                .parentProductCatalogId(saleRequest.getCommercialOperation().get(0)
                        .getProductOfferings().get(0).getProductOfferingPrice().get(0)
                                                                                    .getProductSpecContainmentId())
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
            altaChangedContainedProductList.get(0).setProductId(""); // Doesnt sent it in Alta
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
        String iccidSim = this.getStringValueByKeyFromAdditionalDataList(saleRequest.getAdditionalData(),
                                                                                                    "SIM_ICCID ");
        ChangedCharacteristic changedCharacteristic2 = ChangedCharacteristic
                .builder()
                .characteristicId("799244")
                .characteristicValue(iccidSim) // 8958080008100067567
                .build();
        changedCharacteristicList.add(changedCharacteristic2);

        ChangedContainedProduct changedContainedProduct2 = ChangedContainedProduct
                .builder()
                .productId("")
                .temporaryId("temp3")
                .productCatalogId("7431")
                .changedCharacteristics(changedCharacteristicList)
                .build();
        altaChangedContainedProductList.add(changedContainedProduct2);

        altaProductChanges.setChangedContainedProducts(altaChangedContainedProductList);


        NewProductAltaMobile newProductAlta1 = new NewProductAltaMobile();
        newProductAlta1.setProductCatalogId(saleRequest.getCommercialOperation().get(0)
                    .getProductOfferings().get(0).getProductOfferingProductSpecId());
        newProductAlta1.setTemporaryId("temp1");
        newProductAlta1.setBaId(saleRequest.getRelatedParty().get(0).getBillingArragmentId());
        newProductAlta1.setAccountId(saleRequest.getRelatedParty().get(0).getAccountId());
        newProductAlta1.setInvoiceCompany("TEF");
        newProductAlta1.setProductChanges(altaProductChanges);

        List<NewProductAltaMobile> altaNewProductsList = new ArrayList<>();
        altaNewProductsList.add(newProductAlta1);

        // Building Order Attributes
        List<FlexAttrType> altaOrderAttributesList = this.commonOrderAttributes(saleRequest);

        // Order Attributes when channel is retail
        String channelId = saleRequest.getChannel().getId();
        if (channelId.equalsIgnoreCase("DLC") || channelId.equalsIgnoreCase("DLV")
                || channelId.equalsIgnoreCase("DLS")) {
            //  RETAIL PAYMENT NUMBER ATTRIBUTE
            String paymentNumber = this.getStringValueByKeyFromAdditionalDataList(saleRequest.getAdditionalData(),
                    "NUMERO_TICKET");

            FlexAttrValueType paymentRegisterAttrValue =  FlexAttrValueType
                    .builder()
                    .stringValue(paymentNumber)
                    .valueType("STRING")
                    .build();
            FlexAttrType paymentRegisterAttr = FlexAttrType
                    .builder()
                    .attrName("PAYMENT_REGISTER_NUMBER")
                    .flexAttrValue(paymentRegisterAttrValue)
                    .build();

            //  RETAIL DEVICE SKU ATTRIBUTE
            String deviceSku = this.getStringValueByKeyFromAdditionalDataList(saleRequest.getAdditionalData(),
                    "DEVICE_SKU");

            FlexAttrValueType deviceSkuAttrValue =  FlexAttrValueType
                    .builder()
                    .stringValue(deviceSku)
                    .valueType("STRING")
                    .build();
            FlexAttrType deviceSkuAttr = FlexAttrType
                    .builder()
                    .attrName("DEVICE_SKU")
                    .flexAttrValue(deviceSkuAttrValue)
                    .build();

            //  RETAIL SIM SKU ATTRIBUTE
            String simSku = this.getStringValueByKeyFromAdditionalDataList(saleRequest.getAdditionalData(),
                                                                                                        "SIM_SKU");

            FlexAttrValueType simSkuAttrValue =  FlexAttrValueType
                    .builder()
                    .stringValue(simSku)
                    .valueType("STRING")
                    .build();
            FlexAttrType simSkuAttr = FlexAttrType
                    .builder()
                    .attrName("SIM_SKU")
                    .flexAttrValue(simSkuAttrValue)
                    .build();

            //  RETAIL CASHIER REGISTER NUMBER ATTRIBUTE
            String cashierRegisterNumber = this.getStringValueByKeyFromAdditionalDataList(saleRequest
                                                                            .getAdditionalData(),"NUMERO_CAJA");

            FlexAttrValueType cashierRegisterAttrValue =  FlexAttrValueType
                    .builder()
                    .stringValue(cashierRegisterNumber)
                    .valueType("STRING")
                    .build();
            FlexAttrType cashierRegisterAttr = FlexAttrType
                    .builder()
                    .attrName("SIM_SKU")
                    .flexAttrValue(cashierRegisterAttrValue)
                    .build();

            altaOrderAttributesList.add(paymentRegisterAttr);
            altaOrderAttributesList.add(deviceSkuAttr);
            altaOrderAttributesList.add(simSkuAttr);
            altaOrderAttributesList.add(cashierRegisterAttr);
        }


        AltaMobileRequest altaRequest = AltaMobileRequest
                .builder()
                .newProducts(altaNewProductsList)
                .sourceApp("FE")
                .orderAttributes(altaOrderAttributesList)
                .shipmentDetails(createShipmentDetail(saleRequest))
                .build();
        //if (!StringUtils.isEmpty(cipCode)) altaRequest.setCip(cipCode);

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
        caplRequestProductOrder.setOnlyValidationIndicator(false);

        RemovedAssignedBillingOffers caplBoRemoved1 = new RemovedAssignedBillingOffers();
        List<RemovedAssignedBillingOffers> caplBoRemovedList = new ArrayList<>();
        if (flgOnlyCapl) {
            // Recognizing Capl Mobile or Fija
            if (saleRequest.getProductType().equalsIgnoreCase("WIRELESS")) {
                caplRequestProductOrder.setActionType("CW");
            } else {
                caplRequestProductOrder.setActionType("CH"); // landline, cableTv, broadband, bundle
            }

            caplBoRemoved1.setProductSpecPricingId(this.getStringValueByKeyFromAdditionalDataList(saleRequest
                    .getCommercialOperation().get(0).getProduct().getAdditionalData(), "productSpecPricingID"));
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

        CaplRequest caplRequest = CaplRequest
                .builder()
                .newProducts(caplNewProductsList)
                .sourceApp("FE")
                .orderAttributes(caplOrderAttributes)
                .shipmentDetails(createShipmentDetail(saleRequest))
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

        saleRequest.getCommercialOperation().get(0).getWorkOrDeliveryType().getAdditionalData().stream().forEach(item -> {
            if (item.getKey().equalsIgnoreCase("shopAddress")) {
                shipmentDetailsType.setShopAddress(item.getValue());
            } else if (item.getKey().equalsIgnoreCase("shopName")) {
                shipmentDetailsType.setShopName(item.getValue());
            } else if (item.getKey().equalsIgnoreCase("collectStoreId")) {
                shipmentDetailsType.setCollectStoreId(item.getValue());
            }
        });

        saleRequest.getCommercialOperation().get(0).getWorkOrDeliveryType().getPlace().get(0).getAdditionalData()
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
        List<ChangedContainedProduct> changedContainedProductList = this.changedContainedCaeqList(saleRequest, "temp1");

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

        // Order Attributes
        List<FlexAttrType> caeqOrderAttributes = new ArrayList<>();
        this.addCaeqOderAttributes(caeqOrderAttributes, saleRequest);

        CaeqRequest caeqRequest = CaeqRequest
                .builder()
                .sourceApp("FE")
                .newProducts(newProductCaeqList)
                .orderAttributes(caeqOrderAttributes)
                .shipmentDetails(createShipmentDetail(saleRequest))
                .build();
        if (!StringUtils.isEmpty(cipCode)) caeqRequest.setCip(cipCode);

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
        caeqCaplRequestProductOrder.setCustomerId(customerIdRequest);
        caeqCaplRequestProductOrder.setProductOfferingId(productOfferingIdRequest);
        caeqCaplRequestProductOrder.setOnlyValidationIndicator(false);

        RemovedAssignedBillingOffers caeqCaplBoRemoved1 = new RemovedAssignedBillingOffers();
        List<RemovedAssignedBillingOffers> caeqCaplBoRemovedList = new ArrayList<>();
        if (flgOnlyCapl) {
            // Recognizing Capl Fija
            if (saleRequest.getProductType().equalsIgnoreCase("WIRELESS")) {
                caeqCaplRequestProductOrder.setActionType("CW");
            } else {
                caeqCaplRequestProductOrder.setActionType("CH"); // landline, cableTv, broadband, bundle
            }

            caeqCaplBoRemoved1.setProductSpecPricingId(this.getStringValueByKeyFromAdditionalDataList(saleRequest
                    .getCommercialOperation().get(0).getProduct().getAdditionalData(), "productSpecPricingID"));
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
        List<ChangedContainedProduct> changedContainedProductList = this.changedContainedCaeqList(saleRequest, "temp1");

        caeqCaplProductChanges.setChangedContainedProducts(changedContainedProductList);
        newProductCaeqCapl1.setProductChanges(caeqCaplProductChanges);

        List<NewProductCaeqCapl> caeqCaplNewProductList = new ArrayList<>();
        caeqCaplNewProductList.add(newProductCaeqCapl1);

        // Refactored Code from CAPL
        List<FlexAttrType> caeqCaplOrderAttributes = this.commonOrderAttributes(saleRequest);
        // Adding Caeq Order Attributes
        this.addCaeqOderAttributes(caeqCaplOrderAttributes, saleRequest);

        CaeqCaplRequest caeqCaplRequest = CaeqCaplRequest
                .builder()
                .newProducts(caeqCaplNewProductList)
                .sourceApp("FE")
                .orderAttributes(caeqCaplOrderAttributes)
                .shipmentDetails(createShipmentDetail(saleRequest))
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
        String channelId = saleRequest.getChannel().getId();
        Boolean flgRetailChannel = channelId.equalsIgnoreCase("DLC")
                || channelId.equalsIgnoreCase("DLV")
                || channelId.equalsIgnoreCase("DLS");
        if (flgRetailChannel) {
            deliveryCode = "IS";
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
        commonOrderAttributes.add(deliveryAttr);

        // Payment Method Attribute - Conditional
        if (!flgRetailChannel) {
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
            commonOrderAttributes.add(paymentAttr);
        }

        return commonOrderAttributes;
    }

    public void addCaeqOderAttributes(List<FlexAttrType> caeqOrderAttributes, Sale saleRequest) {
        // Adding CAEQ Attributes
        String documentTypeValue = "";

        documentTypeValue = this.getStringValueByKeyFromAdditionalDataList(saleRequest.getPaymenType()
                                                                        .getAdditionalData(), "paymentDocument");
        if (documentTypeValue.equalsIgnoreCase("Boleta")) {
            documentTypeValue = "BO";
        } else if (documentTypeValue.equalsIgnoreCase("Factura")) {
            documentTypeValue = "FA";
        }
        FlexAttrValueType deliveryAttrValue =  FlexAttrValueType
                .builder()
                .stringValue(documentTypeValue)
                .valueType("STRING")
                .build();
        FlexAttrType documentTypeAttr = FlexAttrType
                .builder()
                .attrName("DOCUMENT_TYPE")
                .flexAttrValue(deliveryAttrValue)
                .build();

        String customerRuc = saleRequest.getRelatedParty().size() < 2
                                        || StringUtils.isEmpty(saleRequest.getRelatedParty().get(1).getNationalId())? ""
                                        : saleRequest.getRelatedParty().get(1).getNationalId();

        FlexAttrValueType paymentAttrValue =  FlexAttrValueType
                .builder()
                .stringValue(customerRuc)
                .valueType("STRING")
                .build();
        FlexAttrType customerRucAttr = FlexAttrType
                .builder()
                .attrName("CUSTOMER_RUC")
                .flexAttrValue(paymentAttrValue)
                .build();

        caeqOrderAttributes.add(documentTypeAttr);
        caeqOrderAttributes.add(customerRucAttr);
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
                .characteristicValue(saleRequest.getCommercialOperation().get(0).getDeviceOffering().get(0).getId())
                .build();

        // EquipmentIMEI Characteristic
        String deviceImei = "000000000000000";
        String channelId = saleRequest.getChannel().getId();
        if (channelId.equalsIgnoreCase("DLC")
                || channelId.equalsIgnoreCase("DLV")
                || channelId.equalsIgnoreCase("DLS")) {
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
        if (saleRequest.getCommercialOperation().get(0).getDeviceOffering().size() > 1) {
            String simGroup = saleRequest.getCommercialOperation().get(0).getDeviceOffering().get(1)
                                                                            .getSimSpecifications().get(0).getType(); // Pendiente confirmación si este atributo tiene como valores nanoSim, estandar, etc.
            ChangedCharacteristic changedCharacteristic4 = ChangedCharacteristic
                    .builder()
                    .characteristicId("16524")
                    .characteristicValue(simGroup)
                    .build();
            changedCharacteristicList.add(changedCharacteristic4);
        }

        ChangedContainedProduct changedContainedProduct1 = ChangedContainedProduct
                .builder()
                .productId(saleRequest.getCommercialOperation().get(0).getProduct().getId()) // Consultar porque hay 2 product ids
                .temporaryId(tempNum)
                .productCatalogId("7411")
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

        // Getting Commercial Operation Types from Additional Data
        final Boolean[] flgAlta = {false};
        for (KeyValueType kv : saleRequest.getCommercialOperation().get(0).getAdditionalData()) {
            String stringKey = kv.getKey();
            Boolean booleanValue = kv.getValue().equalsIgnoreCase("true");
            if (stringKey.equalsIgnoreCase("ALTA")) {
                flgAlta[0] = booleanValue;
            }
        }

        // Logic for Set Acquisition Type Value
        if (flgAlta[0] && saleRequest.getCommercialOperation().get(0).getDeviceOffering().size() == 1) { // Identifying if is Alta only Sim
            acquisitionType = "Private";
        } else if (saleChannelId.equalsIgnoreCase("CC") && deliveryType.equalsIgnoreCase("SP")
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

    public ReserveStockRequest buildReserveStockRequest(ReserveStockRequest request, Sale sale,
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
                .id(sale.getCommercialOperation().get(0).getDeviceOffering().get(0).getSapid())
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
                    .type("IMEI")
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

}
