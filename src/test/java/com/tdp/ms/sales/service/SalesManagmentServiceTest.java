package com.tdp.ms.sales.service;

import com.tdp.genesis.core.constants.HttpHeadersKey;
import com.tdp.ms.sales.business.SalesManagmentService;
import com.tdp.ms.sales.business.impl.SalesManagmentServiceImpl;
import com.tdp.ms.sales.client.BusinessParameterWebClient;
import com.tdp.ms.sales.client.ProductOrderWebClient;
import com.tdp.ms.sales.model.dto.*;
import com.tdp.ms.sales.model.dto.productorder.CreateProductOrderGeneralRequest;
import com.tdp.ms.sales.model.dto.productorder.FlexAttrType;
import com.tdp.ms.sales.model.dto.productorder.caeq.ChangedContainedProduct;
import com.tdp.ms.sales.model.entity.Sale;
import com.tdp.ms.sales.model.request.PostSalesRequest;
import com.tdp.ms.sales.model.request.ReserveStockRequest;
import com.tdp.ms.sales.model.response.BusinessParametersResponse;
import com.tdp.ms.sales.model.response.GetSalesCharacteristicsResponse;
import com.tdp.ms.sales.model.response.ProductorderResponse;
import com.tdp.ms.sales.repository.SalesRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class SalesManagmentServiceTest {

    @MockBean
    private SalesRepository salesRepository;

    @MockBean
    private BusinessParameterWebClient businessParameterWebClient;

    @MockBean
    private ProductOrderWebClient productOrderWebClient;

    @Autowired
    private SalesManagmentService salesManagmentService;

    @Autowired
    private SalesManagmentServiceImpl salesManagmentServiceImpl;

    private static Sale sale;
    private static PostSalesRequest salesRequest;
    private static Sale sale2;
    private static Sale salesResponse;
    private static final HashMap<String, String> headersMap = new HashMap();
    private static final String RH_UNICA_SERVICE_ID = "d4ce144c-6b26-4b5c-ad29-090a3a559d80";
    private static final String RH_UNICA_APPLICATION = "fesimple-sales";
    private static final String RH_UNICA_PID = "d4ce144c-6b26-4b5c-ad29-090a3a559d83";
    private static final String RH_UNICA_USER = "BackendUser";
    private static final String RH_UNICA_TIMESTAMP = "2020-08-26T17:15:20.509-0400";

    private static List<KeyValueType> additionalDatas;
    private static ContactMedium contactMedium;
    private static List<IdentityValidationType> identityValidationTypeList = new ArrayList<>();

    @BeforeAll
    static void setup() {

        ChannelRef channel= new ChannelRef();
        channel.setId("1");
        channel.setHref("s");
        channel.setName("s");
        channel.setStoreId("s");
        channel.setStoreName("s");
        channel.setDealerId("bc12");

        RelatedParty agent= new RelatedParty();
        agent.setId("1");
        agent.setNationalId("Peru");
        agent.setNationalIdType("DNI");
        agent.setFirstName("Sergio");
        agent.setLastName("Rivas");
        additionalDatas = new ArrayList<>();

        KeyValueType additionalData1 = new KeyValueType();
        additionalData1.setKey("s");
        additionalData1.setValue("d");
        KeyValueType additionalData2 = new KeyValueType();
        additionalData2.setKey("deliveryMethod");
        additionalData2.setValue("IS");
        KeyValueType additionalData3 = new KeyValueType();
        additionalData3.setKey("ufxauthorization");
        additionalData3.setValue("14fwTedaos4sdgZvyay8H");
        KeyValueType additionalDataFlowSale = KeyValueType.builder().key("flowSale").value("Presencial").build();
        additionalDatas.add(additionalData1);
        additionalDatas.add(additionalData2);
        additionalDatas.add(additionalData3);
        additionalDatas.add(additionalDataFlowSale);

        EntityRefType entityRefType = new EntityRefType();
        entityRefType.setHref("s");
        entityRefType.setId("s");
        entityRefType.setName("f");

        AddressType addressType = AddressType.builder().stateOrProvince("Lima").build();

        List<EntityRefType> entityRefTypes = new ArrayList<>();

        entityRefTypes.add(entityRefType);
        List<DeviceOffering> deviceOfferings = new ArrayList<>();
        List<Place> places = new ArrayList<>();

        Place place = new Place();

        place.setAdditionalData(additionalDatas);
        place.setHref("s");
        place.setId("s");
        place.setName("s");
        place.setReferredType("s");
        place.setAddress(addressType);
        places.add(place);
        DeviceOffering deviceOffering = new DeviceOffering();

        deviceOffering.setAdditionalData(additionalDatas);
        deviceOffering.setId("s");
        deviceOffering.setSapid("SAD123PID");


        MoneyAmount moneyAmount1 = MoneyAmount
                .builder()
                .value(150.6)
                .currency("SOL")
                .build();

        Instalments instalments = new Instalments();
        instalments.setAmount(moneyAmount1);
        instalments.setOpeningQuota(moneyAmount1);

        FinancingInstalment financingInstalment1 = new FinancingInstalment();
        financingInstalment1.setInstalments(instalments);
        financingInstalment1.setDescription("CONTADO");
        List<FinancingInstalment> financingInstalmentsList = new ArrayList<>();
        financingInstalmentsList.add(financingInstalment1);

        CommitmentPeriod commitmentPeriod1 = new CommitmentPeriod();
        commitmentPeriod1.setFinancingInstalments(financingInstalmentsList);
        List<CommitmentPeriod> commitmentPeriodsList = new ArrayList<>();
        commitmentPeriodsList.add(commitmentPeriod1);

        BillingOffering billingOffering1 = new BillingOffering();
        billingOffering1.setCommitmentPeriods(commitmentPeriodsList);
        List<BillingOffering> billingOfferingList = new ArrayList<>();
        billingOfferingList.add(billingOffering1);

        Offer offer1 = Offer
                .builder()
                .billingOfferings(billingOfferingList)
                .build();
        List<Offer> offersList = new ArrayList<>();
        offersList.add(offer1);
        deviceOffering.setOffers(offersList);

        deviceOfferings.add(deviceOffering);
        ProductInstanceType product= new ProductInstanceType();
        product.setId("s");
        product.setProductSpec(entityRefType);

        CreateProductOrderResponseType order = CreateProductOrderResponseType
                .builder()
                .productOrderId("930686A")
                .productOrderReferenceNumber("761787835447")
                .build();

        OfferingType offeringType1= new OfferingType();
        offeringType1.setId("s");
        offeringType1.setProductOfferingProductSpecId("s");
        List<OfferingType> productOfferings = new ArrayList<>();
        productOfferings.add(offeringType1);


        List<KeyValueType> additionalDataCommercialOperation = new ArrayList<>();
        KeyValueType additionalDataCapl = new KeyValueType();
        additionalDataCapl.setKey("CAPL");
        additionalDataCapl.setValue("true");

        MediumCharacteristic mediumCharacteristic = MediumCharacteristic.builder()
                .phoneNumber("976598623").emailAddress("ezample@everis.com").build();
        WorkOrDeliveryType workOrDeliveryType = WorkOrDeliveryType.builder()
                .contact(mediumCharacteristic).place(places).additionalData(additionalDatas).build();

        product.setAdditionalData(additionalDatas);
        List<CommercialOperationType> comercialOperationTypes = new ArrayList<>();
        CommercialOperationType comercialOperationType = new CommercialOperationType();
        comercialOperationType.setId("1");
        comercialOperationType.setName("h");
        comercialOperationType.setReason("d");
        comercialOperationType.setProduct(product);
        comercialOperationType.setDeviceOffering(deviceOfferings);
        comercialOperationType.setAction("s");
        comercialOperationType.setAdditionalData(additionalDataCommercialOperation);
        comercialOperationType.setOrder(order);
        comercialOperationType.setProductOfferings(productOfferings);
        comercialOperationType.setWorkOrDeliveryType(workOrDeliveryType);
        comercialOperationTypes.add(comercialOperationType);

        Money estimatedRevenue = new Money();

        estimatedRevenue.setUnit("s");
        estimatedRevenue.setValue(12f);

        ContactMedium prospectContact = new ContactMedium();
        MediumCharacteristic mediumChar = MediumCharacteristic.builder().emailAddress("everis@everis.com").build();
        contactMedium = ContactMedium.builder().mediumType("email").characteristic(mediumChar).build();

        MediumCharacteristic characteristic = new MediumCharacteristic();
        characteristic.setBaseType("s");
        characteristic.setCity("lima");
        characteristic.setContactType("s");
        characteristic.setContactType("s");
        characteristic.setEmailAddress("carlos@gmail.com");
        characteristic.setFaxNumber("s");
        characteristic.setPostCode("s");
        characteristic.setPhoneNumber("323234");
        characteristic.setCountry("Peru");
        characteristic.setSchemaLocation("s");
        characteristic.setSocialNetworkId("s");
        characteristic.setStateOrProvince("s");
        characteristic.setStreet1("s");
        characteristic.setStreet2("s");
        prospectContact.setBaseType("ss");
        prospectContact.setCharacteristic(characteristic);

        List<ContactMedium> prospectContacts = new ArrayList<>();

        //prospectContacts.add(prospectContact);
        prospectContacts.add(contactMedium);

        RelatedParty relatedParty = new RelatedParty();

        relatedParty.setCustomerId("1");
        relatedParty.setFirstName("d");
        relatedParty.setFullName("s");
        relatedParty.setHref("s");
        relatedParty.setId("s");
        relatedParty.setLastName("s");
        relatedParty.setNationalId("s");
        relatedParty.setNationalIdType("s");
        relatedParty.setRole("s");

        List<RelatedParty> relatedParties = new ArrayList<>();

        TimePeriod validFor = new TimePeriod();

        validFor.setStartDateTime("");
        validFor.setEndDateTime("");

        relatedParties.add(relatedParty);

        PaymentType paymentType = PaymentType
                .builder()
                .paymentType("EX")
                .build();



        IdentityValidationType identityValidationType = IdentityValidationType.builder()
                .date("2014-09-15T23:14:25.7251173Z").validationType("No Biometric").build();

        identityValidationTypeList.add(identityValidationType);

        sale = Sale
                .builder()
                .id("1")
                .salesId("FE-000000001")
                .name("Sergio")
                .description("venta de lote")
                .priority("x")
                .channel(channel)
                .agent(agent)
                .productType("landline")
                .commercialOperation(comercialOperationTypes)
                .estimatedRevenue(estimatedRevenue)
                .prospectContact(prospectContacts)
                .relatedParty(relatedParties)
                .status("s")
                .statusChangeDate("s")
                .statusChangeReason("s")
                .audioStatus("s")
                .validFor(validFor)
                .additionalData(additionalDatas)
                .identityValidations(identityValidationTypeList)
                .paymenType(paymentType)
                .build();

        // Setting request headers
        headersMap.put(HttpHeadersKey.UNICA_SERVICE_ID, RH_UNICA_SERVICE_ID);
        headersMap.put(HttpHeadersKey.UNICA_APPLICATION, RH_UNICA_APPLICATION);
        headersMap.put(HttpHeadersKey.UNICA_PID, RH_UNICA_PID);
        headersMap.put(HttpHeadersKey.UNICA_USER, RH_UNICA_USER);

        salesRequest = PostSalesRequest
                .builder()
                .sale(sale)
                .headersMap(headersMap)
                .build();


    }

    @Test
    void retrieveDomainTest() {
        List<ContactMedium> contactMediumList = new ArrayList<>();
        contactMediumList.add(contactMedium);
        String domain = salesManagmentServiceImpl.retrieveDomain(contactMediumList);
        Assert.assertEquals(domain, "everis.com");
    }

    @Test
    void retrieveDomain_nullTest() {
        List<ContactMedium> contactMediumList = new ArrayList<>();
        String domain = salesManagmentServiceImpl.retrieveDomain(contactMediumList);
        Assert.assertEquals(domain, null);
    }

    @Test
    void postSalesTest() {
        BusinessParameterExt ext1 = BusinessParameterExt
                .builder()
                .codComercialOperationType("CAEQ")
                .codActionType("CW")
                .codCharacteristicId("9941")
                .codCharacteristicCode("AcquisitionType")
                .codCharacteristicValue("private")
                .build();
        List<BusinessParameterExt> extList = new ArrayList<>();
        extList.add(ext1);
        BusinessParameterData businessParameterData1 = BusinessParameterData
                .builder()
                .ext(extList)
                .build();
        BusinessParameterData businessParameterData2 = BusinessParameterData
                .builder()
                .active(false)
                .build();
        List<BusinessParameterData> dataList = new ArrayList<>();
        dataList.add(businessParameterData1);

        List<BusinessParameterData> dataList2 = new ArrayList<>();
        dataList2.add(businessParameterData2);

        GetSalesCharacteristicsResponse businessParametersResponse = GetSalesCharacteristicsResponse
                .builder()
                .data(dataList)
                .build();
        BusinessParametersResponse expectBusinessParametersResponse = BusinessParametersResponse
                .builder()
                .data(dataList2)
                .build();

        Mockito.when(businessParameterWebClient.getSalesCharacteristicsByCommercialOperationType(any()))
                .thenReturn(Mono.just(businessParametersResponse));

        Mockito.when(businessParameterWebClient.getRiskDomain(any(), any()))
                .thenReturn(Mono.just(expectBusinessParametersResponse));

        ProductorderResponse productorderResponse = new ProductorderResponse();
        CreateProductOrderResponseType createProductOrderResponseType =  new CreateProductOrderResponseType();
        productorderResponse.setCreateProductOrderResponse(createProductOrderResponseType);
        Mockito.when(productOrderWebClient.createProductOrder(any(), eq(salesRequest.getHeadersMap()), any()))
                .thenReturn(Mono.just(productorderResponse));

        Mockito.when(salesRepository.findBySalesId(any())).thenReturn(Mono.just(sale));
        Mockito.when(salesRepository.save(any())).thenReturn(Mono.just(sale));


        Mono<Sale> result = salesManagmentService.post(salesRequest);

    }

    @Test
    void postSalesTokenMcssNotFoundErrorTest() {
        List<KeyValueType> additionalDatas = new ArrayList<>();
        KeyValueType additionalData1 = new KeyValueType();
        additionalData1.setKey("s");
        additionalData1.setValue("d");
        additionalDatas.add(additionalData1);

        Sale saleTest = new Sale();
        saleTest.setId("S001");
        saleTest.setAdditionalData(additionalDatas);

        PostSalesRequest salesRequest = PostSalesRequest
                .builder()
                .sale(saleTest)
                .headersMap(headersMap)
                .build();

        Mono<Sale> result = salesManagmentService.post(salesRequest);

        StepVerifier.create(result).verifyError();
    }

    @Test
    void additionalDataAssigments_Lima_Test() {
        Place place = Place.builder().address(AddressType.builder().region("LIMA").build()).build();
        List<Place> placeList = new ArrayList<>();
        placeList.add(place);

        CommercialOperationType commercialOperationType = CommercialOperationType
                .builder()
                .workOrDeliveryType(WorkOrDeliveryType.builder().place(placeList).build())
                .build();
        List<CommercialOperationType> commercialOperationTypeList = new ArrayList<>();
        commercialOperationTypeList.add(commercialOperationType);

        salesManagmentServiceImpl.additionalDataAssigments(null, Sale.builder()
                .channel(ChannelRef.builder().storeId("string").build())
                .commercialOperation(commercialOperationTypeList).build());
    }

    @Test
    void additionalDataAssigments_Callao_Test() {
        Place place = Place.builder().address(AddressType.builder().region("CALLAO").build()).build();
        List<Place> placeList = new ArrayList<>();
        placeList.add(place);

        CommercialOperationType commercialOperationType = CommercialOperationType
                .builder()
                .workOrDeliveryType(WorkOrDeliveryType.builder().place(placeList).build())
                .build();
        List<CommercialOperationType> commercialOperationTypeList = new ArrayList<>();
        commercialOperationTypeList.add(commercialOperationType);

        salesManagmentServiceImpl.additionalDataAssigments(null, Sale.builder()
                .channel(ChannelRef.builder().storeId("string").build())
                .commercialOperation(commercialOperationTypeList).build());
    }

    @Test
    void additionalDataAssigments_Province_Test() {
        Place place = Place.builder().address(AddressType.builder().region("AREQUIPA").build()).build();
        List<Place> placeList = new ArrayList<>();
        placeList.add(place);

        CommercialOperationType commercialOperationType = CommercialOperationType
                .builder()
                .workOrDeliveryType(WorkOrDeliveryType.builder().place(placeList).build())
                .build();
        List<CommercialOperationType> commercialOperationTypeList = new ArrayList<>();
        commercialOperationTypeList.add(commercialOperationType);

        salesManagmentServiceImpl.additionalDataAssigments(null, Sale.builder()
                .channel(ChannelRef.builder().storeId("string").build())
                .commercialOperation(commercialOperationTypeList).build());
    }

    @Test
    void validateNegotiationTest() {
        Boolean isNegotiation = salesManagmentServiceImpl.validateNegotiation(additionalDatas,
                identityValidationTypeList);

        Assert.assertEquals(true, isNegotiation);
    }

    @Test
    void caplCommercialOperationTest() {
        CreateProductOrderGeneralRequest mainCaplRequestProductOrder = new CreateProductOrderGeneralRequest();

        CreateProductOrderGeneralRequest result = salesManagmentServiceImpl
                .caplCommercialOperation(sale, mainCaplRequestProductOrder,
                        "CC", "CS465", "OF824", "A83HD345DS");

    }

    @Test
    void caeqCommercialOperationTest() {
        CreateProductOrderGeneralRequest mainCaeqRequestProductOrder = new CreateProductOrderGeneralRequest();

        CreateProductOrderGeneralRequest result = salesManagmentServiceImpl
                .caeqCommercialOperation(sale, mainCaeqRequestProductOrder,
                        "CEC", "CS920", "OF201", "JSG423DE6H");

    }

    @Test
    void caeqCaplCommercialOperationTest() {
        CreateProductOrderGeneralRequest mainCaeqCaplRequestProductOrder = new CreateProductOrderGeneralRequest();

        CreateProductOrderGeneralRequest result = salesManagmentServiceImpl
                .caeqCaplCommercialOperation(sale, mainCaeqCaplRequestProductOrder,
                        "CC", "CS158", "OF486", "K3BD9EN349");

    }

    @Test
    void getCommonOrderAttributesTest() {
        List<FlexAttrType> operationOrderAttributes = new ArrayList<>();

        List<FlexAttrType> result = salesManagmentServiceImpl.commonOrderAttributes(sale);

    }

    @Test
    void getChangedContainedCaeqListTest() {
        List<ChangedContainedProduct> changedContainedProducts = new ArrayList<>();

        List<ChangedContainedProduct> result = salesManagmentServiceImpl
                .changedContainedCaeqList(sale);

    }

    @Test
    void buildReserveStockRequestTest() {
        ReserveStockRequest reserveStockRequest = new ReserveStockRequest();
        CreateProductOrderResponseType createProductOrderResponse = CreateProductOrderResponseType
                .builder()
                .productOrderReferenceNumber("761787835447")
                .productOrderId("930686A")
                .build();

        ReserveStockRequest result = salesManagmentServiceImpl.buildReserveStockRequest(reserveStockRequest,
                sale, createProductOrderResponse);

    }

    @Test
    void createShipmentDetailTest() {
        salesManagmentServiceImpl.createShipmentDetail(sale);
    }
}