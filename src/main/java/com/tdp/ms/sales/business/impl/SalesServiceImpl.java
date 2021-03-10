package com.tdp.ms.sales.business.impl;

import com.azure.cosmos.implementation.NotFoundException;
import com.tdp.genesis.core.exception.GenesisException;
import com.tdp.ms.commons.util.DateUtils;
import com.tdp.ms.sales.business.SalesService;
import com.tdp.ms.sales.client.WebClientBusinessParameters;
import com.tdp.ms.sales.client.WebClientReceptor;
import com.tdp.ms.sales.model.dto.KeyValueType;
import com.tdp.ms.sales.model.entity.Sale;
import com.tdp.ms.sales.model.request.GetSalesRequest;
import com.tdp.ms.sales.model.request.ReceptorRequest;
import com.tdp.ms.sales.model.response.BusinessParametersResponse;
import com.tdp.ms.sales.repository.SalesRepository;
import com.tdp.ms.sales.utils.Constants;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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

    private final WebClientBusinessParameters webClient;
    private final WebClientReceptor webClientReceptor;

    Logger logger = LoggerFactory.getLogger(SalesServiceImpl.class);
    
    private static final String FLOW_SALE_PUT = "02";
    
    @Value("${application.endpoints.url.business_parameters.seq_number}")
    private String seqNumber;

    @Override
    public Mono<Sale> getSale(GetSalesRequest request) {
        Mono<Sale> existingSale = salesRepository.findBySalesId(request.getId());

        return existingSale
                .switchIfEmpty(Mono.error(GenesisException.builder()
                        .exceptionId("SVC0004")
                        .addDetail(true)
                        .withDescription("el id " + request.getId() + " no se encuentra registrado en BD.")
                        .push()
                        .build()));
    }

    @Override
    public Mono<Sale> post(Sale request, HashMap<String, String> headersMap) {

        String uuid = UUID.randomUUID().toString();
        while (salesRepository.existsById(uuid) == Mono.just(true)) {
            uuid = UUID.randomUUID().toString();
        }
        request.setId(uuid);

        // Se obtiene el secuencial de businessParameters
        Mono<BusinessParametersResponse> saleSequential = webClient.getNewSaleSequential(seqNumber, headersMap);

        return saleSequential.flatMap(saleSequentialItem -> {
            request.setSalesId(saleSequentialItem.getData().get(0).getValue());

            // asignar fecha de creación
            ZoneId zone = ZoneId.of("America/Lima");
            ZonedDateTime date = ZonedDateTime.now(zone);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.STRING_DATE_TIME_FORMATTER);
            request.setSaleCreationDate(date.format(formatter));

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
                    return salesRepository.save(request);
                });

    }

    @Override
    public Mono<Sale> putEvent(String salesId, Sale request, HashMap<String, String> headersMap) {

        return this.put(salesId, request, headersMap)
                .map(r -> {
                    r.getAdditionalData().add(
                            KeyValueType
                                    .builder()
                                    .key("initialProcessDate")
                                    .value(DateUtils.getDatetimeNowCosmosDbFormat())
                                    .build()
                    );
                    // Llamada a receptor
                    webClientReceptor
                            .register(
                                    ReceptorRequest
                                            .builder()
                                            .businessId(r.getSalesId())
                                            .typeEventFlow(FLOW_SALE_PUT)
                                            .message(r)
                                            .build(),
                                    headersMap
                            )
                            .subscribe();
                    return r;
                });
    }

    @Override
    public Flux<Sale> getSaleList(String saleId, String dealerId,
                                  String idAgent, String customerId, String nationalId, String nationalIdType,
                                  String status, String channelId, String storeId, String orderId, String startDateTime,
                                  String endDateTime, String size, String pageCount, String page,
                                  String maxResultCount) {

        return salesRepository.findAll().filter(item -> filterSalesWithParams(item, saleId, dealerId, idAgent,
                customerId, nationalId, nationalIdType, status, channelId, storeId, orderId, startDateTime,
                endDateTime));
    }

    public Boolean filterSalesWithParams(Sale item, String saleId, String dealerId,
                                         String agentId, String customerId, String nationalId, String nationalIdType,
                                         String status, String channelId, String storeId, String orderId,
                                         String startDateTime, String endDateTime) {

        Boolean channelIdBool = filterChannelId(item, channelId);
        Boolean dealerIdBool = filterDealerId(item, dealerId);
        Boolean agentIdBool = filterAgentId(item, agentId);
        Boolean storeIdBool = filterStoreId(item, storeId);
        Boolean statusBool = filterStatus(item, status);
        Boolean nationalIdBool = filterNationalId(item, nationalId);
        Boolean nationalIdTypeBool = filterNationalIdType(item, nationalIdType);
        Boolean customerIdBool = filterCustomerId(item, customerId);
        Boolean saleCreationDateBool = filterSaleCreationDate(item, startDateTime, endDateTime);
        Boolean saleIdBool = filterSalesId(item, saleId);
        Boolean orderIdBool = filterExistingOrderId(item, orderId);

        return channelIdBool && dealerIdBool && agentIdBool && storeIdBool && statusBool && nationalIdBool
                && nationalIdTypeBool && customerIdBool && saleCreationDateBool && saleIdBool && orderIdBool;
    }

    public Boolean filterChannelId(Sale item, String channelId) {
        if (channelId != null && !channelId.isEmpty()
                && (item.getChannel() == null || item.getChannel().getId() == null)) {
            return false;
        } else if (channelId != null && !channelId.isEmpty() && item.getChannel() != null
                && item.getChannel().getId() != null) {
            return item.getChannel().getId().equalsIgnoreCase(channelId);
        } else {
            return true;
        }
    }

    public Boolean filterDealerId(Sale item, String dealerId) {
        if (dealerId != null && !dealerId.isEmpty()
                && (item.getChannel() == null || item.getChannel().getDealerId() == null)) {
            return false;
        } else if (dealerId != null && !dealerId.isEmpty() && item.getChannel() != null
                && item.getChannel().getDealerId() != null) {
            return item.getChannel().getDealerId().equalsIgnoreCase(dealerId);
        } else {
            return true;
        }
    }

    public Boolean filterAgentId(Sale item, String agentId) {
        if (agentId != null && !agentId.isEmpty() && (item.getAgent() == null || item.getAgent().getId() == null)) {
            return false;
        } else if (agentId != null && !agentId.isEmpty() && item.getAgent() != null && item.getAgent().getId() != null) {
            return item.getAgent().getId().equalsIgnoreCase(agentId);
        } else {
            return true;
        }
    }

    public Boolean filterStoreId(Sale item, String storeId) {
        if (storeId != null && !storeId.isEmpty()
                && (item.getChannel() == null || item.getChannel().getStoreId() == null)) {
            return false;
        } else if (storeId != null && !storeId.isEmpty() && item.getChannel() != null
                && item.getChannel().getStoreId() != null) {
            return item.getChannel().getStoreId().equalsIgnoreCase(storeId);
        } else {
            return true;
        }
    }

    public Boolean filterStatus(Sale item, String status) {
        if (status != null && !status.isEmpty() && item.getStatus() == null) {
            return false;
        } else if (status != null && !status.isEmpty() && item.getStatus() != null) {
            List<String> statusList =  Arrays.asList(status.split(","));
            final Boolean[] isStatusMatched = {false};
            statusList.stream().forEach(stat -> {
                if (stat.equalsIgnoreCase(item.getStatus())) {
                    isStatusMatched[0] = true;
                }
            });
            return isStatusMatched[0];
        } else {
            return true;
        }
    }

    public Boolean filterNationalId(Sale item, String nationalId) {
        if (nationalId != null && !nationalId.isEmpty() && (item.getRelatedParty() == null
                || item.getRelatedParty().get(0).getNationalId() == null)) {
            return false;
        } else if (nationalId != null && !nationalId.isEmpty() && item.getRelatedParty() != null
                && item.getRelatedParty().get(0).getNationalId() != null) {
            return item.getRelatedParty().get(0).getNationalId().equalsIgnoreCase(nationalId);
        } else {
            return true;
        }
    }

    public Boolean filterNationalIdType(Sale item, String nationalIdType) {
        if (nationalIdType != null && !nationalIdType.isEmpty() && (item.getRelatedParty() == null
                || item.getRelatedParty().get(0).getNationalIdType() == null)) {
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

    public Boolean filterSaleCreationDate(Sale item, String startDateTime, String endDateTime) {
        if (startDateTime != null && endDateTime != null && !startDateTime.isEmpty() && !endDateTime.isEmpty()
                && (item.getSaleCreationDate() == null || item.getSaleCreationDate().isEmpty())) {
            return false;
        } else if (startDateTime != null && endDateTime != null && !startDateTime.isEmpty() && !endDateTime.isEmpty()
                && item.getSaleCreationDate() != null && !item.getSaleCreationDate().isEmpty()) {

            try {
                Date startDate = new SimpleDateFormat(Constants.STRING_DATE_TIME_FORMATTER).parse(startDateTime);
                Date endDate = new SimpleDateFormat(Constants.STRING_DATE_TIME_FORMATTER).parse(endDateTime);
                Date requestDate = new SimpleDateFormat(Constants.STRING_DATE_TIME_FORMATTER)
                        .parse(item.getSaleCreationDate());
                return requestDate.after(startDate) && requestDate.before(endDate);
            } catch (ParseException e) {
                return false;
            }

        }
        // No se hace el filtro
        return true;
    }

    public Boolean filterSalesId(Sale item, String saleId) {
        if (saleId != null && !saleId.isEmpty()) {
            return item.getSalesId().compareTo(saleId) == 0;
        }
        // No se hace el filtro
        return true;
    }

    public Boolean  filterExistingOrderId(Sale item, String orderId) {
        final boolean[] existOrderId = {false};
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
}
