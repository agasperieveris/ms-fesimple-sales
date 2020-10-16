package com.tdp.ms.sales.business.impl;

import com.azure.cosmos.implementation.NotFoundException;
import com.tdp.genesis.core.exception.GenesisException;
import com.tdp.ms.sales.business.SalesService;
import com.tdp.ms.sales.client.WebClientBusinessParameters;
import com.tdp.ms.sales.model.entity.Sale;
import com.tdp.ms.sales.model.request.GetSalesRequest;
import com.tdp.ms.sales.model.response.BusinessParametersResponse;
import com.tdp.ms.sales.repository.SalesRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
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
    public Mono<Sale> post(Sale request, Map<String, String> headersMap) {

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
            Date todayDate = Calendar.getInstance().getTime();
            SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy'T'HH:mm:ss");
            String todayDateString = dateFormatter.format(todayDate);
            request.setSaleCreationDate(todayDateString);

            return salesRepository.save(request);
        });
    }

    @Override
    public Mono<Sale> put(String salesId, Sale request) {
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
    public Flux<Sale> getSaleList(String saleId, String dealerId,
                                  String idAgent, String customerId, String nationalId, String nationalIdType,
                                  String status, String channelId, String storeId, String orderId, String startDateTime,
                                  String endDateTime, String size, String pageCount, String page,
                                  String maxResultCount) {

        return salesRepository.findByChannel_IdContainingAndChannel_DealerIdContainingAndAgent_IdContainingAndChannel_StoreIdContainingAndStatusContaining(channelId, dealerId, idAgent, storeId, status)
                .filter(item -> filterNationalId(item, nationalId))
                .filter(item -> filterNationalIdType(item, nationalIdType))
                .filter(item -> filterCustomerId(item, customerId))
                .filter(item -> filterSaleCreationDate(item, startDateTime, endDateTime))
                .filter(item -> filterSalesId(item, saleId))
                .filter(item -> filterExistingOrderId(item, orderId));
    }

    public Boolean filterNationalId(Sale item, String nationalId) {
        if (nationalId != null && (item.getAgent() == null || item.getAgent().getNationalId() == null)) {
            return false;
        } else if (nationalId != null && item.getAgent() != null && item.getAgent().getNationalId() != null
                && !nationalId.isEmpty()) {
            return item.getAgent().getNationalId().equalsIgnoreCase(nationalId);
        } else {
            return true;
        }
    }

    public Boolean filterNationalIdType(Sale item, String nationalIdType) {
        if (nationalIdType != null && (item.getAgent() == null || item.getAgent().getNationalIdType() == null)) {
            return false;
        } else if (nationalIdType != null && item.getAgent() != null && item.getAgent().getNationalIdType() != null
                && !nationalIdType.isEmpty()) {
            return item.getAgent().getNationalIdType().equalsIgnoreCase(nationalIdType);
        } else {
            return true;
        }
    }

    public Boolean filterCustomerId(Sale item, String customerId) {
        if (customerId != null && !customerId.isEmpty() && item.getRelatedParty().get(0).getCustomerId() != null) {
            return item.getRelatedParty().get(0).getCustomerId().equals(customerId);
        }
        // No se hace el filtro
        return true;
    }

    public Boolean filterSaleCreationDate(Sale item, String startDateTime, String endDateTime) {
        if (startDateTime != null && endDateTime != null && (item.getSaleCreationDate() == null
                || item.getSaleCreationDate().isEmpty())) {
            return false;
        } else if (startDateTime != null && endDateTime != null && item.getSaleCreationDate() != null
                && !item.getSaleCreationDate().isEmpty() && !startDateTime.isEmpty() && !endDateTime.isEmpty()) {

            try {
                Date startDate = new SimpleDateFormat("dd/MM/yyyy'T'HH:mm:ss").parse(startDateTime);
                Date endDate = new SimpleDateFormat("dd/MM/yyyy'T'HH:mm:ss").parse(endDateTime);
                Date requestDate = new SimpleDateFormat("dd/MM/yyyy'T'HH:mm:ss").parse(item.getSaleCreationDate());
                return requestDate.after(startDate) && requestDate.before(endDate);
            } catch (ParseException e) {
                e.printStackTrace();
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
