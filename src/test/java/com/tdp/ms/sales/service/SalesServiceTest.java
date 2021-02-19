package com.tdp.ms.sales.service;

import com.microsoft.azure.spring.data.cosmosdb.core.ReactiveCosmosTemplate;
import com.tdp.genesis.core.constants.HttpHeadersKey;
import com.tdp.ms.sales.business.SalesService;
import com.tdp.ms.sales.business.impl.SalesServiceImpl;
import com.tdp.ms.sales.client.WebClientBusinessParameters;
import com.tdp.ms.sales.eventflow.client.SalesWebClient;
import com.tdp.ms.sales.eventflow.client.impl.SalesWebClientImpl;
import com.tdp.ms.sales.model.dto.ChannelRef;
import com.tdp.ms.sales.model.dto.CommercialOperationType;
import com.tdp.ms.sales.model.dto.ContactMedium;
import com.tdp.ms.sales.model.dto.CreateProductOrderResponseType;
import com.tdp.ms.sales.model.dto.DeviceOffering;
import com.tdp.ms.sales.model.dto.EntityRefType;
import com.tdp.ms.sales.model.dto.KeyValueType;
import com.tdp.ms.sales.model.dto.MediumCharacteristic;
import com.tdp.ms.sales.model.dto.Money;
import com.tdp.ms.sales.model.dto.Place;
import com.tdp.ms.sales.model.dto.ProductInstanceType;
import com.tdp.ms.sales.model.dto.RelatedParty;
import com.tdp.ms.sales.model.dto.TimePeriod;
import com.tdp.ms.sales.model.dto.businessparameter.BusinessParameterDataSeq;
import com.tdp.ms.sales.model.entity.Sale;
import com.tdp.ms.sales.model.request.GetSalesRequest;
import com.tdp.ms.sales.model.response.BusinessParametersResponse;
import com.tdp.ms.sales.repository.SalesRepository;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class SalesServiceTest {

    @MockBean
    private WebClientBusinessParameters webClientToken;

    @MockBean
    private SalesRepository salesRepository;

    @MockBean
    private ReactiveCosmosTemplate reactiveCosmosTemplate;

    @Autowired
    private SalesServiceImpl salesServiceImpl;

    @Autowired
    private SalesWebClient salesWebClient;

    @Autowired
    private SalesWebClientImpl salesWebClientImpl;

    @Autowired
    private SalesService salesService;

    private static Sale sale;
    private static Sale sale2;
    private static Sale salesResponse;
    private static  GetSalesRequest request;
    @BeforeAll
    static void setup() {

        Map<String,String> headersMap = new HashMap<>();
        headersMap.put(HttpHeadersKey.UNICA_SERVICE_ID, "1");
        headersMap.put(HttpHeadersKey.UNICA_APPLICATION, "application");
        headersMap.put(HttpHeadersKey.UNICA_PID, "pid");
        headersMap.put(HttpHeadersKey.UNICA_USER, "user");

        request = GetSalesRequest
                   .builder()
                   .id("FE-000000001")
                   .headersMap(headersMap)
                   .build();
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
        List<KeyValueType> additionalDatas = new ArrayList<>();

        KeyValueType additionalData = new KeyValueType();
        additionalData.setKey("s");
        additionalData.setValue("d");
        additionalDatas.add(additionalData);

        EntityRefType entityRefType = new EntityRefType();
        entityRefType.setHref("s");
        entityRefType.setId("s");
        entityRefType.setName("f");
        //entityRefType.setType("s");

         List<EntityRefType> productOfferings = new ArrayList<>();

        productOfferings.add(entityRefType);
        List<DeviceOffering> deviceOfferings = new ArrayList<>();
        List<Place> places = new ArrayList<>();

        Place place = new Place();

        place.setAdditionalData(additionalDatas);
        place.setHref("s");
        place.setId("s");
        place.setName("s");
        place.setReferredType("s");
        places.add(place);
        DeviceOffering deviceOffering = new DeviceOffering();

        deviceOffering.setAdditionalData(additionalDatas);
        deviceOffering.setId("s");

        deviceOfferings.add(deviceOffering);
        ProductInstanceType product= new ProductInstanceType();
        product.setId("s");

        CreateProductOrderResponseType order = CreateProductOrderResponseType
                .builder()
                .productOrderId("930686A")
                .build();

        product.setAdditionalData(additionalDatas);
        List<CommercialOperationType> comercialOperationTypes = new ArrayList<>();
         CommercialOperationType comercialOperationType = new CommercialOperationType();
         comercialOperationType.setId("1");
         comercialOperationType.setName("h");
         comercialOperationType.setReason("d");
         comercialOperationType.setProduct(product);
         comercialOperationType.setDeviceOffering(deviceOfferings);
         comercialOperationType.setAction("s");
         comercialOperationType.setAdditionalData(additionalDatas);
         comercialOperationType.setOrder(order);
         comercialOperationTypes.add(comercialOperationType);

        Money estimatedRevenue = new Money();

        estimatedRevenue.setUnit("s");
        estimatedRevenue.setValue(12f);

        ContactMedium prospectContact = new ContactMedium();
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

        prospectContacts.add(prospectContact);

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
        relatedParty.setCustomerId("333333");

        List<RelatedParty> relatedParties = new ArrayList<>();

        TimePeriod validFor = new TimePeriod();

        validFor.setStartDateTime("");
        validFor.setEndDateTime("");

        relatedParties.add(relatedParty);
        sale = Sale
                .builder()
                .id("1")
                .salesId("FE-000000001")
                .name("Sergio")
                .description("venta de lote")
                .priority("x")
                .channel(channel)
                .agent(agent)
                .productType("s")
                .commercialOperation(comercialOperationTypes)
                .estimatedRevenue(estimatedRevenue)
                .prospectContact(prospectContacts)
                .relatedParty(relatedParties)
                .status("s")
                .statusChangeDate("s")
                .statusChangeReason("s")
                .audioStatus("s")
                .validFor(validFor)
                .saleCreationDate("2021-02-15T11:08:21")
                .additionalData(additionalDatas)
                .build();

        sale2 = Sale
                .builder()
                .id("1")
                .salesId("FE-000000001")
                .name("Sergio")
                .description("venta de lote")
                .build();

        salesResponse = Sale
                .builder()
                .id("1")
                .salesId("FE-"+sale.getSalesId())
                .name("Sergio")
                .description("venta de lote")
                .priority("x")
                .channel(channel)
                .agent(agent)
                .productType("s")
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
                .build();
    }

    @Test
    void getSaleTest(){
        Mockito.when(salesRepository.findBySalesId(any())).thenReturn(Mono.just(sale));

        Mono<Sale> result = salesService.getSale(request);

        StepVerifier.create(result)
                .assertNext(c ->{
                    Assert.assertEquals(c.getId(),salesResponse.getId());
                    Assert.assertEquals(c.getName(),salesResponse.getName());
                    Assert.assertEquals(c.getDescription(),salesResponse.getDescription());
                })
                .verifyComplete();
    }

    @Test
    void postSaveSale() {
        HashMap<String, String> headersMap = new HashMap<String, String>();
        headersMap.put(HttpHeadersKey.UNICA_SERVICE_ID, "serviceId");
        headersMap.put(HttpHeadersKey.UNICA_PID, "pid");
        headersMap.put(HttpHeadersKey.UNICA_APPLICATION, "application");
        headersMap.put(HttpHeadersKey.UNICA_USER, "user");
        Mockito.when(salesRepository.findAll(any()))
                .thenReturn(Flux.just(sale2));

        Mockito.when(salesRepository.save(any()))
                .thenReturn(Mono.just(sale));

        BusinessParameterDataSeq businessParametersDataSeq = BusinessParameterDataSeq
                .builder()
                .value("FE-000000001")
                .build();
        List<BusinessParameterDataSeq> businessParametersDataList = new ArrayList<>();
        businessParametersDataList.add(businessParametersDataSeq);

        Mockito.when(webClientToken.getNewSaleSequential(any(), any()))
                .thenReturn(Mono.just(BusinessParametersResponse
                        .builder()
                        .data(businessParametersDataList)
                        .build()));

        Mono<Sale> result = salesService.post(sale, headersMap);

        StepVerifier.create(result)
                .assertNext(c -> {
                    Assert.assertEquals(c.getId(), sale.getId());
                })
                .verifyComplete();


    }

    @Test
    void putSaveSale() {
        HashMap<String, String> headersMap = new HashMap<String, String>();
        headersMap.put(HttpHeadersKey.UNICA_SERVICE_ID, "serviceId");
        headersMap.put(HttpHeadersKey.UNICA_PID, "pid");
        headersMap.put(HttpHeadersKey.UNICA_APPLICATION, "application");
        headersMap.put(HttpHeadersKey.UNICA_USER, "user");
        
        Mockito.when(salesRepository.findBySalesId(any()))
                .thenReturn(Mono.just(sale2));

        Mockito.when(salesRepository.save(any()))
                .thenReturn(Mono.just(sale2));

        Mono<Sale> result = salesService.put("FE-000000001", sale, headersMap);

        StepVerifier.create(result)
                .assertNext(c -> {
                    Assert.assertEquals(c.getId(), sale2.getId());
                })
                .verifyComplete();
    }

    @Test
    void putEventSaveSale() {
        HashMap<String, String> headersMap = new HashMap<String, String>();
        headersMap.put(HttpHeadersKey.UNICA_SERVICE_ID, "serviceId");
        headersMap.put(HttpHeadersKey.UNICA_PID, "pid");
        headersMap.put(HttpHeadersKey.UNICA_APPLICATION, "application");
        headersMap.put(HttpHeadersKey.UNICA_USER, "user");

        Mockito.when(salesRepository.findBySalesId(any()))
                .thenReturn(Mono.just(sale2));

        Mockito.when(salesRepository.save(any()))
                .thenReturn(Mono.just(sale2));

        Mono<Sale> result = salesService.putEvent("FE-000000001", sale, headersMap);
    }

    @Test
    void getSaleListTest(){
        Mockito.when(reactiveCosmosTemplate.find(any(), any(), any())).thenReturn(Flux.just(sale));

        Flux<Sale> result = salesService.getSaleList("1","bc12",
                "1", "1", "79764312", "DNI",
                "s", "1", "s", "orderId", "2021-02-15T00:00:00",
                "2021-02-16T23:59:59", "size", "pageCount", "page",
                "maxResultCount");

        StepVerifier.create(result)
                .expectNextCount(1);
    }

    @Test
    void criteriaSaleId_whenSaleIdIsNull() {
        salesServiceImpl.criteriaSaleId(new ArrayList<>(), null);
    }

    @Test
    void criteriaSaleId_whenSaleIdIsEmpty() {
        salesServiceImpl.criteriaSaleId(new ArrayList<>(), "");
    }

    @Test
    void criteriaDealerId_whenDealerIdIsNull() {
        salesServiceImpl.criteriaDealerId(new ArrayList<>(), null);
    }

    @Test
    void criteriaDealerId_whenDealerIdIsEmpty() {
        salesServiceImpl.criteriaDealerId(new ArrayList<>(), "");
    }

    @Test
    void criteriaIdAgent_whenIdAgentIsNull() {
        salesServiceImpl.criteriaIdAgent(new ArrayList<>(), null);
    }

    @Test
    void criteriaIdAgent_whenIdAgentIsEmpty() {
        salesServiceImpl.criteriaIdAgent(new ArrayList<>(), "");
    }

    @Test
    void criteriaStatus_whenStatusIsNull() {
        salesServiceImpl.criteriaStatus(new ArrayList<>(), null);
    }

    @Test
    void criteriaStatus_whenStatusIsEmpty() {
        salesServiceImpl.criteriaStatus(new ArrayList<>(), "");
    }

    @Test
    void criteriaChannelId_whenChannelIdIsNull() {
        salesServiceImpl.criteriaChannelId(new ArrayList<>(), null);
    }

    @Test
    void criteriaChannelId_whenChannelIdIsEmpty() {
        salesServiceImpl.criteriaChannelId(new ArrayList<>(), "");
    }

    @Test
    void criteriaStoreId_whenStoreIdIsNull() {
        salesServiceImpl.criteriaStoreId(new ArrayList<>(), null);
    }

    @Test
    void criteriaStoreId_whenStoreIdIsEmpty() {
        salesServiceImpl.criteriaStoreId(new ArrayList<>(), "");
    }

    @Test
    void criteriaDateTime_whenDateTimeIsNull() {
        salesServiceImpl.criteriaDateTime(new ArrayList<>(), null, null);
    }

    @Test
    void criteriaDateTime_whenDateTimeIsEmpty() {
        salesServiceImpl.criteriaDateTime(new ArrayList<>(), null, "");
        salesServiceImpl.criteriaDateTime(new ArrayList<>(), "", null);
        salesServiceImpl.criteriaDateTime(new ArrayList<>(), "", "");
    }

    @Test
    void filterSalesWithParamsTest() {
        salesServiceImpl.filterSalesWithParams(sale, "", "79764312", "DNI", "orderId");
    }

    @Test
    void filterNationalIdTest() {
        salesServiceImpl.filterNationalId(sale, "930686A");
    }

    @Test
    void filterNationalId_Null_Test() {
        salesServiceImpl.filterNationalId(sale, null);
    }

    @Test
    void filterNationalIdTypeTest() {
        salesServiceImpl.filterNationalIdType(sale, "930686A");
    }

    @Test
    void filterNationalIdType_Null_Test() {
        salesServiceImpl.filterNationalIdType(sale, null);
    }

    @Test
    void filterCustomerIdTest() {
        salesServiceImpl.filterCustomerId(sale, "333333");
    }

    @Test
    void filterCustomerId_NullTest() {
        salesServiceImpl.filterCustomerId(sale, null);
    }

    @Test
    void filterExistingOrderIdTest() {
        salesServiceImpl.filterExistingOrderId(sale, "930686A");
    }

    @Test
    void filterExistingOrderId_orderIdNullTest() {
        salesServiceImpl.filterExistingOrderId(sale, null);
    }

    @Test
    void filterExistingOrderId_orderIdEmptyTest() {
        salesServiceImpl.filterExistingOrderId(sale, "");
    }

    @Test
    void validateBeforeUpdate_Test() {
        salesWebClient.validateBeforeUpdate("01", "02", Arrays.asList(KeyValueType.builder().key("createContractDate").value("prueba").build(),
                KeyValueType.builder().key("submitOrderDate").value("prueba submitOrderDate").build(),
                KeyValueType.builder().key("tratamientoDatosDate").value("prueba tratamientoDatosDate").build(),
                KeyValueType.builder().key("afiliacionReciboDate").value("prueba afiliacionReciboDate").build(),
                KeyValueType.builder().key("custodiaDate").value("prueba custodiaDate").build()));

        salesWebClient.validateBeforeUpdate("01", "04", Arrays.asList(KeyValueType.builder().key("createContractDate").value("prueba").build(),
                KeyValueType.builder().key("submitOrderDate").value("prueba submitOrderDate").build(),
                KeyValueType.builder().key("tratamientoDatosDate").value("prueba tratamientoDatosDate").build(),
                KeyValueType.builder().key("afiliacionReciboDate").value("prueba afiliacionReciboDate").build(),
                KeyValueType.builder().key("custodiaDate").value("prueba custodiaDate").build()));

        salesWebClient.validateBeforeUpdate("01", "06", Arrays.asList(KeyValueType.builder().key("createContractDate").value("prueba").build(),
                KeyValueType.builder().key("submitOrderDate").value("prueba submitOrderDate").build(),
                KeyValueType.builder().key("tratamientoDatosDate").value("prueba tratamientoDatosDate").build(),
                KeyValueType.builder().key("afiliacionReciboDate").value("prueba afiliacionReciboDate").build(),
                KeyValueType.builder().key("custodiaDate").value("prueba custodiaDate").build()));

        salesWebClient.validateBeforeUpdate("01", "08", Arrays.asList(KeyValueType.builder().key("createContractDate").value("prueba").build(),
                KeyValueType.builder().key("submitOrderDate").value("prueba submitOrderDate").build(),
                KeyValueType.builder().key("tratamientoDatosDate").value("prueba tratamientoDatosDate").build(),
                KeyValueType.builder().key("afiliacionReciboDate").value("prueba afiliacionReciboDate").build(),
                KeyValueType.builder().key("custodiaDate").value("prueba custodiaDate").build()));

        salesWebClient.validateBeforeUpdate("01", "09", Arrays.asList(KeyValueType.builder().key("createContractDate").value("prueba").build(),
                KeyValueType.builder().key("submitOrderDate").value("prueba submitOrderDate").build(),
                KeyValueType.builder().key("tratamientoDatosDate").value("prueba tratamientoDatosDate").build(),
                KeyValueType.builder().key("afiliacionReciboDate").value("prueba afiliacionReciboDate").build(),
                KeyValueType.builder().key("custodiaDate").value("prueba custodiaDate").build()));

        salesWebClient.validateBeforeUpdate("02", "01", Arrays.asList(KeyValueType.builder().key("createContractDate").value("prueba").build(),
                KeyValueType.builder().key("submitOrderDate").value("prueba submitOrderDate").build(),
                KeyValueType.builder().key("tratamientoDatosDate").value("prueba tratamientoDatosDate").build(),
                KeyValueType.builder().key("afiliacionReciboDate").value("prueba afiliacionReciboDate").build(),
                KeyValueType.builder().key("custodiaDate").value("prueba custodiaDate").build()));

        salesWebClient.validateBeforeUpdate("02", "02", Arrays.asList(KeyValueType.builder().key("createContractDate").value("prueba").build(),
                KeyValueType.builder().key("submitOrderDate").value("prueba submitOrderDate").build(),
                KeyValueType.builder().key("tratamientoDatosDate").value("prueba tratamientoDatosDate").build(),
                KeyValueType.builder().key("afiliacionReciboDate").value("prueba afiliacionReciboDate").build(),
                KeyValueType.builder().key("custodiaDate").value("prueba custodiaDate").build()));

        salesWebClient.validateBeforeUpdate(null, "02", Arrays.asList(KeyValueType.builder().key("createContractDate").value("prueba").build(),
                KeyValueType.builder().key("submitOrderDate").value("prueba submitOrderDate").build(),
                KeyValueType.builder().key("tratamientoDatosDate").value("prueba tratamientoDatosDate").build(),
                KeyValueType.builder().key("afiliacionReciboDate").value("prueba afiliacionReciboDate").build(),
                KeyValueType.builder().key("custodiaDate").value("prueba custodiaDate").build()));

        salesWebClient.validateBeforeUpdate("02", null, Arrays.asList(KeyValueType.builder().key("createContractDate").value("prueba").build(),
                KeyValueType.builder().key("submitOrderDate").value("prueba submitOrderDate").build(),
                KeyValueType.builder().key("tratamientoDatosDate").value("prueba tratamientoDatosDate").build(),
                KeyValueType.builder().key("afiliacionReciboDate").value("prueba afiliacionReciboDate").build(),
                KeyValueType.builder().key("custodiaDate").value("prueba custodiaDate").build()));

        salesWebClient.validateBeforeUpdate("02", "02", null);
    }

    @Test
    void existFieldInAdditionalData_Test() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = SalesWebClientImpl.class.getDeclaredMethod("existFieldInAdditionalData",
                String.class, List.class);
        method.setAccessible(true);
        method.invoke(salesWebClientImpl, "createContractDate",
                Collections.singletonList(KeyValueType.builder().key("createContractDate").value("prueba").build()));
    }

    @Test
    void existFieldInAdditionalData_Null_Test() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = SalesWebClientImpl.class.getDeclaredMethod("existFieldInAdditionalData",
                String.class, List.class);
        method.setAccessible(true);
        method.invoke(salesWebClientImpl, "afiliacionReciboDate",
                Collections.singletonList(KeyValueType.builder().key("createContractDate").value("prueba").build()));
    }
}
