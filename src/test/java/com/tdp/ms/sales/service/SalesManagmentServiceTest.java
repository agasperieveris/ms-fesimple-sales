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
import com.tdp.ms.sales.model.dto.businessparameter.BusinessParameterReasonCodeData;
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

    private static final Sale saleCaeqCaplCasi = MapperUtils.mapper(Sale.class, "{\"id\":\"1dea652d-ad5e-4a69-be2c-f89f9ab12642\",\"salesId\":\"FE-0000019484\",\"name\":\"LUIS ENRIQUE RIVEROS ORDOÃ\u0091EZ\",\"description\":null,\"priority\":null,\"channel\":{\"id\":\"CEC\",\"href\":null,\"name\":null,\"storeId\":\"05537005\",\"dealerId\":\"05537\",\"storeName\":null},\"agent\":{\"id\":\"1\",\"href\":null,\"fullName\":\"Ronald EugenioRIOS PACHECO\",\"firstName\":\"Ronald Eugenio\",\"lastName\":\"RIOS PACHECO\",\"customerId\":null,\"role\":null,\"accountId\":null,\"billingArragmentId\":null,\"score\":null,\"nationalID\":null,\"nationalIDType\":null},\"productType\":\"WIRELESS\",\"commercialOperation\":[{\"id\":null,\"name\":null,\"action\":\"MODIFY\",\"reason\":\"CASI\",\"serviceAvailability\":null,\"order\":null,\"portability\":null,\"workOrDeliveryType\":{\"type\":\"Delivery\",\"mediumDelivery\":\"Expres\",\"scheduleDelivery\":null,\"contact\":{\"phoneNumber\":\"987654123\"},\"place\":[{\"address\":{\"streetNr\":\"1130\",\"streetName\":\"DOMINGO MARTINEZ LUJAN\",\"streetType\":\"JR\",\"streetSuffix\":\"JR\",\"postcode\":null,\"city\":\"SURQUILLO\",\"cityCode\":\"150141\",\"stateOrProvince\":\"LIMA\",\"region\":\"LIMA\",\"regionCode\":\"15\",\"country\":\"PE\",\"floor\":\"2\",\"apartment\":\"\",\"isDangerous\":false,\"addressFormat\":\"Standard\",\"timeZone\":\"Time Zone in Peru\",\"areaCode\":\"150141\",\"neighborhood\":null,\"neighborhoodCode\":\"\",\"comments\":\"calle\",\"isValid\":true,\"housingComplexType\":\"\",\"housingComplex\":null,\"block\":\"\",\"lot\":\"\",\"urbanization\":null,\"coordinates\":{\"latitude\":\"-12.10937992\",\"longitude\":\"-77.016418957\"}},\"additionalData\":[{\"key\":\"stateOrProvinceCode\",\"value\":\"01\"}]}],\"additionalData\":[{\"key\":\"mediumDeliveryLabel\",\"value\":\"Delivery Express\"},{\"key\":\"addressLabel\",\"value\":\"JIRON DOMINGO MARTINEZ LUJAN 1130 PISO 2 \"},{\"key\":\"housingComplexLabel\",\"value\":\"\"},{\"key\":\"collectStoreId\",\"value\":\"05537005\"},{\"key\":\"shipmentAddressId\",\"value\":\"Vacío\"},{\"key\":\"shipmentSiteId\",\"value\":\"NA\"},{\"key\":\"shippingLocality\",\"value\":\"LIMA\"},{\"key\":\"provinceOfShippingAddress\",\"value\":\"01\"},{\"key\":\"shopAddress\",\"value\":\"Agent.direccionPuntoVenta\"},{\"key\":\"shipmentInstructions\",\"value\":\"prueba\"},{\"key\":\"recipientFirstName\"},{\"key\":\"recipientLastName\",\"value\":null},{\"key\":\"deliveryMethod\",\"value\":\"SP\"},{\"key\":\"reasonText\",\"value\":\"\"}]},\"product\":{\"href\":\"/productInventory/v2/products/8091551600\",\"id\":\"8091551600\",\"publicId\":\"920955954\",\"description\":\"Control RPM\",\"name\":\"Plan Internet Control Especial 70\",\"productType\":\"mobile\",\"tags\":null,\"isBundle\":false,\"productSerialNumber\":null,\"billingAccount\":[],\"productOffering\":{\"id\":\"55504\",\"href\":\"/productCatalog/v2/offerings/55504\",\"name\":\"CONTROLRPM\",\"role\":null,\"validFor\":null,\"entityType\":null,\"description\":null,\"@referredType\":null},\"productSpec\":null,\"characteristic\":[{\"valueType\":\"String\",\"@type\":\"StringType\"},{\"valueType\":\"String\",\"@type\":\"StringType\"},{\"valueType\":\"String\",\"@type\":\"StringType\"},{\"valueType\":\"String\",\"@type\":\"StringType\"},{\"valueType\":\"String\",\"@type\":\"StringType\"},{\"valueType\":\"String\",\"@type\":\"StringType\"},{\"valueType\":\"String\",\"@type\":\"StringType\"},{\"valueType\":\"String\",\"@type\":\"StringType\"},{\"valueType\":\"String\",\"@type\":\"StringType\"},{\"valueType\":\"String\",\"@type\":\"StringType\"},{\"valueType\":\"String\",\"@type\":\"StringType\"},{\"valueType\":\"String\",\"@type\":\"StringType\"},{\"valueType\":\"String\",\"@type\":\"StringType\"},{\"valueType\":\"String\",\"@type\":\"StringType\"},{\"valueType\":\"String\",\"@type\":\"StringType\"},{\"valueType\":\"String\",\"@type\":\"StringType\"},{\"valueType\":\"String\",\"@type\":\"StringType\"},{\"valueType\":\"String\",\"@type\":\"StringType\"},{\"valueType\":\"String\",\"@type\":\"StringType\"},{\"valueType\":\"String\",\"@type\":\"StringType\"},{\"valueType\":\"String\",\"@type\":\"StringType\"},{\"valueType\":\"String\",\"@type\":\"StringType\"},{\"valueType\":\"String\",\"@type\":\"StringType\"},{\"valueType\":\"String\",\"@type\":\"StringType\"}],\"productRelationShip\":[{\"type\":null,\"product\":{\"id\":\"8091551622\",\"href\":\"id/8091551622\",\"name\":\"Servicios Adicionales\",\"publicId\":\"920955954\",\"description\":\"AdditionalServices\",\"productType\":\"mobile\",\"tags\":null,\"category\":[],\"startDate\":\"2020-09-18T19:42:46-05:00\",\"terminationDate\":null,\"place\":[{\"id\":\"\",\"href\":null,\"name\":null,\"role\":null,\"validFor\":null,\"entityType\":null,\"description\":null,\"@referredType\":null}],\"productSpec\":{\"id\":\"6321\",\"href\":\"id/6321\",\"name\":null,\"role\":null,\"validFor\":null,\"entityType\":null,\"description\":null,\"@referredType\":null},\"characteristic\":[],\"productPrice\":[],\"relatedParty\":[{\"id\":\"57579513\",\"href\":null,\"name\":\"LUIS ENRIQUE RIVEROS ORDOÑEZ\",\"role\":null,\"validFor\":null,\"description\":null,\"legalId\":null,\"@referredType\":null}],\"productRelationship\":[],\"additionalData\":[{\"key\":\"9311_DirectoryListing\",\"value\":\"N\"},{\"key\":\"12204_CargameSaldoAllowed\",\"value\":\"No\"},{\"key\":\"12194_FacturaDetallada\",\"value\":\"No\"},{\"key\":\"1993524_RCOverrideAmount\",\"value\":\"\"}]}},{\"type\":null,\"product\":{\"id\":\"8091735477\",\"href\":\"id/8091735477\",\"name\":\"Datos\",\"publicId\":\"920955954\",\"description\":\"Data\",\"productType\":\"mobile\",\"tags\":null,\"category\":[],\"startDate\":\"2020-09-18T19:42:46-05:00\",\"terminationDate\":null,\"place\":[{\"id\":\"\",\"href\":null,\"name\":null,\"role\":null,\"validFor\":null,\"entityType\":null,\"description\":null,\"@referredType\":null}],\"productSpec\":{\"id\":\"5371\",\"href\":\"id/5371\",\"name\":null,\"role\":null,\"validFor\":null,\"entityType\":null,\"description\":null,\"@referredType\":null},\"characteristic\":[],\"productPrice\":[],\"relatedParty\":[{\"id\":\"57579513\",\"href\":null,\"name\":\"LUIS ENRIQUE RIVEROS ORDOÑEZ\",\"role\":null,\"validFor\":null,\"description\":null,\"legalId\":null,\"@referredType\":null}],\"productRelationship\":[{\"product\":{\"description\":\"BoltOnsData\",\"relatedParty\":[{\"role\":null,\"name\":\"LUIS ENRIQUE RIVEROS ORDOÑEZ\",\"description\":null,\"id\":\"57579513\"}],\"characteristic\":[],\"name\":\"Paquete de Datos\",\"productRelationship\":[],\"id\":\"8091735478\",\"href\":\"id/8091735478\",\"place\":[{\"role\":null,\"referredType\":null,\"validFor\":null,\"entityType\":null,\"name\":null,\"description\":null,\"id\":\"\",\"href\":null}],\"additionalData\":[{\"value\":\"\",\"key\":\"1993524_RCOverrideAmount\"}],\"category\":[],\"productSpec\":{\"role\":null,\"referredType\":null,\"validFor\":null,\"entityType\":null,\"name\":null,\"description\":null,\"id\":\"5471\",\"href\":\"id/5471\"},\"publicId\":\"920955954\",\"productType\":\"mobile\",\"startDate\":\"2020-09-18T19:42:46-05:00\",\"productPrice\":[]},\"type\":null}],\"additionalData\":[{\"key\":\"1993524_RCOverrideAmount\",\"value\":\"\"}]}},{\"type\":null,\"product\":{\"id\":\"8091735479\",\"href\":\"id/8091735479\",\"name\":\"Friends & Family - F&F (Dúos)\",\"publicId\":\"920955954\",\"description\":\"FriendsandFamily\",\"productType\":\"mobile\",\"tags\":null,\"category\":[],\"startDate\":\"2020-09-18T19:42:46-05:00\",\"terminationDate\":null,\"place\":[{\"id\":\"\",\"href\":null,\"name\":null,\"role\":null,\"validFor\":null,\"entityType\":null,\"description\":null,\"@referredType\":null}],\"productSpec\":{\"id\":\"6671\",\"href\":\"id/6671\",\"name\":null,\"role\":null,\"validFor\":null,\"entityType\":null,\"description\":null,\"@referredType\":null},\"characteristic\":[],\"productPrice\":[],\"relatedParty\":[{\"id\":\"57579513\",\"href\":null,\"name\":\"LUIS ENRIQUE RIVEROS ORDOÑEZ\",\"role\":null,\"validFor\":null,\"description\":null,\"legalId\":null,\"@referredType\":null}],\"productRelationship\":[],\"additionalData\":[{\"key\":\"1993524_RCOverrideAmount\",\"value\":\"\"}]}},{\"type\":null,\"product\":{\"id\":\"8091735481\",\"href\":\"id/8091735481\",\"name\":\"Internacional (LDI)\",\"publicId\":\"920955954\",\"description\":\"International\",\"productType\":\"mobile\",\"tags\":null,\"category\":[],\"startDate\":\"2020-09-18T19:42:46-05:00\",\"terminationDate\":null,\"place\":[{\"id\":\"\",\"href\":null,\"name\":null,\"role\":null,\"validFor\":null,\"entityType\":null,\"description\":null,\"@referredType\":null}],\"productSpec\":{\"id\":\"5821\",\"href\":\"id/5821\",\"name\":null,\"role\":null,\"validFor\":null,\"entityType\":null,\"description\":null,\"@referredType\":null},\"characteristic\":[],\"productPrice\":[],\"relatedParty\":[{\"id\":\"57579513\",\"href\":null,\"name\":\"LUIS ENRIQUE RIVEROS ORDOÑEZ\",\"role\":null,\"validFor\":null,\"description\":null,\"legalId\":null,\"@referredType\":null}],\"productRelationship\":[{\"product\":{\"description\":\"BoltOnsInternational\",\"relatedParty\":[{\"role\":null,\"name\":\"LUIS ENRIQUE RIVEROS ORDOÑEZ\",\"description\":null,\"id\":\"57579513\"}],\"characteristic\":[],\"name\":\"Paquetes de Larga Distancia Internacional\",\"productRelationship\":[],\"id\":\"8091735482\",\"href\":\"id/8091735482\",\"place\":[{\"role\":null,\"referredType\":null,\"validFor\":null,\"entityType\":null,\"name\":null,\"description\":null,\"id\":\"\",\"href\":null}],\"additionalData\":[{\"value\":\"\",\"key\":\"1993524_RCOverrideAmount\"}],\"category\":[],\"productSpec\":{\"role\":null,\"referredType\":null,\"validFor\":null,\"entityType\":null,\"name\":null,\"description\":null,\"id\":\"5871\",\"href\":\"id/5871\"},\"publicId\":\"920955954\",\"productType\":\"mobile\",\"startDate\":\"2020-09-18T19:42:46-05:00\",\"productPrice\":[]},\"type\":null}],\"additionalData\":[{\"key\":\"1993524_RCOverrideAmount\",\"value\":\"\"}]}},{\"type\":null,\"product\":{\"id\":\"8091735483\",\"href\":\"id/8091735483\",\"name\":\"Mensajería (SMS)\",\"publicId\":\"920955954\",\"description\":\"Messaging\",\"productType\":\"mobile\",\"tags\":null,\"category\":[],\"startDate\":\"2020-09-18T19:42:46-05:00\",\"terminationDate\":null,\"place\":[{\"id\":\"\",\"href\":null,\"name\":null,\"role\":null,\"validFor\":null,\"entityType\":null,\"description\":null,\"@referredType\":null}],\"productSpec\":{\"id\":\"6071\",\"href\":\"id/6071\",\"name\":null,\"role\":null,\"validFor\":null,\"entityType\":null,\"description\":null,\"@referredType\":null},\"characteristic\":[],\"productPrice\":[],\"relatedParty\":[{\"id\":\"57579513\",\"href\":null,\"name\":\"LUIS ENRIQUE RIVEROS ORDOÑEZ\",\"role\":null,\"validFor\":null,\"description\":null,\"legalId\":null,\"@referredType\":null}],\"productRelationship\":[{\"product\":{\"description\":\"IncomingSMS\",\"relatedParty\":[{\"role\":null,\"name\":\"LUIS ENRIQUE RIVEROS ORDOÑEZ\",\"description\":null,\"id\":\"57579513\"}],\"characteristic\":[],\"name\":\"SMS entrante\",\"productRelationship\":[],\"id\":\"8091735484\",\"href\":\"id/8091735484\",\"place\":[{\"role\":null,\"referredType\":null,\"validFor\":null,\"entityType\":null,\"name\":null,\"description\":null,\"id\":\"\",\"href\":null}],\"additionalData\":[{\"value\":\"\",\"key\":\"1993524_RCOverrideAmount\"}],\"category\":[],\"productSpec\":{\"role\":null,\"referredType\":null,\"validFor\":null,\"entityType\":null,\"name\":null,\"description\":null,\"id\":\"6121\",\"href\":\"id/6121\"},\"publicId\":\"920955954\",\"productType\":\"mobile\",\"startDate\":\"2020-09-18T19:42:46-05:00\",\"productPrice\":[]},\"type\":null},{\"product\":{\"description\":\"MMS\",\"relatedParty\":[{\"role\":null,\"name\":\"LUIS ENRIQUE RIVEROS ORDOÑEZ\",\"description\":null,\"id\":\"57579513\"}],\"characteristic\":[],\"name\":\"MMS\",\"productRelationship\":[],\"id\":\"8091735485\",\"href\":\"id/8091735485\",\"place\":[{\"role\":null,\"referredType\":null,\"validFor\":null,\"entityType\":null,\"name\":null,\"description\":null,\"id\":\"\",\"href\":null}],\"additionalData\":[{\"value\":\"\",\"key\":\"1993524_RCOverrideAmount\"}],\"category\":[],\"productSpec\":{\"role\":null,\"referredType\":null,\"validFor\":null,\"entityType\":null,\"name\":null,\"description\":null,\"id\":\"6271\",\"href\":\"id/6271\"},\"publicId\":\"920955954\",\"productType\":\"mobile\",\"startDate\":\"2020-09-18T19:42:46-05:00\",\"productPrice\":[]},\"type\":null},{\"product\":{\"description\":\"OutgoingSMS\",\"relatedParty\":[{\"role\":null,\"name\":\"LUIS ENRIQUE RIVEROS ORDOÑEZ\",\"description\":null,\"id\":\"57579513\"}],\"characteristic\":[],\"name\":\"SMS saliente\",\"productRelationship\":[],\"id\":\"8091735486\",\"href\":\"id/8091735486\",\"place\":[{\"role\":null,\"referredType\":null,\"validFor\":null,\"entityType\":null,\"name\":null,\"description\":null,\"id\":\"\",\"href\":null}],\"additionalData\":[{\"value\":\"Activo\",\"key\":\"12174_GSMOutgoingSMS\"},{\"value\":\"\",\"key\":\"1993524_RCOverrideAmount\"}],\"category\":[],\"productSpec\":{\"role\":null,\"referredType\":null,\"validFor\":null,\"entityType\":null,\"name\":null,\"description\":null,\"id\":\"6171\",\"href\":\"id/6171\"},\"publicId\":\"920955954\",\"productType\":\"mobile\",\"startDate\":\"2020-09-18T19:42:46-05:00\",\"productPrice\":[]},\"type\":null}],\"additionalData\":[{\"key\":\"1993524_RCOverrideAmount\",\"value\":\"\"}]}},{\"type\":null,\"product\":{\"id\":\"8091735488\",\"href\":\"id/8091735488\",\"name\":\"Plan\",\"publicId\":\"920955954\",\"description\":\"Plan\",\"productType\":\"mobile\",\"tags\":null,\"category\":[],\"startDate\":\"2020-09-18T19:42:46-05:00\",\"terminationDate\":null,\"place\":[{\"id\":\"\",\"href\":null,\"name\":null,\"role\":null,\"validFor\":null,\"entityType\":null,\"description\":null,\"@referredType\":null}],\"productSpec\":{\"id\":\"5021\",\"href\":\"id/5021\",\"name\":null,\"role\":null,\"validFor\":null,\"entityType\":null,\"description\":null,\"@referredType\":null},\"characteristic\":[],\"productPrice\":[],\"relatedParty\":[{\"id\":\"57579513\",\"href\":null,\"name\":\"LUIS ENRIQUE RIVEROS ORDOÑEZ\",\"role\":null,\"validFor\":null,\"description\":null,\"legalId\":null,\"@referredType\":null}],\"productRelationship\":[],\"additionalData\":[{\"key\":\"4167898_Defaultappsatfunpack\",\"value\":\"\"},{\"key\":\"4360638_SharingDataIndicatorOwner\",\"value\":\"No\"},{\"key\":\"9341_Product\",\"value\":\"Control\"},{\"key\":\"4360648_SharingDataIndicatorConsumer\",\"value\":\"No\"},{\"key\":\"9391_PlanExtraUsageCreditLimit\",\"value\":\"0\"},{\"key\":\"1354561_UpgradeIndicator\",\"value\":\"3\"},{\"key\":\"9371_PlanInfo\",\"value\":\"\"},{\"key\":\"9381_OverridePlanRCAmount\",\"value\":\"-1\"},{\"key\":\"1837341_SMSCreditLimit\",\"value\":\"0\"},{\"key\":\"9321_POClasificacion\",\"value\":\"Movil Control\"},{\"key\":\"9331_PlanGroup\",\"value\":\"MOVPOSR5\"},{\"key\":\"9361_PlanRank\",\"value\":\"70\"},{\"key\":\"1837331_DataCreditLimit\",\"value\":\"0\"},{\"key\":\"1837321_VoiceCreditLimit\",\"value\":\"0\"}]}},{\"type\":null,\"product\":{\"id\":\"8091735493\",\"href\":\"id/8091735493\",\"name\":\"Promociones y Descuentos Especiales\",\"publicId\":\"920955954\",\"description\":\"PromotionsandDiscounts\",\"productType\":\"mobile\",\"tags\":null,\"category\":[],\"startDate\":\"2020-09-18T19:42:46-05:00\",\"terminationDate\":null,\"place\":[{\"id\":\"\",\"href\":null,\"name\":null,\"role\":null,\"validFor\":null,\"entityType\":null,\"description\":null,\"@referredType\":null}],\"productSpec\":{\"id\":\"6821\",\"href\":\"id/6821\",\"name\":null,\"role\":null,\"validFor\":null,\"entityType\":null,\"description\":null,\"@referredType\":null},\"characteristic\":[],\"productPrice\":[],\"relatedParty\":[{\"id\":\"57579513\",\"href\":null,\"name\":\"LUIS ENRIQUE RIVEROS ORDOÑEZ\",\"role\":null,\"validFor\":null,\"description\":null,\"legalId\":null,\"@referredType\":null}],\"productRelationship\":[],\"additionalData\":[{\"key\":\"1993524_RCOverrideAmount\",\"value\":\"\"}]}},{\"type\":null,\"product\":{\"id\":\"8091551623\",\"href\":\"id/8091735494\",\"name\":\"SIM y Dispositivo\",\"publicId\":\"920955954\",\"description\":\"SimDevice\",\"productType\":\"mobile\",\"tags\":null,\"category\":[],\"startDate\":\"2020-09-18T19:42:46-05:00\",\"terminationDate\":null,\"place\":[{\"id\":\"\",\"href\":null,\"name\":null,\"role\":null,\"validFor\":null,\"entityType\":null,\"description\":null,\"@referredType\":null}],\"productSpec\":{\"id\":\"6971\",\"href\":\"id/6971\",\"name\":null,\"role\":null,\"validFor\":null,\"entityType\":null,\"description\":null,\"@referredType\":null},\"characteristic\":[],\"productPrice\":[],\"relatedParty\":[{\"id\":\"57579513\",\"href\":null,\"name\":\"LUIS ENRIQUE RIVEROS ORDOÑEZ\",\"role\":null,\"validFor\":null,\"description\":null,\"legalId\":null,\"@referredType\":null}],\"productRelationship\":[{\"product\":{\"description\":\"Device\",\"relatedParty\":[{\"role\":null,\"name\":\"LUIS ENRIQUE RIVEROS ORDOÑEZ\",\"description\":null,\"id\":\"57579513\"}],\"characteristic\":[],\"name\":\"Dispositivo\",\"productRelationship\":[],\"id\":\"8091735495\",\"href\":\"id/8091735495\",\"place\":[{\"role\":null,\"referredType\":null,\"validFor\":null,\"entityType\":null,\"name\":null,\"description\":null,\"id\":\"\",\"href\":null}],\"additionalData\":[{\"value\":\"No\",\"key\":\"9991_DeviceUnlocked\"},{\"value\":\"\",\"key\":\"888084_DeviceCommercialClassification\"},{\"value\":\"1937493\",\"key\":\"15734_Equipment_CID\"},{\"value\":\"18/09/2020 19:11:09\",\"key\":\"10021_DevicePurchaseDate\"},{\"value\":\"123900000159379\",\"key\":\"9871_IMEI\"},{\"value\":\"Yes\",\"key\":\"4378796_DeviceListPrice\"},{\"value\":\"\",\"key\":\"1993524_RCOverrideAmount\"},{\"value\":\"18/09/2020 19:11:09\",\"key\":\"9901_ManufacturersWarrantyStartDate\"},{\"value\":\"Yes\",\"key\":\"9931_ShipmentAllowed\"},{\"value\":\"\",\"key\":\"1333461_ExternalPrice\"},{\"value\":\"Media Alta\",\"key\":\"9961_DeviceGama\"},{\"value\":\"NA\",\"key\":\"10001_DeviceMoreDetails\"},{\"value\":\"LineactivationProvide\",\"key\":\"9701_Operation\"},{\"value\":\"ConsessionPurchased\",\"key\":\"9941_AcquisitionType\"},{\"value\":\"N\",\"key\":\"10031_RoboPlusIndicator\"},{\"value\":\"nanoSIM\",\"key\":\"16524_SIMGroup\"},{\"value\":\"17/09/2021 00:00:00\",\"key\":\"9921_WarrantyManufacturerEndDate\"},{\"value\":\"Smartphone\",\"key\":\"9891_DeviceType\"},{\"value\":\"18\",\"key\":\"10011_DeviceCommitmentPeriod\"},{\"value\":\"12\",\"key\":\"9911_ManufacturersWarrantyDuration\"},{\"value\":\"TMGPEHUVTP10NES001\",\"key\":\"9951_SAP_ID\"},{\"value\":\"No\",\"key\":\"9981_Outlet\"},{\"value\":\"VTR-L09 P10\",\"key\":\"9881_MODELO\"},{\"value\":\"No\",\"key\":\"9291_PostpagoFacil\"},{\"value\":\"HUAWEI\",\"key\":\"8811_MARCA\"},{\"value\":\"No\",\"key\":\"9971_LoanedInd\"}],\"category\":[],\"productSpec\":{\"role\":null,\"referredType\":null,\"validFor\":null,\"entityType\":null,\"name\":null,\"description\":null,\"id\":\"5721\",\"href\":\"id/5721\"},\"publicId\":\"920955954\",\"productType\":\"mobile\",\"startDate\":\"2020-09-18T19:42:46-05:00\",\"productPrice\":[]},\"type\":null},{\"product\":{\"description\":\"SimCard\",\"relatedParty\":[{\"role\":null,\"name\":\"LUIS ENRIQUE RIVEROS ORDOÑEZ\",\"description\":null,\"id\":\"57579513\"}],\"characteristic\":[],\"name\":\"Tarjeta SIM\",\"productRelationship\":[],\"id\":\"8091735496\",\"href\":\"id/8091735496\",\"place\":[{\"role\":null,\"referredType\":null,\"validFor\":null,\"entityType\":null,\"name\":null,\"description\":null,\"id\":\"\",\"href\":null}],\"additionalData\":[{\"value\":\"8958080008100200853\",\"key\":\"799244_ICCID\"},{\"value\":\"32514197\",\"key\":\"9811_PUK2\"},{\"value\":\"3389\",\"key\":\"9801_PIN2\"},{\"value\":\"003\",\"key\":\"1829053_TRANSPORT_KEY\"},{\"value\":\"TSPE41282F3R510201\",\"key\":\"9751_SIMTypeSKU\"},{\"value\":\"\",\"key\":\"1993524_RCOverrideAmount\"},{\"value\":\"2345\",\"key\":\"9781_PIN1\"},{\"value\":\"19710028\",\"key\":\"9791_PUK1\"},{\"value\":\"ConsessionPurchased\",\"key\":\"9941_AcquisitionType\"},{\"value\":\"78BDA967FFFF4629\",\"key\":\"9851_KID\"},{\"value\":\"4G\",\"key\":\"9861_Technology\"},{\"value\":\"9FFF34F93D3EC629\",\"key\":\"9841_KIC\"},{\"value\":\"\",\"key\":\"1333461_ExternalPrice\"},{\"value\":\"39FFF157C0D4E0912553C90451C1B5F2\",\"key\":\"9831_KI\"},{\"value\":\"90.02.ME\",\"key\":\"1829063_CARD_PROFILE\"},{\"value\":\"3030DB8CBF656639\",\"key\":\"9821_ADM1\"},{\"value\":\"nanoSIM\",\"key\":\"16524_SIMGroup\"}],\"category\":[],\"productSpec\":{\"role\":null,\"referredType\":null,\"validFor\":null,\"entityType\":null,\"name\":null,\"description\":null,\"id\":\"5671\",\"href\":\"id/5671\"},\"publicId\":\"920955954\",\"productType\":\"mobile\",\"startDate\":\"2020-09-18T19:42:46-05:00\",\"productPrice\":[]},\"type\":null},{\"product\":{\"description\":\"DeviceCommitment\",\"relatedParty\":[{\"role\":null,\"name\":\"LUIS ENRIQUE RIVEROS ORDOÑEZ\",\"description\":null,\"id\":\"57579513\"}],\"characteristic\":[],\"name\":\"Compromiso de Equipo\",\"productRelationship\":[],\"id\":\"8091735503\",\"href\":\"id/8091735503\",\"place\":[{\"role\":null,\"referredType\":null,\"validFor\":null,\"entityType\":null,\"name\":null,\"description\":null,\"id\":\"\",\"href\":null}],\"additionalData\":[{\"value\":\"\",\"key\":\"1993524_RCOverrideAmount\"},{\"value\":\"969638\",\"key\":\"4375597_PurchaseOrderActionID\"},{\"value\":\"TMGPEHUVTP10NES001\",\"key\":\"14794_RelatedDeviceSapId\"},{\"value\":\"70\",\"key\":\"4378806_InitialPlanRank\"},{\"value\":\"N\",\"key\":\"14824_EarlyCAEQ\"},{\"value\":\"17/03/2022 19:11:09\",\"key\":\"14804_DeviceCommitmentPeriodEndDate\"},{\"value\":\"0\",\"key\":\"4379396_CommitmentExtension\"},{\"value\":\"18\",\"key\":\"14774_DeviceCommitmentPeriodDuration\"},{\"value\":\"0.0\",\"key\":\"14814_DeviceFinalPrice\"},{\"value\":\"\",\"key\":\"1926446_FuturePlanRankInSplitFlow\"},{\"value\":\"18/09/2020 19:11:09\",\"key\":\"14784_DeviceCommitmentPeriodStartDate\"}],\"category\":[],\"productSpec\":{\"role\":null,\"referredType\":null,\"validFor\":null,\"entityType\":null,\"name\":null,\"description\":null,\"id\":\"1276561\",\"href\":\"id/1276561\"},\"publicId\":\"920955954\",\"productType\":\"mobile\",\"startDate\":\"2020-09-18T19:42:46-05:00\",\"productPrice\":[]},\"type\":null}],\"additionalData\":[{\"key\":\"1993524_RCOverrideAmount\",\"value\":\"\"},{\"key\":\"9681_BlacklistModel\",\"value\":\"\"},{\"key\":\"9661_BlacklistIMEI\",\"value\":\"\"},{\"key\":\"15994_BlacklistMake\",\"value\":\"\"},{\"key\":\"1479761_DeviceMisuseInd\",\"value\":\"No\"}]}},{\"type\":null,\"product\":{\"id\":\"8091735500\",\"href\":\"id/8091735500\",\"name\":\"Voz\",\"publicId\":\"920955954\",\"description\":\"Voice\",\"productType\":\"mobile\",\"tags\":null,\"category\":[],\"startDate\":\"2020-09-18T19:42:46-05:00\",\"terminationDate\":null,\"place\":[{\"id\":\"\",\"href\":null,\"name\":null,\"role\":null,\"validFor\":null,\"entityType\":null,\"description\":null,\"@referredType\":null}],\"productSpec\":{\"id\":\"5071\",\"href\":\"id/5071\",\"name\":null,\"role\":null,\"validFor\":null,\"entityType\":null,\"description\":null,\"@referredType\":null},\"characteristic\":[],\"productPrice\":[],\"relatedParty\":[{\"id\":\"57579513\",\"href\":null,\"name\":\"LUIS ENRIQUE RIVEROS ORDOÑEZ\",\"role\":null,\"validFor\":null,\"description\":null,\"legalId\":null,\"@referredType\":null}],\"productRelationship\":[{\"product\":{\"description\":\"OutgoingCall\",\"relatedParty\":[{\"role\":null,\"name\":\"LUIS ENRIQUE RIVEROS ORDOÑEZ\",\"description\":null,\"id\":\"57579513\"}],\"characteristic\":[],\"name\":\"Llamada saliente\",\"productRelationship\":[],\"id\":\"8091735501\",\"href\":\"id/8091735501\",\"place\":[{\"role\":null,\"referredType\":null,\"validFor\":null,\"entityType\":null,\"name\":null,\"description\":null,\"id\":\"\",\"href\":null}],\"additionalData\":[{\"value\":\"Activo\",\"key\":\"9471_ConferenceCalls\"},{\"value\":\"Inactivo\",\"key\":\"9501_ForwardCallsNoAnswer\"},{\"value\":\"Inactivo\",\"key\":\"9511_ForwardCallsInconditional\"},{\"value\":\"\",\"key\":\"1993524_RCOverrideAmount\"},{\"value\":\"Inactivo\",\"key\":\"9491_ForwardCallsBusyTone\"},{\"value\":\"Activo\",\"key\":\"9481_ForwardCallsbydefault\"}],\"category\":[],\"productSpec\":{\"role\":null,\"referredType\":null,\"validFor\":null,\"entityType\":null,\"name\":null,\"description\":null,\"id\":\"5121\",\"href\":\"id/5121\"},\"publicId\":\"920955954\",\"productType\":\"mobile\",\"startDate\":\"2020-09-18T19:42:46-05:00\",\"productPrice\":[]},\"type\":null}],\"additionalData\":[{\"key\":\"1993524_RCOverrideAmount\",\"value\":\"\"},{\"key\":\"9401_CallWaiting\",\"value\":\"Activo\"},{\"key\":\"9451_VPNPlatform\",\"value\":\"Y\"},{\"key\":\"9411_VoiceMail\",\"value\":\"Activo\"},{\"key\":\"9461_VoiceToText\",\"value\":\"N\"},{\"key\":\"9441_LostCallNotification\",\"value\":\"Activo\"},{\"key\":\"9431_Notifyavailability\",\"value\":\"Activo\"},{\"key\":\"9421_CallerID\",\"value\":\"Activo\"}]}}],\"productPrice\":[{\"id\":\"PLAN_8091735492_125824\",\"name\":\"Plan Internet Control Especial 70\",\"description\":null,\"isMandatory\":null,\"priceType\":\"recurring\",\"recurringChargePeriod\":null,\"unitOfMeasure\":null,\"price\":{\"amount\":59.32,\"units\":\"PEN\"},\"taxIncluded\":null,\"taxRate\":0,\"taxType\":null,\"prodPriceAlteration\":null,\"additionalData\":null},{\"id\":\"8091735491\",\"name\":\"Technical for Extra Usage Plan Credit Limit parameters\",\"description\":null,\"isMandatory\":null,\"priceType\":\"one time\",\"recurringChargePeriod\":null,\"unitOfMeasure\":null,\"price\":{\"amount\":0,\"units\":\"PEN\"},\"taxIncluded\":null,\"taxRate\":0,\"taxType\":null,\"prodPriceAlteration\":null,\"additionalData\":null},{\"id\":\"8091735489\",\"name\":\"BO Tarifa de Acumulador SVA\",\"description\":null,\"isMandatory\":null,\"priceType\":\"one time\",\"recurringChargePeriod\":null,\"unitOfMeasure\":null,\"price\":{\"amount\":0,\"units\":\"PEN\"},\"taxIncluded\":null,\"taxRate\":0,\"taxType\":null,\"prodPriceAlteration\":null,\"additionalData\":null},{\"id\":\"8091735497\",\"name\":\"Precio de Simcard Bajo\",\"description\":null,\"isMandatory\":null,\"priceType\":\"one time\",\"recurringChargePeriod\":null,\"unitOfMeasure\":null,\"price\":{\"amount\":0.85,\"units\":\"PEN\"},\"taxIncluded\":null,\"taxRate\":0,\"taxType\":null,\"prodPriceAlteration\":null,\"additionalData\":null},{\"id\":\"8091735504\",\"name\":\"Cargo por Penalidad\",\"description\":null,\"isMandatory\":null,\"priceType\":\"one time\",\"recurringChargePeriod\":null,\"unitOfMeasure\":null,\"price\":{\"amount\":0,\"units\":\"PEN\"},\"taxIncluded\":null,\"taxRate\":0,\"taxType\":null,\"prodPriceAlteration\":null,\"additionalData\":null},{\"id\":\"8091735498\",\"name\":\"TechnicalPP\",\"description\":null,\"isMandatory\":null,\"priceType\":\"one time\",\"recurringChargePeriod\":null,\"unitOfMeasure\":null,\"price\":{\"amount\":0,\"units\":\"PEN\"},\"taxIncluded\":null,\"taxRate\":0,\"taxType\":null,\"prodPriceAlteration\":null,\"additionalData\":null}],\"place\":[],\"additionalData\":[{\"key\":\"9271_UnrecognizedOwnershipIndicator\",\"value\":\"No\"},{\"key\":\"9211_PortingInd\",\"value\":\"N\"},{\"key\":\"734398409_RLC_Vanity Class\",\"value\":\"Regular\"},{\"key\":\"1470631_VoluntarySuspendStartDate\",\"value\":\"\"},{\"key\":\"9281_AllowedStartDateforRetention\",\"value\":\"18/09/2020 19:11:09\"},{\"key\":\"9251_IMSIR\",\"value\":\"716071000022629\"},{\"key\":\"2356_TemporaryVoiceLineMSISDN\",\"value\":\"\"},{\"key\":\"9301_ExternalPlatformSVABlocking\",\"value\":\"N\"},{\"key\":\"7601_MSISDN\",\"value\":\"920955954\"},{\"key\":\"1993524_RCOverrideAmount\",\"value\":\"\"},{\"key\":\"9221_NumberType\",\"value\":\"CPP\"},{\"key\":\"9231_Network\",\"value\":\"GSM\"},{\"key\":\"1479491_CostCenter\",\"value\":\"\"},{\"key\":\"1412211_TotalOfVoluntarySuspendDays\",\"value\":\"0\"},{\"key\":\"9241_IMSI\",\"value\":\"716071000022629\"},{\"key\":\"19234_ResourceCategory\",\"value\":\"Regular\"},{\"key\":\"15044_SubscriberGroupValue\",\"value\":\"POS2\"},{\"key\":\"9201_ProveedorAnterior\",\"value\":\"\"},{\"key\":\"productSpecPricingID\",\"value\":\"8091735492_125824\"},{\"key\":\"offerApId\",\"value\":\"8091735473\"}]},\"productOfferings\":[{\"id\":\"4394733\",\"href\":\"/offerings/4417988\",\"correlationId\":null,\"name\":null,\"description\":\"Control Movistar Libre\",\"type\":\"CONTROL\",\"category\":null,\"isPromotion\":null,\"billingMethod\":null,\"frameworkAgreement\":null,\"compatibleProducts\":null,\"isBundle\":null,\"offeringUrl\":null,\"validFor\":null,\"bundledProductOffering\":null,\"productSpecification\":[{\"id\":null,\"href\":null,\"name\":null,\"productType\":null,\"tags\":null,\"minCardinality\":null,\"maxCardinality\":5,\"defaultCardinality\":null,\"periodDuration\":null,\"refinedProduct\":{\"productCharacteristics\":null,\"subProducts\":null},\"productPrice\":null}],\"isDowngrade\":null,\"productOfferingPrice\":[{\"id\":null,\"name\":\"RV Plan Mi Movistar S/55.9\",\"description\":null,\"isMandatory\":null,\"validFor\":null,\"priceType\":null,\"recurringChargePeriod\":null,\"unitOfMeasure\":null,\"price\":{\"amount\":47.37,\"units\":\"PEN\"},\"minPrice\":null,\"maxPrice\":null,\"taxAmount\":null,\"priceWithTax\":{\"amount\":55.9,\"units\":\"PEN\"},\"originalAmount\":null,\"originalTaxAmount\":null,\"taxIncluded\":null,\"taxRate\":0,\"taxType\":null,\"productOfferPriceAlteration\":null,\"pricedComponents\":null,\"priceLocation\":null,\"priceConsumer\":null,\"benefits\":null,\"additionalData\":[{\"key\":\"Nombre del Plan\",\"value\":\"Plan Mi Movistar S/55.9\"},{\"key\":\"Precio Mensual\",\"value\":\"S/ 55.9\"},{\"key\":\"Internet\",\"value\":\"16GB\"},{\"key\":\"Minutos Movistar\",\"value\":\"Ilimitado llamadas Peru, USA y Canada\"},{\"key\":\"Minutos por Destino\",\"value\":\"\"},{\"key\":\"SMS\",\"value\":\"Ilimitado\"},{\"key\":\"Minutos\",\"value\":\"\"},{\"key\":\"Limite de Consumo Adicional\",\"value\":\"\"},{\"key\":\"Beneficios del Plan\",\"value\":\"Datos Internacionales en America + WhatsApp Internacional solo texto \"},{\"key\":\"Llamadas Internacionales\",\"value\":\"\"},{\"key\":\"Amigos y Familia (DÃºos)\",\"value\":\"\"},{\"key\":\"Apps Ilimitadas\",\"value\":\"Grupo03\"}],\"productSpecContainmentID\":\"7491\",\"pricePlanSpecContainmentID\":\"4395113\"}],\"offeringPenalties\":null,\"upFront\":null,\"benefits\":null,\"additionalData\":null,\"currentPlanRelationID\":null,\"productOfferingProductSpecID\":\"4394743\"}],\"deviceOffering\":[{\"id\":\"2130333\",\"sapid\":\"TMGPEHUVTP10NES001\",\"brand\":\"HUAWEI\",\"model\":\"VTR-L09 P10\",\"gama\":\"Media Alta\",\"clasificacionComercial\":\"SmartphoneA+\",\"offers\":[{\"id\":null,\"name\":\"Control Vuela Movistar\",\"description\":\"Control Vuela Movistar\",\"billingOfferings\":[{\"id\":\"1814623\",\"name\":\"Plan Acceso Total S/20 C\",\"benefits\":[],\"commitmentPeriods\":[{\"name\":\"0\",\"financingInstalments\":[{\"codigo\":\"TELEFCONT\",\"description\":\"Contado\",\"instalments\":{\"type\":null,\"rate\":{\"value\":2032.2,\"currency\":\"PEN\",\"tax_included\":false},\"discount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"portDiscount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"postPagoFacil\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"amount\":{\"value\":2032.2,\"currency\":\"PEN\",\"tax_included\":false},\"recurrence\":\"monthly\",\"total_amount\":{\"value\":2398,\"currency\":\"PEN\",\"tax_included\":true},\"penalty_amount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"number_of_instalments\":null,\"opening_quota\":{\"value\":null,\"currency\":null,\"tax_included\":false},\"final_quota\":null,\"instalments_info\":[]}},{\"codigo\":\"TELEFCONT\",\"description\":\"Contado\",\"instalments\":{\"type\":null,\"rate\":{\"value\":1837.29,\"currency\":\"PEN\",\"tax_included\":false},\"discount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"portDiscount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"postPagoFacil\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"amount\":{\"value\":1837.29,\"currency\":\"PEN\",\"tax_included\":false},\"recurrence\":\"monthly\",\"total_amount\":{\"value\":2168,\"currency\":\"PEN\",\"tax_included\":true},\"penalty_amount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"number_of_instalments\":null,\"opening_quota\":{\"value\":null,\"currency\":null,\"tax_included\":false},\"final_quota\":null,\"instalments_info\":[]}},{\"codigo\":\"TELEFCONT\",\"description\":\"Contado\",\"instalments\":{\"type\":null,\"rate\":{\"value\":1667.8,\"currency\":\"PEN\",\"tax_included\":false},\"discount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"portDiscount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"postPagoFacil\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"amount\":{\"value\":1667.8,\"currency\":\"PEN\",\"tax_included\":false},\"recurrence\":\"monthly\",\"total_amount\":{\"value\":1968,\"currency\":\"PEN\",\"tax_included\":true},\"penalty_amount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"number_of_instalments\":null,\"opening_quota\":{\"value\":null,\"currency\":null,\"tax_included\":false},\"final_quota\":null,\"instalments_info\":[]}},{\"codigo\":\"TELEFCONT\",\"description\":\"Contado\",\"instalments\":{\"type\":null,\"rate\":{\"value\":2227.12,\"currency\":\"PEN\",\"tax_included\":false},\"discount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"portDiscount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"postPagoFacil\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"amount\":{\"value\":2227.12,\"currency\":\"PEN\",\"tax_included\":false},\"recurrence\":\"monthly\",\"total_amount\":{\"value\":2628,\"currency\":\"PEN\",\"tax_included\":true},\"penalty_amount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"number_of_instalments\":null,\"opening_quota\":{\"value\":null,\"currency\":null,\"tax_included\":false},\"final_quota\":null,\"instalments_info\":[]}},{\"codigo\":\"TELEFCONT\",\"description\":\"Contado\",\"instalments\":{\"type\":null,\"rate\":{\"value\":2049.15,\"currency\":\"PEN\",\"tax_included\":false},\"discount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"portDiscount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"postPagoFacil\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"amount\":{\"value\":2049.15,\"currency\":\"PEN\",\"tax_included\":false},\"recurrence\":\"monthly\",\"total_amount\":{\"value\":2418,\"currency\":\"PEN\",\"tax_included\":true},\"penalty_amount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"number_of_instalments\":null,\"opening_quota\":{\"value\":null,\"currency\":null,\"tax_included\":false},\"final_quota\":null,\"instalments_info\":[]}},{\"codigo\":\"TELEFCONT\",\"description\":\"Contado\",\"instalments\":{\"type\":null,\"rate\":{\"value\":2074.58,\"currency\":\"PEN\",\"tax_included\":false},\"discount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"portDiscount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"postPagoFacil\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"amount\":{\"value\":2074.58,\"currency\":\"PEN\",\"tax_included\":false},\"recurrence\":\"monthly\",\"total_amount\":{\"value\":2448,\"currency\":\"PEN\",\"tax_included\":true},\"penalty_amount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"number_of_instalments\":null,\"opening_quota\":{\"value\":null,\"currency\":null,\"tax_included\":false},\"final_quota\":null,\"instalments_info\":[]}},{\"codigo\":\"TELEFCONT\",\"description\":\"Contado\",\"instalments\":{\"type\":null,\"rate\":{\"value\":2227.12,\"currency\":\"PEN\",\"tax_included\":false},\"discount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"portDiscount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"postPagoFacil\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"amount\":{\"value\":2227.12,\"currency\":\"PEN\",\"tax_included\":false},\"recurrence\":\"monthly\",\"total_amount\":{\"value\":2628,\"currency\":\"PEN\",\"tax_included\":true},\"penalty_amount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"number_of_instalments\":null,\"opening_quota\":{\"value\":null,\"currency\":null,\"tax_included\":false},\"final_quota\":null,\"instalments_info\":[]}},{\"codigo\":\"TELEFCONT\",\"description\":\"Contado\",\"instalments\":{\"type\":null,\"rate\":{\"value\":1803.39,\"currency\":\"PEN\",\"tax_included\":false},\"discount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"portDiscount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"postPagoFacil\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"amount\":{\"value\":1803.39,\"currency\":\"PEN\",\"tax_included\":false},\"recurrence\":\"monthly\",\"total_amount\":{\"value\":2128,\"currency\":\"PEN\",\"tax_included\":true},\"penalty_amount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"number_of_instalments\":null,\"opening_quota\":{\"value\":null,\"currency\":null,\"tax_included\":false},\"final_quota\":null,\"instalments_info\":[]}}]},{\"name\":\"12\",\"financingInstalments\":[{\"codigo\":\"TELEFCONT\",\"description\":\"Contado\",\"instalments\":{\"type\":null,\"rate\":{\"value\":2032.2,\"currency\":\"PEN\",\"tax_included\":false},\"discount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"portDiscount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"postPagoFacil\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"amount\":{\"value\":2032.2,\"currency\":\"PEN\",\"tax_included\":false},\"recurrence\":\"monthly\",\"total_amount\":{\"value\":2398,\"currency\":\"PEN\",\"tax_included\":true},\"penalty_amount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"number_of_instalments\":null,\"opening_quota\":{\"value\":null,\"currency\":null,\"tax_included\":false},\"final_quota\":null,\"instalments_info\":[]}},{\"codigo\":\"TELEFCONT\",\"description\":\"Contado\",\"instalments\":{\"type\":null,\"rate\":{\"value\":1837.29,\"currency\":\"PEN\",\"tax_included\":false},\"discount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"portDiscount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"postPagoFacil\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"amount\":{\"value\":1837.29,\"currency\":\"PEN\",\"tax_included\":false},\"recurrence\":\"monthly\",\"total_amount\":{\"value\":2168,\"currency\":\"PEN\",\"tax_included\":true},\"penalty_amount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"number_of_instalments\":null,\"opening_quota\":{\"value\":null,\"currency\":null,\"tax_included\":false},\"final_quota\":null,\"instalments_info\":[]}},{\"codigo\":\"TELEFCONT\",\"description\":\"Contado\",\"instalments\":{\"type\":null,\"rate\":{\"value\":1667.8,\"currency\":\"PEN\",\"tax_included\":false},\"discount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"portDiscount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"postPagoFacil\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"amount\":{\"value\":1667.8,\"currency\":\"PEN\",\"tax_included\":false},\"recurrence\":\"monthly\",\"total_amount\":{\"value\":1968,\"currency\":\"PEN\",\"tax_included\":true},\"penalty_amount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"number_of_instalments\":null,\"opening_quota\":{\"value\":null,\"currency\":null,\"tax_included\":false},\"final_quota\":null,\"instalments_info\":[]}},{\"codigo\":\"TELEFCONT\",\"description\":\"Contado\",\"instalments\":{\"type\":null,\"rate\":{\"value\":2227.12,\"currency\":\"PEN\",\"tax_included\":false},\"discount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"portDiscount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"postPagoFacil\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"amount\":{\"value\":2227.12,\"currency\":\"PEN\",\"tax_included\":false},\"recurrence\":\"monthly\",\"total_amount\":{\"value\":2628,\"currency\":\"PEN\",\"tax_included\":true},\"penalty_amount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"number_of_instalments\":null,\"opening_quota\":{\"value\":null,\"currency\":null,\"tax_included\":false},\"final_quota\":null,\"instalments_info\":[]}},{\"codigo\":\"TELEFCONT\",\"description\":\"Contado\",\"instalments\":{\"type\":null,\"rate\":{\"value\":2049.15,\"currency\":\"PEN\",\"tax_included\":false},\"discount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"portDiscount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"postPagoFacil\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"amount\":{\"value\":2049.15,\"currency\":\"PEN\",\"tax_included\":false},\"recurrence\":\"monthly\",\"total_amount\":{\"value\":2418,\"currency\":\"PEN\",\"tax_included\":true},\"penalty_amount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"number_of_instalments\":null,\"opening_quota\":{\"value\":null,\"currency\":null,\"tax_included\":false},\"final_quota\":null,\"instalments_info\":[]}},{\"codigo\":\"TELEFCONT\",\"description\":\"Contado\",\"instalments\":{\"type\":null,\"rate\":{\"value\":2074.58,\"currency\":\"PEN\",\"tax_included\":false},\"discount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"portDiscount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"postPagoFacil\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"amount\":{\"value\":2074.58,\"currency\":\"PEN\",\"tax_included\":false},\"recurrence\":\"monthly\",\"total_amount\":{\"value\":2448,\"currency\":\"PEN\",\"tax_included\":true},\"penalty_amount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"number_of_instalments\":null,\"opening_quota\":{\"value\":null,\"currency\":null,\"tax_included\":false},\"final_quota\":null,\"instalments_info\":[]}},{\"codigo\":\"TELEFCONT\",\"description\":\"Contado\",\"instalments\":{\"type\":null,\"rate\":{\"value\":2227.12,\"currency\":\"PEN\",\"tax_included\":false},\"discount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"portDiscount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"postPagoFacil\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"amount\":{\"value\":2227.12,\"currency\":\"PEN\",\"tax_included\":false},\"recurrence\":\"monthly\",\"total_amount\":{\"value\":2628,\"currency\":\"PEN\",\"tax_included\":true},\"penalty_amount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"number_of_instalments\":null,\"opening_quota\":{\"value\":null,\"currency\":null,\"tax_included\":false},\"final_quota\":null,\"instalments_info\":[]}},{\"codigo\":\"TELEFCONT\",\"description\":\"Contado\",\"instalments\":{\"type\":null,\"rate\":{\"value\":1803.39,\"currency\":\"PEN\",\"tax_included\":false},\"discount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"portDiscount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"postPagoFacil\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"amount\":{\"value\":1803.39,\"currency\":\"PEN\",\"tax_included\":false},\"recurrence\":\"monthly\",\"total_amount\":{\"value\":2128,\"currency\":\"PEN\",\"tax_included\":true},\"penalty_amount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"number_of_instalments\":null,\"opening_quota\":{\"value\":null,\"currency\":null,\"tax_included\":false},\"final_quota\":null,\"instalments_info\":[]}}]}]}],\"instalments\":null,\"requires\":[],\"skus\":[],\"sales_channel_ids\":[],\"purchase_link\":null,\"valid_until\":null},{\"id\":null,\"name\":\"Control Vuela\",\"description\":\"Oferta planes control 4g\",\"billingOfferings\":[{\"id\":\"2013503\",\"name\":\"Movistar Internet 2MB (4G)\",\"benefits\":[],\"commitmentPeriods\":[{\"name\":\"0\",\"financingInstalments\":[{\"codigo\":\"TELEFCONT\",\"description\":\"Contado\",\"instalments\":{\"type\":null,\"rate\":{\"value\":2032.2,\"currency\":\"PEN\",\"tax_included\":false},\"discount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"portDiscount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"postPagoFacil\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"amount\":{\"value\":2032.2,\"currency\":\"PEN\",\"tax_included\":false},\"recurrence\":\"monthly\",\"total_amount\":{\"value\":2398,\"currency\":\"PEN\",\"tax_included\":true},\"penalty_amount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"number_of_instalments\":null,\"opening_quota\":{\"value\":null,\"currency\":null,\"tax_included\":false},\"final_quota\":null,\"instalments_info\":[]}},{\"codigo\":\"TELEFCONT\",\"description\":\"Contado\",\"instalments\":{\"type\":null,\"rate\":{\"value\":1837.29,\"currency\":\"PEN\",\"tax_included\":false},\"discount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"portDiscount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"postPagoFacil\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"amount\":{\"value\":1837.29,\"currency\":\"PEN\",\"tax_included\":false},\"recurrence\":\"monthly\",\"total_amount\":{\"value\":2168,\"currency\":\"PEN\",\"tax_included\":true},\"penalty_amount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"number_of_instalments\":null,\"opening_quota\":{\"value\":null,\"currency\":null,\"tax_included\":false},\"final_quota\":null,\"instalments_info\":[]}},{\"codigo\":\"TELEFCONT\",\"description\":\"Contado\",\"instalments\":{\"type\":null,\"rate\":{\"value\":1667.8,\"currency\":\"PEN\",\"tax_included\":false},\"discount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"portDiscount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"postPagoFacil\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"amount\":{\"value\":1667.8,\"currency\":\"PEN\",\"tax_included\":false},\"recurrence\":\"monthly\",\"total_amount\":{\"value\":1968,\"currency\":\"PEN\",\"tax_included\":true},\"penalty_amount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"number_of_instalments\":null,\"opening_quota\":{\"value\":null,\"currency\":null,\"tax_included\":false},\"final_quota\":null,\"instalments_info\":[]}},{\"codigo\":\"TELEFCONT\",\"description\":\"Contado\",\"instalments\":{\"type\":null,\"rate\":{\"value\":2227.12,\"currency\":\"PEN\",\"tax_included\":false},\"discount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"portDiscount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"postPagoFacil\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"amount\":{\"value\":2227.12,\"currency\":\"PEN\",\"tax_included\":false},\"recurrence\":\"monthly\",\"total_amount\":{\"value\":2628,\"currency\":\"PEN\",\"tax_included\":true},\"penalty_amount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"number_of_instalments\":null,\"opening_quota\":{\"value\":null,\"currency\":null,\"tax_included\":false},\"final_quota\":null,\"instalments_info\":[]}},{\"codigo\":\"TELEFCONT\",\"description\":\"Contado\",\"instalments\":{\"type\":null,\"rate\":{\"value\":2049.15,\"currency\":\"PEN\",\"tax_included\":false},\"discount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"portDiscount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"postPagoFacil\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"amount\":{\"value\":2049.15,\"currency\":\"PEN\",\"tax_included\":false},\"recurrence\":\"monthly\",\"total_amount\":{\"value\":2418,\"currency\":\"PEN\",\"tax_included\":true},\"penalty_amount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"number_of_instalments\":null,\"opening_quota\":{\"value\":null,\"currency\":null,\"tax_included\":false},\"final_quota\":null,\"instalments_info\":[]}},{\"codigo\":\"TELEFCONT\",\"description\":\"Contado\",\"instalments\":{\"type\":null,\"rate\":{\"value\":2074.58,\"currency\":\"PEN\",\"tax_included\":false},\"discount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"portDiscount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"postPagoFacil\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"amount\":{\"value\":2074.58,\"currency\":\"PEN\",\"tax_included\":false},\"recurrence\":\"monthly\",\"total_amount\":{\"value\":2448,\"currency\":\"PEN\",\"tax_included\":true},\"penalty_amount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"number_of_instalments\":null,\"opening_quota\":{\"value\":null,\"currency\":null,\"tax_included\":false},\"final_quota\":null,\"instalments_info\":[]}},{\"codigo\":\"TELEFCONT\",\"description\":\"Contado\",\"instalments\":{\"type\":null,\"rate\":{\"value\":2227.12,\"currency\":\"PEN\",\"tax_included\":false},\"discount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"portDiscount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"postPagoFacil\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"amount\":{\"value\":2227.12,\"currency\":\"PEN\",\"tax_included\":false},\"recurrence\":\"monthly\",\"total_amount\":{\"value\":2628,\"currency\":\"PEN\",\"tax_included\":true},\"penalty_amount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"number_of_instalments\":null,\"opening_quota\":{\"value\":null,\"currency\":null,\"tax_included\":false},\"final_quota\":null,\"instalments_info\":[]}},{\"codigo\":\"TELEFCONT\",\"description\":\"Contado\",\"instalments\":{\"type\":null,\"rate\":{\"value\":1803.39,\"currency\":\"PEN\",\"tax_included\":false},\"discount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"portDiscount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"postPagoFacil\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"amount\":{\"value\":1803.39,\"currency\":\"PEN\",\"tax_included\":false},\"recurrence\":\"monthly\",\"total_amount\":{\"value\":2128,\"currency\":\"PEN\",\"tax_included\":true},\"penalty_amount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"number_of_instalments\":null,\"opening_quota\":{\"value\":null,\"currency\":null,\"tax_included\":false},\"final_quota\":null,\"instalments_info\":[]}}]},{\"name\":\"12\",\"financingInstalments\":[{\"codigo\":\"TELEFCONT\",\"description\":\"Contado\",\"instalments\":{\"type\":null,\"rate\":{\"value\":2032.2,\"currency\":\"PEN\",\"tax_included\":false},\"discount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"portDiscount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"postPagoFacil\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"amount\":{\"value\":2032.2,\"currency\":\"PEN\",\"tax_included\":false},\"recurrence\":\"monthly\",\"total_amount\":{\"value\":2398,\"currency\":\"PEN\",\"tax_included\":true},\"penalty_amount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"number_of_instalments\":null,\"opening_quota\":{\"value\":null,\"currency\":null,\"tax_included\":false},\"final_quota\":null,\"instalments_info\":[]}},{\"codigo\":\"TELEFCONT\",\"description\":\"Contado\",\"instalments\":{\"type\":null,\"rate\":{\"value\":1837.29,\"currency\":\"PEN\",\"tax_included\":false},\"discount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"portDiscount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"postPagoFacil\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"amount\":{\"value\":1837.29,\"currency\":\"PEN\",\"tax_included\":false},\"recurrence\":\"monthly\",\"total_amount\":{\"value\":2168,\"currency\":\"PEN\",\"tax_included\":true},\"penalty_amount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"number_of_instalments\":null,\"opening_quota\":{\"value\":null,\"currency\":null,\"tax_included\":false},\"final_quota\":null,\"instalments_info\":[]}},{\"codigo\":\"TELEFCONT\",\"description\":\"Contado\",\"instalments\":{\"type\":null,\"rate\":{\"value\":1667.8,\"currency\":\"PEN\",\"tax_included\":false},\"discount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"portDiscount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"postPagoFacil\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"amount\":{\"value\":1667.8,\"currency\":\"PEN\",\"tax_included\":false},\"recurrence\":\"monthly\",\"total_amount\":{\"value\":1968,\"currency\":\"PEN\",\"tax_included\":true},\"penalty_amount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"number_of_instalments\":null,\"opening_quota\":{\"value\":null,\"currency\":null,\"tax_included\":false},\"final_quota\":null,\"instalments_info\":[]}},{\"codigo\":\"TELEFCONT\",\"description\":\"Contado\",\"instalments\":{\"type\":null,\"rate\":{\"value\":2227.12,\"currency\":\"PEN\",\"tax_included\":false},\"discount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"portDiscount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"postPagoFacil\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"amount\":{\"value\":2227.12,\"currency\":\"PEN\",\"tax_included\":false},\"recurrence\":\"monthly\",\"total_amount\":{\"value\":2628,\"currency\":\"PEN\",\"tax_included\":true},\"penalty_amount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"number_of_instalments\":null,\"opening_quota\":{\"value\":null,\"currency\":null,\"tax_included\":false},\"final_quota\":null,\"instalments_info\":[]}},{\"codigo\":\"TELEFCONT\",\"description\":\"Contado\",\"instalments\":{\"type\":null,\"rate\":{\"value\":2049.15,\"currency\":\"PEN\",\"tax_included\":false},\"discount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"portDiscount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"postPagoFacil\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"amount\":{\"value\":2049.15,\"currency\":\"PEN\",\"tax_included\":false},\"recurrence\":\"monthly\",\"total_amount\":{\"value\":2418,\"currency\":\"PEN\",\"tax_included\":true},\"penalty_amount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"number_of_instalments\":null,\"opening_quota\":{\"value\":null,\"currency\":null,\"tax_included\":false},\"final_quota\":null,\"instalments_info\":[]}},{\"codigo\":\"TELEFCONT\",\"description\":\"Contado\",\"instalments\":{\"type\":null,\"rate\":{\"value\":2074.58,\"currency\":\"PEN\",\"tax_included\":false},\"discount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"portDiscount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"postPagoFacil\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"amount\":{\"value\":2074.58,\"currency\":\"PEN\",\"tax_included\":false},\"recurrence\":\"monthly\",\"total_amount\":{\"value\":2448,\"currency\":\"PEN\",\"tax_included\":true},\"penalty_amount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"number_of_instalments\":null,\"opening_quota\":{\"value\":null,\"currency\":null,\"tax_included\":false},\"final_quota\":null,\"instalments_info\":[]}},{\"codigo\":\"TELEFCONT\",\"description\":\"Contado\",\"instalments\":{\"type\":null,\"rate\":{\"value\":2227.12,\"currency\":\"PEN\",\"tax_included\":false},\"discount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"portDiscount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"postPagoFacil\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"amount\":{\"value\":2227.12,\"currency\":\"PEN\",\"tax_included\":false},\"recurrence\":\"monthly\",\"total_amount\":{\"value\":2628,\"currency\":\"PEN\",\"tax_included\":true},\"penalty_amount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"number_of_instalments\":null,\"opening_quota\":{\"value\":null,\"currency\":null,\"tax_included\":false},\"final_quota\":null,\"instalments_info\":[]}},{\"codigo\":\"TELEFCONT\",\"description\":\"Contado\",\"instalments\":{\"type\":null,\"rate\":{\"value\":1803.39,\"currency\":\"PEN\",\"tax_included\":false},\"discount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"portDiscount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"postPagoFacil\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"amount\":{\"value\":1803.39,\"currency\":\"PEN\",\"tax_included\":false},\"recurrence\":\"monthly\",\"total_amount\":{\"value\":2128,\"currency\":\"PEN\",\"tax_included\":true},\"penalty_amount\":{\"value\":0,\"currency\":\"PEN\",\"tax_included\":false},\"number_of_instalments\":null,\"opening_quota\":{\"value\":null,\"currency\":null,\"tax_included\":false},\"final_quota\":null,\"instalments_info\":[]}}]}]}],\"instalments\":null,\"requires\":[],\"skus\":[],\"sales_channel_ids\":[],\"purchase_link\":null,\"valid_until\":null}],\"stock\":{\"item\":{\"id\":\"TMGPEHUVTP10NES001\",\"type\":\"NUEVO\",\"name\":\"HUAWEI P10 NEGRO\",\"condition\":null,\"SKU\":null},\"site\":{\"id\":\"XU30\",\"href\":null,\"name\":null,\"@referredType\":null},\"amount\":{\"amount\":1,\"maximum\":0,\"minimum\":0,\"units\":\"units\"},\"reservationId\":null},\"additionalData\":[{\"key\":\"Dimensions\",\"value\":\"145.3 mm x 69.3 mm x 6.98 mm\"},{\"key\":\"OsVersion\",\"value\":\"Version: 7\"},{\"key\":\"Processor\",\"value\":\"Procesador de 4 x 2.4 GHz + 4 x 1.8 GHz , 8 nucleos\"},{\"key\":\"Model\",\"value\":\"VTR-L09 P10\"},{\"key\":\"GroupId\",\"value\":\"35\"},{\"key\":\"GroupName\",\"value\":\"HUAWEI VTR-L09 P10 LTE\"},{\"key\":\"Weight\",\"value\":\"Aprox. 145 gr.\"},{\"key\":\"Email\",\"value\":\"Si\"},{\"key\":\"Code\",\"value\":\"TMGPEHUVTP10NES001\"},{\"key\":\"Sapid\",\"value\":\"TMGPEHUVTP10NES001\"},{\"key\":\"Colour\",\"value\":\"NEGRO\"},{\"key\":\"ColourCode\",\"value\":\"#000000\"},{\"key\":\"Displayname\",\"value\":\"HUAWEI P10 NEGRO\"},{\"key\":\"ProductPriceRelation\",\"value\":\"\"},{\"key\":\"ValidFrom\",\"value\":\"5/04/2017\"},{\"key\":\"ValidUntil\",\"value\":\"1/01/2030\"},{\"key\":\"ChannelName\",\"value\":null},{\"key\":\"DeviceType\",\"value\":\"Smartphone\"},{\"key\":\"SimType\",\"value\":\"Seleccionar un tipo de SIM\"},{\"key\":\"Brand\",\"value\":\"HUAWEI\"},{\"key\":\"Lot\",\"value\":\"NUEVO\"},{\"key\":\"ClasificacionComercial\",\"value\":\"SmartphoneA+\"},{\"key\":\"GamaSAP\",\"value\":\"A+\"},{\"key\":\"GamaSafe\",\"value\":\"Media Alta\"},{\"key\":\"CostoPromedioSinIGVSoles\",\"value\":\"1784.8\"},{\"key\":\"ConectivityFrequencyBand\",\"value\":\"2G: GSM / GPRS / EDGE, 850 / 900 / 1800 / 1900, 3G: HSDPA / HSUPA / HSPA+ , 850 / 900 / 1700 /1900 / 2100, 4G: Bandas: B1, B2, B3, B4, B5, B7, B8, B9, B12, B17, B18, B19, B20, B26, B28, B29, B38, B39, B40\"},{\"key\":\"ConectivityNetworkTechnology\",\"value\":\"4G\"},{\"key\":\"SimGroup\",\"value\":\"nanoSIM\"},{\"key\":\"OsType\",\"value\":\"Android\"},{\"key\":\"KeyboardType\",\"value\":\"QWERTY virtual\"},{\"key\":\"StorageRam\",\"value\":\"4 GB\"},{\"key\":\"StorageInternal\",\"value\":\"Capacidad total: 32 GB , Capacidad para usuario: 18 GB\"},{\"key\":\"StorageExternal\",\"value\":\"256 GB\"},{\"key\":\"ScreenType\",\"value\":\"Pantalla tactil: Si\"},{\"key\":\"ScreenSize\",\"value\":\"Tama�o: 5.1\"},{\"key\":\"ScreenResolution\",\"value\":\"1920 x 1080\"},{\"key\":\"BatteryDuration\",\"value\":\"18h\"},{\"key\":\"BatterySize\",\"value\":\"Li-Ion de 3200 mAh, Bateria interna no extraible\"},{\"key\":\"CameraRearResolution\",\"value\":\"20 MP + 12 MP\"},{\"key\":\"CameraRearZoom\",\"value\":\"10x\"},{\"key\":\"CameraFrontResolution\",\"value\":\"8 MP\"},{\"key\":\"CameraFrontZoom\",\"value\":\"NA\"},{\"key\":\"CameraRearFlash\",\"value\":\"Flash incorporado: Si\"},{\"key\":\"CameraOtherCharacteristics\",\"value\":\"Monocromatico, Toma nocturna, Pintura con luz, Camara rapida, Camara lenta, Marca de agua, Nota de audio, Escaneo de documentos.\"},{\"key\":\"VideoRecording\",\"value\":\"Si\"},{\"key\":\"VideoResolution\",\"value\":\"4K\"},{\"key\":\"VideoQuality\",\"value\":\"Grabacion en alta definicion (HD): Si\"},{\"key\":\"Warranty\",\"value\":\"12 meses\"},{\"key\":\"ConectivityBluetooth\",\"value\":\"Si\"},{\"key\":\"ConectivityGps\",\"value\":\"Si\"},{\"key\":\"ConectivityWifi\",\"value\":\"Si\"},{\"key\":\"ConectivityWifiZone\",\"value\":\"Si\"},{\"key\":\"ConectivityNfc\",\"value\":\"Si\"},{\"key\":\"ConectivityPc\",\"value\":\"Si\"},{\"key\":\"Sms\",\"value\":\"Si\"},{\"key\":\"Mms\",\"value\":\"Si\"},{\"key\":\"Aplications\",\"value\":\"Facebook: Twitter: Whatsapp: Disponible en la tienda de aplicaciones Youtube: Google+: Pre-cargado : Google Maps: Pre-cargado Tienda de aplicaciones: Play Store , Napster, Mi Movistar, TU Go, Futbol Movistar\"},{\"key\":\"ConectivityFm\",\"value\":\"No\"},{\"key\":\"MusicPlayer\",\"value\":\"Si\"},{\"key\":\"TvTuner\",\"value\":\"No\"},{\"key\":\"ConectivityInfrared\",\"value\":\"No\"},{\"key\":\"VoiceDialing\",\"value\":\"No\"},{\"key\":\"VoiceRecorder\",\"value\":\"Si\"},{\"key\":\"ShippingAllowed\",\"value\":\"VERDADERO\"},{\"key\":\"MarkdownIndicator\",\"value\":\"FALSO\"},{\"key\":\"ImageUrl\",\"value\":\"http://www.movistar.com.pe/documents/80379/5583109/HUAWEI+VTR-L09+P10+LTE_Negro_Big_Imagen.png/a291f8e4-8745-4ae4-a1c1-c48205cbcda2?t=1496187956964\"},{\"key\":\"Lvtxt\",\"value\":\"NA\"},{\"key\":\"Add\",\"value\":\"NA\"},{\"key\":\"ImageShortUrl\",\"value\":\"http://www.movistar.com.pe/documents/80379/5583109/HUAWEI+VTR-L09+P10+LTE_Negro_Thumb_Imagen.png/8aac2290-3591-4744-ad56-6df4a8b7b99d?t=1496187957355\"},{\"key\":\"Id\",\"value\":\"2130333\"},{\"key\":\"Class\",\"value\":\"com.tdp.ms.device.model.entity.Device\"}],\"device_type\":\"Smartphone\",\"display_name\":\"HUAWEI P10 NEGRO\",\"costoPromedioSinIGVSoles\":\"1784.8\",\"sim_specifications\":[{\"sapid\":\"TSPE4128234R510P01\",\"type\":\"nanoSIM\",\"description\":\"TSPE4128234R510P01 USIM 4G 128K 2/3/4FF R5 PACK\",\"price\":[{\"value\":12.72,\"currency\":\"PEN\",\"tax_included\":false},{\"value\":15.01,\"currency\":\"PEN\",\"tax_included\":true}]}]}],\"additionalData\":[{\"key\":\"CAEQ\",\"value\":\"true\"},{\"key\":\"CAPL\",\"value\":\"true\"},{\"key\":\"CASI\",\"value\":\"true\"},{\"key\":\"multiChanges\",\"value\":\"Y\"}]}],\"estimatedRevenue\":null,\"paymenType\":{\"additionalData\":[{\"key\":\"paymentDocument\",\"value\":\"Boleta\"}]},\"prospectContact\":[{\"characteristic\":{\"emailAddress\":\"a@prueba.com\"},\"mediumType\":\"email address\"}],\"relatedParty\":[{\"id\":null,\"href\":null,\"fullName\":\"LUIS ENRIQUE RIVEROS ORDOÃ\u0091EZ\",\"firstName\":null,\"lastName\":null,\"customerId\":\"57579048\",\"role\":\"customer\",\"accountId\":null,\"billingArragmentId\":\"\",\"score\":{\"scoreId\":null,\"score\":\"9999\",\"numberLinesScore\":null,\"restrictionsScore\":null,\"actionScore\":null,\"financingCapacity\":\"999\",\"purchaseLimit\":\"6235\",\"MessageScore\":null},\"nationalID\":\"42786713\",\"nationalIDType\":\"DNI\"}],\"saleCreationDate\":\"17/12/2020T11:08:21\",\"status\":\"VALIDADO\",\"statusChangeDate\":null,\"statusChangeReason\":null,\"audioStatus\":null,\"identityValidations\":[{\"validationType\":\"noBiometric\",\"isValidation\":true,\"date\":\"2020-12-17T16:34:21.142Z\",\"additionalData\":[{\"key\":\"string\",\"value\":\"string\"}]}],\"audioUrl\":null,\"validFor\":null,\"additionalData\":[{\"key\":\"warehouse\",\"value\":\"XU30\"},{\"key\":\"flowSale\",\"value\":\"Retail\"},{\"key\":\"userSimple\",\"value\":\"\"},{\"key\":\"Agent.neighborhoodCode\",\"value\":\"15\"},{\"key\":\"Agent.stateOrProvince\",\"value\":\"01\"},{\"key\":\"deliveryMethod\",\"value\":\"SP\"},{\"key\":\"DELIVERY_METHOD\",\"value\":\"IS\"},{\"key\":\"PAYMENT_METHOD\",\"value\":\"EX\"},{\"key\":\"DEVICE_SKU\",\"value\":\"3432432423423432\"},{\"key\":\"SIM_SKU\",\"value\":\"3432432423423432\"},{\"key\":\"MOVILE_IMEI\",\"value\":\"123900000015969\"},{\"key\":\"NUMERO_CAJA\",\"value\":\"3432432423423432\"},{\"key\":\"NUMERO_TICKET\",\"value\":\"3432432423423432\"},{\"key\":\"SIM_ICCID\",\"value\":\"3432432423423432\"}]}");
    private static BusinessParametersReasonCode businessParametersReasonCode;

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

        businessParametersReasonCode = BusinessParametersReasonCode.builder()
                .data(Arrays.asList(BusinessParameterReasonCodeData.builder()
                        .ext(Arrays.asList(ReasonCodeExt.builder()
                                .casi(false)
                                .caeq(false)
                                .capl(false)
                                .build()))
                        .build()))
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

        Mockito.when(businessParameterWebClient.getParametersReasonCode(any()))
                .thenReturn(Mono.just(businessParametersReasonCode));

        ProductorderResponse productorderResponse = new ProductorderResponse();
        CreateProductOrderResponseType createProductOrderResponseType =  new CreateProductOrderResponseType();
        productorderResponse.setCreateProductOrderResponse(createProductOrderResponseType);
        Mockito.when(productOrderWebClient.createProductOrder(any(), eq(salesRequest.getHeadersMap()), any()))
                .thenReturn(Mono.just(productorderResponse));

        Mockito.when(salesRepository.findBySalesId(any())).thenReturn(Mono.just(sale));
        Mockito.when(salesRepository.save(any())).thenReturn(Mono.just(sale));

        salesManagmentService.post(salesRequest);

        Method methodMainFuntion = SalesManagmentServiceImpl.class.getDeclaredMethod("mainFunction", Sale.class, PostSalesRequest.class,
                Boolean[].class, Boolean[].class, Boolean[].class, Boolean[].class, Boolean[].class, Boolean.class, String[].class);
        methodMainFuntion.setAccessible(true);
        final Boolean[] flag = {false};
        final Boolean[] flagTrue = {true};
        final Boolean[] flgFinanciamiento = {false};
        final String[] sapidSimcard = {""};
        methodMainFuntion.invoke(salesManagmentServiceImpl, sale, salesRequest, flagTrue, flag, flag, flag, flag, false, sapidSimcard);

        salesRequest.getSale().getAdditionalData().stream()
                .filter(item -> item.getKey().equalsIgnoreCase("ufxauthorization"))
                .findFirst()
                .ifPresent(item -> item.setValue(""));
        salesManagmentService.post(salesRequest);
        methodMainFuntion.invoke(salesManagmentServiceImpl, sale, salesRequest, flagTrue, flag, flag, flag, flag, false, sapidSimcard);

        salesRequest.getSale().setProductType("cualquier otra cosa");
        salesManagmentService.post(salesRequest);
        methodMainFuntion.invoke(salesManagmentServiceImpl, sale, salesRequest, flagTrue, flag, flag, flag, flag, false, sapidSimcard);

        salesRequest.getSale().setProductType("WIRELESS");
        salesManagmentService.post(salesRequest);
        methodMainFuntion.invoke(salesManagmentServiceImpl, sale, salesRequest, flagTrue, flag, flag, flag, flag, false, sapidSimcard);

        // Segundo IF
        salesRequest.getSale().getCommercialOperation().get(0).getOrder().setProductOrderId("string");
        salesRequest.getSale().getCommercialOperation().get(0).getDeviceOffering().get(0).getStock().setReservationId("string");
        salesManagmentService.post(salesRequest);
        methodMainFuntion.invoke(salesManagmentServiceImpl, sale, salesRequest, flagTrue, flag, flag, flag, flag, false, sapidSimcard);

        Method method = SalesManagmentServiceImpl.class.getDeclaredMethod("processFija", List.class, Sale.class,
                PostSalesRequest.class, Boolean.class);
        method.setAccessible(true);
        salesRequest.getSale().setProductType(Constants.WIRELINE);
        method.invoke(salesManagmentServiceImpl, bpFinanciamientoFijaResponseList, salesRequest.getSale(), salesRequest, false);

        salesRequest.getSale().setProductType("WIRELESS");
        /* validationsAndBuildings method */
        Method method2 = SalesManagmentServiceImpl.class.getDeclaredMethod("validationsAndBuildings",
                BusinessParametersResponse.class, List.class, BusinessParametersResponseObjectExt.class,
                BusinessParametersResponseObjectExt.class, BusinessParametersReasonCode.class, Sale.class, PostSalesRequest.class, String[].class, String.class,
                Boolean[].class, Boolean[].class, Boolean[].class, Boolean[].class, Boolean[].class, String.class, String.class, String.class, Boolean.class);
        method2.setAccessible(true);

        BusinessParametersResponse getRiskDomain = MapperUtils.mapper(BusinessParametersResponse.class, "{\"metadata\":{\"info\":\"Dominios de Riesgos SPAN\",\"type\":\"KeyValueActive\",\"label\":{\"key\":\"id\",\"value\":\"nombreDominio\",\"active\":\"estado\",\"ext\":\"-\"}},\"data\":[{\"key\":\"430\",\"value\":\"plusmail.cf\",\"active\":false,\"ext\":\"-\"}]}");
        BusinessParametersResponse getRiskDomainTrue = MapperUtils.mapper(BusinessParametersResponse.class, "{\"metadata\":{\"info\":\"Dominios de Riesgos SPAN\",\"type\":\"KeyValueActive\",\"label\":{\"key\":\"id\",\"value\":\"nombreDominio\",\"active\":\"estado\",\"ext\":\"-\"}},\"data\":[{\"key\":\"430\",\"value\":\"plusmail.cf\",\"active\":true,\"ext\":\"-\"}]}");
        BusinessParametersResponseObjectExt getBonificacionSim = MapperUtils.mapper(BusinessParametersResponseObjectExt.class, "{\"metadata\":{\"info\":\"Códigos de bonificación\",\"type\":\"KeyValueActiveExt\",\"label\":{\"key\":\"channel\",\"value\":\"productSpecPricingID\",\"active\":\"active\",\"ext\":\"parentProductCatalogID\"}},\"data\":[{\"key\":\"CC\",\"value\":\"34572615\",\"active\":true,\"ext\":\"7431\"}]}");
        BusinessParametersResponseObjectExt getParametersSimCard = MapperUtils.mapper(BusinessParametersResponseObjectExt.class, "{\"metadata\":{\"info\":\"Parámetros del simcard para sales\",\"type\":\"KeyValueActiveExt\",\"label\":{\"key\":\"codParam\",\"value\":\"desParam\",\"active\":\"active\",\"ext\":\"-\"}},\"data\":[{\"key\":\"sku\",\"value\":\"SKU0001\",\"active\":true,\"ext\":\" \"},{\"key\":\"sapid\",\"value\":\"TSPE4128234R510201\",\"active\":true,\"ext\":\"-\"}]}");

        String commercialOperationReason = "PORTA";
        String channelIdRequest = salesRequest.getSale().getChannel().getId();
        String customerIdRequest = salesRequest.getSale().getRelatedParty().get(0).getCustomerId();
        String productOfferingIdRequest = salesRequest.getSale().getCommercialOperation()
                .get(0).getProductOfferings().get(0).getId();
        method2.invoke(salesManagmentServiceImpl, getRiskDomainTrue, Arrays.asList(BusinessParameterExt.builder().build()),
                getBonificacionSim, getParametersSimCard, businessParametersReasonCode, salesRequest.getSale(), salesRequest, sapidSimcard, commercialOperationReason,
                flag, flag, flag, flag, flag, channelIdRequest, customerIdRequest, productOfferingIdRequest, false);
        Sale sale1 = CommonsMocks.createSaleMock();
        salesRequest.setSale(sale1);
        method2.invoke(salesManagmentServiceImpl, getRiskDomain, Arrays.asList(BusinessParameterExt.builder().build()),
                getBonificacionSim, getParametersSimCard, businessParametersReasonCode, salesRequest.getSale(), salesRequest, sapidSimcard, commercialOperationReason,
                flag, flag, flag, flag, flag, channelIdRequest, customerIdRequest, productOfferingIdRequest, false);
    }

    @Test
    void retryRequest_Test() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ReserveStockResponse reserveStockResponse =  new ReserveStockResponse();
        Mockito.when(stockWebClient.reserveStock(any(), any(), any())).thenReturn(Mono.just(reserveStockResponse));

        PostSalesRequest salesRequest = PostSalesRequest
                .builder()
                .sale(sale)
                .headersMap(headersMap)
                .build();

        Method method = SalesManagmentServiceImpl.class.getDeclaredMethod("retryRequest", PostSalesRequest.class,
                Sale.class, Boolean.class, Boolean.class, Boolean.class, Boolean.class, String.class);
        method.setAccessible(true);
        Sale sale = CommonsMocks.createSaleMock2();

        Mockito.when(salesRepository.save(any())).thenReturn(Mono.just(sale));

        method.invoke(salesManagmentServiceImpl, salesRequest, sale, true, true, false, false, "");
        method.invoke(salesManagmentServiceImpl, salesRequest, sale, true, true, true, false, "");
        method.invoke(salesManagmentServiceImpl, salesRequest, sale, false, false, false, false, "");
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

        Mockito.when(businessParameterWebClient.getParametersReasonCode(any()))
                .thenReturn(Mono.just(businessParametersReasonCode));

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
        saleTest.setStatus("VALIDADO");
        saleTest.setAdditionalData(Arrays.asList(KeyValueType.builder().key("flowSale").value("Retail").build()));

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

        KeyValueType additionalData1 = new KeyValueType();
        additionalData1.setKey("MOVILE_IMEI");
        additionalData1.setValue("test");
        saleTest.getAdditionalData().add(additionalData1);
        saleTest.setStatus("VALIDADO");

        saleTest.getAdditionalData().stream()
                .filter(item -> item.getKey().equalsIgnoreCase("flowSale"))
                .findFirst()
                .ifPresent(item -> item.setValue("Retail"));

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

        KeyValueType additionalData1 = new KeyValueType();
        additionalData1.setKey("MOVILE_IMEI");
        additionalData1.setValue("test");
        saleTest.getAdditionalData().add(additionalData1);

        KeyValueType additionalData2 = new KeyValueType();
        additionalData2.setKey("SIM_ICCID");
        additionalData2.setValue("test");
        saleTest.getAdditionalData().add(additionalData2);
        saleTest.setStatus("VALIDADO");

        saleTest.getAdditionalData().stream()
                .filter(item -> item.getKey().equalsIgnoreCase("flowSale"))
                .findFirst()
                .ifPresent(item -> item.setValue("Retail"));

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
        saleTest.setStatus("VALIDADO");

        saleTest.getAdditionalData().stream()
                .filter(item -> item.getKey().equalsIgnoreCase("flowSale"))
                .findFirst()
                .ifPresent(item -> item.setValue("Retail"));

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
        BusinessParametersResponseObjectExt getBonificacionSim = MapperUtils.mapper(BusinessParametersResponseObjectExt.class, "{\"metadata\":{\"info\":\"Códigos de bonificación\",\"type\":\"KeyValueActiveExt\",\"label\":{\"key\":\"channel\",\"value\":\"productSpecPricingID\",\"active\":\"active\",\"ext\":\"parentProductCatalogID\"}},\"data\":[{\"key\":\"CC\",\"value\":\"34572615\",\"active\":true,\"ext\":\"7431\"}]}");

        CreateProductOrderGeneralRequest mainCaplRequestProductOrder = new CreateProductOrderGeneralRequest();

        CreateProductOrderGeneralRequest result = salesManagmentServiceImpl
                .caplCommercialOperation(sale, mainCaplRequestProductOrder,
                        "CC", "CS465", "OF824", "A83HD345DS",
                        getBonificacionSim);

    }

    @Test
    void caeqCommercialOperationTest() {
        BusinessParametersResponseObjectExt getBonificacionSim = MapperUtils.mapper(BusinessParametersResponseObjectExt.class, "{\"metadata\":{\"info\":\"Códigos de bonificación\",\"type\":\"KeyValueActiveExt\",\"label\":{\"key\":\"channel\",\"value\":\"productSpecPricingID\",\"active\":\"active\",\"ext\":\"parentProductCatalogID\"}},\"data\":[{\"key\":\"CC\",\"value\":\"34572615\",\"active\":true,\"ext\":\"7431\"}]}");

        CreateProductOrderGeneralRequest mainCaeqRequestProductOrder = new CreateProductOrderGeneralRequest();
        sale.getCommercialOperation().get(0).setReason("CAEQ");
        CreateProductOrderGeneralRequest result = salesManagmentServiceImpl
                .caeqCommercialOperation(sale, mainCaeqRequestProductOrder, false,
                        "CEC", "CS920", "OF201", "JSG423DE6H",
                        "string", businessParametersReasonCode, getBonificacionSim);

        /*CreateProductOrderGeneralRequest mainCaeqRequestProductOrderCaeqCasi = new CreateProductOrderGeneralRequest();
        salesManagmentServiceImpl.caeqCommercialOperation(saleCaeqCaplCasi, mainCaeqRequestProductOrderCaeqCasi,
                true, saleCaeqCaplCasi.getChannel().getId(), "CS920", "OF201", "JSG423DE6H", "string");*/
    }

    @Test
    void caeqCaplCommercialOperationTest() {
        CreateProductOrderGeneralRequest mainCaeqCaplRequestProductOrder = new CreateProductOrderGeneralRequest();
        sale.getCommercialOperation().get(0).setReason("CAEQ");

        BusinessParametersResponseObjectExt getBonificacionSim = MapperUtils.mapper(BusinessParametersResponseObjectExt.class, "{\"metadata\":{\"info\":\"Códigos de bonificación\",\"type\":\"KeyValueActiveExt\",\"label\":{\"key\":\"channel\",\"value\":\"productSpecPricingID\",\"active\":\"active\",\"ext\":\"parentProductCatalogID\"}},\"data\":[{\"key\":\"CC\",\"value\":\"34572615\",\"active\":true,\"ext\":\"7431\"}]}");

        CreateProductOrderGeneralRequest result = salesManagmentServiceImpl
                .caeqCaplCommercialOperation(sale, mainCaeqCaplRequestProductOrder, false,
                        "CC", "CS158", "OF486", "K3BD9EN349",
                        "string", businessParametersReasonCode, getBonificacionSim);
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

        salesManagmentServiceImpl.changedContainedCaeqList(sale, "temp1", "string", false);

        // deviceOfferings con solo un objeto
        sale.getCommercialOperation().get(0).setDeviceOffering(Collections.singletonList(sale.getCommercialOperation().get(0).getDeviceOffering().get(0)));
        salesManagmentServiceImpl.changedContainedCaeqList(sale, "temp1", "string", false);
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
                .newProductsInNewOfferings(Arrays.asList(NewProductInNewOfferingInstanceConfigurationType.builder()
                        .productOrderItemReferenceNumber("123456789A")
                        .build()))
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
        sale.setCommercialOperation(Collections.singletonList(CommercialOperationType.builder()
                .reason("CAPL")
                .build()));

        PostSalesRequest postSalesRequest = PostSalesRequest
                .builder()
                .sale(sale)
                .headersMap(headersMap)
                .build();

        ReceptorResponse receptorResponse =  new ReceptorResponse();
        Mockito.when(webClientReceptor.register(any(), any())).thenReturn(Mono.just(receptorResponse));

        method.invoke(salesManagmentServiceImpl, postSalesRequest);

        postSalesRequest.getSale().getCommercialOperation().get(0).setReason("CAEQ");
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
                .newProductsInNewOfferings(Arrays.asList(NewProductInNewOfferingInstanceConfigurationType.builder()
                        .productOrderItemReferenceNumber("123456789A")
                        .build()))
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

        Mockito.when(businessParameterWebClient.getParametersReasonCode(any()))
                .thenReturn(Mono.just(businessParametersReasonCode));

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
                PostSalesRequest.class, String.class, Boolean.class);
        method.setAccessible(true);

        method.invoke(salesManagmentServiceImpl, bpFinanciamientoFijaResponseList.get(0).getData().get(0).getExt(),
                salesRequest, "CH", false);

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
                Sale.class, CreateQuotationRequest.class, ProductorderResponse.class);
        method.setAccessible(true);

        method.invoke(salesManagmentServiceImpl, salesRequest, sale,
                new CreateQuotationRequest(), ProductorderResponse.builder().createProductOrderResponse(CreateProductOrderResponseType.builder().productOrderId("string").build()).build());

        sale.setIdentityValidations(null);
        method.invoke(salesManagmentServiceImpl, salesRequest, sale,
                new CreateQuotationRequest(), ProductorderResponse.builder().createProductOrderResponse(CreateProductOrderResponseType.builder().productOrderId("string").build()).build());
        method.invoke(salesManagmentServiceImpl, salesRequest, sale,
                new CreateQuotationRequest(), ProductorderResponse.builder().createProductOrderResponse(CreateProductOrderResponseType.builder().productOrderId("").build()).build());
    }

    @Test
    void deviceOfferingIsNullOrEmpty_Test() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = SalesManagmentServiceImpl.class.getDeclaredMethod("deviceOfferingIsNullOrEmpty", Sale.class);
        method.setAccessible(true);
        saleCaeqCaplCasi.getCommercialOperation().get(0).getDeviceOffering().get(0).setStock(null);
        method.invoke(salesManagmentServiceImpl, saleCaeqCaplCasi);
        saleCaeqCaplCasi.getCommercialOperation().get(0).setDeviceOffering(new ArrayList<>());
        method.invoke(salesManagmentServiceImpl, saleCaeqCaplCasi);
        saleCaeqCaplCasi.getCommercialOperation().get(0).setDeviceOffering(null);
        method.invoke(salesManagmentServiceImpl, saleCaeqCaplCasi);
    }
    @Test
    void changedContainedCaeqList_Test() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = SalesManagmentServiceImpl.class.getDeclaredMethod("changedContainedCaeqList", Sale.class,
                String.class, String.class, Boolean.class);
        method.setAccessible(true);
        method.invoke(salesManagmentServiceImpl, saleCaeqCaplCasi, "string", "string", true);
    }
}