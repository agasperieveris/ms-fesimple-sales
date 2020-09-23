package com.tdp.ms.sales.service;

import com.tdp.genesis.core.constants.HttpHeadersKey;
import com.tdp.ms.sales.business.SalesService;
import com.tdp.ms.sales.model.dto.*;
import com.tdp.ms.sales.model.entity.Sale;
import com.tdp.ms.sales.model.request.GetSalesRequest;
import com.tdp.ms.sales.model.request.SalesRequest;
import com.tdp.ms.sales.model.response.SalesResponse;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class SalesServiceTest {

    @MockBean
    private SalesRepository salesRepository;

    @Autowired
    private SalesService salesService;

    private static Sale sale;
    private static SalesRequest salesRequest;
    private static Sale sale2;
    private static SalesResponse salesResponse;
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
        Channel channel= new Channel();
        channel.setId("1");
        channel.setHref("s");
        channel.setName("s");
        channel.setStoreId("s");
        channel.setStoreName("s");

        Agent agent= new Agent();
        agent.setId("1");
        agent.setNationalId("Peru");
        agent.setNationalId("DNI");
        List<AdditionalData> additionalDatas = new ArrayList<>();

        AdditionalData additionalData = new AdditionalData();
        additionalData.setKey("s");
        additionalData.setValue("d");
        additionalDatas.add(additionalData);

        EntityRefType entityRefType = new EntityRefType();
        entityRefType.setHref("s");
        entityRefType.setId("s");
        entityRefType.setName("f");
        entityRefType.setType("s");

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
        Product product= new Product();
        product.setId("s");

        product.setAdditionalData(additionalDatas);
        List<ComercialOperationType> comercialOperationTypes = new ArrayList<>();
         ComercialOperationType comercialOperationType = new ComercialOperationType();
         comercialOperationType.setId("1");
         comercialOperationType.setName("h");
         comercialOperationType.setReason("d");
         comercialOperationType.setProduct(product);
         comercialOperationType.setDeviceOffering(deviceOfferings);
         comercialOperationType.setAction("s");
         comercialOperationType.setAdditionalData(additionalDatas);
         comercialOperationTypes.add(comercialOperationType);

        EstimatedRevenue estimatedRevenue = new EstimatedRevenue();

        estimatedRevenue.setUnit("s");
        estimatedRevenue.setValue(12f);

        ProspectContact prospectContact = new ProspectContact();
        Characteristic characteristic = new Characteristic();
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

        List<ProspectContact> prospectContacts = new ArrayList<>();

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

        List<RelatedParty> relatedParties = new ArrayList<>();

        ValidFor validFor = new ValidFor();

        validFor.setStartDateTime("");
        validFor.setEndDateTime("");

        relatedParties.add(relatedParty);
        sale = Sale
                .builder()
                .id("1")
                .salesId(1l)
                .name("Sergio")
                .description("venta de lote")
                .priority("x")
                .channel(channel)
                .agent(agent)
                .productType("s")
                .comercialOperationType(comercialOperationTypes)
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

        salesRequest = SalesRequest
                .builder()
                .id("1")
                .salesId("FE-000000001")
                .name("Sergio")
                .description("venta de lote")
                .priority("x")
                .channel(channel)
                .agent(agent)
                .productType("s")
                .comercialOperationType(comercialOperationTypes)
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

        sale2 = Sale
                .builder()
                .id("1")
                .salesId(Long.valueOf(1))
                .name("Sergio")
                .description("venta de lote")
                .build();

        salesResponse = SalesResponse
                .builder()
                .id("1")
                .salesId("FE-"+sale.getSalesId())
                .name("Sergio")
                .description("venta de lote")
                .priority("x")
                .channel(channel)
                .agent(agent)
                .productType("s")
                .comercialOperationType(comercialOperationTypes)
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

        Mono<SalesResponse> result = salesService.getSale(request);

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
        Mockito.when(salesRepository.findAll(any()))
                .thenReturn(Flux.just(sale2));

        Mockito.when(salesRepository.save(any()))
                .thenReturn(Mono.just(sale));

        Mono<SalesResponse> result = salesService.post(sale);

        StepVerifier.create(result)
                .assertNext(c -> {
                    Assert.assertEquals(c.getId(), sale.getId());
                })
                .verifyComplete();


    }

    @Test
    void putSaveSale() {
        Mockito.when(salesRepository.findById(sale2.getId()))
                .thenReturn(Mono.just(sale2));

        Mockito.when(salesRepository.save(any()))
                .thenReturn(Mono.just(sale2));

        Mono<SalesResponse> result = salesService.put(salesRequest);

        StepVerifier.create(result)
                .assertNext(c -> {
                    Assert.assertEquals(c.getId(), sale2.getId());
                })
                .verifyComplete();
    }

    @Test
    void confirmationSale() {
        Map<String, String> headersMap = new HashMap<String, String>();
        headersMap.put(HttpHeadersKey.UNICA_SERVICE_ID, "serviceId");
        headersMap.put(HttpHeadersKey.UNICA_APPLICATION, "application");
        headersMap.put(HttpHeadersKey.UNICA_PID, "pid");
        headersMap.put(HttpHeadersKey.UNICA_USER, "user");
        headersMap.put("ufxauthorization", "ufxauthorization");

        Mockito.when(salesRepository.findById(sale2.getId()))
                .thenReturn(Mono.just(sale2));

        Mockito.when(salesRepository.save(any()))
                .thenReturn(Mono.just(sale2));

        Mono<SalesResponse> result = salesService.confirmationSalesLead(salesResponse, headersMap);

        StepVerifier.create(result)
                .assertNext(c -> {
                    Assert.assertEquals(c.getId(), sale2.getId());
                })
                .verifyComplete();
    }
}
