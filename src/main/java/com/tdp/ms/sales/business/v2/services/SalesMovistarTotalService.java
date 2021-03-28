package com.tdp.ms.sales.business.v2.services;

import com.tdp.ms.sales.client.GetSkuWebClient;
import com.tdp.ms.sales.client.ProductOrderWebClient;
import com.tdp.ms.sales.client.QuotationWebClient;
import com.tdp.ms.sales.client.StockWebClient;
import com.tdp.ms.sales.model.dto.*;
import com.tdp.ms.sales.model.dto.productorder.CreateProductOrderGeneralRequest;
import com.tdp.ms.sales.model.dto.productorder.FlexAttrType;
import com.tdp.ms.sales.model.dto.productorder.altafija.ServiceabilityInfoType;
import com.tdp.ms.sales.model.dto.productorder.caeq.ChangedContainedProduct;
import com.tdp.ms.sales.model.dto.productorder.capl.NewAssignedBillingOffers;
import com.tdp.ms.sales.model.entity.Sale;
import com.tdp.ms.sales.model.request.GetSalesRequest;
import com.tdp.ms.sales.model.request.PostSalesRequest;
import com.tdp.ms.sales.model.request.ReserveStockRequest;
import com.tdp.ms.sales.model.response.BusinessParametersResponseObjectExt;
import com.tdp.ms.sales.model.response.ReserveStockResponse;
import com.tdp.ms.sales.repository.SalesRepository;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;

/**
 * Class: SalesMovistarTotalService. <br/>
 * <b>Copyright</b>: &copy; 2021 Telef&oacute;nica del Per&uacute;<br/>
 * <b>Company</b>: Telef&oacute;nica del Per&uacute;<br/>
 *
 * @author Telef&oacute;nica del Per&uacute; (TDP) <br/>
 *         <u>Service Provider</u>: Everis Per&uacute; SAC (EVE) <br/>
 *         <u>Developed by</u>: <br/>
 *         <ul>
 *         <li>Sergio Rivas</li>
 *         </ul>
 *         <u>Changes</u>:<br/>
 *         <ul>
 *         <li>2021-03-26 Creaci&oacute;n de la clase.</li>
 *         </ul>
 * @version 1.0
 */

public interface SalesMovistarTotalService {
    /**
     * llama la implementación para cuando es un caso de MT.
     *
     * @author @srivasme
     * @param request Datos de la venta
     * @return Sale, datos de la nueva venta registrada en la BBDD de la Web Convergente
     */
    Mono<Sale> postSaleMovistarTotal(PostSalesRequest request);

    ServiceabilityInfoType buildServiceabilityInfoType(PostSalesRequest request,
                                                       CommercialOperationType currentCommercialOperationType);

    String setUpFrontIndicator(String productType);

    boolean validateNegotiation(List<KeyValueType> additionalData, List<IdentityValidationType> identityValidationType);

    void postSalesEventFlow(PostSalesRequest request);

    List<ChangedContainedProduct> changedContainedCaeqList(Sale saleRequest, String tempNum, String sapidSimcardBp,
                                               boolean flgCasi, CommercialOperationType currentCommercialOperationType,
                                               boolean flgAlta, boolean isRetail);

    List<FlexAttrType> commonOrderAttributes(Sale saleRequest, final boolean flgFinanciamiento,
                                             final boolean sendIndicator, final boolean isRetail,
                                             CommercialOperationType currentCommercialOperationType);

    ShipmentDetailsType createShipmentDetail(Sale saleRequest, CommercialOperationType currentCommercialOperationType);

    Mono<Sale> creationOrderValidation(Sale saleRequest, CreateProductOrderGeneralRequest productOrderRequest,
                                       CommercialOperationType currentCommercialOperationType,
                                       HashMap<String, String> headersMap,
                                       ProductOrderWebClient productOrderWebClient,
                                       GetSkuWebClient getSkuWebClient);

    List<KeyValueType> additionalDataAssigments(List<KeyValueType> input, Sale saleRequest,
                                                CommercialOperationType currentCommercialOperationType);

    Mono<Sale> reserveStockMobile(PostSalesRequest request, Sale saleRequest, boolean flgCasi,
                                  boolean flgFinanciamiento, String sapidSimcard,
                                  CommercialOperationType currentCommercialOperationType);

    ReserveStockRequest buildReserveStockRequest(ReserveStockRequest request, Sale sale,
                                                 CreateProductOrderResponseType createOrderResponse,
                                                 String sapidSimcardBp,
                                                 CommercialOperationType currentCommercialOperationType);

    void setReserveReponseInSales(ReserveStockResponse reserveStockResponse, Sale saleRequest,
                                  CommercialOperationType commercialOperationType);

    Mono<Sale> createQuotationMobile(PostSalesRequest request, Sale sale, boolean flgCasi,
                                     boolean flgFinanciamiento, CommercialOperationType currentCommercialOperationType);

    void addCaeqOderAttributes(List<FlexAttrType> caeqOrderAttributes, Sale saleRequest, boolean flgCasi,
                               boolean isRetail);

    void validationToAddSimcardBonus(Sale sale, BusinessParametersResponseObjectExt bonificacionSimcardResponse,
                                     List<NewAssignedBillingOffers> altaNewBoList,
                                     CommercialOperationType currentCommercialOperationType);
}
