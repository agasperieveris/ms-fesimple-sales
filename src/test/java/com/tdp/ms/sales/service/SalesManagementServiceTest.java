package com.tdp.ms.sales.service;

import static org.mockito.ArgumentMatchers.any;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.tdp.genesis.core.constants.HttpHeadersKey;
import com.tdp.ms.sales.business.SalesManagementService;
import com.tdp.ms.sales.client.WebClientBusinessParameters;
import com.tdp.ms.sales.model.dto.BusinessParametersData;
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
import com.tdp.ms.sales.model.entity.Sale;
import com.tdp.ms.sales.model.response.BusinessParametersResponse;
import com.tdp.ms.sales.repository.SalesRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class SalesManagementServiceTest {

    @MockBean
    private WebClientBusinessParameters webClientToken;

    @MockBean
    private SalesRepository salesRepository;

    @Autowired
    private SalesManagementService salesManagementService;

    private static Sale sale;
    private static Sale sale2;
    
    @BeforeAll
    static void setup() {

        Map<String,String> headersMap = new HashMap<>();
        headersMap.put(HttpHeadersKey.UNICA_SERVICE_ID, "1");
        headersMap.put(HttpHeadersKey.UNICA_APPLICATION, "application");
        headersMap.put(HttpHeadersKey.UNICA_PID, "pid");
        headersMap.put(HttpHeadersKey.UNICA_USER, "user");

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
    }

    @Test
    void postSaveSale() {
        Map<String, String> headersMap = new HashMap<String, String>();
        headersMap.put(HttpHeadersKey.UNICA_SERVICE_ID, "serviceId");
        headersMap.put(HttpHeadersKey.UNICA_PID, "pid");
        headersMap.put(HttpHeadersKey.UNICA_APPLICATION, "application");
        headersMap.put(HttpHeadersKey.UNICA_USER, "user");
        Mockito.when(salesRepository.findAll(any()))
                .thenReturn(Flux.just(sale2));

        Mockito.when(salesRepository.save(any()))
                .thenReturn(Mono.just(sale));

        BusinessParametersData businessParametersData = BusinessParametersData
                .builder()
                .value("FE-000000001")
                .build();
        List<BusinessParametersData> businessParametersDataList = new ArrayList<>();
        businessParametersDataList.add(businessParametersData);

        Mockito.when(webClientToken.getNewSaleSequential(any(), any()))
                .thenReturn(Mono.just(BusinessParametersResponse
                        .builder()
                        .data(businessParametersDataList)
                        .build()));

        Mono<Sale> result = salesManagementService.post(sale, headersMap);

        StepVerifier.create(result)
                .assertNext(c -> {
                    Assert.assertEquals(c.getId(), sale.getId());
                })
                .verifyComplete();


    }
}
