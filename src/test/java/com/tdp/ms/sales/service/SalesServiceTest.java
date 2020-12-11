package com.tdp.ms.sales.service;

import com.tdp.genesis.core.constants.HttpHeadersKey;
import com.tdp.ms.sales.business.SalesService;
import com.tdp.ms.sales.business.impl.SalesServiceImpl;
import com.tdp.ms.sales.client.WebClientBusinessParameters;
import com.tdp.ms.sales.eventflow.client.SalesWebClient;
import com.tdp.ms.sales.eventflow.client.impl.SalesWebClientImpl;
import com.tdp.ms.sales.model.dto.*;
import com.tdp.ms.sales.model.dto.businessparameter.BusinessParameterDataSeq;
import com.tdp.ms.sales.model.entity.Sale;
import com.tdp.ms.sales.model.request.GetSalesRequest;
import com.tdp.ms.sales.model.request.SalesRequest;
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
import java.util.*;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class SalesServiceTest {

    @MockBean
    private WebClientBusinessParameters webClientToken;

    @MockBean
    private SalesRepository salesRepository;

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
                .saleCreationDate("24/09/2020T12:43:03")
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
        Mockito.when(salesRepository.findAll()).thenReturn(Flux.just(sale));

        Flux<Sale> result = salesService.getSaleList("1","bc12",
                "1", "1", "Peru", "DNI",
                "s", "1", "s", "orderId", "24/09/2020T12:43:00",
                "24/09/2020T12:43:21", "size", "pageCount", "page",
                "maxResultCount");

        StepVerifier.create(result)
                .expectNextCount(1);
    }

    @Test
    void filterSalesWithParamsTest() {
        salesServiceImpl.filterSalesWithParams(sale, "1","bc12",
                "1", "1", "Peru", "DNI",
                "s", "1", "s", "orderId", "24/09/2020T12:43:00",
                "24/09/2020T12:43:21");
    }

    @Test
    void filterChannelIdTest() {
        salesServiceImpl.filterChannelId(sale, "930686A");
    }

    @Test
    void filterChannelId_Null_Test() {
        salesServiceImpl.filterChannelId(sale, null);
    }

    @Test
    void filterDealerIdTest() {
        salesServiceImpl.filterDealerId(sale, "930686A");
    }

    @Test
    void filterDealerId_Null_Test() {
        salesServiceImpl.filterDealerId(sale, null);
    }

    @Test
    void filterAgentIdTest() {
        salesServiceImpl.filterAgentId(sale, "930686A");
    }

    @Test
    void filterAgentId_Null_Test() {
        salesServiceImpl.filterAgentId(sale, null);
    }

    @Test
    void filterStoreIdTest() {
        salesServiceImpl.filterStoreId(sale, "930686A");
    }

    @Test
    void filterStoreId_Null_Test() {
        salesServiceImpl.filterStoreId(sale, null);
    }

    @Test
    void filterStatusTest() {
        salesServiceImpl.filterStatus(sale, "930686A");
    }

    @Test
    void filterStatus_Null_Test() {
        salesServiceImpl.filterStatus(sale, null);
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
    void filterSaleCreationDateTest() {
        salesServiceImpl.filterSaleCreationDate(sale, "24/09/2020T12:43:00",
                "24/09/2020T12:43:21");
    }

    @Test
    void filterSaleCreationDate_nullDateTest() {
        sale.setSaleCreationDate(null);
        salesServiceImpl.filterSaleCreationDate(sale, "24/09/2020T12:43:00",
                "24/09/2020T12:43:21");
    }

    @Test
    void filterSaleCreationDate_nullStartDate_nullEndDateTest() {
        salesServiceImpl.filterSaleCreationDate(sale, null, null);
    }

    @Test
    void filterSalesIdTest() {
        salesServiceImpl.filterSalesId(sale, "FE-0000000486");
    }

    @Test
    void filterSalesId_salesIdNullTest() {
        salesServiceImpl.filterSalesId(sale, null);
    }

    @Test
    void filterSalesId_salesIdEmptyTest() {
        salesServiceImpl.filterSalesId(sale, "");
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
