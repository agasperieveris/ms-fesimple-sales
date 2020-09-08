package com.tdp.ms.sales.business.impl;

import com.tdp.ms.sales.business.SalesService;
import com.tdp.ms.sales.model.entity.Sale;
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

    @Override
    public Mono<SalesResponse> post(Sale request) {

        String uuid = UUID.randomUUID().toString();
        while (salesRepository.existsById(uuid) == Mono.just(true)) {
            uuid = UUID.randomUUID().toString();
        }
        request.setId(uuid);

        // TODO: salesId debe tener el formato "FE-000000001", se debe mejorar para autogenerarse aceptando concurrencia
        // Obtener el valor más alto de salesId y aumentar en 1
        Flux<Sale> saleFlux = salesRepository.findAll(Sort.by(Sort.Direction.DESC, "salesId"));

        System.out.println("antes de return saleFlux");

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


                    request.setSalesId(salesNum);
                    Mono<Sale> sale = salesRepository.save(request);
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
                                .idSales("FE-" + salesId)
                                .description(saleItem.getDescription())
                                .additionalData(saleItem.getAdditionalData())
                                .channel(saleItem.getChannel())
                                .commercialOperation(saleItem.getComercialOperationType())
                                .endDateTime(saleItem.getEndDateTime())
                                .name(saleItem.getName())
                                .priority(saleItem.getPriority())
                                .productType(saleItem.getProductType())
                                .prospectContact(saleItem.getProspectContact())
                                .relatedParty(saleItem.getRelatedParty())
                                .startDateTime(saleItem.getStartDateTime())
                                .status(saleItem.getStatus())
                                .statusChangeDate(saleItem.getStatusChangeDate())
                                .statusChangeReason(saleItem.getStatusChangeReason())
                                .build();

                        return Mono.just(salesResponse);
                    });
                });

    }

    @Override
    public Mono<SalesResponse> put(Sale request) {
        // buscar en la colección
        Mono<Sale> existingSale = salesRepository.findById(request.getId());

        return existingSale.flatMap(item -> {
            Sale salesUpdate = Sale
                    .builder()
                    .id(item.getId())
                    .salesId(item.getSalesId())
                    .description(request.getDescription())
                    .additionalData(request.getAdditionalData())
                    .channel(request.getChannel())
                    .comercialOperationType(request.getComercialOperationType())
                    .endDateTime(request.getEndDateTime())
                    .name(request.getName())
                    .priority(request.getPriority())
                    .productType(request.getProductType())
                    .prospectContact(request.getProspectContact())
                    .relatedParty(request.getRelatedParty())
                    .startDateTime(request.getStartDateTime())
                    .status(request.getStatus())
                    .statusChangeDate(request.getStatusChangeDate())
                    .statusChangeReason(request.getStatusChangeReason())
                    .build();

            // actualizar
            return salesRepository.save(salesUpdate).flatMap(updateSaleItem -> {
                String salesId;

                salesId = String.valueOf(updateSaleItem.getSalesId());
                int len = salesId.length();
                if (len < 9) {
                    for (int i = 0; i < 9 - len; i++) {
                        salesId = "0" + salesId;
                    }
                }


                SalesResponse response = SalesResponse
                        .builder()
                        .id(updateSaleItem.getId())
                        .idSales("FE-" + salesId)
                        .description(updateSaleItem.getDescription())
                        .additionalData(updateSaleItem.getAdditionalData())
                        .channel(updateSaleItem.getChannel())
                        .commercialOperation(updateSaleItem.getComercialOperationType())
                        .endDateTime(updateSaleItem.getEndDateTime())
                        .name(updateSaleItem.getName())
                        .priority(updateSaleItem.getPriority())
                        .productType(updateSaleItem.getProductType())
                        .prospectContact(updateSaleItem.getProspectContact())
                        .relatedParty(updateSaleItem.getRelatedParty())
                        .startDateTime(updateSaleItem.getStartDateTime())
                        .status(updateSaleItem.getStatus())
                        .statusChangeDate(updateSaleItem.getStatusChangeDate())
                        .statusChangeReason(updateSaleItem.getStatusChangeReason())
                        .build();

                return Mono.just(response);
            });
        });

    }
}
