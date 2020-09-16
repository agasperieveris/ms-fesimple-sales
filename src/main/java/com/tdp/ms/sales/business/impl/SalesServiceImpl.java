package com.tdp.ms.sales.business.impl;

import com.azure.cosmos.implementation.NotFoundException;
import com.tdp.genesis.core.exception.GenesisException;
import com.tdp.ms.sales.business.SalesService;
import com.tdp.ms.sales.model.dto.ValidFor;
import com.tdp.ms.sales.model.entity.Sale;
import com.tdp.ms.sales.model.request.GetSalesRequest;
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
    public Mono<SalesResponse> getSale(GetSalesRequest request) {

        Mono<Sale> existingSale = salesRepository.findById(request.getId());

        return existingSale
                .switchIfEmpty(Mono.error( GenesisException.builder()
                        .exceptionId("SVC0004")
                        .addDetail(true)
                        .withDescription("el id "+ request.getId()+" no se encuentra registrado en BD.")
                        .push()
                        .build()))
                .flatMap(item -> {
                    String salesId;
                    salesId = String.valueOf(item.getSalesId());

                    if (salesId.length() < 9) {
                        for (int i = 0; i < 9 - salesId.length(); i++) {
                            salesId = "0" + salesId;
                        }
                    }
                    SalesResponse response = SalesResponse
                            .builder()
                            .salesId("FE-" +item.getSalesId())
                            .id(item.getId())
                            .name(item.getName())
                            .description(item.getDescription())
                            .priority(item.getPriority())
                            .channel(item.getChannel())
                            .agent(item.getAgent())
                            .productType(item.getProductType())
                            .commercialOperation(item.getComercialOperationType())
                            .estimatedRevenue(item.getEstimatedRevenue())
                            .prospectContact(item.getProspectContact())
                            .relatedParty(item.getRelatedParty())
                            .status(item.getStatus())
                            .statusChangeDate(item.getStatusChangeDate())
                            .statusChangeReason(item.getStatusChangeReason())
                            .audioStatus(item.getAudioStatus())
                            .validFor(item.getValidFor())
                            .additionalData(item.getAdditionalData())
                            .build();

                    return Mono.just(response);
                });
    }

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

                        ValidFor validFor = new ValidFor();
                        validFor.setStartDateTime(saleItem.getStartDateTime());
                        validFor.setEndDateTime(saleItem.getEndDateTime());

                        SalesResponse salesResponse = SalesResponse
                                .builder()
                                .id(saleItem.getId())
                                .salesId("FE-" + salesId)
                                .description(saleItem.getDescription())
                                .additionalData(saleItem.getAdditionalData())
                                .channel(saleItem.getChannel())
                                .commercialOperation(saleItem.getComercialOperationType())
                                .validFor(validFor)
                                .name(saleItem.getName())
                                .priority(saleItem.getPriority())
                                .productType(saleItem.getProductType())
                                .prospectContact(saleItem.getProspectContact())
                                .relatedParty(saleItem.getRelatedParty())
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


        return existingSale
                .switchIfEmpty(Mono.error(new NotFoundException("El id solicitado no se encuentra registrado.")))
                .flatMap(item -> {
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

                ValidFor validFor = new ValidFor();
                validFor.setStartDateTime(updateSaleItem.getStartDateTime());
                validFor.setEndDateTime(updateSaleItem.getEndDateTime());

                SalesResponse response = SalesResponse
                        .builder()
                        .id(updateSaleItem.getId())
                        .salesId("FE-" + salesId)
                        .description(updateSaleItem.getDescription())
                        .additionalData(updateSaleItem.getAdditionalData())
                        .channel(updateSaleItem.getChannel())
                        .commercialOperation(updateSaleItem.getComercialOperationType())
                        .validFor(validFor)
                        .name(updateSaleItem.getName())
                        .priority(updateSaleItem.getPriority())
                        .productType(updateSaleItem.getProductType())
                        .prospectContact(updateSaleItem.getProspectContact())
                        .relatedParty(updateSaleItem.getRelatedParty())
                        .status(updateSaleItem.getStatus())
                        .statusChangeDate(updateSaleItem.getStatusChangeDate())
                        .statusChangeReason(updateSaleItem.getStatusChangeReason())
                        .build();

                return Mono.just(response);
            });
        });

    }
}
