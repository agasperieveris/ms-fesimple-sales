package com.tdp.ms.sales.expose;

import com.tdp.genesis.core.constants.HttpHeadersKey;
import com.tdp.ms.sales.model.entity.AdditionalData;
import com.tdp.ms.sales.model.entity.Agent;
import com.tdp.ms.sales.model.entity.Channel;
import com.tdp.ms.sales.model.entity.Characteristic;
import com.tdp.ms.sales.model.entity.ComercialOperationType;
import com.tdp.ms.sales.model.entity.DeviceOffering;
import com.tdp.ms.sales.model.entity.Place;
import com.tdp.ms.sales.model.entity.Product;
import com.tdp.ms.sales.model.entity.ProductOfering;
import com.tdp.ms.sales.model.entity.ProspectContact;
import com.tdp.ms.sales.model.entity.RelatedParty;
import com.tdp.ms.sales.model.request.SalesRequest;
import com.tdp.ms.sales.model.response.SalesResponse;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.RequestBodySpec;
import org.springframework.web.reactive.function.BodyInserters;

@SpringBootTest
@AutoConfigureWebTestClient(timeout = "20000")
public class SalesControllerTest {

    @Autowired
    private WebTestClient webClient;

    @BeforeAll
    public static void setup() {
    }

    @Test
    public void post_sales_by_valid_test() {
        SalesResponse response = createMockSales();
        WebTestClient.ResponseSpec responseSpec = webClient.post().uri("/fesimple/v1/sales/created")
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeadersKey.UNICA_SERVICE_ID, "550e8400-e29b-41d4-a716-446655440000")
                .header(HttpHeadersKey.UNICA_APPLICATION, "genesis")
                .header(HttpHeadersKey.UNICA_PID, "550e8400-e29b-41d4-a716-446655440000")
                .header(HttpHeadersKey.UNICA_USER, "genesis").body(BodyInserters.fromValue(response)).exchange()
                .expectStatus().isCreated();

