package com.tdp.ms.sales.service;

import com.tdp.genesis.core.constants.HttpHeadersKey;
import com.tdp.ms.commons.util.MapperUtils;
import com.tdp.ms.sales.business.impl.SalesManagmentServiceImpl;
import com.tdp.ms.sales.client.GetSkuWebClient;
import com.tdp.ms.sales.client.ProductOrderWebClient;
import com.tdp.ms.sales.model.dto.*;
import com.tdp.ms.sales.model.dto.businessparameter.BusinessParameterFinanciamientoFijaExt;
import com.tdp.ms.sales.model.dto.productorder.CreateProductOrderGeneralRequest;
import com.tdp.ms.sales.model.dto.productorder.Customer;
import com.tdp.ms.sales.model.dto.productorder.FlexAttrType;
import com.tdp.ms.sales.model.dto.productorder.altafija.AltaFijaRequest;
import com.tdp.ms.sales.model.dto.productorder.altafija.NewProductAltaFija;
import com.tdp.ms.sales.model.dto.productorder.altafija.ProductOrderAltaFijaRequest;
import com.tdp.ms.sales.model.dto.productorder.altafija.ServiceabilityOfferType;
import com.tdp.ms.sales.model.dto.productorder.altamobile.AltaMobileRequest;
import com.tdp.ms.sales.model.dto.productorder.altamobile.ProductOrderAltaMobileRequest;
import com.tdp.ms.sales.model.dto.productorder.caeq.CaeqRequest;
import com.tdp.ms.sales.model.dto.productorder.caeq.ChangedContainedProduct;
import com.tdp.ms.sales.model.dto.productorder.caeq.ProductOrderCaeqRequest;
import com.tdp.ms.sales.model.dto.productorder.caeqcapl.CaeqCaplRequest;
import com.tdp.ms.sales.model.dto.productorder.caeqcapl.ProductOrderCaeqCaplRequest;
import com.tdp.ms.sales.model.dto.productorder.capl.CaplRequest;
import com.tdp.ms.sales.model.dto.productorder.capl.NewAssignedBillingOffers;
import com.tdp.ms.sales.model.dto.productorder.capl.ProductOrderCaplRequest;
import com.tdp.ms.sales.model.dto.quotation.CreateQuotationRequestBody;
import com.tdp.ms.sales.model.dto.quotation.Item;
import com.tdp.ms.sales.model.dto.reservestock.StockItem;
import com.tdp.ms.sales.model.entity.Sale;
import com.tdp.ms.sales.model.request.CreateQuotationRequest;
import com.tdp.ms.sales.model.request.PostSalesRequest;
import com.tdp.ms.sales.model.response.*;
import com.tdp.ms.sales.utils.CommonsMocks;
import com.tdp.ms.sales.utils.Constants;
import com.tdp.ms.sales.utils.ConstantsTest;
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
import java.util.HashMap;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class SalesManagmentServicePrivateMethodsTest {

    @Autowired
    private SalesManagmentServiceImpl salesManagmentServiceImpl;

    private static Sale sale;
    private static PostSalesRequest salesRequest;
    private static final HashMap<String, String> headersMap = new HashMap();

    @MockBean
    private GetSkuWebClient getSkuWebClient;
    @MockBean
    private ProductOrderWebClient productOrderWebClient;

    @BeforeAll
    static void setup() {
        // Setting request headers
        headersMap.put(HttpHeadersKey.UNICA_SERVICE_ID, ConstantsTest.RH_UNICA_SERVICE_ID);
        headersMap.put(HttpHeadersKey.UNICA_APPLICATION, ConstantsTest.RH_UNICA_APPLICATION);
        headersMap.put(HttpHeadersKey.UNICA_PID, ConstantsTest.RH_UNICA_PID);
        headersMap.put(HttpHeadersKey.UNICA_USER, ConstantsTest.RH_UNICA_USER);

        sale = CommonsMocks.createSaleMock();

        salesRequest = PostSalesRequest
                .builder()
                .sale(sale)
                .headersMap(headersMap)
                .build();
    }

    @Test
    void setReserveReponseInSalesTest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = SalesManagmentServiceImpl.class.getDeclaredMethod("setReserveReponseInSales",
                ReserveStockResponse.class, Sale.class);

        method.setAccessible(true);

        QuantityType amount = QuantityType
                .builder()
                .amount(120)
                .build();

        SiteRefType site = SiteRefType
                .builder()
                .id("D0001")
                .build();

        StockItem stockItem1 = StockItem
                .builder()
                .amount(amount)
                .site(site)
                .build();

        List<StockItem> itemList = new ArrayList<>();
        itemList.add(stockItem1);

        ReserveStockResponse reserveStockResponse = ReserveStockResponse
                .builder()
                .id("R0001")
                .items(itemList)
                .build();

        Sale sale = CommonsMocks.createSaleMock();

        method.invoke(salesManagmentServiceImpl, reserveStockResponse, sale);

        Assert.assertEquals(sale.getCommercialOperation().get(0).getDeviceOffering().get(0).getStock().getAmount(), amount);
        Assert.assertEquals(sale.getCommercialOperation().get(0).getDeviceOffering().get(0).getStock().getSite(), site);
    }

    @Test
    void buildCreateQuotationRequestTest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = SalesManagmentServiceImpl.class.getDeclaredMethod("buildCreateQuotationRequest",
                CreateQuotationRequest.class, PostSalesRequest.class, Boolean.class);

        method.setAccessible(true);

        CreateQuotationRequest createQuotationRequest = new CreateQuotationRequest();
        Sale sale = CommonsMocks.createSaleMock();
        PostSalesRequest salesRequest = PostSalesRequest
                .builder()
                .sale(sale)
                .headersMap(headersMap)
                .build();

        method.invoke(salesManagmentServiceImpl, createQuotationRequest, salesRequest, true);

        Assert.assertEquals(createQuotationRequest.getBody().getAccountId(),
                sale.getRelatedParty().get(0).getAccountId());
        Assert.assertEquals(createQuotationRequest.getBody().getOperationType(),
                sale.getCommercialOperation().get(0).getReason());
    }

    @Test
    void setQuotationResponseInSalesTest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = SalesManagmentServiceImpl.class.getDeclaredMethod("setQuotationResponseInSales",
                CreateQuotationResponse.class, Sale.class);

        method.setAccessible(true);

        Sale sale = CommonsMocks.createSaleMock();

        CreateQuotationResponse quotationResponse = CreateQuotationResponse
                .builder()
                .numberOfInstalments(1)
                .recurringChargePeriod("monthly")
                .amountPerInstalment(130)
                .build();

        method.invoke(salesManagmentServiceImpl, quotationResponse, sale);

        Assert.assertEquals(sale.getAdditionalData().get(sale.getAdditionalData().size() - 1).getValue(),
                quotationResponse.getAmountPerInstalment().toString());
    }

    @Test
    void getStringValueByKeyFromAdditionalDataListTest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = SalesManagmentServiceImpl.class.getDeclaredMethod("getStringValueByKeyFromAdditionalDataList",
                List.class, String.class);

        method.setAccessible(true);

        Sale sale = CommonsMocks.createSaleMock();

        String deliveryMethod = (String) method.invoke(salesManagmentServiceImpl, sale.getAdditionalData(), "deliveryMethod");

        Assert.assertEquals(deliveryMethod, "IS");
    }

    @Test
    void getStringValueFromBusinessParameterDataListByKeyAndActiveTrueTest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = SalesManagmentServiceImpl.class.getDeclaredMethod("getStringValueFromBusinessParameterDataListByKeyAndActiveTrue",
                List.class, String.class);

        method.setAccessible(true);

        List<BusinessParameterDataObjectExt> businessParameterDataList = new ArrayList<>();
        BusinessParameterDataObjectExt businessParameterData1 = BusinessParameterDataObjectExt
                .builder()
                .key("sapid")
                .value("TMEST873691J52")
                .active(true)
                .build();
        businessParameterDataList.add(businessParameterData1);

        String stringValue = (String) method.invoke(salesManagmentServiceImpl, businessParameterDataList, "sapid");

        Assert.assertEquals(stringValue, "TMEST873691J52");
    }

    @Test
    void altaCommercialOperationTest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = SalesManagmentServiceImpl.class.getDeclaredMethod("altaCommercialOperation", Sale.class,
                CreateProductOrderGeneralRequest.class, String.class, String.class, String.class, String.class,
                BusinessParametersResponseObjectExt.class, String.class, Boolean.class, Boolean.class);

        method.setAccessible(true);

        CreateProductOrderGeneralRequest altaRequest = new CreateProductOrderGeneralRequest();
        List<BusinessParameterDataObjectExt> dataList = new ArrayList<>();
        BusinessParameterDataObjectExt data1 = BusinessParameterDataObjectExt
                .builder()
                .value("34572615")
                .ext("7431")
                .build();
        dataList.add(data1);
        BusinessParametersResponseObjectExt businessParametersResponseObjectExt = BusinessParametersResponseObjectExt
                .builder()
                .data(dataList)
                .build();
        Sale sale = CommonsMocks.createSaleMock();
        sale.getChannel().setId("CC");
        sale.getCommercialOperation().get(0).setReason("ALTA");

        method.invoke(salesManagmentServiceImpl,sale, altaRequest, "CC", "C0001", "OF0001", "CIP0001",
                businessParametersResponseObjectExt, "SAPID0001", false, false);
    }

    @Test
    void altaCommercialOperation_whenChannelIdIsRetail_Test() throws NoSuchMethodException, InvocationTargetException,
            IllegalAccessException {
        Method method = SalesManagmentServiceImpl.class.getDeclaredMethod("altaCommercialOperation", Sale.class,
                CreateProductOrderGeneralRequest.class, String.class, String.class, String.class, String.class,
                BusinessParametersResponseObjectExt.class, String.class, Boolean.class, Boolean.class);

        method.setAccessible(true);

        CreateProductOrderGeneralRequest altaRequest = new CreateProductOrderGeneralRequest();
        List<BusinessParameterDataObjectExt> dataList = new ArrayList<>();
        BusinessParameterDataObjectExt data1 = BusinessParameterDataObjectExt
                .builder()
                .value("34572615")
                .ext("7431")
                .build();
        dataList.add(data1);
        BusinessParametersResponseObjectExt businessParametersResponseObjectExt = BusinessParametersResponseObjectExt
                .builder()
                .data(dataList)
                .build();
        Sale sale = CommonsMocks.createSaleMock();
        sale.getChannel().setId("DLC");
        sale.getCommercialOperation().get(0).setReason("ALTA");

        method.invoke(salesManagmentServiceImpl,sale, altaRequest, "CC", "C0001", "OF0001", "CIP0001",
                businessParametersResponseObjectExt, "SAPID0001", false, false);
    }

    @Test
    void creationOrderValidation_status_Nuevo_test() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = SalesManagmentServiceImpl.class.getDeclaredMethod("creationOrderValidation", Sale.class,
                CreateProductOrderGeneralRequest.class, HashMap.class);
        method.setAccessible(true);

        List<GetSkuResponse> getSkuResponseList = Arrays.asList(GetSkuResponse.builder().deviceType("mobile_phone").sku("31024026").build(),
                GetSkuResponse.builder().deviceType("sim").sku("32004482").build());

        Mockito.when(getSkuWebClient.createSku("", "", "", 1.00, "Provide", "", "", "", "", "", "", headersMap))
                .thenReturn(Mono.just(getSkuResponseList).flatMapMany(Flux::fromIterable));

        Mockito.when(productOrderWebClient.createProductOrder(any(), any(), any()))
                .thenReturn(Mono.just(ProductorderResponse.builder().build()));

        CreateProductOrderGeneralRequest createProductOrderGeneralRequest = CreateProductOrderGeneralRequest
                .builder().createProductOrderRequest(ProductOrderCaplRequest.builder().build()).build();

        Sale saleRequest = Sale.builder()
                .commercialOperation(Arrays.asList(CommercialOperationType.builder().reason("ALTA").build()))
                .additionalData(Arrays.asList(KeyValueType.builder().key("flowSale").value("Retail").build()))
                .status("NUEVO")
                .build();

        method.invoke(salesManagmentServiceImpl,saleRequest, createProductOrderGeneralRequest, headersMap);
    }

    @Test
    void creationOrderValidation_status_capl_Negociacion_test() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = SalesManagmentServiceImpl.class.getDeclaredMethod("creationOrderValidation", Sale.class,
                CreateProductOrderGeneralRequest.class, HashMap.class);
        method.setAccessible(true);

        Mockito.when(getSkuWebClient.createSku("string", "default","",0.00,"Provide","","string","2","string","123456","1000.00",headersMap))
                .thenReturn(Flux.just(GetSkuResponse.builder().deviceType("mobile_phone").sku("31024026").build(),
                        GetSkuResponse.builder().deviceType("sim").sku("32004482").build()));

        Mockito.when(productOrderWebClient.createProductOrder(any(), any(), any()))
                .thenReturn(Mono.just(ProductorderResponse.builder().build()));

        CreateProductOrderGeneralRequest createProductOrderGeneralRequest = CreateProductOrderGeneralRequest
                .builder().createProductOrderRequest(ProductOrderCaplRequest.builder()
                        .actionType("string").customer(Customer.builder().customerId("string").build()).customerId("string")
                        .onlyValidationIndicator("false").productOfferingId("string").request(CaplRequest.builder().build())
                        .salesChannel("string")
                        .build()).build();

        DeviceOffering deviceOfferingSmartphone = new DeviceOffering();
        deviceOfferingSmartphone.setId("123456");
        deviceOfferingSmartphone.setCostoPromedioSinIgvSoles("1000.00");
        deviceOfferingSmartphone.setDeviceType(Constants.DEVICE_TYPE_SMARTPHONE);
        deviceOfferingSmartphone.setSimSpecifications(Arrays.asList(SimSpecification.builder()
                .sapid("string").price(Arrays.asList(MoneyAmount.builder()
                        .value(1.00).build())).build()));

        List<DeviceOffering> deviceOfferingList = new ArrayList<>();
        deviceOfferingList.add(deviceOfferingSmartphone);

        Sale saleRequest = Sale.builder()
                .additionalData(Arrays.asList(KeyValueType.builder().key("flowSale").value("Retail").build()))
                .status("NEGOCIACION")
                .channel(ChannelRef.builder().storeId("string").dealerId("string").href("string").id("string")
                        .name("string").storeName("string").build())
                .commercialOperation(Arrays.asList(CommercialOperationType.builder().reason("ALTA")
                        .deviceOffering(deviceOfferingList).build()))
                .build();

        method.invoke(salesManagmentServiceImpl, saleRequest, createProductOrderGeneralRequest, headersMap);
    }

    @Test
    void creationOrderValidation_status_caeq_Negociacion_test() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = SalesManagmentServiceImpl.class.getDeclaredMethod("creationOrderValidation", Sale.class,
                CreateProductOrderGeneralRequest.class, HashMap.class);
        method.setAccessible(true);

        Mockito.when(getSkuWebClient.createSku("string", "default","",0.00,"Provide","","string","2","string","123456","1000.00",headersMap))
                .thenReturn(Flux.just(GetSkuResponse.builder().deviceType("mobile_phone").sku("31024026").build(),
                        GetSkuResponse.builder().deviceType("sim").sku("32004482").build()));

        Mockito.when(productOrderWebClient.createProductOrder(any(), any(), any()))
                .thenReturn(Mono.just(ProductorderResponse.builder().build()));

        CreateProductOrderGeneralRequest createProductOrderGeneralRequest = CreateProductOrderGeneralRequest
                .builder().createProductOrderRequest(ProductOrderCaeqRequest.builder()
                        .actionType("string").customer(Customer.builder().customerId("string").build()).onlyValidationIndicator("false")
                        .productOfferingId("string").salesChannel("string")
                        .request(CaeqRequest.builder().build()).build()).build();

        DeviceOffering deviceOfferingSmartphone = new DeviceOffering();
        deviceOfferingSmartphone.setId("123456");
        deviceOfferingSmartphone.setCostoPromedioSinIgvSoles("1000.00");
        deviceOfferingSmartphone.setDeviceType(Constants.DEVICE_TYPE_SMARTPHONE);
        deviceOfferingSmartphone.setSimSpecifications(Arrays.asList(SimSpecification.builder()
                .sapid("string").price(Arrays.asList(MoneyAmount.builder()
                        .value(1.00).build())).build()));

        List<DeviceOffering> deviceOfferingList = new ArrayList<>();
        deviceOfferingList.add(deviceOfferingSmartphone);

        Sale saleRequest = Sale.builder()
                .additionalData(Arrays.asList(KeyValueType.builder().key("flowSale").value("Retail").build()))
                .status("NEGOCIACION")
                .channel(ChannelRef.builder().storeId("string").dealerId("string").href("string").id("string")
                        .name("string").storeName("string").build())
                .commercialOperation(Arrays.asList(CommercialOperationType.builder().reason("ALTA")
                        .deviceOffering(deviceOfferingList).build()))
                .build();

        method.invoke(salesManagmentServiceImpl, saleRequest, createProductOrderGeneralRequest, headersMap);
    }

    @Test
    void creationOrderValidation_status_caeqcapl_Negociacion_test() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = SalesManagmentServiceImpl.class.getDeclaredMethod("creationOrderValidation", Sale.class,
                CreateProductOrderGeneralRequest.class, HashMap.class);
        method.setAccessible(true);

        Mockito.when(getSkuWebClient.createSku("string", "default","",0.00,"Provide","","string","2","string","123456","1000.00",headersMap))
                .thenReturn(Flux.just(GetSkuResponse.builder().deviceType("mobile_phone").sku("31024026").build(),
                        GetSkuResponse.builder().deviceType("sim").sku("32004482").build()));

        Mockito.when(productOrderWebClient.createProductOrder(any(), any(), any()))
                .thenReturn(Mono.just(ProductorderResponse.builder().build()));

        CreateProductOrderGeneralRequest createProductOrderGeneralRequest = CreateProductOrderGeneralRequest
                .builder().createProductOrderRequest(ProductOrderCaeqCaplRequest.builder()
                        .actionType("string").customer(Customer.builder().customerId("string").build()).onlyValidationIndicator("false")
                        .productOfferingId("string").salesChannel("string").request(CaeqCaplRequest.builder().build()).build()).build();

        DeviceOffering deviceOfferingSmartphone = new DeviceOffering();
        deviceOfferingSmartphone.setId("123456");
        deviceOfferingSmartphone.setCostoPromedioSinIgvSoles("1000.00");
        deviceOfferingSmartphone.setDeviceType(Constants.DEVICE_TYPE_SMARTPHONE);
        deviceOfferingSmartphone.setSimSpecifications(Arrays.asList(SimSpecification.builder()
                .sapid("string").price(Arrays.asList(MoneyAmount.builder()
                        .value(1.00).build())).build()));

        List<DeviceOffering> deviceOfferingList = new ArrayList<>();
        deviceOfferingList.add(deviceOfferingSmartphone);

        Sale saleRequest = Sale.builder()
                .additionalData(Arrays.asList(KeyValueType.builder().key("flowSale").value("Retail").build()))
                .status("NEGOCIACION")
                .channel(ChannelRef.builder().storeId("string").dealerId("string").href("string").id("string")
                        .name("string").storeName("string").build())
                .commercialOperation(Arrays.asList(CommercialOperationType.builder().reason("ALTA")
                        .deviceOffering(deviceOfferingList).build()))
                .build();

        method.invoke(salesManagmentServiceImpl, saleRequest, createProductOrderGeneralRequest, headersMap);
    }

    @Test
    void creationOrderValidation_status_AltaFija_Negociacion_test() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = SalesManagmentServiceImpl.class.getDeclaredMethod("creationOrderValidation", Sale.class,
                CreateProductOrderGeneralRequest.class, HashMap.class);
        method.setAccessible(true);

        Mockito.when(getSkuWebClient.createSku("string", "default","",0.00,"Provide","","string","2","string","123456","1000.00",headersMap))
                .thenReturn(Flux.just(GetSkuResponse.builder().deviceType("mobile_phone").sku("31024026").build(),
                        GetSkuResponse.builder().deviceType("sim").sku("32004482").build()));

        Mockito.when(productOrderWebClient.createProductOrder(any(), any(), any()))
                .thenReturn(Mono.just(ProductorderResponse.builder().build()));

        CreateProductOrderGeneralRequest createProductOrderGeneralRequest = CreateProductOrderGeneralRequest
                .builder().createProductOrderRequest(ProductOrderAltaFijaRequest.builder()
                        .actionType("string").customer(new Customer("123456")).onlyValidationIndicator("false")
                        .productOfferingId("string").salesChannel("string").request(new AltaFijaRequest()).build()).build();

        DeviceOffering deviceOfferingSmartphone = new DeviceOffering();
        deviceOfferingSmartphone.setId("123456");
        deviceOfferingSmartphone.setCostoPromedioSinIgvSoles("1000.00");
        deviceOfferingSmartphone.setDeviceType(Constants.DEVICE_TYPE_SMARTPHONE);
        deviceOfferingSmartphone.setSimSpecifications(Arrays.asList(SimSpecification.builder()
                .sapid("string").price(Arrays.asList(MoneyAmount.builder()
                        .value(1.00).build())).build()));

        List<DeviceOffering> deviceOfferingList = new ArrayList<>();
        deviceOfferingList.add(deviceOfferingSmartphone);

        Sale saleRequest = Sale.builder()
                .additionalData(Arrays.asList(KeyValueType.builder().key("flowSale").value("Retail").build()))
                .status("NEGOCIACION")
                .channel(ChannelRef.builder().storeId("string").dealerId("string").href("string").id("string")
                        .name("string").storeName("string").build())
                .commercialOperation(Arrays.asList(CommercialOperationType.builder().reason("ALTA")
                        .deviceOffering(deviceOfferingList).build()))
                .build();

        method.invoke(salesManagmentServiceImpl, saleRequest, createProductOrderGeneralRequest, headersMap);
    }

    @Test
    void creationOrderValidation_status_AltaMobile_Negociacion_test() throws NoSuchMethodException,
                                                                InvocationTargetException, IllegalAccessException {
        Method method = SalesManagmentServiceImpl.class.getDeclaredMethod("creationOrderValidation", Sale.class,
                CreateProductOrderGeneralRequest.class, HashMap.class);
        method.setAccessible(true);

        Mockito.when(getSkuWebClient.createSku("string", "default","123456",1.00,"Change","","string","2","string","123456","1000.00",headersMap))
                .thenReturn(Flux.just(GetSkuResponse.builder().deviceType("mobile_phone").sku("31024026").build(),
                        GetSkuResponse.builder().deviceType("sim").sku("32004482").build()));

        Mockito.when(productOrderWebClient.createProductOrder(any(), any(), any()))
                .thenReturn(Mono.just(ProductorderResponse.builder().build()));

        Customer customer = Customer.builder().customerId("C0001").build();
        CreateProductOrderGeneralRequest createProductOrderGeneralRequest = CreateProductOrderGeneralRequest
                .builder().createProductOrderRequest(ProductOrderAltaMobileRequest.builder()
                        .actionType("string").customer(customer).onlyValidationIndicator("false")
                        .productOfferingId("string").salesChannel("string").request(new AltaMobileRequest()).build()).build();

        DeviceOffering deviceOfferingSim = new DeviceOffering();
        deviceOfferingSim.setId("123456");
        deviceOfferingSim.setCostoPromedioSinIgvSoles("1.00");
        deviceOfferingSim.setDeviceType(Constants.DEVICE_TYPE_SIM);
        deviceOfferingSim.setSimSpecifications(Arrays.asList(SimSpecification.builder()
                .sapid("string").price(Arrays.asList(MoneyAmount.builder()
                        .value(1.00).build())).build()));

        DeviceOffering deviceOfferingSmartphone = new DeviceOffering();
        deviceOfferingSmartphone.setId("123456");
        deviceOfferingSmartphone.setCostoPromedioSinIgvSoles("1000.00");
        deviceOfferingSmartphone.setDeviceType(Constants.DEVICE_TYPE_SMARTPHONE);
        deviceOfferingSmartphone.setSimSpecifications(Arrays.asList(SimSpecification.builder()
                .sapid("string").price(Arrays.asList(MoneyAmount.builder()
                        .value(1.00).build())).build()));

        List<DeviceOffering> deviceOfferingList = new ArrayList<>();
        deviceOfferingList.add(deviceOfferingSim);
        deviceOfferingList.add(deviceOfferingSmartphone);

        Sale saleRequest = Sale.builder()
                .additionalData(Arrays.asList(KeyValueType.builder().key("flowSale").value("Retail").build()))
                .status("NEGOCIACION")
                .channel(ChannelRef.builder().storeId("string").dealerId("string").href("string").id("string")
                        .name("string").storeName("string").build())
                .commercialOperation(Arrays.asList(CommercialOperationType.builder().reason("CAPL")
                        .deviceOffering(deviceOfferingList)
                        .build()))
                .build();

        method.invoke(salesManagmentServiceImpl, saleRequest, createProductOrderGeneralRequest, headersMap);
    }

    @Test
    void creationOrderValidation_status_AltaMobile_Negociacion_BadRequest_Id_Sim_test() throws NoSuchMethodException,
            InvocationTargetException, IllegalAccessException {
        Method method = SalesManagmentServiceImpl.class.getDeclaredMethod("creationOrderValidation", Sale.class,
                CreateProductOrderGeneralRequest.class, HashMap.class);
        method.setAccessible(true);

        Customer customer = Customer.builder().customerId("C0001").build();
        CreateProductOrderGeneralRequest createProductOrderGeneralRequest = CreateProductOrderGeneralRequest
                .builder().createProductOrderRequest(ProductOrderAltaMobileRequest.builder()
                        .actionType("string").customer(customer).onlyValidationIndicator("false")
                        .productOfferingId("string").salesChannel("string").request(new AltaMobileRequest()).build()).build();

        DeviceOffering deviceOfferingSim = new DeviceOffering();
        deviceOfferingSim.setId("");
        deviceOfferingSim.setCostoPromedioSinIgvSoles("1.00");
        deviceOfferingSim.setDeviceType(Constants.DEVICE_TYPE_SIM);
        deviceOfferingSim.setSimSpecifications(Arrays.asList(SimSpecification.builder()
                .sapid("string").price(Arrays.asList(MoneyAmount.builder()
                        .value(1.00).build())).build()));

        List<DeviceOffering> deviceOfferingList = new ArrayList<>();
        deviceOfferingList.add(deviceOfferingSim);

        Sale saleRequest = Sale.builder()
                .additionalData(Arrays.asList(KeyValueType.builder().key("flowSale").value("Retail").build()))
                .status("NEGOCIACION")
                .channel(ChannelRef.builder().storeId("string").dealerId("string").href("string").id("string")
                        .name("string").storeName("string").build())
                .commercialOperation(Arrays.asList(CommercialOperationType.builder().reason("CAPL")
                        .deviceOffering(deviceOfferingList)
                        .build()))
                .build();

        method.invoke(salesManagmentServiceImpl, saleRequest, createProductOrderGeneralRequest, headersMap);
    }

    @Test
    void creationOrderValidation_status_AltaMobile_Negociacion_BadRequest1_Costo_Sim_test() throws NoSuchMethodException,
            InvocationTargetException, IllegalAccessException {
        Method method = SalesManagmentServiceImpl.class.getDeclaredMethod("creationOrderValidation", Sale.class,
                CreateProductOrderGeneralRequest.class, HashMap.class);
        method.setAccessible(true);

        Customer customer = Customer.builder().customerId("C0001").build();
        CreateProductOrderGeneralRequest createProductOrderGeneralRequest = CreateProductOrderGeneralRequest
                .builder().createProductOrderRequest(ProductOrderAltaMobileRequest.builder()
                        .actionType("string").customer(customer).onlyValidationIndicator("false")
                        .productOfferingId("string").salesChannel("string").request(new AltaMobileRequest()).build()).build();

        DeviceOffering deviceOfferingSim = new DeviceOffering();
        deviceOfferingSim.setId("123456");
        deviceOfferingSim.setCostoPromedioSinIgvSoles("");
        deviceOfferingSim.setDeviceType(Constants.DEVICE_TYPE_SIM);
        deviceOfferingSim.setSimSpecifications(Arrays.asList(SimSpecification.builder()
                .sapid("string").price(Arrays.asList(MoneyAmount.builder()
                        .value(1.00).build())).build()));

        List<DeviceOffering> deviceOfferingList = new ArrayList<>();
        deviceOfferingList.add(deviceOfferingSim);

        Sale saleRequest = Sale.builder()
                .additionalData(Arrays.asList(KeyValueType.builder().key("flowSale").value("Retail").build()))
                .status("NEGOCIACION")
                .channel(ChannelRef.builder().storeId("string").dealerId("string").href("string").id("string")
                        .name("string").storeName("string").build())
                .commercialOperation(Arrays.asList(CommercialOperationType.builder().reason("CAPL")
                        .deviceOffering(deviceOfferingList)
                        .build()))
                .build();

        method.invoke(salesManagmentServiceImpl, saleRequest, createProductOrderGeneralRequest, headersMap);
    }

    @Test
    void creationOrderValidation_status_AltaMobile_Negociacion_BadRequest_Id_Smatphone_test() throws NoSuchMethodException,
            InvocationTargetException, IllegalAccessException {
        Method method = SalesManagmentServiceImpl.class.getDeclaredMethod("creationOrderValidation", Sale.class,
                CreateProductOrderGeneralRequest.class, HashMap.class);
        method.setAccessible(true);

        Customer customer = Customer.builder().customerId("C0001").build();
        CreateProductOrderGeneralRequest createProductOrderGeneralRequest = CreateProductOrderGeneralRequest
                .builder().createProductOrderRequest(ProductOrderAltaMobileRequest.builder()
                        .actionType("string").customer(customer).onlyValidationIndicator("false")
                        .productOfferingId("string").salesChannel("string").request(new AltaMobileRequest()).build()).build();

        DeviceOffering deviceOfferingSim = new DeviceOffering();
        deviceOfferingSim.setId("");
        deviceOfferingSim.setCostoPromedioSinIgvSoles("1.00");
        deviceOfferingSim.setDeviceType(Constants.DEVICE_TYPE_SMARTPHONE);
        deviceOfferingSim.setSimSpecifications(Arrays.asList(SimSpecification.builder()
                .sapid("string").price(Arrays.asList(MoneyAmount.builder()
                        .value(1.00).build())).build()));

        List<DeviceOffering> deviceOfferingList = new ArrayList<>();
        deviceOfferingList.add(deviceOfferingSim);

        Sale saleRequest = Sale.builder()
                .additionalData(Arrays.asList(KeyValueType.builder().key("flowSale").value("Retail").build()))
                .status("NEGOCIACION")
                .channel(ChannelRef.builder().storeId("string").dealerId("string").href("string").id("string")
                        .name("string").storeName("string").build())
                .commercialOperation(Arrays.asList(CommercialOperationType.builder().reason("CAPL")
                        .deviceOffering(deviceOfferingList)
                        .build()))
                .build();

        method.invoke(salesManagmentServiceImpl, saleRequest, createProductOrderGeneralRequest, headersMap);
    }

    @Test
    void creationOrderValidation_status_AltaMobile_Negociacion_BadRequest1_Costo_Smartphone_test() throws NoSuchMethodException,
            InvocationTargetException, IllegalAccessException {
        Method method = SalesManagmentServiceImpl.class.getDeclaredMethod("creationOrderValidation", Sale.class,
                CreateProductOrderGeneralRequest.class, HashMap.class);
        method.setAccessible(true);

        Customer customer = Customer.builder().customerId("C0001").build();
        CreateProductOrderGeneralRequest createProductOrderGeneralRequest = CreateProductOrderGeneralRequest
                .builder().createProductOrderRequest(ProductOrderAltaMobileRequest.builder()
                        .actionType("string").customer(customer).onlyValidationIndicator("false")
                        .productOfferingId("string").salesChannel("string").request(new AltaMobileRequest()).build()).build();

        DeviceOffering deviceOfferingSim = new DeviceOffering();
        deviceOfferingSim.setId("123456");
        deviceOfferingSim.setCostoPromedioSinIgvSoles("");
        deviceOfferingSim.setDeviceType(Constants.DEVICE_TYPE_SMARTPHONE);
        deviceOfferingSim.setSimSpecifications(Arrays.asList(SimSpecification.builder()
                .sapid("string").price(Arrays.asList(MoneyAmount.builder()
                        .value(1.00).build())).build()));

        List<DeviceOffering> deviceOfferingList = new ArrayList<>();
        deviceOfferingList.add(deviceOfferingSim);

        Sale saleRequest = Sale.builder()
                .additionalData(Arrays.asList(KeyValueType.builder().key("flowSale").value("Retail").build()))
                .status("NEGOCIACION")
                .channel(ChannelRef.builder().storeId("string").dealerId("string").href("string").id("string")
                        .name("string").storeName("string").build())
                .commercialOperation(Arrays.asList(CommercialOperationType.builder().reason("CAPL")
                        .deviceOffering(deviceOfferingList)
                        .build()))
                .build();

        method.invoke(salesManagmentServiceImpl, saleRequest, createProductOrderGeneralRequest, headersMap);
    }

    @Test
    void buildCreateQuotationFijaRequestTest() throws NoSuchMethodException, InvocationTargetException,
                                                                                                IllegalAccessException {

        Method method = SalesManagmentServiceImpl.class.getDeclaredMethod("buildCreateQuotationFijaRequest",
                CreateQuotationRequest.class, PostSalesRequest.class, List.class);
        method.setAccessible(true);

        Sale sale = CommonsMocks.createSaleMock();
        sale.setProductType(Constants.WIRELINE);

        RefinedProductType refinedProduct = new RefinedProductType();
        ProductSpecCharacteristicType productSpecCharacteristicType1 = new ProductSpecCharacteristicType();
        productSpecCharacteristicType1.setId("test");
        List<ProductSpecCharacteristicType> productCharacteristicsList = new ArrayList<>();
        productCharacteristicsList.add(productSpecCharacteristicType1);
        refinedProduct.setProductCharacteristics(productCharacteristicsList);

        sale.getCommercialOperation().get(0).getProductOfferings().get(0).getProductSpecification().get(0).setRefinedProduct(refinedProduct);

        CreateQuotationRequest createQuotationRequest = new CreateQuotationRequest();
        PostSalesRequest postSalesRequest = new PostSalesRequest();
        postSalesRequest.setHeadersMap(headersMap);
        postSalesRequest.setSale(sale);

        BusinessParameterFinanciamientoFijaExt ext1 = new BusinessParameterFinanciamientoFijaExt();
        ext1.setNomParameter("financialEntity");
        ext1.setCodParameterValue("TEST0001");
        List<BusinessParameterFinanciamientoFijaExt> bpFinanciamiento = new ArrayList<>();

        MoneyType moneyTypeUpfront = MoneyType.builder().amount(100.00).build();
        UpFrontType upFront = new UpFrontType();
        upFront.setIndicator("N");
        upFront.setPrice(moneyTypeUpfront);
        sale.getCommercialOperation().get(0).getProductOfferings().get(0).setUpFront(upFront);

        CreateProductOrderResponseType order = new CreateProductOrderResponseType();
        order.setProductOrderId("123123");
        order.setNewProductsInNewOfferings(CommonsMocks.createOrderNewProductsInNewOfferingsList());
        sale.getCommercialOperation().get(0).setOrder(order);

        method.invoke(salesManagmentServiceImpl,createQuotationRequest, postSalesRequest, bpFinanciamiento);
    }

    @Test
    void getStringValueFromBpExtListByParameterNameTest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = SalesManagmentServiceImpl.class.getDeclaredMethod("getStringValueFromBpExtListByParameterName",
                String.class, List.class);

        method.setAccessible(true);

        BusinessParameterFinanciamientoFijaExt ext1 = new BusinessParameterFinanciamientoFijaExt();
        ext1.setNomParameter("financialEntity");
        ext1.setCodParameterValue("FE001");
        List<BusinessParameterFinanciamientoFijaExt> extList = new ArrayList<>();
        extList.add(ext1);

        String extValue = (String) method.invoke(salesManagmentServiceImpl, "financialEntity", extList);

        Assert.assertEquals(extValue, "FE001");
    }

    @Test
    void retrieveCharacteristicsTest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = SalesManagmentServiceImpl.class.getDeclaredMethod("retrieveCharacteristics",
                GetSalesCharacteristicsResponse.class);

        method.setAccessible(true);

        BusinessParameterExt ext1 = new BusinessParameterExt();
        List<BusinessParameterExt> extList = new ArrayList<>();
        extList.add(ext1);

        BusinessParameterData data1 = new BusinessParameterData();
        data1.setExt(extList);

        List<BusinessParameterData> dataList = new ArrayList<>();
        dataList.add(data1);
        GetSalesCharacteristicsResponse salesCharacteristicsResponse = new GetSalesCharacteristicsResponse();
        salesCharacteristicsResponse.setData(dataList);

        method.invoke(salesManagmentServiceImpl, salesCharacteristicsResponse);
    }

    @Test
    void buildOrderAttributesListAltaFijaTest() throws NoSuchMethodException, InvocationTargetException,
            IllegalAccessException {
        Method method = SalesManagmentServiceImpl.class.getDeclaredMethod("buildOrderAttributesListAltaFija",
                List.class, Sale.class, CreateQuotationRequest.class);

        method.setAccessible(true);

        List<FlexAttrType> altaFijaOrderAttributesList = new ArrayList<>();

        Sale sale = CommonsMocks.createSaleMock();
        UpFrontType upFront = new UpFrontType();
        upFront.setIndicator("Y");
        sale.getCommercialOperation().get(0).getProductOfferings().get(0).setUpFront(upFront);

        sale.getCommercialOperation().get(0).getWorkOrDeliveryType().setScheduleDelivery("SSD");

        CreateQuotationRequestBody body = new CreateQuotationRequestBody();
        com.tdp.ms.sales.model.dto.quotation.MoneyAmount downPayment = com.tdp.ms.sales.model.dto.quotation.MoneyAmount
                .builder()
                .amount("120")
                .build();
        com.tdp.ms.sales.model.dto.quotation.MoneyAmount totalAmount = com.tdp.ms.sales.model.dto.quotation.MoneyAmount
                .builder()
                .amount("120")
                .build();
        body.setDownPayment(downPayment);
        body.setTotalAmount(totalAmount);
        body.setFinancialEntity("FE001");

        CreateQuotationRequest createQuotationRequest = new CreateQuotationRequest();
        createQuotationRequest.setBody(body);


        method.invoke(salesManagmentServiceImpl, altaFijaOrderAttributesList, sale, createQuotationRequest);
    }

    @Test
    void buildServiceAvailabilityAltaFijaTest() throws NoSuchMethodException, InvocationTargetException,
            IllegalAccessException {
        Method method = SalesManagmentServiceImpl.class.getDeclaredMethod("buildServiceAvailabilityAltaFija",
                Sale.class, List.class);

        method.setAccessible(true);

        List<ServiceabilityOfferType> serviceabilityOffersList = new ArrayList<>();
        Sale sale = CommonsMocks.createSaleMock();

        ServiceType serviceType1 = new ServiceType();
        serviceType1.setType("landline");
        serviceType1.setAllocationId("0036");
        ServiceType serviceType2 = new ServiceType();
        serviceType2.setType("broadband");
        ServiceType serviceType3 = new ServiceType();
        serviceType3.setType("tv");
        List<ServiceType> servicesList = new ArrayList<>();
        servicesList.add(serviceType1);
        servicesList.add(serviceType2);
        servicesList.add(serviceType3);

        sale.getCommercialOperation().get(0).getServiceAvailability().getOffers().get(0).setServices(servicesList);

        method.invoke(salesManagmentServiceImpl,sale, serviceabilityOffersList);
    }

    @Test
    void buildMobilePortabilityTypeTest() throws NoSuchMethodException, InvocationTargetException,
            IllegalAccessException {
        Method method = SalesManagmentServiceImpl.class.getDeclaredMethod("buildMobilePortabilityType",
                Sale.class);

        method.setAccessible(true);

        Sale sale = CommonsMocks.createSaleMock();

        method.invoke(salesManagmentServiceImpl,sale);
    }

    @Test
    void getAcquisitionTypeValueTest() throws NoSuchMethodException, InvocationTargetException,
            IllegalAccessException {
        Method method = SalesManagmentServiceImpl.class.getDeclaredMethod("getAcquisitionTypeValue",
                Sale.class);

        method.setAccessible(true);

        Sale sale = CommonsMocks.createSaleMock();
        sale.getChannel().setId("ST");
        KeyValueType keyValueTypeDelivery = KeyValueType
                .builder()
                .key("deliveryMethod")
                .value("IS")
                .build();
        sale.getAdditionalData().add(keyValueTypeDelivery);
        KeyValueType keyValueTypeAlta = KeyValueType
                .builder()
                .key("ALTA")
                .value("true")
                .build();
        sale.getCommercialOperation().get(0).getAdditionalData().add(keyValueTypeAlta);

        String acquisitionTypeValue = (String) method.invoke(salesManagmentServiceImpl, sale);

        Assert.assertEquals(acquisitionTypeValue, "Sale");
    }

    @Test
    void buildNewProductsAltaFijaListTest() throws NoSuchMethodException, InvocationTargetException,
            IllegalAccessException {
        Method method = SalesManagmentServiceImpl.class.getDeclaredMethod("buildNewProductsAltaFijaList",
                Sale.class, List.class, List.class, List.class);

        method.setAccessible(true);

        Sale sale = CommonsMocks.createSaleMock();
        List<NewAssignedBillingOffers> newAssignedBillingOffersLandlineList = new ArrayList<>();
        List<NewAssignedBillingOffers> newAssignedBillingOffersBroadbandList = new ArrayList<>();
        List<NewAssignedBillingOffers> newAssignedBillingOffersCableTvList = new ArrayList<>();

        List<NewProductAltaFija> newProductsAltaFijaList = (List) method.invoke(salesManagmentServiceImpl, sale,
                newAssignedBillingOffersLandlineList, newAssignedBillingOffersBroadbandList,
                newAssignedBillingOffersCableTvList);
    }

    @Test
    void casiAttributes_Test() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = SalesManagmentServiceImpl.class.getDeclaredMethod("casiAttributes",
                Sale.class, String.class, ChangedContainedProduct.class, List.class, Boolean.class);
        method.setAccessible(true);
        method.invoke(salesManagmentServiceImpl, sale, "string", ChangedContainedProduct.builder().build(),
                new ArrayList<ChangedContainedProduct>(), true);
    }

    @Test
    void casiAndRetailOrderAttributes_Test() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = SalesManagmentServiceImpl.class.getDeclaredMethod("casiAndRetailOrderAttributes",
                List.class, Sale.class, Boolean.class);
        method.setAccessible(true);
        List<KeyValueType> list = new ArrayList<>();
        list.add(KeyValueType.builder().key("flowsale").value("Retail").build());
        list.add(KeyValueType.builder().key("DEVICE_SKU").value("DEVICE_SKU").build());
        list.add(KeyValueType.builder().key("SIM_SKU").value("SIM_SKU").build());
        list.add(KeyValueType.builder().key("CASHIER_REGISTER_NUMBER").value("CASHIER_REGISTER_NUMBER").build());
        sale.setAdditionalData(list);
        method.invoke(salesManagmentServiceImpl, new ArrayList<>(), sale, true);
    }

    @Test
    void assignBillingOffersTest() throws NoSuchMethodException, InvocationTargetException,
            IllegalAccessException {
        Method method = SalesManagmentServiceImpl.class.getDeclaredMethod("assignBillingOffers",
                List.class, List.class, List.class, List.class);

        method.setAccessible(true);

        List<NewAssignedBillingOffers> newAssignedBillingOffersCableTvList =  new ArrayList<>();
        List<NewAssignedBillingOffers> newAssignedBillingOffersBroadbandList =  new ArrayList<>();
        List<NewAssignedBillingOffers> newAssignedBillingOffersLandlineList =  new ArrayList<>();

        ComponentProdOfferPriceType productPrice1 =  new ComponentProdOfferPriceType();
        productPrice1.setProductSpecContainmentId("123123");
        List<ComponentProdOfferPriceType> productPriceList = new ArrayList<>();
        productPriceList.add(productPrice1);

        ComposingProductType composingProductType1 = new ComposingProductType();
        composingProductType1.setProductType("sva");
        composingProductType1.setProductPrice(productPriceList);
        List<ComposingProductType> productSpecificationList = new ArrayList<>();
        productSpecificationList.add(composingProductType1);


        List<KeyValueType> additionalDataList1 = new ArrayList<>();
        additionalDataList1.add(KeyValueType.builder().key("productType").value("cableTv").build());
        additionalDataList1.add(KeyValueType.builder().key("parentProductCatalogID").value("123456").build());

        List<KeyValueType> additionalDataList2 = new ArrayList<>();
        additionalDataList2.add(KeyValueType.builder().key("productType").value("broadband").build());
        additionalDataList1.add(KeyValueType.builder().key("parentProductCatalogID").value("123456").build());

        List<KeyValueType> additionalDataList3 = new ArrayList<>();
        additionalDataList3.add(KeyValueType.builder().key("productType").value("landline").build());
        additionalDataList1.add(KeyValueType.builder().key("parentProductCatalogID").value("123456").build());

        OfferingType offeringType1 = new OfferingType();
        offeringType1.setProductSpecification(productSpecificationList);
        offeringType1.setId("1");
        offeringType1.setAdditionalData(additionalDataList1);

        OfferingType offeringType2 = new OfferingType();
        offeringType2.setProductSpecification(productSpecificationList);
        offeringType2.setId("2");
        offeringType2.setAdditionalData(additionalDataList2);

        OfferingType offeringType3 = new OfferingType();
        offeringType3.setProductSpecification(productSpecificationList);
        offeringType3.setId("3");
        offeringType3.setAdditionalData(additionalDataList3);

        List<OfferingType> productOfferingsList = new ArrayList<>();
        productOfferingsList.add(offeringType1);
        productOfferingsList.add(offeringType2);
        productOfferingsList.add(offeringType3);

        method.invoke(salesManagmentServiceImpl, productOfferingsList, newAssignedBillingOffersCableTvList,
                newAssignedBillingOffersBroadbandList, newAssignedBillingOffersLandlineList);
    }

    @Test
    void casiAndRetailOrderAttributesTest() throws NoSuchMethodException, InvocationTargetException,
            IllegalAccessException {
        Method method = SalesManagmentServiceImpl.class.getDeclaredMethod("casiAndRetailOrderAttributes",
                List.class, Sale.class, Boolean.class);

        method.setAccessible(true);

        List<FlexAttrType> caeqOrderAttributesList = new ArrayList<>();
        Sale sale = CommonsMocks.createSaleMock();
        sale.getAdditionalData().add(KeyValueType.builder().key("DEVICE_SKU").value("123456").build());
        sale.getAdditionalData().add(KeyValueType.builder().key("SIM_SKU").value("123456").build());
        sale.getAdditionalData().add(KeyValueType.builder().key("NUMERO_CAJA").value("123456").build());
        sale.getAdditionalData().remove(3);
        sale.getAdditionalData().add(KeyValueType.builder().key(Constants.FLOWSALE).value(Constants.FLOWSALE_RETAIL).build());

        method.invoke(salesManagmentServiceImpl, caeqOrderAttributesList, sale, true);
    }

    @Test
    void addCaeqOderAttributesTest() throws NoSuchMethodException, InvocationTargetException,
            IllegalAccessException {
        Method method = SalesManagmentServiceImpl.class.getDeclaredMethod("addCaeqOderAttributes",
                List.class, Sale.class, Boolean.class);

        method.setAccessible(true);

        List<FlexAttrType> caeqOrderAttributesList = new ArrayList<>();
        Sale sale = CommonsMocks.createSaleMock();

        RelatedParty relatedParty2 = new RelatedParty();
        relatedParty2.setNationalId("1503761783461661");
        sale.getRelatedParty().add(relatedParty2);

        method.invoke(salesManagmentServiceImpl, caeqOrderAttributesList, sale, true);
    }

    @Test
    void validationToAddSimcardBonusTest() throws NoSuchMethodException, InvocationTargetException,
            IllegalAccessException {
        Method method = SalesManagmentServiceImpl.class.getDeclaredMethod("validationToAddSimcardBonus",
                Sale.class, BusinessParametersResponseObjectExt.class, List.class);

        method.setAccessible(true);

        BusinessParametersResponseObjectExt getBonificacionSim = MapperUtils.mapper(BusinessParametersResponseObjectExt.class, "{\"metadata\":{\"info\":\"Códigos de bonificación\",\"type\":\"KeyValueActiveExt\",\"label\":{\"key\":\"channel\",\"value\":\"productSpecPricingID\",\"active\":\"active\",\"ext\":\"parentProductCatalogID\"}},\"data\":[{\"key\":\"CC\",\"value\":\"34572615\",\"active\":true,\"ext\":\"7431\"}]}");
        List<NewAssignedBillingOffers> newBoList = new ArrayList<>();
        Sale sale = CommonsMocks.createSaleMock();
        sale.getCommercialOperation().get(0).getDeviceOffering().get(0).setDeviceType(Constants.DEVICE_TYPE_SIM);
        sale.getCommercialOperation().get(0).getAdditionalData().add(KeyValueType.builder()
                .key(Constants.KEY_DELIVERY_METHOD).value("SP").build());

        method.invoke(salesManagmentServiceImpl, sale, getBonificacionSim, newBoList);
    }

    @Test
    void getServiceIdFromProductConfigurationByLineOfBussinessTypeTest() throws NoSuchMethodException,
                                                                InvocationTargetException, IllegalAccessException {
        Method method = SalesManagmentServiceImpl.class.getDeclaredMethod(
                "getServiceIdFromProductConfigurationByLineOfBussinessType",
                CommercialOperationType.class, String.class);

        method.setAccessible(true);

        Sale sale = CommonsMocks.createSaleMock();

        CreateProductOrderResponseType order = new CreateProductOrderResponseType();
        order.setProductOrderId("123123");
        order.setNewProductsInNewOfferings(CommonsMocks.createOrderNewProductsInNewOfferingsList());
        sale.getCommercialOperation().get(0).setOrder(order);

        String serviceId = (String) method.invoke(salesManagmentServiceImpl,
                sale.getCommercialOperation().get(0), "cableTv");

        Assert.assertEquals(serviceId, "123123");
    }

    @Test
    void addOrderInfoToCreateQuotationFijaRequestTest() throws NoSuchMethodException,
            InvocationTargetException, IllegalAccessException {
        Method method = SalesManagmentServiceImpl.class.getDeclaredMethod(
                "addOrderInfoToCreateQuotationFijaRequest", CreateQuotationRequest.class, Sale.class);

        method.setAccessible(true);

        Item itemQuotation1 = new Item();
        itemQuotation1.setOfferingId("123123");
        itemQuotation1.setPublicId("123123");
        List<Item> itemsQuotationList = new ArrayList<>();
        itemsQuotationList.add(itemQuotation1);

        CreateQuotationRequestBody createQuotationRequestBody = new CreateQuotationRequestBody();
        createQuotationRequestBody.setItems(itemsQuotationList);

        CreateQuotationRequest createQuotationRequest = new CreateQuotationRequest();
        createQuotationRequest.setBody(createQuotationRequestBody);

        Sale sale = CommonsMocks.createSaleMock();

        CreateProductOrderResponseType order = new CreateProductOrderResponseType();
        order.setProductOrderId("123123");
        order.setProductOrderReferenceNumber("123123");
        order.setNewProductsInNewOfferings(CommonsMocks.createOrderNewProductsInNewOfferingsList());
        sale.getCommercialOperation().get(0).setOrder(order);
        sale.setProductType(Constants.WIRELINE);

        method.invoke(salesManagmentServiceImpl,createQuotationRequest, sale);
    }

    @Test
    void isValidToCallSalesEventFLowTest() throws NoSuchMethodException,
            InvocationTargetException, IllegalAccessException {
        Method method = SalesManagmentServiceImpl.class.getDeclaredMethod(
                "isValidToCallSalesEventFLow", Sale.class);

        method.setAccessible(true);

        Sale sale = CommonsMocks.createSaleMock();

        Boolean result = (Boolean) method.invoke(salesManagmentServiceImpl,sale);

        Assert.assertEquals(true, result);
    }

}