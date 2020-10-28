package com.tdp.ms.sales.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tdp.genesis.core.exception.GenesisException;
import com.tdp.genesis.core.exception.GenesisExceptionBuilder;
import com.tdp.genesis.core.utils.PropertyUtils;
import com.tdp.ms.sales.model.dto.*;
import com.tdp.ms.sales.model.entity.Sale;
import com.tdp.ms.sales.repository.SalesRepository;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.env.PropertyResolver;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class StockImplExceptionTest {

    @MockBean
    private SalesRepository salesRepository;
    private static Sale sale;
    private static List<KeyValueType> additionalDatas;
    private static ContactMedium contactMedium;
    private static List<IdentityValidationType> identityValidationTypeList = new ArrayList<>();

    @Autowired
    private StockWebClient stockWebClient;
    private ObjectMapper MAPPER = new ObjectMapper();

    @BeforeAll
    static void setUp() {
        preparePropertyResolverForPropertyUtils();

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
    }

    private static void preparePropertyResolverForPropertyUtils() {
        PropertyResolver resolver = mock(PropertyResolver.class);
        PropertyUtils.setResolver(resolver);
    }

    @Test
    public void fallbackReserveStockTest() throws JsonProcessingException, GenesisException {
        Mockito.when(salesRepository.save(any())).thenReturn(Mono.just(sale));

        GenesisExceptionBuilder builder = GenesisException.builder();

        builder.exceptionId("SVC0001")
                .userMessage("Test")
                .exceptionText("Test")
                .wildcards(new String[]{"Bad Request"})
                .addDetail(true)
                .withDescription("Test");

        GenesisException ge = builder.build();

        WebClientResponseException webClientResponseException =
                new WebClientResponseException("There was a problem from Reserve Stock FE+Simple Service",
                        400, "Problem", null, MAPPER.writeValueAsBytes(ge), null);

        stockWebClient.fallbackReserveStock(webClientResponseException, sale);
    }

    @Test
    public void throwExceptionCreateProductOrder_On_BadRequest_Test() throws JsonProcessingException, GenesisException {
        Mockito.when(salesRepository.save(any())).thenReturn(Mono.just(sale));

        GenesisExceptionBuilder builder = GenesisException.builder();

        builder.exceptionId("SVC0001")
                .userMessage("Test")
                .exceptionText("Test")
                .wildcards(new String[]{"Bad Request"})
                .addDetail(true)
                .withDescription("Test");

        GenesisException ge = builder.build();

        WebClientResponseException webClientResponseException =
                new WebClientResponseException("There was a problem from Reserve Stock FE+Simple Service",
                        400, "Problem", null, MAPPER.writeValueAsBytes(ge), null);

        stockWebClient.throwExceptionReserveStock(webClientResponseException);
    }

    @Test
    public void throwExceptionCreateProductOrder_On_NotFound_Exception_Test() throws JsonProcessingException, GenesisException {
        Mockito.when(salesRepository.save(any())).thenReturn(Mono.just(sale));

        GenesisExceptionBuilder builder = GenesisException.builder();

        builder.exceptionId("SVC0004")
                .userMessage("Test")
                .exceptionText("Test")
                .wildcards(new String[]{"Not Found"})
                .addDetail(true)
                .withDescription("Test");

        GenesisException ge = builder.build();

        WebClientResponseException webClientResponseException =
                new WebClientResponseException("There was a problem from Reserve Stock FE+Simple Service",
                        404, "Problem", null, MAPPER.writeValueAsBytes(ge), null);

        stockWebClient.throwExceptionReserveStock(webClientResponseException);
    }

    @Test
    public void throwExceptionCreateProductOrder_On_ServerFailed_Exception_Test() throws JsonProcessingException, GenesisException {
        Mockito.when(salesRepository.save(any())).thenReturn(Mono.just(sale));

        GenesisExceptionBuilder builder = GenesisException.builder();

        builder.exceptionId("SVR1008")
                .userMessage("Test")
                .exceptionText("Test")
                .wildcards(new String[]{"Service Failed"})
                .addDetail(true)
                .withDescription("Test");

        GenesisException ge = builder.build();

        WebClientResponseException webClientResponseException =
                new WebClientResponseException("There was a problem from Reserve Stock FE+Simple Service",
                        500, "Problem", null, MAPPER.writeValueAsBytes(ge), null);

        stockWebClient.throwExceptionReserveStock(webClientResponseException);
    }

    @Test
    public void throwExceptionCreateProductOrder_On_Unauthorized_Exception_Test() throws JsonProcessingException, GenesisException {
        Mockito.when(salesRepository.save(any())).thenReturn(Mono.just(sale));

        GenesisExceptionBuilder builder = GenesisException.builder();

        builder.exceptionId("SVC0001")
                .userMessage("Test")
                .exceptionText("Test")
                .wildcards(new String[]{"Unauthorized"})
                .addDetail(true)
                .withDescription("Test");

        GenesisException ge = builder.build();

        WebClientResponseException webClientResponseException =
                new WebClientResponseException("There was a problem from Reserve Stock FE+Simple Service",
                        401, "Problem", null, MAPPER.writeValueAsBytes(ge), null);

        stockWebClient.throwExceptionReserveStock(webClientResponseException);
    }

    @Test
    public void fallbackReserveStock_On_IllegalState_Exception_Test() throws GenesisException {
        stockWebClient.throwExceptionReserveStock(new IllegalStateException());
    }

}