        responseSpec.expectBody(SalesResponse.class);
    }

    @Test
    public void get_sales_by_valid_test() {
        SalesRequest sales = new SalesRequest();
        sales.setName("Lincoln Jeampier");
        sales.setStatus("estado de la venta");
        sales.setStatusAudio("motivo de la venta");
        sales.setNationalID("DNI");
        sales.setNationalIDType("12345678");
        sales.setStartDateTime("2020-08-27T20:21:59.657Z");
        sales.setEndDateTime("2020-08-27T20:21:59.657Z");

        List<SalesResponse> response = createListSalesResponse();

        WebTestClient.ResponseSpec responseSpec = ((RequestBodySpec) webClient.get().uri("/fesimple/v1/sales")
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeadersKey.UNICA_SERVICE_ID, "550e8400-e29b-41d4-a716-446655440000")
                .header(HttpHeadersKey.UNICA_APPLICATION, "genesis")
                .header(HttpHeadersKey.UNICA_PID, "550e8400-e29b-41d4-a716-446655440000")
                .header(HttpHeadersKey.UNICA_USER, "genesis")).body(BodyInserters.fromValue(sales)).exchange()
                        .expectStatus().isOk();

        responseSpec.expectBody().jsonPath("$[0].id").isEqualTo((response.get(0).getId()));

    }

    @Test
    public void put_sales_by_valid_test() {
        SalesResponse response = createMockSales();
        WebTestClient.ResponseSpec responseSpec = webClient.put().uri("/fesimple/v1/sales/update")
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeadersKey.UNICA_SERVICE_ID, "550e8400-e29b-41d4-a716-446655440000")
                .header(HttpHeadersKey.UNICA_APPLICATION, "genesis")
                .header(HttpHeadersKey.UNICA_PID, "550e8400-e29b-41d4-a716-446655440000")
                .header(HttpHeadersKey.UNICA_USER, "genesis").body(BodyInserters.fromValue(response)).exchange()
                .expectStatus().isOk();

        responseSpec.expectBody(SalesResponse.class);
    }

    public List<SalesResponse> createListSalesResponse() {
        List<SalesResponse> response = new ArrayList<>();
        response.add(createMockSales());
        return response;
    }

    public SalesResponse createMockSales() {
        SalesRequest sales = new SalesRequest();
        sales.setName("Lincoln Jeampier");
        sales.setStatus("estado de la venta");
        sales.setStatusAudio("motivo de la venta");
        sales.setNationalID("DNI");
        sales.setNationalIDType("12345678");
        sales.setStartDateTime("2020-08-27T20:21:59.657Z");
        sales.setEndDateTime("2020-08-27T20:21:59.657Z");

        List<Agent> agent = new ArrayList<>();
        agent.add(Agent.builder().id("123456789").nationalID(sales.getNationalID())
                .nationalIDType(sales.getNationalIDType()).build());

        List<AdditionalData> additionalData = new ArrayList<>();
        additionalData.add(AdditionalData.builder().key("string").value("string").build());
        List<ProductOfering> productOfferings = new ArrayList<>();
        productOfferings.add(ProductOfering.builder().id("string").name("string").productType("Mobile").build());
        List<DeviceOffering> deviceOffering = new ArrayList<>();
        deviceOffering.add(DeviceOffering.builder().id("string").name("string").build());
        List<Place> place = new ArrayList<>();
        place.add(Place.builder().id("string").name("stirng").build());

        List<Product> product = new ArrayList<>();
        product.add(Product.builder().id("string").publicId("string").publicType("string")
                .additionalData(additionalData).place(place).build());
        List<ComercialOperationType> comercialOperation = new ArrayList<>();
        comercialOperation.add(ComercialOperationType.builder().id("Identificador de la Operación Comercial")
                .name("Nombre de la Operación Comercial").action("Provide").reason("ALTA")
                .additionalData(additionalData).orderId("946088A").productOfferings(productOfferings)
                .deviceOffering(deviceOffering).product(product).build());

        List<ProspectContact> prospectContact = new ArrayList<>();

        prospectContact.add(
                ProspectContact.builder().preferred(true).mediumType("email").startDateTime(sales.getStartDateTime())
                        .characteristic(new Characteristic()).emailAddress("lincoln.morales.@telefonica.com")

                        .city(null).build());

        prospectContact.add(
                ProspectContact.builder().preferred(true).mediumType("phone").startDateTime(sales.getStartDateTime())
                        .characteristic(new Characteristic()).phoneNumber("962340461").build());

        prospectContact.add(ProspectContact.builder().preferred(false).mediumType("postal address")
                .startDateTime(sales.getStartDateTime()).characteristic(new Characteristic())
                .street1("CL JAUJA 574 UR CERCADO DE TARMA").postCode("string").city("string").country("string")
                .build());

        prospectContact.add(ProspectContact.builder().preferred(false).mediumType("installation address")
                .startDateTime(sales.getStartDateTime()).characteristic(new Characteristic())
                .street1("CL JAUJA 574 UR CERCADO DE TARMA").postCode("string").city("string").stateOrProvince("string")
                .region("string").build());

        List<RelatedParty> relatedParty = new ArrayList<>();
        relatedParty.add(RelatedParty.builder().baseType("Customer").id("123456789").customerId("56843169")
                .firstName("Lincoln Jeampier").lastName("Morales Alba").nationalID(sales.getNationalID())
                .nationalIDType(sales.getNationalIDType()).build());

        return SalesResponse.builder().id("123456789").idSales("FE-000000001").name(sales.getName())
                .priority("priority")
                .channel(Channel.builder().agent(agent).name("RETAIL").storeId("Punto de venta").storeName("entidad")
                        .build())
                .productType("MT").commercialOperation(comercialOperation).prospectContact(prospectContact)
                .relatedParty(relatedParty).status("string").statusChangeDate("string").statusChangeReason("string")
                .startDateTime(sales.getStartDateTime()).endDateTime(sales.getEndDateTime())
                .additionalData(additionalData).build();
    }

}
