package com.tdp.ms.sales.expose;

import com.tdp.genesis.core.constants.HttpHeadersKey;
import com.tdp.ms.commons.dto.sales.RelatedParty;
import com.tdp.ms.sales.business.SalesManagmentService;
import com.tdp.ms.sales.model.dto.ChannelRef;
import com.tdp.ms.sales.model.dto.CommercialOperationType;
import com.tdp.ms.sales.model.dto.ContactMedium;
import com.tdp.ms.sales.model.dto.CreateProductOrderResponseType;
import com.tdp.ms.sales.model.dto.DeviceOffering;
import com.tdp.ms.sales.model.dto.EntityRefType;
import com.tdp.ms.sales.model.dto.KeyValueType;
import com.tdp.ms.sales.model.dto.MediumCharacteristic;
import com.tdp.ms.sales.model.dto.Money;
import com.tdp.ms.sales.model.dto.OfferingType;
import com.tdp.ms.sales.model.dto.PaymentType;
import com.tdp.ms.sales.model.dto.Place;
import com.tdp.ms.sales.model.dto.ProductInstanceType;
import com.tdp.ms.sales.model.dto.TimePeriod;
import com.tdp.ms.sales.model.entity.Sale;
import com.tdp.ms.sales.model.request.PostSalesRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@AutoConfigureWebTestClient(timeout = "20000")
public class SalesControllerTest {
    @Autowired
    private WebTestClient webClient;

    @MockBean
    private SalesManagmentService salesManagmentService;

    private static Sale sale;
    private static PostSalesRequest request;
    private static final HashMap<String, String> headersMap = new HashMap();
    private static final String RH_UNICA_SERVICE_ID = "d4ce144c-6b26-4b5c-ad29-090a3a559d80";
    private static final String RH_UNICA_APPLICATION = "fesimple-sales";
    private static final String RH_UNICA_PID = "d4ce144c-6b26-4b5c-ad29-090a3a559d83";
    private static final String RH_UNICA_USER = "BackendUser";
    private static final String RH_UNICA_TIMESTAMP = "2020-08-26T17:15:20.509-0400";

    @BeforeAll
    static void setup() {
        // Setting request headers
        headersMap.put(HttpHeadersKey.UNICA_SERVICE_ID, RH_UNICA_SERVICE_ID);
        headersMap.put(HttpHeadersKey.UNICA_APPLICATION, RH_UNICA_APPLICATION);
        headersMap.put(HttpHeadersKey.UNICA_PID, RH_UNICA_PID);
        headersMap.put(HttpHeadersKey.UNICA_USER, RH_UNICA_USER);

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

        KeyValueType additionalData1 = new KeyValueType();
        additionalData1.setKey("s");
        additionalData1.setValue("d");
        KeyValueType additionalData2 = new KeyValueType();
        additionalData2.setKey("deliveryMethod");
        additionalData2.setValue("IS");
        KeyValueType additionalData3 = new KeyValueType();
        additionalData3.setKey("ufxauthorization");
        additionalData3.setValue("14fwTedaos4sdgZvyay8H");
        additionalDatas.add(additionalData1);
        additionalDatas.add(additionalData2);
        additionalDatas.add(additionalData3);

        EntityRefType entityRefType = new EntityRefType();
        entityRefType.setHref("s");
        entityRefType.setId("s");
        entityRefType.setName("f");

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
        places.add(place);
        DeviceOffering deviceOffering = new DeviceOffering();

        deviceOffering.setAdditionalData(additionalDatas);
        deviceOffering.setId("s");

        deviceOfferings.add(deviceOffering);
        ProductInstanceType product= new ProductInstanceType();
        product.setId("s");
        product.setProductSpec(entityRefType);

        CreateProductOrderResponseType order = CreateProductOrderResponseType
                .builder()
                .productOrderId("930686A")
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

        List<RelatedParty> relatedParties = new ArrayList<>();

        TimePeriod validFor = new TimePeriod();

        validFor.setStartDateTime("");
        validFor.setEndDateTime("");

        relatedParties.add(relatedParty);

        PaymentType paymentType = PaymentType
                .builder()
                .paymentType("EX")
                .build();

        sale = Sale
                .builder()
                .id("1")
                .salesId("FE-000000001")
                .name("Cesar")
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
                .paymenType(paymentType)
                .build();

        request = PostSalesRequest
                .builder()
                .sale(sale)
                .headersMap(headersMap)
                .build();
    }

    @Test
    void createdSales() {
        Mockito.when(salesManagmentService.post(any()))
                .thenReturn(Mono.just(sale));

        WebTestClient.ResponseSpec responseSpec = webClient.post()
                .uri("/fesimple/v1/sales")
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeadersKey.UNICA_SERVICE_ID, "550e8400-e29b-41d4-a716-446655440000")
                .header(HttpHeadersKey.UNICA_APPLICATION, "genesis")
                .header(HttpHeadersKey.UNICA_PID, "550e8400-e29b-41d4-a716-446655440000")
                .header(HttpHeadersKey.UNICA_USER, "genesis")
                .bodyValue(sale)
                .exchange();

        responseSpec.expectStatus().isCreated();

        responseSpec.expectBody()
                .jsonPath("$.id").isEqualTo(sale.getId())
                .jsonPath("$.name").isEqualTo(sale.getName())
                .jsonPath("$.description").isEqualTo(sale.getDescription());
    }

}
