package com.tdp.ms.sales.service;

import com.tdp.genesis.core.constants.HttpHeadersKey;
import com.tdp.ms.sales.business.impl.SalesManagmentServiceImpl;
import com.tdp.ms.sales.client.GetSkuWebClient;
import com.tdp.ms.sales.client.ProductOrderWebClient;
import com.tdp.ms.sales.model.dto.*;
import com.tdp.ms.sales.model.dto.productorder.CreateProductOrderGeneralRequest;
import com.tdp.ms.sales.model.dto.productorder.Customer;
import com.tdp.ms.sales.model.dto.productorder.altafija.AltaFijaRequest;
import com.tdp.ms.sales.model.dto.productorder.altafija.ProductOrderAltaFijaRequest;
import com.tdp.ms.sales.model.dto.productorder.altamobile.AltaMobileRequest;
import com.tdp.ms.sales.model.dto.productorder.altamobile.ProductOrderAltaMobileRequest;
import com.tdp.ms.sales.model.dto.productorder.caeq.CaeqRequest;
import com.tdp.ms.sales.model.dto.productorder.caeq.ProductOrderCaeqRequest;
import com.tdp.ms.sales.model.dto.productorder.caeqcapl.CaeqCaplRequest;
import com.tdp.ms.sales.model.dto.productorder.caeqcapl.ProductOrderCaeqCaplRequest;
import com.tdp.ms.sales.model.dto.productorder.capl.CaplRequest;
import com.tdp.ms.sales.model.dto.productorder.capl.ProductOrderCaplRequest;
import com.tdp.ms.sales.model.dto.reservestock.StockItem;
import com.tdp.ms.sales.model.entity.Sale;
import com.tdp.ms.sales.model.request.CreateQuotationRequest;
import com.tdp.ms.sales.model.request.PostSalesRequest;
import com.tdp.ms.sales.model.response.*;
import com.tdp.ms.sales.utils.CommonsMocks;
import com.tdp.ms.sales.utils.Constants;
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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;

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
        headersMap.put(HttpHeadersKey.UNICA_SERVICE_ID, Constants.RH_UNICA_SERVICE_ID);
        headersMap.put(HttpHeadersKey.UNICA_APPLICATION, Constants.RH_UNICA_APPLICATION);
        headersMap.put(HttpHeadersKey.UNICA_PID, Constants.RH_UNICA_PID);
        headersMap.put(HttpHeadersKey.UNICA_USER, Constants.RH_UNICA_USER);

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
                BusinessParametersResponseObjectExt.class, String.class);

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

        method.invoke(salesManagmentServiceImpl,sale, altaRequest, "CC", "C0001", "OF0001", "CIP0001",
                businessParametersResponseObjectExt, "SAPID0001");
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

        Mockito.when(getSkuWebClient.createSku("string", "default","string",1.00,"Provide","","string","2","string","string","1.00",headersMap))
                .thenReturn(Flux.just(GetSkuResponse.builder().deviceType("mobile_phone").sku("31024026").build(),
                        GetSkuResponse.builder().deviceType("sim").sku("32004482").build()));

        Mockito.when(productOrderWebClient.createProductOrder(any(), any(), any()))
                .thenReturn(Mono.just(ProductorderResponse.builder().build()));

        CreateProductOrderGeneralRequest createProductOrderGeneralRequest = CreateProductOrderGeneralRequest
                .builder().createProductOrderRequest(ProductOrderCaplRequest.builder()
                        .actionType("string").customer(Customer.builder().customerId("string").build()).customerId("string")
                        .onlyValidationIndicator(false).productOfferingId("string").request(CaplRequest.builder().build())
                        .salesChannel("string")
                        .build()).build();

        Sale saleRequest = Sale.builder()
                .additionalData(Arrays.asList(KeyValueType.builder().key("flowSale").value("Retail").build()))
                .status("NEGOCIACION")
                .channel(ChannelRef.builder().storeId("string").dealerId("string").href("string").id("string")
                        .name("string").storeName("string").build())
                .commercialOperation(Arrays.asList(CommercialOperationType.builder().reason("ALTA")
                        .deviceOffering(Arrays.asList(DeviceOffering.builder()
                                .sapid("string").costoPromedioSinIgvSoles("1.00")
                                .simSpecifications(Arrays.asList(SimSpecification.builder()
                                        .sapid("string").price(Arrays.asList(MoneyAmount.builder()
                                                .value(1.00).build())).build())).build())).build()))
                .build();

        method.invoke(salesManagmentServiceImpl, saleRequest, createProductOrderGeneralRequest, headersMap);
    }

    @Test
    void creationOrderValidation_status_caeq_Negociacion_test() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = SalesManagmentServiceImpl.class.getDeclaredMethod("creationOrderValidation", Sale.class,
                CreateProductOrderGeneralRequest.class, HashMap.class);
        method.setAccessible(true);

        Mockito.when(getSkuWebClient.createSku("string", "default","string",1.00,"Provide","","string","2","string","string","1.00",headersMap))
                .thenReturn(Flux.just(GetSkuResponse.builder().deviceType("mobile_phone").sku("31024026").build(),
                        GetSkuResponse.builder().deviceType("sim").sku("32004482").build()));

        Mockito.when(productOrderWebClient.createProductOrder(any(), any(), any()))
                .thenReturn(Mono.just(ProductorderResponse.builder().build()));

        CreateProductOrderGeneralRequest createProductOrderGeneralRequest = CreateProductOrderGeneralRequest
                .builder().createProductOrderRequest(ProductOrderCaeqRequest.builder()
                        .actionType("string").customerId("string").onlyValidationIndicator(false)
                        .productOfferingId("string").salesChannel("string")
                        .request(CaeqRequest.builder().build()).build()).build();

        Sale saleRequest = Sale.builder()
                .additionalData(Arrays.asList(KeyValueType.builder().key("flowSale").value("Retail").build()))
                .status("NEGOCIACION")
                .channel(ChannelRef.builder().storeId("string").dealerId("string").href("string").id("string")
                        .name("string").storeName("string").build())
                .commercialOperation(Arrays.asList(CommercialOperationType.builder().reason("ALTA")
                        .deviceOffering(Arrays.asList(DeviceOffering.builder()
                                .sapid("string").costoPromedioSinIgvSoles("1.00")
                                .simSpecifications(Arrays.asList(SimSpecification.builder()
                                        .sapid("string").price(Arrays.asList(MoneyAmount.builder()
                                                .value(1.00).build())).build())).build())).build()))
                .build();

        method.invoke(salesManagmentServiceImpl, saleRequest, createProductOrderGeneralRequest, headersMap);
    }

    @Test
    void creationOrderValidation_status_caeqcapl_Negociacion_test() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = SalesManagmentServiceImpl.class.getDeclaredMethod("creationOrderValidation", Sale.class,
                CreateProductOrderGeneralRequest.class, HashMap.class);
        method.setAccessible(true);

        Mockito.when(getSkuWebClient.createSku("string", "default","string",1.00,"Provide","","string","2","string","string","1.00",headersMap))
                .thenReturn(Flux.just(GetSkuResponse.builder().deviceType("mobile_phone").sku("31024026").build(),
                        GetSkuResponse.builder().deviceType("sim").sku("32004482").build()));

        Mockito.when(productOrderWebClient.createProductOrder(any(), any(), any()))
                .thenReturn(Mono.just(ProductorderResponse.builder().build()));

        CreateProductOrderGeneralRequest createProductOrderGeneralRequest = CreateProductOrderGeneralRequest
                .builder().createProductOrderRequest(ProductOrderCaeqCaplRequest.builder()
                        .actionType("string").customerId("string").onlyValidationIndicator(false)
                        .productOfferingId("string").salesChannel("string").request(CaeqCaplRequest.builder().build()).build()).build();

        Sale saleRequest = Sale.builder()
                .additionalData(Arrays.asList(KeyValueType.builder().key("flowSale").value("Retail").build()))
                .status("NEGOCIACION")
                .channel(ChannelRef.builder().storeId("string").dealerId("string").href("string").id("string")
                        .name("string").storeName("string").build())
                .commercialOperation(Arrays.asList(CommercialOperationType.builder().reason("ALTA")
                        .deviceOffering(Arrays.asList(DeviceOffering.builder()
                                .sapid("string").costoPromedioSinIgvSoles("1.00")
                                .simSpecifications(Arrays.asList(SimSpecification.builder()
                                        .sapid("string").price(Arrays.asList(MoneyAmount.builder()
                                                .value(1.00).build())).build())).build())).build()))
                .build();

        method.invoke(salesManagmentServiceImpl, saleRequest, createProductOrderGeneralRequest, headersMap);
    }

    @Test
    void creationOrderValidation_status_AltaFija_Negociacion_test() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = SalesManagmentServiceImpl.class.getDeclaredMethod("creationOrderValidation", Sale.class,
                CreateProductOrderGeneralRequest.class, HashMap.class);
        method.setAccessible(true);

        Mockito.when(getSkuWebClient.createSku("string", "default","string",1.00,"Provide","","string","2","string","string","1.00",headersMap))
                .thenReturn(Flux.just(GetSkuResponse.builder().deviceType("mobile_phone").sku("31024026").build(),
                        GetSkuResponse.builder().deviceType("sim").sku("32004482").build()));

        Mockito.when(productOrderWebClient.createProductOrder(any(), any(), any()))
                .thenReturn(Mono.just(ProductorderResponse.builder().build()));

        CreateProductOrderGeneralRequest createProductOrderGeneralRequest = CreateProductOrderGeneralRequest
                .builder().createProductOrderRequest(ProductOrderAltaFijaRequest.builder()
                        .actionType("string").customerId("string").onlyValidationIndicator(false)
                        .productOfferingId("string").salesChannel("string").request(new AltaFijaRequest()).build()).build();

        Sale saleRequest = Sale.builder()
                .additionalData(Arrays.asList(KeyValueType.builder().key("flowSale").value("Retail").build()))
                .status("NEGOCIACION")
                .channel(ChannelRef.builder().storeId("string").dealerId("string").href("string").id("string")
                        .name("string").storeName("string").build())
                .commercialOperation(Arrays.asList(CommercialOperationType.builder().reason("ALTA")
                        .deviceOffering(Arrays.asList(DeviceOffering.builder()
                                .sapid("string").costoPromedioSinIgvSoles("1.00")
                                .simSpecifications(Arrays.asList(SimSpecification.builder()
                                        .sapid("string").price(Arrays.asList(MoneyAmount.builder()
                                                .value(1.00).build())).build())).build())).build()))
                .build();

        method.invoke(salesManagmentServiceImpl, saleRequest, createProductOrderGeneralRequest, headersMap);
    }

    @Test
    void creationOrderValidation_status_AltaMobile_Negociacion_test() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = SalesManagmentServiceImpl.class.getDeclaredMethod("creationOrderValidation", Sale.class,
                CreateProductOrderGeneralRequest.class, HashMap.class);
        method.setAccessible(true);

        Mockito.when(getSkuWebClient.createSku("string", "default","string",1.00,"Change","","string","2","string","string","1.00",headersMap))
                .thenReturn(Flux.just(GetSkuResponse.builder().deviceType("mobile_phone").sku("31024026").build(),
                        GetSkuResponse.builder().deviceType("sim").sku("32004482").build()));

        Mockito.when(productOrderWebClient.createProductOrder(any(), any(), any()))
                .thenReturn(Mono.just(ProductorderResponse.builder().build()));

        CreateProductOrderGeneralRequest createProductOrderGeneralRequest = CreateProductOrderGeneralRequest
                .builder().createProductOrderRequest(ProductOrderAltaMobileRequest.builder()
                        .actionType("string").customerId("string").onlyValidationIndicator(false)
                        .productOfferingId("string").salesChannel("string").request(new AltaMobileRequest()).build()).build();

        Sale saleRequest = Sale.builder()
                .additionalData(Arrays.asList(KeyValueType.builder().key("flowSale").value("Retail").build()))
                .status("NEGOCIACION")
                .channel(ChannelRef.builder().storeId("string").dealerId("string").href("string").id("string")
                        .name("string").storeName("string").build())
                .commercialOperation(Arrays.asList(CommercialOperationType.builder().reason("CAPL")
                        .deviceOffering(Arrays.asList(DeviceOffering.builder()
                                .sapid("string").costoPromedioSinIgvSoles("1.00")
                                .simSpecifications(Arrays.asList(SimSpecification.builder()
                                        .sapid("string").price(Arrays.asList(MoneyAmount.builder()
                                                .value(1.00).build())).build())).build())).build()))
                .build();

        method.invoke(salesManagmentServiceImpl, saleRequest, createProductOrderGeneralRequest, headersMap);
    }
}