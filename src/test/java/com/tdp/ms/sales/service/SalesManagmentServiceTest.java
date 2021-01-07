package com.tdp.ms.sales.service;

import com.tdp.genesis.core.constants.HttpHeadersKey;
import com.tdp.ms.commons.util.MapperUtils;
import com.tdp.ms.sales.business.SalesManagmentService;
import com.tdp.ms.sales.business.impl.SalesManagmentServiceImpl;
import com.tdp.ms.sales.client.BusinessParameterWebClient;
import com.tdp.ms.sales.client.ProductOrderWebClient;
import com.tdp.ms.sales.client.QuotationWebClient;
import com.tdp.ms.sales.client.StockWebClient;
import com.tdp.ms.sales.client.WebClientReceptor;
import com.tdp.ms.sales.model.dto.*;
import com.tdp.ms.sales.model.dto.businessparameter.BusinessParameterDataSeq;
import com.tdp.ms.sales.model.dto.businessparameter.BusinessParameterFinanciamientoFijaData;
import com.tdp.ms.sales.model.dto.businessparameter.BusinessParameterFinanciamientoFijaExt;
import com.tdp.ms.sales.model.dto.productorder.CreateProductOrderGeneralRequest;
import com.tdp.ms.sales.model.dto.productorder.FlexAttrType;
import com.tdp.ms.sales.model.dto.productorder.caeq.ChangedContainedProduct;
import com.tdp.ms.sales.model.entity.Sale;
import com.tdp.ms.sales.model.request.CreateQuotationRequest;
import com.tdp.ms.sales.model.request.PostSalesRequest;
import com.tdp.ms.sales.model.request.ReserveStockRequest;
import com.tdp.ms.sales.model.response.*;
import com.tdp.ms.sales.repository.SalesRepository;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import com.tdp.ms.sales.utils.CommonsMocks;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class SalesManagmentServiceTest {

    @MockBean
    private SalesRepository salesRepository;

    @MockBean
    private BusinessParameterWebClient businessParameterWebClient;

    @MockBean
    private ProductOrderWebClient productOrderWebClient;

    @MockBean
    private StockWebClient stockWebClient;

    @MockBean
    private QuotationWebClient quotationWebClient;

    @MockBean
    private WebClientReceptor webClientReceptor;

    @Autowired
    private SalesManagmentService salesManagmentService;

    @Autowired
    private SalesManagmentServiceImpl salesManagmentServiceImpl;

    private static Sale sale;
    private static PostSalesRequest salesRequest;
    private static final HashMap<String, String> headersMap = new HashMap();
    private static final String RH_UNICA_SERVICE_ID = "d4ce144c-6b26-4b5c-ad29-090a3a559d80";
    private static final String RH_UNICA_APPLICATION = "fesimple-sales";
    private static final String RH_UNICA_PID = "d4ce144c-6b26-4b5c-ad29-090a3a559d83";
    private static final String RH_UNICA_USER = "BackendUser";

    @BeforeAll
    static void setup() {

        // Setting request headers
        headersMap.put(HttpHeadersKey.UNICA_SERVICE_ID, RH_UNICA_SERVICE_ID);
        headersMap.put(HttpHeadersKey.UNICA_APPLICATION, RH_UNICA_APPLICATION);
        headersMap.put(HttpHeadersKey.UNICA_PID, RH_UNICA_PID);
        headersMap.put(HttpHeadersKey.UNICA_USER, RH_UNICA_USER);

        sale = CommonsMocks.createSaleMock();

        salesRequest = PostSalesRequest
                .builder()
                .sale(sale)
                .headersMap(headersMap)
                .build();
    }

    @Test
    void retrieveDomainTest() {
        List<ContactMedium> contactMediumList = new ArrayList<>();
        contactMediumList.add(sale.getProspectContact().get(0));
        String domain = salesManagmentServiceImpl.retrieveDomain(contactMediumList);
        Assert.assertEquals(domain, "everis.com");
    }

    @Test
    void retrieveDomain_nullTest() {
        List<ContactMedium> contactMediumList = new ArrayList<>();
        String domain = salesManagmentServiceImpl.retrieveDomain(contactMediumList);
        Assert.assertEquals(domain, null);
    }

    @Test
    void postSalesTest() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Sale sale = CommonsMocks.createSaleMock2();
        sale.getCommercialOperation().get(0).getOrder().setProductOrderId("");

        PostSalesRequest salesRequest = PostSalesRequest
                .builder()
                .sale(sale)
                .headersMap(headersMap)
                .build();

        BusinessParameterExt ext1 = BusinessParameterExt
                .builder()
                .codComercialOperationType("CAEQ")
                .codActionType("CW")
                .codCharacteristicId("9941")
                .codCharacteristicCode("AcquisitionType")
                .codCharacteristicValue("private")
                .build();
        List<BusinessParameterExt> extList = new ArrayList<>();
        extList.add(ext1);
        BusinessParameterData businessParameterData1 = BusinessParameterData
                .builder()
                .ext(extList)
                .build();
        BusinessParameterDataSeq businessParameterData2 = BusinessParameterDataSeq
                .builder()
                .active(false)
                .build();
        List<BusinessParameterData> dataList = new ArrayList<>();
        dataList.add(businessParameterData1);

        List<BusinessParameterDataSeq> dataList2 = new ArrayList<>();
        dataList2.add(businessParameterData2);

        GetSalesCharacteristicsResponse businessParametersResponse = GetSalesCharacteristicsResponse
                .builder()
                .data(dataList)
                .build();
        BusinessParametersResponse expectBusinessParametersResponse = BusinessParametersResponse
                .builder()
                .data(dataList2)
                .build();

        BusinessParametersFinanciamientoFijaResponse bpFijaResponse = BusinessParametersFinanciamientoFijaResponse.builder()
                .data(Arrays.asList(BusinessParameterFinanciamientoFijaData.builder()
                        .ext(Arrays.asList(BusinessParameterFinanciamientoFijaExt.builder()
                                        .id(1)
                                        .nomProductType("Landline")
                                        .nomParameter("financialEntity")
                                        .desParameterTitle("Código de financiamiento fija")
                                        .codParameterValue("FVFIR00006")
                                        .build(),
                                BusinessParameterFinanciamientoFijaExt.builder()
                                        .id(2)
                                        .nomProductType("Landline")
                                        .nomParameter("chargeCodeInstallation")
                                        .desParameterTitle("Código de financiamiento asociado a la instalación")
                                        .codParameterValue("FRVTSE_001")
                                        .build(),
                                BusinessParameterFinanciamientoFijaExt.builder()
                                        .id(3)
                                        .nomProductType("Landline")
                                        .nomParameter("chargeCodeDevicePremium")
                                        .desParameterTitle("Código de financiamiento asociado a Upgrade a Modem Premium")
                                        .codParameterValue("FRIOEQ_002")
                                        .build(),
                                BusinessParameterFinanciamientoFijaExt.builder()
                                        .id(4)
                                        .nomProductType("Landline")
                                        .nomParameter("chargeCodeUltraWifi")
                                        .desParameterTitle("Código de financiamiento asociado a Ultra Wifi")
                                        .codParameterValue("FRIOEQ_007")
                                        .build()))
                        .build()))
                .build();

        List<BusinessParametersFinanciamientoFijaResponse> bpFinanciamientoFijaResponseList = new ArrayList<>();
        bpFinanciamientoFijaResponseList.add(bpFijaResponse);

        Mockito.when(businessParameterWebClient.getSalesCharacteristicsByCommercialOperationType(any()))
                .thenReturn(Mono.just(businessParametersResponse));

        Mockito.when(businessParameterWebClient.getRiskDomain(any(), any()))
                .thenReturn(Mono.just(expectBusinessParametersResponse));

        Mockito.when(businessParameterWebClient.getParametersFinanciamientoFija(any()))
                .thenReturn(Mono.just(bpFijaResponse));

        ProductorderResponse productorderResponse = new ProductorderResponse();
        CreateProductOrderResponseType createProductOrderResponseType =  new CreateProductOrderResponseType();
        productorderResponse.setCreateProductOrderResponse(createProductOrderResponseType);
        Mockito.when(productOrderWebClient.createProductOrder(any(), eq(salesRequest.getHeadersMap()), any()))
                .thenReturn(Mono.just(productorderResponse));

        Mockito.when(salesRepository.findBySalesId(any())).thenReturn(Mono.just(sale));
        Mockito.when(salesRepository.save(any())).thenReturn(Mono.just(sale));


        salesManagmentService.post(salesRequest);

        salesRequest.getSale().getAdditionalData().stream()
                .filter(item -> item.getKey().equalsIgnoreCase("ufxauthorization"))
                .findFirst()
                .ifPresent(item -> item.setValue(""));
        salesManagmentService.post(salesRequest);

        salesRequest.getSale().setProductType("WIRELESS");
        salesManagmentService.post(salesRequest);

        // Segundo IF
        salesRequest.getSale().getCommercialOperation().get(0).getOrder().setProductOrderId("string");
        salesRequest.getSale().getCommercialOperation().get(0).getDeviceOffering().get(0).getStock().setReservationId("string");
        salesManagmentService.post(salesRequest);

        Method method = SalesManagmentServiceImpl.class.getDeclaredMethod("processFija", List.class, Sale.class,
                PostSalesRequest.class, Boolean[].class);
        method.setAccessible(true);
        final Boolean[] flgFinanciamiento = {false};
        method.invoke(salesManagmentServiceImpl, bpFinanciamientoFijaResponseList, salesRequest.getSale(), salesRequest, flgFinanciamiento);

        /* validationsAndBuildings method */
        Method method2 = SalesManagmentServiceImpl.class.getDeclaredMethod("validationsAndBuildings",
                BusinessParametersResponse.class, List.class, BusinessParametersResponseObjectExt.class,
                BusinessParametersResponseObjectExt.class , Sale.class, PostSalesRequest.class, String[].class, String.class,
                Boolean[].class, Boolean[].class, Boolean[].class, Boolean[].class, Boolean[].class, String.class, String.class, String.class);
        method2.setAccessible(true);

        BusinessParametersResponse getRiskDomain = MapperUtils.mapper(BusinessParametersResponse.class, "{\"metadata\":{\"info\":\"Dominios de Riesgos SPAN\",\"type\":\"KeyValueActive\",\"label\":{\"key\":\"id\",\"value\":\"nombreDominio\",\"active\":\"estado\",\"ext\":\"-\"}},\"data\":[{\"key\":\"430\",\"value\":\"plusmail.cf\",\"active\":false,\"ext\":\"-\"}]}");
        BusinessParametersResponse getRiskDomainTrue = MapperUtils.mapper(BusinessParametersResponse.class, "{\"metadata\":{\"info\":\"Dominios de Riesgos SPAN\",\"type\":\"KeyValueActive\",\"label\":{\"key\":\"id\",\"value\":\"nombreDominio\",\"active\":\"estado\",\"ext\":\"-\"}},\"data\":[{\"key\":\"430\",\"value\":\"plusmail.cf\",\"active\":true,\"ext\":\"-\"}]}");
        BusinessParametersResponseObjectExt getBonificacionSim = MapperUtils.mapper(BusinessParametersResponseObjectExt.class, "{\"metadata\":{\"info\":\"Códigos de bonificación\",\"type\":\"KeyValueActiveExt\",\"label\":{\"key\":\"channel\",\"value\":\"productSpecPricingID\",\"active\":\"active\",\"ext\":\"parentProductCatalogID\"}},\"data\":[{\"key\":\"CC\",\"value\":\"34572615\",\"active\":true,\"ext\":\"7431\"}]}");
        BusinessParametersResponseObjectExt getParametersSimCard = MapperUtils.mapper(BusinessParametersResponseObjectExt.class, "{\"metadata\":{\"info\":\"Parámetros del simcard para sales\",\"type\":\"KeyValueActiveExt\",\"label\":{\"key\":\"codParam\",\"value\":\"desParam\",\"active\":\"active\",\"ext\":\"-\"}},\"data\":[{\"key\":\"sku\",\"value\":\"SKU0001\",\"active\":true,\"ext\":\" \"},{\"key\":\"sapid\",\"value\":\"TSPE4128234R510201\",\"active\":true,\"ext\":\"-\"}]}");
        final String[] sapidSimcard = {""};
        String commercialOperationReason = "PORTA";
        String channelIdRequest = salesRequest.getSale().getChannel().getId();
        String customerIdRequest = salesRequest.getSale().getRelatedParty().get(0).getCustomerId();
        String productOfferingIdRequest = salesRequest.getSale().getCommercialOperation()
                .get(0).getProductOfferings().get(0).getId();
        final Boolean[] flag = {true};
        method2.invoke(salesManagmentServiceImpl, getRiskDomainTrue, Arrays.asList(BusinessParameterExt.builder().build()),
                getBonificacionSim, getParametersSimCard, salesRequest.getSale(), salesRequest, sapidSimcard, commercialOperationReason,
                flag, flag, flag, flag, flag, channelIdRequest, customerIdRequest, productOfferingIdRequest);
        Sale sale1 = CommonsMocks.createSaleMock();
        salesRequest.setSale(sale1);
        method2.invoke(salesManagmentServiceImpl, getRiskDomain, Arrays.asList(BusinessParameterExt.builder().build()),
                getBonificacionSim, getParametersSimCard, salesRequest.getSale(), salesRequest, sapidSimcard, commercialOperationReason,
                flag, flag, flag, flag, flag, channelIdRequest, customerIdRequest, productOfferingIdRequest);
    }

    @Test
    void assignBillingOffers_Test() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = SalesManagmentServiceImpl.class.getDeclaredMethod("assignBillingOffers", List.class, List.class, List.class, List.class);
        method.setAccessible(true);
        Sale sale = CommonsMocks.createSaleMock2();
        method.invoke(salesManagmentServiceImpl, sale.getCommercialOperation().get(0).getProductOfferings(),
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }

    @Test
    void postSales_MigracionFija_Test() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Sale sale = CommonsMocks.createSaleMock2();
        sale.getCommercialOperation().get(0).getOrder().setProductOrderId("");

        PostSalesRequest salesRequest = PostSalesRequest
                .builder()
                .sale(sale)
                .headersMap(headersMap)
                .build();

        BusinessParameterExt ext1 = BusinessParameterExt
                .builder()
                .codComercialOperationType("CAEQ")
                .codActionType("CW")
                .codCharacteristicId("9941")
                .codCharacteristicCode("AcquisitionType")
                .codCharacteristicValue("private")
                .build();
        List<BusinessParameterExt> extList = new ArrayList<>();
        extList.add(ext1);
        BusinessParameterData businessParameterData1 = BusinessParameterData
                .builder()
                .ext(extList)
                .build();
        BusinessParameterDataSeq businessParameterData2 = BusinessParameterDataSeq
                .builder()
                .active(false)
                .build();
        List<BusinessParameterData> dataList = new ArrayList<>();
        dataList.add(businessParameterData1);

        List<BusinessParameterDataSeq> dataList2 = new ArrayList<>();
        dataList2.add(businessParameterData2);

        GetSalesCharacteristicsResponse businessParametersResponse = GetSalesCharacteristicsResponse
                .builder()
                .data(dataList)
                .build();
        BusinessParametersResponse expectBusinessParametersResponse = BusinessParametersResponse
                .builder()
                .data(dataList2)
                .build();

        BusinessParametersFinanciamientoFijaResponse bpFijaResponse = BusinessParametersFinanciamientoFijaResponse.builder()
                .data(Arrays.asList(BusinessParameterFinanciamientoFijaData.builder()
                        .ext(Arrays.asList(BusinessParameterFinanciamientoFijaExt.builder()
                                        .id(1)
                                        .nomProductType("Landline")
                                        .nomParameter("financialEntity")
                                        .desParameterTitle("Código de financiamiento fija")
                                        .codParameterValue("FVFIR00006")
                                        .build(),
                                BusinessParameterFinanciamientoFijaExt.builder()
                                        .id(2)
                                        .nomProductType("Landline")
                                        .nomParameter("chargeCodeInstallation")
                                        .desParameterTitle("Código de financiamiento asociado a la instalación")
                                        .codParameterValue("FRVTSE_001")
                                        .build(),
                                BusinessParameterFinanciamientoFijaExt.builder()
                                        .id(3)
                                        .nomProductType("Landline")
                                        .nomParameter("chargeCodeDevicePremium")
                                        .desParameterTitle("Código de financiamiento asociado a Upgrade a Modem Premium")
                                        .codParameterValue("FRIOEQ_002")
                                        .build(),
                                BusinessParameterFinanciamientoFijaExt.builder()
                                        .id(4)
                                        .nomProductType("Landline")
                                        .nomParameter("chargeCodeUltraWifi")
                                        .desParameterTitle("Código de financiamiento asociado a Ultra Wifi")
                                        .codParameterValue("FRIOEQ_007")
                                        .build()))
                        .build()))
                .build();

        List<BusinessParametersFinanciamientoFijaResponse> bpFinanciamientoFijaResponseList = new ArrayList<>();
        bpFinanciamientoFijaResponseList.add(bpFijaResponse);

        Mockito.when(businessParameterWebClient.getSalesCharacteristicsByCommercialOperationType(any()))
                .thenReturn(Mono.just(businessParametersResponse));

        Mockito.when(businessParameterWebClient.getRiskDomain(any(), any()))
                .thenReturn(Mono.just(expectBusinessParametersResponse));

        Mockito.when(businessParameterWebClient.getParametersFinanciamientoFija(any()))
                .thenReturn(Mono.just(bpFijaResponse));

        ProductorderResponse productorderResponse = new ProductorderResponse();
        CreateProductOrderResponseType createProductOrderResponseType =  new CreateProductOrderResponseType();
        productorderResponse.setCreateProductOrderResponse(createProductOrderResponseType);
        Mockito.when(productOrderWebClient.createProductOrder(any(), eq(salesRequest.getHeadersMap()), any()))
                .thenReturn(Mono.just(productorderResponse));

        Mockito.when(salesRepository.findBySalesId(any())).thenReturn(Mono.just(sale));
        Mockito.when(salesRepository.save(any())).thenReturn(Mono.just(sale));

        salesRequest.getSale().getCommercialOperation().get(0).setReason("CAPL");
        salesRequest.getSale().getCommercialOperation().get(0).setAction("MODIFY");
        salesManagmentService.post(salesRequest);
    }

    @Test
    void postSalesIdNotFoundErrorTest() {
        Sale saleTest = CommonsMocks.createSaleMock();
        saleTest.setId("");

        PostSalesRequest salesRequest = PostSalesRequest
                .builder()
                .sale(saleTest)
                .headersMap(headersMap)
                .build();

        Mono<Sale> result = salesManagmentService.post(salesRequest);

        StepVerifier.create(result).verifyError();
    }

    @Test
    void postSalesSalesIdNotFoundErrorTest() {
        Sale saleTest = CommonsMocks.createSaleMock();
        saleTest.setSalesId("");

        PostSalesRequest salesRequest = PostSalesRequest
                .builder()
                .sale(saleTest)
                .headersMap(headersMap)
                .build();

        Mono<Sale> result = salesManagmentService.post(salesRequest);

        StepVerifier.create(result).verifyError();
    }

    @Test
    void postSalesRetailMovilImeiNotFoundErrorTest() {
        Sale saleTest = CommonsMocks.createSaleMock();
        saleTest.getChannel().setId("DLC");

        PostSalesRequest salesRequest = PostSalesRequest
                .builder()
                .sale(saleTest)
                .headersMap(headersMap)
                .build();

        Mono<Sale> result = salesManagmentService.post(salesRequest);

        StepVerifier.create(result).verifyError();
    }

    @Test
    void postSalesRetailsSimIccidNotFoundErrorTest() {
        Sale saleTest = CommonsMocks.createSaleMock();
        saleTest.getChannel().setId("DLC");

        KeyValueType additionalData1 = new KeyValueType();
        additionalData1.setKey("MOVILE_IMEI");
        additionalData1.setValue("test");
        saleTest.getAdditionalData().add(additionalData1);

        PostSalesRequest salesRequest = PostSalesRequest
                .builder()
                .sale(saleTest)
                .headersMap(headersMap)
                .build();

        Mono<Sale> result = salesManagmentService.post(salesRequest);

        StepVerifier.create(result).verifyError();
    }

    @Test
    void postSalesRetailsNumeroCajaNotFoundErrorTest() {
        Sale saleTest = CommonsMocks.createSaleMock();
        saleTest.getChannel().setId("DLC");

        KeyValueType additionalData1 = new KeyValueType();
        additionalData1.setKey("MOVILE_IMEI");
        additionalData1.setValue("test");
        saleTest.getAdditionalData().add(additionalData1);

        KeyValueType additionalData2 = new KeyValueType();
        additionalData2.setKey("SIM_ICCID");
        additionalData2.setValue("test");
        saleTest.getAdditionalData().add(additionalData2);

        PostSalesRequest salesRequest = PostSalesRequest
                .builder()
                .sale(saleTest)
                .headersMap(headersMap)
                .build();

        Mono<Sale> result = salesManagmentService.post(salesRequest);

        StepVerifier.create(result).verifyError();
    }

    @Test
    void postSalesRetailsNumeroTicketNotFoundErrorTest() {
        Sale saleTest = CommonsMocks.createSaleMock();
        saleTest.getChannel().setId("DLC");

        KeyValueType additionalData1 = new KeyValueType();
        additionalData1.setKey("MOVILE_IMEI");
        additionalData1.setValue("test");
        saleTest.getAdditionalData().add(additionalData1);

        KeyValueType additionalData2 = new KeyValueType();
        additionalData2.setKey("SIM_ICCID");
        additionalData2.setValue("test");
        saleTest.getAdditionalData().add(additionalData2);

        KeyValueType additionalData3 = new KeyValueType();
        additionalData3.setKey("NUMERO_CAJA");
        additionalData3.setValue("test");
        saleTest.getAdditionalData().add(additionalData3);

        PostSalesRequest salesRequest = PostSalesRequest
                .builder()
                .sale(saleTest)
                .headersMap(headersMap)
                .build();

        Mono<Sale> result = salesManagmentService.post(salesRequest);

        StepVerifier.create(result).verifyError();
    }

    @Test
    void additionalDataAssigments_Lima_Test() {
        Place place = Place.builder().address(AddressType.builder().region("LIMA").build()).build();
        List<Place> placeList = new ArrayList<>();
        placeList.add(place);

        CommercialOperationType commercialOperationType = CommercialOperationType
                .builder()
                .workOrDeliveryType(WorkOrDeliveryType.builder().place(placeList).build())
                .build();
        List<CommercialOperationType> commercialOperationTypeList = new ArrayList<>();
        commercialOperationTypeList.add(commercialOperationType);

        salesManagmentServiceImpl.additionalDataAssigments(null, Sale.builder()
                .channel(ChannelRef.builder().storeId("string").build())
                .commercialOperation(commercialOperationTypeList).build());
    }

    @Test
    void additionalDataAssigments_Callao_Test() {
        Place place = Place.builder().address(AddressType.builder().region("CALLAO").build()).build();
        List<Place> placeList = new ArrayList<>();
        placeList.add(place);

        CommercialOperationType commercialOperationType = CommercialOperationType
                .builder()
                .workOrDeliveryType(WorkOrDeliveryType.builder().place(placeList).build())
                .build();
        List<CommercialOperationType> commercialOperationTypeList = new ArrayList<>();
        commercialOperationTypeList.add(commercialOperationType);

        salesManagmentServiceImpl.additionalDataAssigments(null, Sale.builder()
                .channel(ChannelRef.builder().storeId("string").build())
                .commercialOperation(commercialOperationTypeList).build());
    }

    @Test
    void additionalDataAssigments_Province_Test() {
        Place place = Place.builder().address(AddressType.builder().region("AREQUIPA").build()).build();
        List<Place> placeList = new ArrayList<>();
        placeList.add(place);

        CommercialOperationType commercialOperationType = CommercialOperationType
                .builder()
                .workOrDeliveryType(WorkOrDeliveryType.builder().place(placeList).build())
                .build();
        List<CommercialOperationType> commercialOperationTypeList = new ArrayList<>();
        commercialOperationTypeList.add(commercialOperationType);

        salesManagmentServiceImpl.additionalDataAssigments(null, Sale.builder()
                .channel(ChannelRef.builder().storeId("string").build())
                .commercialOperation(commercialOperationTypeList).build());
    }

    @Test
    void validateNegotiationTest() {
        Boolean isNegotiation = salesManagmentServiceImpl.validateNegotiation(sale.getAdditionalData(),
                sale.getIdentityValidations());

        Assert.assertEquals(true, isNegotiation);
    }

    @Test
    void caplCommercialOperationTest() {
        CreateProductOrderGeneralRequest mainCaplRequestProductOrder = new CreateProductOrderGeneralRequest();

        CreateProductOrderGeneralRequest result = salesManagmentServiceImpl
                .caplCommercialOperation(sale, mainCaplRequestProductOrder,
                        "CC", "CS465", "OF824", "A83HD345DS");

    }

    @Test
    void caeqCommercialOperationTest() {
        CreateProductOrderGeneralRequest mainCaeqRequestProductOrder = new CreateProductOrderGeneralRequest();
        sale.getCommercialOperation().get(0).setReason("CAEQ");
        CreateProductOrderGeneralRequest result = salesManagmentServiceImpl
                .caeqCommercialOperation(sale, mainCaeqRequestProductOrder,
                        "CEC", "CS920", "OF201", "JSG423DE6H");

    }

    @Test
    void caeqCaplCommercialOperationTest() {
        CreateProductOrderGeneralRequest mainCaeqCaplRequestProductOrder = new CreateProductOrderGeneralRequest();
        sale.getCommercialOperation().get(0).setReason("CAEQ");

        CreateProductOrderGeneralRequest result = salesManagmentServiceImpl
                .caeqCaplCommercialOperation(sale, mainCaeqCaplRequestProductOrder,
                        "CC", "CS158", "OF486", "K3BD9EN349");

    }

    @Test
    void getCommonOrderAttributesTest() {
        List<FlexAttrType> operationOrderAttributes = new ArrayList<>();

        List<FlexAttrType> result = salesManagmentServiceImpl.commonOrderAttributes(sale);

    }

    @Test
    void getChangedContainedCaeqListTest() {
        List<ChangedContainedProduct> changedContainedProducts = new ArrayList<>();
        sale.getCommercialOperation().get(0).setReason("CAEQ");

        salesManagmentServiceImpl.changedContainedCaeqList(sale, "temp1");

        // deviceOfferings con solo un objeto
        sale.getCommercialOperation().get(0).setDeviceOffering(Collections.singletonList(sale.getCommercialOperation().get(0).getDeviceOffering().get(0)));
        salesManagmentServiceImpl.changedContainedCaeqList(sale, "temp1");
    }

    @Test
    void setChangedContainedProductProductId_Test()  throws NoSuchMethodException, InvocationTargetException,
            IllegalAccessException {
        Method method = SalesManagmentServiceImpl.class.getDeclaredMethod("setChangedContainedProductProductId",
                ChangedContainedProduct.class, List.class);

        method.setAccessible(true);
        method.invoke(salesManagmentServiceImpl,ChangedContainedProduct.builder().build(), Arrays.asList(RelatedProductType.builder()
                .product(ProductRefInfoType.builder()
                        .description("SimDevice").name("Device").id("string")
                        .productRelationship(Collections.singletonList(ProductProductRelationShip.builder()
                                .product(ProductRelationShipProduct.builder()
                                        .description("Device").id("8091734238").build()).build()))
                        .build())
                .build()));
    }

    @Test
    void buildReserveStockRequestTest()  throws NoSuchMethodException, InvocationTargetException,
                                                                                                IllegalAccessException {
        Method method = SalesManagmentServiceImpl.class.getDeclaredMethod("buildReserveStockRequest",
                ReserveStockRequest.class, Sale.class, CreateProductOrderResponseType.class, String.class);

        method.setAccessible(true);

        Sale sale = CommonsMocks.createSaleMock();
        ReserveStockRequest reserveStockRequest = new ReserveStockRequest();
        CreateProductOrderResponseType createProductOrderResponse = CreateProductOrderResponseType
                .builder()
                .productOrderReferenceNumber("761787835447")
                .productOrderId("930686A")
                .build();

        method.invoke(salesManagmentServiceImpl,reserveStockRequest, sale, createProductOrderResponse, "");
    }

    @Test
    void createShipmentDetailTest() {
        salesManagmentServiceImpl.createShipmentDetail(sale);
    }

    @Test
    void postSalesEventFlowTest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = SalesManagmentServiceImpl.class.getDeclaredMethod("postSalesEventFlow",
                PostSalesRequest.class);

        method.setAccessible(true);

        Sale sale = CommonsMocks.createSaleMock();
        PostSalesRequest postSalesRequest = PostSalesRequest
                .builder()
                .sale(sale)
                .headersMap(headersMap)
                .build();

        ReceptorResponse receptorResponse =  new ReceptorResponse();
        Mockito.when(webClientReceptor.register(any(), any())).thenReturn(Mono.just(receptorResponse));

        method.invoke(salesManagmentServiceImpl,postSalesRequest);
    }

    @Test
    void postSalesEventFlow_when_AdditionalDataIsNull_Test() throws NoSuchMethodException, InvocationTargetException,
                                                                                                IllegalAccessException {
        Method method = SalesManagmentServiceImpl.class.getDeclaredMethod("postSalesEventFlow",
                PostSalesRequest.class);

        method.setAccessible(true);

        Sale sale = new Sale();

        PostSalesRequest postSalesRequest = PostSalesRequest
                .builder()
                .sale(sale)
                .headersMap(headersMap)
                .build();

        ReceptorResponse receptorResponse =  new ReceptorResponse();
        Mockito.when(webClientReceptor.register(any(), any())).thenReturn(Mono.just(receptorResponse));

        method.invoke(salesManagmentServiceImpl, postSalesRequest);
    }

    @Test
    void callToReserveStockAndCreateQuotationTest() throws NoSuchMethodException, InvocationTargetException,
            IllegalAccessException {
        Method method = SalesManagmentServiceImpl.class.getDeclaredMethod("callToReserveStockAndCreateQuotation",
                PostSalesRequest.class, Sale.class, Boolean.class, Boolean.class, String.class);

        method.setAccessible(true);

        Sale sale = CommonsMocks.createSaleMock();

        CreateProductOrderResponseType createProductOrderResponse = CreateProductOrderResponseType
                .builder()
                .productOrderReferenceNumber("761787835447")
                .productOrderId("930686A")
                .build();
        sale.getCommercialOperation().get(0).setOrder(createProductOrderResponse);

        PostSalesRequest postSalesRequest = PostSalesRequest
                .builder()
                .sale(sale)
                .headersMap(headersMap)
                .build();

        ReserveStockResponse reserveStockResponse =  new ReserveStockResponse();
        Mockito.when(stockWebClient.reserveStock(any(), any(), any())).thenReturn(Mono.just(reserveStockResponse));

        method.invoke(salesManagmentServiceImpl, postSalesRequest, sale, false, false, "");
    }

    @Test
    void callToCreateQuotation_when_FlgFinanciamientoIsTrue_Test() throws NoSuchMethodException,
                                                                    InvocationTargetException, IllegalAccessException {
        Method method = SalesManagmentServiceImpl.class.getDeclaredMethod("callToCreateQuotation",
                PostSalesRequest.class, Sale.class, Boolean.class, Boolean.class);

        method.setAccessible(true);

        Sale sale = CommonsMocks.createSaleMock();

        PostSalesRequest postSalesRequest = PostSalesRequest
                .builder()
                .sale(sale)
                .headersMap(headersMap)
                .build();

        CreateQuotationResponse createQuotationResponse =  new CreateQuotationResponse();
        Mockito.when(quotationWebClient.createQuotation(any(), any())).thenReturn(Mono.just(createQuotationResponse));

        method.invoke(salesManagmentServiceImpl, postSalesRequest, sale, false, true);
    }

    @Test
    void callToCreateQuotation_when_FlgFinanciamientoIsFalse_Test() throws NoSuchMethodException,
            InvocationTargetException, IllegalAccessException {
        Method method = SalesManagmentServiceImpl.class.getDeclaredMethod("callToCreateQuotation",
                PostSalesRequest.class, Sale.class, Boolean.class, Boolean.class);

        method.setAccessible(true);

        Sale sale = CommonsMocks.createSaleMock();

        PostSalesRequest postSalesRequest = PostSalesRequest
                .builder()
                .sale(sale)
                .headersMap(headersMap)
                .build();

        CreateQuotationResponse createQuotationResponse =  new CreateQuotationResponse();
        Mockito.when(salesRepository.save(any())).thenReturn(Mono.just(sale));

        method.invoke(salesManagmentServiceImpl, postSalesRequest, sale, false, false);
    }

    @Test
    void commercialOperationInputValidations_Test() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = SalesManagmentServiceImpl.class.getDeclaredMethod("commercialOperationInputValidations", Sale.class);
        method.setAccessible(true);
        method.invoke(salesManagmentServiceImpl, Sale.builder()
                .commercialOperation(Collections.singletonList(CommercialOperationType.builder()
                        .productOfferings(null)
                        .deviceOffering(null)
                        .additionalData(null)
                        .build()))
                .build());
    }

    @Test
    void wirelineMigrations_Test() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Sale sale = CommonsMocks.createSaleMock2();
        sale.getCommercialOperation().get(0).getOrder().setProductOrderId("");

        PostSalesRequest salesRequest = PostSalesRequest
                .builder()
                .sale(sale)
                .headersMap(headersMap)
                .build();

        BusinessParameterExt ext1 = BusinessParameterExt
                .builder()
                .codComercialOperationType("CAEQ")
                .codActionType("CW")
                .codCharacteristicId("9941")
                .codCharacteristicCode("AcquisitionType")
                .codCharacteristicValue("private")
                .build();
        List<BusinessParameterExt> extList = new ArrayList<>();
        extList.add(ext1);
        BusinessParameterData businessParameterData1 = BusinessParameterData
                .builder()
                .ext(extList)
                .build();
        BusinessParameterDataSeq businessParameterData2 = BusinessParameterDataSeq
                .builder()
                .active(false)
                .build();
        List<BusinessParameterData> dataList = new ArrayList<>();
        dataList.add(businessParameterData1);

        List<BusinessParameterDataSeq> dataList2 = new ArrayList<>();
        dataList2.add(businessParameterData2);

        GetSalesCharacteristicsResponse businessParametersResponse = GetSalesCharacteristicsResponse
                .builder()
                .data(dataList)
                .build();
        BusinessParametersResponse expectBusinessParametersResponse = BusinessParametersResponse
                .builder()
                .data(dataList2)
                .build();

        BusinessParametersFinanciamientoFijaResponse bpFijaResponse = BusinessParametersFinanciamientoFijaResponse.builder()
                .data(Arrays.asList(BusinessParameterFinanciamientoFijaData.builder()
                        .ext(Arrays.asList(BusinessParameterFinanciamientoFijaExt.builder()
                                        .id(1)
                                        .nomProductType("Landline")
                                        .nomParameter("financialEntity")
                                        .desParameterTitle("Código de financiamiento fija")
                                        .codParameterValue("FVFIR00006")
                                        .build(),
                                BusinessParameterFinanciamientoFijaExt.builder()
                                        .id(2)
                                        .nomProductType("Landline")
                                        .nomParameter("chargeCodeInstallation")
                                        .desParameterTitle("Código de financiamiento asociado a la instalación")
                                        .codParameterValue("FRVTSE_001")
                                        .build(),
                                BusinessParameterFinanciamientoFijaExt.builder()
                                        .id(3)
                                        .nomProductType("Landline")
                                        .nomParameter("chargeCodeDevicePremium")
                                        .desParameterTitle("Código de financiamiento asociado a Upgrade a Modem Premium")
                                        .codParameterValue("FRIOEQ_002")
                                        .build(),
                                BusinessParameterFinanciamientoFijaExt.builder()
                                        .id(4)
                                        .nomProductType("Landline")
                                        .nomParameter("chargeCodeUltraWifi")
                                        .desParameterTitle("Código de financiamiento asociado a Ultra Wifi")
                                        .codParameterValue("FRIOEQ_007")
                                        .build()))
                        .build()))
                .build();

        List<BusinessParametersFinanciamientoFijaResponse> bpFinanciamientoFijaResponseList = new ArrayList<>();
        bpFinanciamientoFijaResponseList.add(bpFijaResponse);

        Mockito.when(businessParameterWebClient.getSalesCharacteristicsByCommercialOperationType(any()))
                .thenReturn(Mono.just(businessParametersResponse));

        Mockito.when(businessParameterWebClient.getRiskDomain(any(), any()))
                .thenReturn(Mono.just(expectBusinessParametersResponse));

        Mockito.when(businessParameterWebClient.getParametersFinanciamientoFija(any()))
                .thenReturn(Mono.just(bpFijaResponse));

        ProductorderResponse productorderResponse = new ProductorderResponse();
        CreateProductOrderResponseType createProductOrderResponseType =  new CreateProductOrderResponseType();
        productorderResponse.setCreateProductOrderResponse(createProductOrderResponseType);
        Mockito.when(productOrderWebClient.createProductOrder(any(), eq(salesRequest.getHeadersMap()), any()))
                .thenReturn(Mono.just(productorderResponse));

        Mockito.when(salesRepository.findBySalesId(any())).thenReturn(Mono.just(sale));
        Mockito.when(salesRepository.save(any())).thenReturn(Mono.just(sale));

        salesRequest.getSale().getCommercialOperation().get(0).setReason("CAPL");
        salesRequest.getSale().getCommercialOperation().get(0).setAction("MODIFY");

        Method method = SalesManagmentServiceImpl.class.getDeclaredMethod("wirelineMigrations", List.class,
                PostSalesRequest.class, Boolean[].class, String.class);
        method.setAccessible(true);

        final Boolean[] flgFinanciamiento = {true};
        method.invoke(salesManagmentServiceImpl, bpFinanciamientoFijaResponseList.get(0).getData().get(0).getExt(),
                salesRequest, flgFinanciamiento, "CH");

        /*Method methodFillProductOfferingProductSpecId = SalesManagmentServiceImpl.class.getDeclaredMethod("fillProductOfferingProductSpecId", List.class, List.class);
        methodFillProductOfferingProductSpecId.setAccessible(true);
        methodFillProductOfferingProductSpecId.invoke(salesManagmentServiceImpl,
                Arrays.asList(MigrationComponent.builder()
                        .componentName("landline")
                        .build()), sale.getCommercialOperation().get(0).getProductOfferings().get(0).getProductSpecification());*/
    }

    @Test
    void compareComponents_Test() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = SalesManagmentServiceImpl.class.getDeclaredMethod("compareComponents", String.class);
        method.setAccessible(true);

        method.invoke(salesManagmentServiceImpl, "broadband");
        method.invoke(salesManagmentServiceImpl, "cableTv");
        method.invoke(salesManagmentServiceImpl, "device");
        method.invoke(salesManagmentServiceImpl, "landline");
        method.invoke(salesManagmentServiceImpl, "accessories");
    }

    @Test
    void buildGenesisError_Test() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = SalesManagmentServiceImpl.class.getDeclaredMethod("buildGenesisError", String.class, String.class);
        method.setAccessible(true);
        method.invoke(salesManagmentServiceImpl, "test", "test");
    }

    @Test
    void addOrderIntoSale_Test() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Sale sale = CommonsMocks.createSaleMock2();
        sale.getCommercialOperation().get(0).getOrder().setProductOrderId("");

        PostSalesRequest salesRequest = PostSalesRequest
                .builder()
                .sale(sale)
                .headersMap(headersMap)
                .build();

        ProductorderResponse productorderResponse = new ProductorderResponse();
        CreateProductOrderResponseType createProductOrderResponseType =  new CreateProductOrderResponseType();
        productorderResponse.setCreateProductOrderResponse(createProductOrderResponseType);
        Mockito.when(productOrderWebClient.createProductOrder(any(), eq(salesRequest.getHeadersMap()), any()))
                .thenReturn(Mono.just(productorderResponse));

        Mockito.when(salesRepository.findBySalesId(any())).thenReturn(Mono.just(sale));
        Mockito.when(salesRepository.save(any())).thenReturn(Mono.just(sale));

        CreateQuotationResponse createQuotationResponse =  new CreateQuotationResponse();
        Mockito.when(quotationWebClient.createQuotation(any(), any())).thenReturn(Mono.just(createQuotationResponse));

        Method method = SalesManagmentServiceImpl.class.getDeclaredMethod("addOrderIntoSale", PostSalesRequest.class,
                Sale.class, Boolean[].class, CreateQuotationRequest.class, ProductorderResponse.class);
        method.setAccessible(true);
        final Boolean[] flgFinanciamiento = {false};
        method.invoke(salesManagmentServiceImpl, salesRequest, sale, flgFinanciamiento,
                new CreateQuotationRequest(), ProductorderResponse.builder().createProductOrderResponse(CreateProductOrderResponseType.builder().productOrderId("string").build()).build());
        flgFinanciamiento[0] = true;
        sale.setIdentityValidations(null);
        method.invoke(salesManagmentServiceImpl, salesRequest, sale, flgFinanciamiento,
                new CreateQuotationRequest(), ProductorderResponse.builder().createProductOrderResponse(CreateProductOrderResponseType.builder().productOrderId("string").build()).build());
        method.invoke(salesManagmentServiceImpl, salesRequest, sale, flgFinanciamiento,
                new CreateQuotationRequest(), ProductorderResponse.builder().createProductOrderResponse(CreateProductOrderResponseType.builder().productOrderId("").build()).build());
    }
}