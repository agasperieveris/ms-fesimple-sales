package com.tdp.ms.sales.business.impl;

import com.azure.cosmos.implementation.NotFoundException;
import com.microsoft.azure.spring.data.cosmosdb.core.ReactiveCosmosTemplate;
import com.microsoft.azure.spring.data.cosmosdb.core.query.Criteria;
import com.microsoft.azure.spring.data.cosmosdb.core.query.CriteriaType;
import com.microsoft.azure.spring.data.cosmosdb.core.query.DocumentQuery;
import com.tdp.genesis.core.exception.GenesisException;
import com.tdp.ms.commons.util.DateUtils;
import com.tdp.ms.sales.business.SalesService;
import com.tdp.ms.sales.client.WebClientBusinessParameters;
import com.tdp.ms.sales.client.WebClientReceptor;
import com.tdp.ms.sales.model.dto.KeyValueType;
import com.tdp.ms.sales.model.entity.Sale;
import com.tdp.ms.sales.model.request.GetSalesRequest;
import com.tdp.ms.sales.model.request.ReceptorRequest;
import com.tdp.ms.sales.repository.SalesRepository;
import com.tdp.ms.sales.utils.Commons;
import com.tdp.ms.sales.utils.Constants;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * Class: SalesServiceImpl. <br/>
 * <b>Copyright</b>: &copy; 2019 Telef&oacute;nica del Per&uacute;<br/>
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
 *         <li>YYYY-MM-DD Creaci&oacute;n del proyecto.</li>
 *         </ul>
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class SalesServiceImpl implements SalesService {
    @Autowired
    private SalesRepository salesRepository;
    @Autowired
    private ReactiveCosmosTemplate reactiveCosmosTemplate;

    private final WebClientBusinessParameters webClient;
    private final WebClientReceptor webClientReceptor;

    Logger logger = LoggerFactory.getLogger(SalesServiceImpl.class);

    private static final String FLOW_SALE_PUT = "02";

    @Value("${application.endpoints.url.business_parameters.seq_number}")
    private String seqNumber;

    @Override
    public Mono<Sale> getSale(GetSalesRequest request) {
        Mono<Sale> existingSale = salesRepository.findBySalesId(request.getId());

        return existingSale.switchIfEmpty(Mono.error(GenesisException.builder().exceptionId("SVC0004").addDetail(true)
                .withDescription("el id " + request.getId() + " no se encuentra registrado en BD.").push().build()));
    }

    @Override
    public Mono<Sale> post(Sale request, HashMap<String, String> headersMap) {

        String uuid = UUID.randomUUID().toString();
        while (salesRepository.existsById(uuid) == Mono.just(true)) {
            uuid = UUID.randomUUID().toString();
        }
        request.setId(uuid);

        return webClient.getNewSaleSequential(seqNumber, headersMap).flatMap(saleSequentialItem -> {
            ZoneId zone = ZoneId.of("America/Lima");
            ZonedDateTime date = ZonedDateTime.now(zone);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.STRING_DATE_TIME_FORMATTER);
            request.setSaleCreationDate(date.format(formatter));
            request.setSalesId(saleSequentialItem.getData().get(0).getValue());
            return salesRepository.save(request);
        });
    }

    @Override
    public Mono<Sale> put(String salesId, Sale request, HashMap<String, String> headersMap) {
        // buscar en la colección
        Mono<Sale> existingSale = salesRepository.findBySalesId(salesId);

        return existingSale
                .switchIfEmpty(Mono.error(new NotFoundException("El salesId solicitado no se encuentra registrado.")))
                .flatMap(item -> {
                    request.setSalesId(item.getSalesId());
                    request.setStatusChangeDate(Commons.getDatetimeNow());
                    request.setStatusChangeReason("Sale Update");
                    return salesRepository.save(request);
                });

    @Override
    public Mono<Sale> putEventFlow1(String salesId, Sale request, HashMap<String, String> headersMap) {
        return this.put(salesId, request, headersMap).map(saleUpdated -> {
            this.postSalesEventFlow(PostSalesRequest.builder().sale(saleUpdated).headersMap(headersMap).build());
            return saleUpdated;
        });
    }

    @Override
    public Mono<Sale> putEvent(String salesId, Sale request, HashMap<String, String> headersMap) {

        return this.put(salesId, request, headersMap).map(r -> {
            r.getAdditionalData().add(KeyValueType.builder().key("initialProcessDate")
                    .value(DateUtils.getDatetimeNowCosmosDbFormat()).build()

            );
            r.setStatusChangeDate(Commons.getDatetimeNow());
            r.setStatusChangeReason("Event Update");
            // Llamada a receptor
            webClientReceptor.register(ReceptorRequest.builder().businessId(r.getSalesId()).typeEventFlow(FLOW_SALE_PUT)
                    .message(r).build(), headersMap).subscribe();
            return r;
        });
    }

    @Override
    public Flux<Sale> getSaleList(String saleId, String dealerId, String idAgent, String customerId, String nationalId,
            String nationalIdType, String status, String channelId, String storeId, String orderId,
            String startDateTime, String endDateTime, String size, String pageCount, String page,
            String maxResultCount) {

        List<Criteria> criteriaList = new ArrayList<>();
        criteriaSaleId(criteriaList, saleId);
        criteriaDealerId(criteriaList, dealerId);
        criteriaIdAgent(criteriaList, idAgent);
        criteriaStatus(criteriaList, status);
        criteriaChannelId(criteriaList, channelId);
        criteriaStoreId(criteriaList, storeId);
        criteriaDateTime(criteriaList, startDateTime, endDateTime);

        return reactiveCosmosTemplate
                .find(new DocumentQuery(buildCriteria(criteriaList, CriteriaType.AND)), Sale.class,
                        Sale.class.getSimpleName())
                .filter(item -> filterSalesWithParams(item, customerId, nationalId, nationalIdType, orderId));
    }

    private Criteria buildCriteria(List<Criteria> criteriaList, CriteriaType criteriaType) {
        if (criteriaList.size() == 0) {
            return Criteria.getInstance(CriteriaType.ALL);
        } else if (criteriaList.size() == 1) {
            return criteriaList.get(0);
        } else {
            Criteria criteria = criteriaList.get(0);
            criteriaList.remove(criteria);
            return Criteria.getInstance(criteriaType, criteria, buildCriteria(criteriaList, criteriaType));
        }
    }

    public void criteriaSaleId(List<Criteria> criteriaList, String saleId) {
        if (saleId != null && !saleId.isEmpty()) {
            criteriaList.add(Criteria.getInstance(CriteriaType.IS_EQUAL, "salesId", Collections.singletonList(saleId)));
        }
    }

    public void criteriaDealerId(List<Criteria> criteriaList, String dealerId) {
        if (dealerId != null && !dealerId.isEmpty()) {
            criteriaList.add(Criteria.getInstance(CriteriaType.IS_EQUAL, "channel.dealerId",
                    Collections.singletonList(dealerId)));
        }
    }

    public void criteriaIdAgent(List<Criteria> criteriaList, String idAgent) {
        if (idAgent != null && !idAgent.isEmpty()) {
            criteriaList
                    .add(Criteria.getInstance(CriteriaType.IS_EQUAL, "agent.id", Collections.singletonList(idAgent)));
        }
    }

    public void criteriaStatus(List<Criteria> criteriaList, String status) {
        if (status != null && !status.isEmpty()) {
            List<String> statusList = new LinkedList<>(Arrays.asList(status.split(",")));
            criteriaList.add(Criteria.getInstance(CriteriaType.IN, "status", Collections.singletonList(statusList)));
        }
    }

    public void criteriaChannelId(List<Criteria> criteriaList, String channelId) {
        if (channelId != null && !channelId.isEmpty()) {
            criteriaList.add(
                    Criteria.getInstance(CriteriaType.IS_EQUAL, "channel.id", Collections.singletonList(channelId)));
        }
    }

    public void criteriaStoreId(List<Criteria> criteriaList, String storeId) {
        if (storeId != null && !storeId.isEmpty()) {
            criteriaList.add(
                    Criteria.getInstance(CriteriaType.IS_EQUAL, "channel.storeId", Collections.singletonList(storeId)));
        }
    }

    public void criteriaDateTime(List<Criteria> criteriaList, String startDateTime, String endDateTime) {
        if (startDateTime != null && !startDateTime.isEmpty() && endDateTime != null && !endDateTime.isEmpty()) {
            criteriaList.add(Criteria.getInstance(CriteriaType.BETWEEN, "saleCreationDate",
                    Arrays.asList(startDateTime, endDateTime)));
        }
    }

    public Boolean filterSalesWithParams(Sale item, String customerId, String nationalId, String nationalIdType,
            String orderId) {
        Boolean nationalIdBool = filterNationalId(item, nationalId);
        Boolean nationalIdTypeBool = filterNationalIdType(item, nationalIdType);
        Boolean customerIdBool = filterCustomerId(item, customerId);
        Boolean orderIdBool = filterExistingOrderId(item, orderId);
        return nationalIdBool && nationalIdTypeBool && customerIdBool && orderIdBool;
    }

    public Boolean filterNationalId(Sale item, String nationalId) {
        if (nationalId != null && !nationalId.isEmpty()
                && (item.getRelatedParty() == null || item.getRelatedParty().get(0).getNationalId() == null)) {
            return false;
        } else if (nationalId != null && !nationalId.isEmpty() && item.getRelatedParty() != null
                && item.getRelatedParty().get(0).getNationalId() != null) {
            return item.getRelatedParty().get(0).getNationalId().equalsIgnoreCase(nationalId);
        } else {
            return true;
        }
    }

    public Boolean filterNationalIdType(Sale item, String nationalIdType) {
        if (nationalIdType != null && !nationalIdType.isEmpty()
                && (item.getRelatedParty() == null || item.getRelatedParty().get(0).getNationalIdType() == null)) {
            return false;
        } else if (nationalIdType != null && !nationalIdType.isEmpty() && item.getRelatedParty() != null
                && item.getRelatedParty().get(0).getNationalIdType() != null) {
            return item.getRelatedParty().get(0).getNationalIdType().equalsIgnoreCase(nationalIdType);
        } else {
            return true;
        }
    }

    public Boolean filterCustomerId(Sale item, String customerId) {
        if (customerId != null && !customerId.isEmpty() && item.getRelatedParty().get(0).getCustomerId() != null) {
            return item.getRelatedParty().get(0).getCustomerId().equalsIgnoreCase(customerId);
        }
        // No se hace el filtro
        return true;
    }

    public Boolean filterExistingOrderId(Sale item, String orderId) {
        final boolean[] existOrderId = { false };
        if (orderId != null && !orderId.isEmpty() && item.getCommercialOperation() == null) {
            // Se quita de la respuesta
            return false;
        } else if (orderId != null && !orderId.isEmpty() && item.getCommercialOperation() != null) {
            item.getCommercialOperation().forEach(cot -> {
                if (cot.getOrder() != null && cot.getOrder().getProductOrderId() != null
                        && cot.getOrder().getProductOrderId().compareTo(orderId) == 0) {
                    existOrderId[0] = true;
                }
            });
            return existOrderId[0];
        }
        // No se hace el filtro
        return true;
    }

    private void postSalesEventFlow(PostSalesRequest request) {
        request.getSale().getAdditionalData().add(KeyValueType.builder().key("initialProcessDate")
                .value(DateUtils.getDatetimeNowCosmosDbFormat()).build());

        callReceptors(request);
    }

    private void callReceptors(PostSalesRequest request) {
        callWebClientReceptor(request, FLOW_SALE_POST);

        String reason = request.getSale().getCommercialOperation().get(0).getReason();
        if (reason.equalsIgnoreCase("CAPL") || reason.equalsIgnoreCase("CAEQ")) {
            callWebClientReceptor(request, FLOW_SALE_INVITATION);
        }
    }

    private void callWebClientReceptor(PostSalesRequest request, String eventFlowCode) {
        // Llamada a receptor
        webClientReceptor.register(ReceptorRequest.builder().businessId(request.getSale().getSalesId())
                .typeEventFlow(eventFlowCode).message(request.getSale()).build(), request.getHeadersMap()).subscribe();
    }
}
