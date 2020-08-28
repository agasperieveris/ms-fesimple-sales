package com.tdp.ms.sales.expose;

import java.util.*;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Class: SalesController. <br/>
 * <b>Copyright</b>: &copy; 2019 Telef&oacute;nica del Per&uacute;<br/>
 * <b>Company</b>: Telef&oacute;nica del Per&uacute;<br/>
 *
 * @author Telef&oacute;nica del Per&uacute; (TDP) <br/>
 *         <u>Service Provider</u>: Everis Per&uacute; SAC (EVE) <br/>
 *         <u>Developed by</u>: <br/>
 *         <ul>
 *         <li>Developer name</li>
 *         </ul>
 *         <u>Changes</u>:<br/>
 *         <ul>
 *         <li>YYYY-MM-DD Creaci&oacute;n del proyecto.</li>
 *         </ul>
 * @version 1.0
 */
@RestController
@RequestMapping("/fesimple/v1/sales")
public class SalesController {
  
    @GetMapping
    public Flux<SalesResponse> getSales(@Valid @RequestBody SalesRequest request,
            @RequestHeader(HttpHeadersKey.UNICA_SERVICE_ID) String serviceId,
            @RequestHeader(HttpHeadersKey.UNICA_APPLICATION) String application,
            @RequestHeader(HttpHeadersKey.UNICA_PID) String pid,
            @RequestHeader(HttpHeadersKey.UNICA_USER) String user) {
        SalesRequest sales = new SalesRequest();
        sales.setIdSales(request.getIdSales());
        sales.setName(request.getName());
        sales.setStatus(request.getStatus());
        sales.setStatusAudio(request.getStatusAudio());
        sales.setNationalID(request.getNationalID());
        sales.setNationalIDType(request.getNationalIDType());
        sales.setStartDateTime(request.getStartDateTime());
        sales.setEndDateTime(request.getEndDateTime());

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

        List<SalesResponse> response = new ArrayList<>();
        response.add(SalesResponse.builder().id("123456789").idSales("FE-000000001").name(sales.getName())
                .priority("priority")
                .channel(Channel.builder().agent(agent).name("RETAIL").storeId("Punto de venta").storeName("entidad")
                        .build())
                .productType("MT").commercialOperation(comercialOperation).prospectContact(prospectContact)
                .relatedParty(relatedParty).status("string").statusChangeDate("string").statusChangeReason("string")
                .startDateTime(sales.getStartDateTime()).endDateTime(sales.getEndDateTime())
                .additionalData(additionalData).build());

        return Flux.fromIterable(response);
    }

    @PostMapping("/created")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<SalesResponse> createdSales(@Valid @RequestBody SalesResponse response,
            @RequestHeader(HttpHeadersKey.UNICA_SERVICE_ID) String serviceId,
            @RequestHeader(HttpHeadersKey.UNICA_APPLICATION) String application,
            @RequestHeader(HttpHeadersKey.UNICA_PID) String pid,
            @RequestHeader(HttpHeadersKey.UNICA_USER) String user) {
        return Mono.just(response);
    }

    @PutMapping("/update")
    public Mono<SalesResponse> updateSales(@Valid @RequestBody SalesResponse response,
            @RequestHeader(HttpHeadersKey.UNICA_SERVICE_ID) String serviceId,
            @RequestHeader(HttpHeadersKey.UNICA_APPLICATION) String application,
            @RequestHeader(HttpHeadersKey.UNICA_PID) String pid,
            @RequestHeader(HttpHeadersKey.UNICA_USER) String user) {
        return Mono.just(response);
    }

}
