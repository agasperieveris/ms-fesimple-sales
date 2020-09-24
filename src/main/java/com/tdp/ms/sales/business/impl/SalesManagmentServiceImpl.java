package com.tdp.ms.sales.business.impl;

import com.tdp.ms.sales.business.SalesManagmentService;
import com.tdp.ms.sales.client.BusinessParameterWebClient;
import com.tdp.ms.sales.model.entity.Sale;
import com.tdp.ms.sales.model.request.PostSalesRequest;
import com.tdp.ms.sales.model.response.SalesResponse;
import com.tdp.ms.sales.repository.SalesRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Class: SalesManagmentServiceImpl. <br/>
 * <b>Copyright</b>: &copy; 2019 Telef&oacute;nica del Per&uacute;<br/>
 * <b>Company</b>: Telef&oacute;nica del Per&uacute;<br/>
 *
 * @author Telef&oacute;nica del Per&uacute; (TDP) <br/>
 *         <u>Service Provider</u>: Everis Per&uacute; SAC (EVE) <br/>
 *         <u>Developed by</u>: <br/>
 *         <ul>
 *         <li>Cesar Gomez</li>
 *         </ul>
 *         <u>Changes</u>:<br/>
 *         <ul>
 *         <li>2020-09-24 Creaci&oacute;n del proyecto.</li>
 *         </ul>
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class SalesManagmentServiceImpl implements SalesManagmentService {

    @Autowired
    private SalesRepository salesRepository;

    @Autowired
    private BusinessParameterWebClient businessParameterWebClient;

    @Override
    public Mono<SalesResponse> post(PostSalesRequest request) {

        String uuid = UUID.randomUUID().toString();
        while (salesRepository.existsById(uuid) == Mono.just(true)) {
            uuid = UUID.randomUUID().toString();
        }
        request.getSale().setId(uuid);

        // TODO: salesId debe tener el formato "FE-000000001", se debe mejorar para autogenerarse aceptando concurrencia
        // Obtener el valor más alto de salesId y aumentar en 1
        Flux<Sale> saleFlux = salesRepository.findAll(Sort.by(Sort.Direction.DESC, "salesId"));

        return saleFlux
                .collectList()
                .flatMap(item -> {
                    // Completar con 0 (el número es de 9 dígitos)
                    // Situación 1: Si no hay datos en la coleccion, se le asigna 1
                    // Situación 2: Si es 999999999, no se completa con 0

                    Long salesNum;
                    if (item.isEmpty()) {
                        salesNum = Long.valueOf(1);
                    } else {
                        salesNum = item.get(0).getSalesId();
                        salesNum++;
                    }


                    request.getSale().setSalesId(salesNum);
                    Mono<Sale> sale = salesRepository.save(request.getSale());
                    return sale.flatMap(saleItem -> {
                        String salesId;

                        salesId = String.valueOf(saleItem.getSalesId());
                        int len = salesId.length();
                        if (len < 9) {
                            for (int i = 0; i < 9 - len; i++) {
                                salesId = "0" + salesId;
                            }
                        }

                        SalesResponse salesResponse = SalesResponse
                                .builder()
                                .id(saleItem.getId())
                                .salesId("FE-" + salesId)
                                .description(saleItem.getDescription())
                                .additionalData(saleItem.getAdditionalData())
                                .channel(saleItem.getChannel())
                                .agent(saleItem.getAgent())
                                .comercialOperationType(saleItem.getComercialOperationType())
                                .estimatedRevenue(saleItem.getEstimatedRevenue())
                                .paymentType(saleItem.getPaymentType())
                                .validFor(saleItem.getValidFor())
                                .name(saleItem.getName())
                                .priority(saleItem.getPriority())
                                .productType(saleItem.getProductType())
                                .prospectContact(saleItem.getProspectContact())
                                .relatedParty(saleItem.getRelatedParty())
                                .saleCreationDate(saleItem.getSaleCreationDate())
                                .status(saleItem.getStatus())
                                .statusChangeDate(saleItem.getStatusChangeDate())
                                .statusChangeReason(saleItem.getStatusChangeReason())
                                .audioStatus(saleItem.getAudioStatus())
                                .identityValidations(saleItem.getIdentityValidations())
                                .audioUrl(saleItem.getAudioUrl())
                                .build();

                        return Mono.just(salesResponse);
                    });
                });
    }

}
