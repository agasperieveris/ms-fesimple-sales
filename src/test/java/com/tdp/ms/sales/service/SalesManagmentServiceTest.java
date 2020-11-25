package com.tdp.ms.sales.service;

import com.tdp.genesis.core.constants.HttpHeadersKey;
import com.tdp.ms.sales.business.SalesManagmentService;
import com.tdp.ms.sales.business.impl.SalesManagmentServiceImpl;
import com.tdp.ms.sales.client.BusinessParameterWebClient;
import com.tdp.ms.sales.client.ProductOrderWebClient;
import com.tdp.ms.sales.model.dto.*;
import com.tdp.ms.sales.model.dto.productorder.CreateProductOrderGeneralRequest;
import com.tdp.ms.sales.model.dto.productorder.FlexAttrType;
import com.tdp.ms.sales.model.dto.productorder.caeq.ChangedContainedProduct;
import com.tdp.ms.sales.model.entity.Sale;
import com.tdp.ms.sales.model.request.PostSalesRequest;
import com.tdp.ms.sales.model.request.ReserveStockRequest;
import com.tdp.ms.sales.model.response.BusinessParametersResponse;
import com.tdp.ms.sales.model.response.GetSalesCharacteristicsResponse;
import com.tdp.ms.sales.model.response.ProductorderResponse;
import com.tdp.ms.sales.repository.SalesRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    void postSalesTest() {
        Sale sale = CommonsMocks.createSaleMock();
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
        BusinessParameterData businessParameterData2 = BusinessParameterData
                .builder()
                .active(false)
                .build();
        List<BusinessParameterData> dataList = new ArrayList<>();
        dataList.add(businessParameterData1);

        List<BusinessParameterData> dataList2 = new ArrayList<>();
        dataList2.add(businessParameterData2);

        GetSalesCharacteristicsResponse businessParametersResponse = GetSalesCharacteristicsResponse
                .builder()
                .data(dataList)
                .build();
        BusinessParametersResponse expectBusinessParametersResponse = BusinessParametersResponse
                .builder()
                .data(dataList2)
                .build();

        Mockito.when(businessParameterWebClient.getSalesCharacteristicsByCommercialOperationType(any()))
                .thenReturn(Mono.just(businessParametersResponse));

        Mockito.when(businessParameterWebClient.getRiskDomain(any(), any()))
                .thenReturn(Mono.just(expectBusinessParametersResponse));

        ProductorderResponse productorderResponse = new ProductorderResponse();
        CreateProductOrderResponseType createProductOrderResponseType =  new CreateProductOrderResponseType();
        productorderResponse.setCreateProductOrderResponse(createProductOrderResponseType);
        Mockito.when(productOrderWebClient.createProductOrder(any(), eq(salesRequest.getHeadersMap()), any()))
                .thenReturn(Mono.just(productorderResponse));

        Mockito.when(salesRepository.findBySalesId(any())).thenReturn(Mono.just(sale));
        Mockito.when(salesRepository.save(any())).thenReturn(Mono.just(sale));


        Mono<Sale> result = salesManagmentService.post(salesRequest);

    }

    @Test
    void postSalesTokenMcssNotFoundErrorTest() {
        List<KeyValueType> additionalDatas = new ArrayList<>();
        KeyValueType additionalData1 = new KeyValueType();
        additionalData1.setKey("s");
        additionalData1.setValue("d");
        additionalDatas.add(additionalData1);

        Sale saleTest = CommonsMocks.createSaleMock();
        saleTest.getCommercialOperation().get(0).getOrder().setProductOrderId("");
        saleTest.setId("S001");
        saleTest.setAdditionalData(additionalDatas);

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

        CreateProductOrderGeneralRequest result = salesManagmentServiceImpl
                .caeqCommercialOperation(sale, mainCaeqRequestProductOrder,
                        "CEC", "CS920", "OF201", "JSG423DE6H");

    }

    @Test
    void caeqCaplCommercialOperationTest() {
        CreateProductOrderGeneralRequest mainCaeqCaplRequestProductOrder = new CreateProductOrderGeneralRequest();

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

        List<ChangedContainedProduct> result = salesManagmentServiceImpl
                .changedContainedCaeqList(sale, "temp1");

    }

    @Test
    void buildReserveStockRequestTest() {
        ReserveStockRequest reserveStockRequest = new ReserveStockRequest();
        CreateProductOrderResponseType createProductOrderResponse = CreateProductOrderResponseType
                .builder()
                .productOrderReferenceNumber("761787835447")
                .productOrderId("930686A")
                .build();

        ReserveStockRequest result = salesManagmentServiceImpl.buildReserveStockRequest(reserveStockRequest,
                sale, createProductOrderResponse, "");

    }

    @Test
    void createShipmentDetailTest() {
        salesManagmentServiceImpl.createShipmentDetail(sale);
    }
}