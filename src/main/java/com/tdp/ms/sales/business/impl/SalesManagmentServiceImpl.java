package com.tdp.ms.sales.business.impl;

import com.tdp.genesis.core.exception.GenesisException;
import com.tdp.ms.sales.business.SalesManagmentService;
import com.tdp.ms.sales.client.BusinessParameterWebClient;
import com.tdp.ms.sales.client.ProductOrderWebClient;
import com.tdp.ms.sales.client.QuotationWebClient;
import com.tdp.ms.sales.client.StockWebClient;
import com.tdp.ms.sales.model.dto.BusinessParameterExt;
import com.tdp.ms.sales.model.dto.ContactMedium;
import com.tdp.ms.sales.model.dto.CreateProductOrderResponseType;
import com.tdp.ms.sales.model.dto.IdentityValidationType;
import com.tdp.ms.sales.model.dto.KeyValueType;
import com.tdp.ms.sales.model.dto.ShipmentDetailsType;
import com.tdp.ms.sales.model.dto.SiteRefType;
import com.tdp.ms.sales.model.dto.TimePeriod;
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
import com.tdp.ms.sales.model.request.ReserveStockRequest;
import com.tdp.ms.sales.model.response.BusinessParametersResponse;
import com.tdp.ms.sales.model.response.CreateQuotationResponse;
import com.tdp.ms.sales.model.response.GetSalesCharacteristicsResponse;
import com.tdp.ms.sales.model.response.ReserveStockResponse;
import com.tdp.ms.sales.repository.SalesRepository;
import com.tdp.ms.sales.utils.Commons;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
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

    private final static String SHIPPING_LOCALITY = "shippingLocality";
    private final static String PROVINCE_OF_SHIPPING_ADDRESS = "provinceOfShippingAddress";
    private final static String SHOP_ADDRESS = "shopAddress";

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

    @Override
    public Mono<Sale> post(PostSalesRequest request) {

        // Getting Sale object
        Sale saleRequest = request.getSale();
        final Boolean[] flgCapl = {false};
        final Boolean[] flgCaeq = {false};
        final Boolean[] flgCasi = {false};
        final Boolean[] flgFinanciamiento = {false};

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
            }
        }

        flgFinanciamiento[0] = !StringUtils.isEmpty(saleRequest.getCommercialOperation().get(0).getDeviceOffering().get(0).getOffers()
                .get(0).getBillingOfferings().get(0).getCommitmentPeriods().get(0).getFinancingInstalments().get(0)
                .getDescription()) && !saleRequest.getCommercialOperation().get(0).getDeviceOffering().get(0).getOffers()
                .get(0).getBillingOfferings().get(0).getCommitmentPeriods().get(0).getFinancingInstalments().get(0)
                .getDescription().equals("CONTADO");

        if (StringUtils.isEmpty(saleRequest.getCommercialOperation().get(0).getOrder().getProductOrderId())
                && StringUtils.isEmpty(saleRequest.getCommercialOperation().get(0).getDeviceOffering().get(0)
                .getStock().getReservationId())) {
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
            System.out.println("COMMERCIAL OPERACTION: " + commercialOperationType);
            Mono<List<BusinessParameterExt>> salesCharsByCOT = businessParameterWebClient
                    .getSalesCharacteristicsByCommercialOperationType(
                            GetSalesCharacteristicsRequest
                                    .builder()
                                    .commercialOperationType(commercialOperationType)
                                    .headersMap(request.getHeadersMap())
                                    .build())
                    .map(this::retrieveCharacteristics);


            return Mono.zip(getRiskDomain, salesCharsByCOT)
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
                        if (flgCapl[0] && !flgCaeq[0] && !flgCasi[0]) {

                            mainRequestProductOrder = this.caplCommercialOperation(saleRequest, mainRequestProductOrder,
                                    channelIdRequest, customerIdRequest, productOfferingIdRequest, cipCode);

                        } else if (!flgCapl[0] && flgCaeq[0] && !flgCasi[0]) { // Recognizing CAEQ Commercial Operation Type

                            mainRequestProductOrder = this.caeqCommercialOperation(saleRequest, mainRequestProductOrder,
                                    channelIdRequest, customerIdRequest, productOfferingIdRequest, cipCode);

                        } else if (flgCapl[0] && flgCaeq[0] && !flgCasi[0]) { // Recognizing CAEQ+CAPL Commercial Operation Type

                            mainRequestProductOrder = this.caeqCaplCommercialOperation(saleRequest, mainRequestProductOrder,
                                    channelIdRequest, customerIdRequest, productOfferingIdRequest, cipCode);
                        }
                        System.out.println("BOOLEAN flgCapl: " + flgCapl[0]);
                        System.out.println("BOOLEAN flgCaeq: " + flgCaeq[0]);
                        System.out.println("BOOLEAN flgCasi: " + flgCasi[0]);

                        System.out.println("REQUESTTT PRODUCT ORDER: " + mainRequestProductOrder);
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

                                    // Ship Delivery logic (tambo) - SERGIO
                                    if (saleRequest.getCommercialOperation().get(0).getWorkOrDeliveryType()
                                            .getMediumDelivery().equalsIgnoreCase("Tienda")) {
                                        saleRequest.setAdditionalData(additionalDataAssigments(saleRequest
                                                .getAdditionalData(), saleRequest));
                                    }
                                    System.out.println("BOOLEAN CAEQ: " + flgCaeq[0]);
                                    // Call to Reserve Stock Service When Commercial Operation include CAEQ
                                    if (flgCaeq[0]) {
                                        ReserveStockRequest reserveStockRequest = new ReserveStockRequest();
                                        reserveStockRequest = this.buildReserveStockRequest(reserveStockRequest,
                                                saleRequest, createOrderResponse.getCreateProductOrderResponse());

                                        return stockWebClient.reserveStock(reserveStockRequest,
                                                request.getHeadersMap(), saleRequest)
                                                .flatMap(reserveStockResponse -> {

                                                    this.setReserveReponseInSales(reserveStockResponse, saleRequest);

                                                    // Call to Create Quotation Service When CommercialOperation Contains CAEQ
                                                    return this.callToCreateQuotation(request, saleRequest, flgCasi[0],
                                                            flgFinanciamiento[0]);
                                                });
                                    } else {
                                        if (flgCasi[0]) {
                                            // Call to Create Quotation Service When CommercialOperation Contains CASI
                                            return this.callToCreateQuotation(request, saleRequest, flgCasi[0],
                                                                                                flgFinanciamiento[0]);
                                        } else {
                                            // Case when is Only CAPL
                                            return salesRepository.save(saleRequest);
                                        }
                                    }
                                });
                    });

        } else if (!StringUtils.isEmpty(saleRequest.getCommercialOperation().get(0).getOrder().getProductOrderId())
                && StringUtils.isEmpty(saleRequest.getCommercialOperation().get(0).getDeviceOffering().get(0)
                                                                                                        .getStock())
                && StringUtils.isEmpty(saleRequest.getCommercialOperation().get(0).getDeviceOffering().get(0)
                                                                                    .getStock().getReservationId())) { // Retry from Reservation

            // Call to Reserve Stock Service When Commercial Operation include CAEQ
            if (flgCaeq[0]) {
                ReserveStockRequest reserveStockRequest = new ReserveStockRequest();
                reserveStockRequest = this.buildReserveStockRequest(reserveStockRequest,
                        saleRequest, saleRequest.getCommercialOperation().get(0).getOrder());

                return stockWebClient.reserveStock(reserveStockRequest,
                        request.getHeadersMap(), saleRequest)
                        .flatMap(reserveStockResponse -> {

                            this.setReserveReponseInSales(reserveStockResponse, saleRequest);

                            // Call to Create Quotation Service When CommercialOperation Contains CAEQ
                            return this.callToCreateQuotation(request, saleRequest, flgCasi[0], flgFinanciamiento[0]);
                        });
            } else {
                if (flgCasi[0]) {

                    // Call to Create Quotation Service When CommercialOperation Contains CASI
                    return this.callToCreateQuotation(request, saleRequest, flgCasi[0], flgFinanciamiento[0]);
                } else {
                    // Case when is Only CAPL
                    return salesRepository.save(saleRequest);
                }
            }

        } else if (!StringUtils.isEmpty(saleRequest.getCommercialOperation().get(0).getOrder().getProductOrderId())
                && !StringUtils.isEmpty(saleRequest.getCommercialOperation().get(0).getDeviceOffering().get(0)
                .getStock().getReservationId())) { // Retry from Create Quotation

            // Call to Create Quotation Service When CommercialOperation Contains CAEQ
            return this.callToCreateQuotation(request, saleRequest, flgCasi[0], flgFinanciamiento[0]);
        } else {
            return salesRepository.save(saleRequest);
        }
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
                        return salesRepository.save(sale);
                    });
        } else {
            return salesRepository.save(sale);
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
                .getDeviceOffering().get(1).getSimSpecifications().get(0).getPrice().get(0).getValue().doubleValue();

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
                .units("PEN")
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
                .amount(sale.getCommercialOperation().get(0).getDeviceOffering().get(0).getOffers().get(0)
                        .getBillingOfferings().get(0).getCommitmentPeriods().get(0).getFinancingInstalments().get(0)
                        .getInstalments().getTotalAmount().getValue().toString())
                .units("")
                .build();

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
                .orderId(sale.getCommercialOperation().get(0).getOrder().getProductOrderId())
                .accountId(sale.getRelatedParty().get(0).getAccountId())
                .billingAgreement(sale.getRelatedParty().get(0).getBillingArragmentId())
                .commercialAgreement("N")
                .customer(customerQuotation)
                .operationType(sale.getCommercialOperation().get(0).getReason())
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
            if (saleRequest.getProductType().equals("mobile")) {
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
            if (saleRequest.getProductType().equals("mobile")) {
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
        List<ChangedContainedProduct> changedContainedProductList = this.changedContainedCaeqList(saleRequest);

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

}
