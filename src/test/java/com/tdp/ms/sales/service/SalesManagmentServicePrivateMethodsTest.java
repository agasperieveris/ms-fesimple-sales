package com.tdp.ms.sales.service;

import com.tdp.genesis.core.constants.HttpHeadersKey;
import com.tdp.ms.sales.business.impl.SalesManagmentServiceImpl;
import com.tdp.ms.sales.model.dto.BusinessParameterData;
import com.tdp.ms.sales.model.dto.BusinessParameterDataObjectExt;
import com.tdp.ms.sales.model.dto.QuantityType;
import com.tdp.ms.sales.model.dto.SiteRefType;
import com.tdp.ms.sales.model.dto.productorder.CreateProductOrderGeneralRequest;
import com.tdp.ms.sales.model.dto.reservestock.StockItem;
import com.tdp.ms.sales.model.entity.Sale;
import com.tdp.ms.sales.model.request.CreateQuotationRequest;
import com.tdp.ms.sales.model.request.PostSalesRequest;
import com.tdp.ms.sales.model.response.BusinessParametersResponse;
import com.tdp.ms.sales.model.response.BusinessParametersResponseObjectExt;
import com.tdp.ms.sales.model.response.CreateQuotationResponse;
import com.tdp.ms.sales.model.response.ReserveStockResponse;
import com.tdp.ms.sales.utils.CommonsMocks;
import com.tdp.ms.sales.utils.Constants;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class SalesManagmentServicePrivateMethodsTest {

    @Autowired
    private SalesManagmentServiceImpl salesManagmentServiceImpl;

    private static Sale sale;
    private static PostSalesRequest salesRequest;
    private static final HashMap<String, String> headersMap = new HashMap();

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

}