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
                                  String idAgent, String customerId, String nationalID, String nationalIdType,
                                  String status, String channelId, String storeId, String orderId, String startDateTime,
                                  String endDateTime, String size, String pageCount, String page,
                                  String maxResultCount) {

        return salesRepository.findByChannel_DealerIdContainingAndAgent_IdContainingAndAgent_CustomerIdContainingAndAgent_NationalIdContainingAndAgent_NationalIdTypeContainingAndChannel_StoreIdContainingAndStatusContaining(dealerId, idAgent, customerId, nationalID, nationalIdType, storeId, status)
                .filter(item -> {
                    if (item.getSaleCreationDate() == null) {
                        return false;
                    } else if (startDateTime != null && endDateTime != null
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
                }).filter(item -> {
                    if (saleId != null && !saleId.isEmpty()) {
                        return item.getSalesId().compareTo(saleId) == 0;
                    }
                    // No se hace el filtro
                    return true;
                }).filter(item -> {
                    final boolean[] existOrderId = {false};
                    if (orderId != null && !orderId.isEmpty()) {
                        item.getCommercialOperation().forEach(cot -> {
                            if (cot.getOrder().getProductOrderId().compareTo(orderId) == 0) {
                                existOrderId[0] = true;
                            }
                        });
                        return existOrderId[0];
                    }
                    // No se hace el filtro
                    return true;
                });
    }
}
