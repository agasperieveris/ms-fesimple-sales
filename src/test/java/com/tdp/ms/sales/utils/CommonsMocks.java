package com.tdp.ms.sales.utils;

import com.tdp.genesis.core.constants.HttpHeadersKey;
import com.tdp.ms.sales.model.dto.*;
import com.tdp.ms.sales.model.entity.Sale;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CommonsMocks {

    public static Sale createSaleMock() {
        Sale sale = new Sale();
        List<KeyValueType> additionalDatas = new ArrayList<>();
        ContactMedium contactMedium = new ContactMedium();
        List<IdentityValidationType> identityValidationTypeList = new ArrayList<>();

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
        deviceOffering.setDeviceType("SmartPhone");

        StockType stockType = StockType
                .builder()
                .reservationId("")
                .build();
        deviceOffering.setStock(stockType);


        MoneyAmount moneyAmount1 = MoneyAmount
                .builder()
                .value(150.6)
                .currency("SOL")
                .build();

        Instalments instalments = new Instalments();
        instalments.setAmount(moneyAmount1);
        instalments.setOpeningQuota(moneyAmount1);
        instalments.setTotalAmount(moneyAmount1);

        FinancingInstalment financingInstalment1 = new FinancingInstalment();
        financingInstalment1.setInstalments(instalments);
        financingInstalment1.setDescription("TELEFCONT");
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

        List<MoneyAmount> pricesList = new ArrayList<>();
        pricesList.add(moneyAmount1);

        SimSpecification simSpecification1 = SimSpecification
                .builder()
                .price(pricesList)
                .build();

        List<SimSpecification> simSpecificationsList = new ArrayList<>();
        simSpecificationsList.add(simSpecification1);
        deviceOffering.setSimSpecifications(simSpecificationsList);

        deviceOfferings.add(deviceOffering);
        deviceOfferings.add(deviceOffering);
        ProductInstanceType product= new ProductInstanceType();
        product.setId("s");
        product.setProductSpec(entityRefType);
        product.setPublicId("234562433");
        product.setProductOffering(entityRefType);
        product.setProductRelationShip(Arrays.asList(RelatedProductType.builder()
                .product(ProductRefInfoType.builder()
                        .description("SimDevice").name("Device").id("string")
                        .productRelationship(Arrays.asList(ProductProductRelationShip.builder()
                                .product(ProductRelationShipProduct.builder()
                                        .description("Device").id("8091734238").build()).build()))
                        .build())
                .build()));

        TopLevelProductConfigurationType productConfiguration = new TopLevelProductConfigurationType();
        productConfiguration.setLineOfBusinessType("cableTv");
        productConfiguration.setServiceId("123456");

        NewProductInNewOfferingInstanceConfigurationType newProducts1 = new NewProductInNewOfferingInstanceConfigurationType();
        newProducts1.setProductConfiguration(productConfiguration);

        List<NewProductInNewOfferingInstanceConfigurationType> newProductsInNewOfferingsList = new ArrayList<>();
        newProductsInNewOfferingsList.add(newProducts1);

        CreateProductOrderResponseType order = CreateProductOrderResponseType
                .builder()
                .productOrderId("930686A")
                .productOrderReferenceNumber("761787835447")
                .build();

        OfferingType offeringType1= new OfferingType();
        offeringType1.setId("s");
        offeringType1.setProductOfferingProductSpecId("s");
        offeringType1.setType("BROADBAND");

        MoneyType maxPrice = MoneyType
                .builder()
                .amount(150.0)
                .build();

        BenefitType benefitType1 = new BenefitType();
        benefitType1.setDownloadSpeed("1000Mbps");

        List<BenefitType> benefitsList = new ArrayList<>();
        benefitsList.add(benefitType1);
        offeringType1.setBenefits(benefitsList);

        ComponentProdOfferPriceType componentProdOfferPriceType1 = ComponentProdOfferPriceType
                .builder()
                .maxPrice(maxPrice)
                .benefits(benefitsList)
                .price(MoneyType.builder().amount(100.00).build())
                .build();
        List<ComponentProdOfferPriceType> productOfferingPricesList = new ArrayList<>();
        productOfferingPricesList.add(componentProdOfferPriceType1);
        offeringType1.setProductOfferingPrice(productOfferingPricesList);

        ProductSpecCharacteristicType productCharacteristics1 =  new ProductSpecCharacteristicType();
        productCharacteristics1.setId("test");
        List<ProductSpecCharacteristicType> productCharacteristicsList = new ArrayList<>();
        productCharacteristicsList.add(productCharacteristics1);
        RefinedProductType refinedProduct = new RefinedProductType();
        refinedProduct.setProductCharacteristics(productCharacteristicsList);

        ComposingProductType productSpecification1 = new ComposingProductType();
        productSpecification1.setProductType("landline");
        productSpecification1.setRefinedProduct(refinedProduct);
        productSpecification1.setProductPrice(productOfferingPricesList);

        ComposingProductType productSpecification2 = new ComposingProductType();
        productSpecification2.setProductType("broadband");
        productSpecification2.setProductPrice(Arrays.asList(ComponentProdOfferPriceType.builder().build(),
                ComponentProdOfferPriceType.builder().build(),
                ComponentProdOfferPriceType.builder().additionalData(Arrays.asList(KeyValueType.builder().key("downloadSpeed").value("string").build())).build()));
        productSpecification2.setRefinedProduct(refinedProduct);

        ComposingProductType productSpecification3 = new ComposingProductType();
        productSpecification3.setProductType("cableTv");
        productSpecification3.setRefinedProduct(refinedProduct);

        ComposingProductType productSpecification4 = new ComposingProductType();
        productSpecification4.setProductType("Device");
        productSpecification4.setRefinedProduct(refinedProduct);

        ComposingProductType productSpecification5 = new ComposingProductType();
        productSpecification5.setProductType("Accesories");
        productSpecification5.setRefinedProduct(refinedProduct);

        List<ComposingProductType> productSpecificationList = new ArrayList<>();
        productSpecificationList.add(productSpecification1);
        productSpecificationList.add(productSpecification2);
        productSpecificationList.add(productSpecification3);
        productSpecificationList.add(productSpecification4);
        productSpecificationList.add(productSpecification5);
        offeringType1.setProductSpecification(productSpecificationList);

        KeyValueType keyValueModemPremium = KeyValueType.builder().key("modemPremium").value("true").build();
        KeyValueType keyValueUltraWifi = KeyValueType.builder().key("ultraWifi").value("true").build();
        List<KeyValueType> additionalDataOfferingTypeList = new ArrayList<>();
        additionalDataOfferingTypeList.add(keyValueModemPremium);
        additionalDataOfferingTypeList.add(keyValueUltraWifi);
        offeringType1.setAdditionalData(additionalDataOfferingTypeList);

        List<OfferingType> productOfferings = new ArrayList<>();
        productOfferings.add(offeringType1);
        productOfferings.add(offeringType1);
        productOfferings.add(offeringType1);
        productOfferings.add(offeringType1);

        productOfferings.get(0).getProductSpecification().get(0).setProductType("cableTv");
        productOfferings.get(1).getProductSpecification().get(0).setProductType(Constants.PRODUCT_TYPE_BROADBAND);
        productOfferings.get(3).getProductSpecification().get(0).setProductType("device");
        productOfferings.get(2).getProductSpecification().get(0).setProductType("landline");


        List<KeyValueType> additionalDataCommercialOperation = new ArrayList<>();
        KeyValueType additionalDataCapl = new KeyValueType();
        additionalDataCapl.setKey("CAPL");
        additionalDataCapl.setValue("true");

        MediumCharacteristic mediumCharacteristic = MediumCharacteristic.builder()
                .phoneNumber("976598623").emailAddress("ezample@everis.com").build();
        WorkOrDeliveryType workOrDeliveryType = WorkOrDeliveryType.builder()
                .contact(mediumCharacteristic).mediumDelivery("DELIVERY").place(places).additionalData(additionalDatas).build();

        ServiceType serviceType1 = new ServiceType();
        serviceType1.setType("VOICE");
        serviceType1.setAllocationId("test");
        List<ServiceType> servicesList1 = new ArrayList<>();
        servicesList1.add(serviceType1);
        AvailableOffersType availableOffersType1 = new AvailableOffersType();
        availableOffersType1.setServices(servicesList1);
        availableOffersType1.setPriority(1);

        ServiceType serviceType2 = new ServiceType();
        serviceType2.setType("BB");
        serviceType2.setAllocationId("test");
        List<ServiceType> servicesList2 = new ArrayList<>();
        servicesList2.add(serviceType2);
        AvailableOffersType availableOffersType2 = new AvailableOffersType();
        availableOffersType2.setServices(servicesList2);
        availableOffersType2.setPriority(1);

        ServiceType serviceType3 = new ServiceType();
        serviceType3.setType("TV");
        serviceType3.setAllocationId("test");
        List<ServiceType> servicesList3 = new ArrayList<>();
        servicesList3.add(serviceType3);
        AvailableOffersType availableOffersType3 = new AvailableOffersType();
        availableOffersType3.setServices(servicesList3);
        availableOffersType3.setPriority(1);

        List<AvailableOffersType> offersServicesList = new ArrayList<>();
        offersServicesList.add(availableOffersType1);
        offersServicesList.add(availableOffersType2);
        offersServicesList.add(availableOffersType3);
        ServiceAvailabilityReportType serviceAvailability = new ServiceAvailabilityReportType();
        serviceAvailability.setOffers(offersServicesList);
        serviceAvailability.setAdditionalData(additionalDatas);

        serviceAvailability.getAdditionalData().add(KeyValueType.builder()
                .key("networkAccessTechnologyLandline").value("FTTH").build());
        serviceAvailability.getAdditionalData().add(KeyValueType.builder()
                .key("serviceTechnologyLandline").value("HFC").build());
        serviceAvailability.getAdditionalData().add(KeyValueType.builder()
                .key("maxSpeed").value("1000Mbps").build());
        serviceAvailability.getAdditionalData().add(KeyValueType.builder()
                .key("networkAccessTechnologyBroadband").value("FTTH").build());
        serviceAvailability.getAdditionalData().add(KeyValueType.builder()
                .key("serviceTechnologyBroadband").value("HFC").build());

        PortabilityType portability =  new PortabilityType();
        portability.setReceipt("test");
        portability.setProductType("test");
        portability.setPlanType("test");
        portability.setDonorActivationDate("2021-02-19-05:00");
        portability.setDonorEquipmentContractEndDate("test");
        portability.setIdProcess("test");
        portability.setIdProcessGroup("test");
        portability.setCustomerContactPhone("test");

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
        comercialOperationType.setServiceAvailability(serviceAvailability);
        comercialOperationType.setPortability(portability);
        comercialOperationTypes.add(comercialOperationType);

        Money estimatedRevenue = new Money();

        estimatedRevenue.setUnit("s");
        estimatedRevenue.setValue(12f);

        ContactMedium prospectContact = new ContactMedium();
        MediumCharacteristic mediumChar = MediumCharacteristic.builder().emailAddress("everis@everis.com").build();
        contactMedium = ContactMedium
                .builder()
                .mediumType("email")
                .characteristic(mediumChar)
                .build();

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
        ScoreType scoreType = ScoreType
                .builder()
                .score("f")
                .build();
        relatedParty.setScore(scoreType);

        List<RelatedParty> relatedParties = new ArrayList<>();

        TimePeriod validFor = new TimePeriod();

        validFor.setStartDateTime("");
        validFor.setEndDateTime("");

        relatedParties.add(relatedParty);

        List<KeyValueType> paymentAdditionalData = new ArrayList<>();
        paymentAdditionalData.add(KeyValueType.builder().key("paymentDocument").value("Boleta").build());
        PaymentType paymentType = PaymentType
                .builder()
                .paymentType("EX")
                .cid("string")
                .additionalData(paymentAdditionalData)
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
                .productType("WIRELESS")
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

        return sale;
    }

    public static Sale createSaleMock2() {
        Sale sale;
        List<KeyValueType> additionalDatas = new ArrayList<>();
        ContactMedium contactMedium = new ContactMedium();
        List<IdentityValidationType> identityValidationTypeList = new ArrayList<>();

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

        KeyValueType additionalData4= new KeyValueType();
        additionalData4.setKey("paymentTypeLabel");
        additionalData4.setValue("PAGO EFECTIVO");
        additionalDatas.add(additionalData4);

        additionalDatas.add(KeyValueType.builder().key("SIM_ICCID").value("123123123").build());
        additionalDatas.add(KeyValueType.builder().key("MOVILE_IMEI").value("123123123").build());

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
        DeviceOffering deviceOffering2 = new DeviceOffering();

        deviceOffering.setAdditionalData(additionalDatas);
        deviceOffering.setId("s");
        deviceOffering.setDeviceType("Smartphone");
        deviceOffering.setSapid("SAD123PID");
        deviceOffering.setOffers(Arrays.asList(Offer.builder().billingOfferings(Arrays.asList(BillingOffering.builder()
                .commitmentPeriods(Arrays.asList(CommitmentPeriod.builder()
                        .financingInstalments(Arrays.asList(FinancingInstalment.builder()
                                .instalments(Instalments.builder()
                                        .openingQuota(MoneyAmount.builder().value(1).build())
                                        .totalAmount(MoneyAmount.builder().value(1).build())
                                        .build())
                                .build()))
                        .build()))
                .build())).build()));

        deviceOffering2.setAdditionalData(additionalDatas);
        deviceOffering2.setId("s");
        deviceOffering2.setDeviceType("SIM");
        deviceOffering2.setSapid("SAD123PID");
        deviceOffering2.setOffers(Arrays.asList(Offer.builder().billingOfferings(Arrays.asList(BillingOffering.builder()
                .commitmentPeriods(Arrays.asList(CommitmentPeriod.builder()
                        .financingInstalments(Arrays.asList(FinancingInstalment.builder()
                                .instalments(Instalments.builder()
                                        .openingQuota(MoneyAmount.builder().value(1).build())
                                        .totalAmount(MoneyAmount.builder().value(1).build())
                                        .build())
                                .build()))
                        .build()))
                .build())).build()));

        StockType stockType = StockType
                .builder()
                .reservationId("")
                .build();
        deviceOffering.setStock(stockType);
        deviceOffering2.setStock(stockType);


        MoneyAmount moneyAmount1 = MoneyAmount
                .builder()
                .value(150.6)
                .currency("SOL")
                .build();

        Instalments instalments = new Instalments();
        instalments.setAmount(moneyAmount1);
        instalments.setOpeningQuota(moneyAmount1);
        instalments.setTotalAmount(moneyAmount1);

        FinancingInstalment financingInstalment1 = new FinancingInstalment();
        financingInstalment1.setInstalments(instalments);
        financingInstalment1.setDescription("TELEFCONT");
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
        //deviceOffering.setOffers(offersList);

        List<MoneyAmount> pricesList = new ArrayList<>();
        pricesList.add(moneyAmount1);

        SimSpecification simSpecification1 = SimSpecification
                .builder()
                .price(pricesList)
                .build();

        List<SimSpecification> simSpecificationsList = new ArrayList<>();
        simSpecificationsList.add(simSpecification1);
        deviceOffering.setSimSpecifications(simSpecificationsList);
        deviceOffering2.setSimSpecifications(simSpecificationsList);

        deviceOfferings.add(deviceOffering);
        deviceOfferings.add(deviceOffering2);
        deviceOfferings.get(0).setDeviceType("Smartphone");
        ProductInstanceType product= new ProductInstanceType();
        product.setId("s");
        product.setProductSpec(entityRefType);
        product.setPublicId("234562433");
        product.setProductOffering(entityRefType);

        CreateProductOrderResponseType order = CreateProductOrderResponseType
                .builder()
                .productOrderId("930686A")
                .productOrderReferenceNumber("761787835447")
                .build();

        OfferingType offeringType1= new OfferingType();
        offeringType1.setId("s");
        offeringType1.setProductOfferingProductSpecId("s");
        offeringType1.setProductOfferingPrice(Arrays.asList(ComponentProdOfferPriceType.builder()
                .pricePlanSpecContainmentId("string").productSpecContainmentId("string")
                .maxPrice(MoneyType.builder().amount(1).build()).build()));

        ComposingProductType productSpecification = ComposingProductType.builder()
                .name("TV")
                .productType("landline")
                .refinedProduct(RefinedProductType.builder()
                        .productCharacteristics(Arrays.asList(ProductSpecCharacteristicType.builder()
                                .id("string")
                                .name("productOfferingProductSpecID")
                                .build()))
                        .build())
                .build();
        ComposingProductType productSpecificationSva = productSpecification;
        productSpecificationSva.setProductType("sva");

        MoneyType maxPrice = MoneyType
                .builder()
                .amount(150.0)
                .build();
        ComponentProdOfferPriceType componentProdOfferPriceType1 = ComponentProdOfferPriceType
                .builder()
                .maxPrice(maxPrice)
                .build();
        List<ComponentProdOfferPriceType> productOfferingPricesList = new ArrayList<>();
        productOfferingPricesList.add(componentProdOfferPriceType1);
        offeringType1.setProductOfferingPrice(productOfferingPricesList);
        List<OfferingType> productOfferings = new ArrayList<>();
        productOfferings.add(offeringType1);


        List<KeyValueType> additionalDataCommercialOperation = Arrays.asList(KeyValueType.builder().key("CAPL").value("true").build(),
                KeyValueType.builder().key("CAEQ").value("true").build(), KeyValueType.builder().key("CASI").value("true").build(),
                KeyValueType.builder().key("ALTA").value("true").build());

        KeyValueType additionalDataCapl = new KeyValueType();
        additionalDataCapl.setKey("CAPL");
        additionalDataCapl.setValue("true");

        MediumCharacteristic mediumCharacteristic = MediumCharacteristic.builder()
                .phoneNumber("976598623").emailAddress("ezample@everis.com").build();
        WorkOrDeliveryType workOrDeliveryType = WorkOrDeliveryType.builder()
                .scheduleDelivery("SLA")
                .mediumDelivery("DELIVERY")
                .workOrder(WorkforceTeamTimeSlotType.builder()
                        .workforceTeams(Arrays.asList(WorkforceTeamAvailabilityType.builder()
                                .id("string").build()))
                        .build())
                .contact(mediumCharacteristic).place(places).additionalData(additionalDatas).build();

        ServiceType serviceType1 = new ServiceType();
        serviceType1.setType("VOICE");
        serviceType1.setAllocationId("test");
        List<ServiceType> servicesList1 = new ArrayList<>();
        servicesList1.add(serviceType1);
        AvailableOffersType availableOffersType1 = new AvailableOffersType();
        availableOffersType1.setServices(servicesList1);
        availableOffersType1.setPriority(1);

        ServiceType serviceType2 = new ServiceType();
        serviceType2.setType("BB");
        serviceType2.setAllocationId("test");
        List<ServiceType> servicesList2 = new ArrayList<>();
        servicesList2.add(serviceType2);
        AvailableOffersType availableOffersType2 = new AvailableOffersType();
        availableOffersType2.setServices(servicesList2);
        availableOffersType2.setPriority(1);

        ServiceType serviceType3 = new ServiceType();
        serviceType3.setType("TV");
        serviceType3.setAllocationId("test");
        List<ServiceType> servicesList3 = new ArrayList<>();
        servicesList3.add(serviceType3);
        AvailableOffersType availableOffersType3 = new AvailableOffersType();
        availableOffersType3.setServices(servicesList3);
        availableOffersType3.setPriority(1);

        List<AvailableOffersType> offersServicesList = new ArrayList<>();
        offersServicesList.add(availableOffersType1);
        offersServicesList.add(availableOffersType2);
        offersServicesList.add(availableOffersType3);
        ServiceAvailabilityReportType serviceAvailability = new ServiceAvailabilityReportType();
        serviceAvailability.setOffers(offersServicesList);
        serviceAvailability.setAdditionalData(additionalDatas);

        PortabilityType portability =  new PortabilityType();
        portability.setReceipt("test");
        portability.setProductType("test");
        portability.setPlanType("test");
        portability.setDonorActivationDate("test");
        portability.setDonorEquipmentContractEndDate("test");
        portability.setIdProcess("test");
        portability.setIdProcessGroup("test");
        portability.setCustomerContactPhone("test");

        product.setAdditionalData(additionalDatas);
        List<CommercialOperationType> comercialOperationTypes = new ArrayList<>();
        CommercialOperationType comercialOperationType = new CommercialOperationType();
        comercialOperationType.setId("1");
        comercialOperationType.setName("h");
        comercialOperationType.setReason("ALTA");
        comercialOperationType.setProduct(product);
        comercialOperationType.setDeviceOffering(deviceOfferings);
        comercialOperationType.setAction("PROVIDE");
        comercialOperationType.setAdditionalData(additionalDataCommercialOperation);
        comercialOperationType.setOrder(order);
        comercialOperationType.setProductOfferings(productOfferings);
        comercialOperationType.setWorkOrDeliveryType(workOrDeliveryType);
        comercialOperationType.setServiceAvailability(serviceAvailability);
        comercialOperationType.setPortability(portability);
        comercialOperationType.setProductOfferings(Arrays.asList(OfferingType.builder()
                .upFront(UpFrontType.builder().indicator("string").build())
                .productOfferingPrice(Arrays.asList(ComponentProdOfferPriceType.builder()
                        .maxPrice(MoneyType.builder().amount(1).build()).build()))
                .productSpecification(Arrays.asList(productSpecification, productSpecificationSva))
                .additionalData(Arrays.asList(KeyValueType.builder().key("productType").value("channelTV").build(),
                        KeyValueType.builder().key("parentProductCatalogID").value("string").build()))
                .build()));
        comercialOperationType.setProduct(ProductInstanceType.builder()
                .productRelationShip(Arrays.asList(RelatedProductType.builder()
                        .product(ProductRefInfoType.builder()
                                .productType("broadband").id("string").name("Internet_Plan").build())
                        .build()))
                .publicId("string")
                .build());
        comercialOperationTypes.add(comercialOperationType);

        Money estimatedRevenue = new Money();

        estimatedRevenue.setUnit("s");
        estimatedRevenue.setValue(12f);

        ContactMedium prospectContact = new ContactMedium();
        MediumCharacteristic mediumChar = MediumCharacteristic.builder().emailAddress("everis@everis.com").build();
        contactMedium = ContactMedium
                .builder()
                .mediumType("email")
                .characteristic(mediumChar)
                .build();

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
        ScoreType scoreType = ScoreType
                .builder()
                .score("f")
                .build();
        relatedParty.setScore(scoreType);

        List<RelatedParty> relatedParties = new ArrayList<>();

        TimePeriod validFor = new TimePeriod();

        validFor.setStartDateTime("");
        validFor.setEndDateTime("");

        relatedParties.add(relatedParty);

        PaymentType paymentType = PaymentType
                .builder()
                .paymentType("EX")
                .cid("string")
                .build();

        IdentityValidationType identityValidationType = IdentityValidationType.builder()
                .date("2014-09-15T23:14:25.7251173Z").validationType("No Biometric").build();

        identityValidationTypeList.add(identityValidationType);

        sale = Sale
                .builder()
                .productType("WIRELINE")
                .id("1")
                .salesId("FE-000000001")
                .name("Sergio")
                .description("venta de lote")
                .priority("x")
                .channel(channel)
                .agent(agent)
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

        return sale;
    }

    public static HashMap<String,String> createHeadersMock() {
        HashMap<String,String> headersMap = new HashMap();
        headersMap.put(HttpHeadersKey.UNICA_SERVICE_ID, ConstantsTest.RH_UNICA_SERVICE_ID);
        headersMap.put(HttpHeadersKey.UNICA_APPLICATION, ConstantsTest.RH_UNICA_APPLICATION);
        headersMap.put(HttpHeadersKey.UNICA_PID, ConstantsTest.RH_UNICA_PID);
        headersMap.put(HttpHeadersKey.UNICA_USER, ConstantsTest.RH_UNICA_USER);
        return headersMap;
    }

}
