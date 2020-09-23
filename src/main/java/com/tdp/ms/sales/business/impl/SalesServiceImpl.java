package com.tdp.ms.sales.business.impl;

import com.azure.cosmos.implementation.NotFoundException;
import com.tdp.genesis.core.exception.GenesisException;
import com.tdp.ms.sales.business.SalesService;
import com.tdp.ms.sales.model.entity.Sale;
import com.tdp.ms.sales.model.request.GetSalesRequest;
import com.tdp.ms.sales.model.request.SalesRequest;
import com.tdp.ms.sales.model.response.SalesResponse;
import com.tdp.ms.sales.repository.SalesRepository;

import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    // Tipos de razones de operación comercial
    private final String alta = "ALTA";
    private final String porta = "PORTA";
    private final String capl = "CAPL";
    private final String caeq = "CAEQ";
    private final String casi = "CASI";

    private static final Logger log = LoggerFactory.getLogger(SalesServiceImpl.class);

    @Override
    public Mono<SalesResponse> getSale(GetSalesRequest request) {
        // se recorre el id para obtener el número (Long)
        int index = 3;
        while (request.getId().charAt(index) != '0') {
            index++;
        }

        Long idSales = Long.parseLong(request.getId().substring(index));

        Mono<Sale> existingSale = salesRepository.findBySalesId(idSales);

        return existingSale
                .switchIfEmpty(Mono.error(GenesisException.builder()
                        .exceptionId("SVC0004")
                        .addDetail(true)
                        .withDescription("el id " + request.getId() + " no se encuentra registrado en BD.")
                        .push()
                        .build()))
                .flatMap(item -> {
                    String salesId;
                    salesId = String.valueOf(item.getSalesId());

                    int salesLen = salesId.length();
                    if (salesLen < 9) {
                        for (int i = 0; i < 9 - salesLen; i++) {
                            salesId = "0" + salesId;
                        }
                    }
                    SalesResponse response = SalesResponse
                            .builder()
                            .salesId("FE-" + salesId)
                            .id(item.getId())
                            .name(item.getName())
                            .description(item.getDescription())
                            .priority(item.getPriority())
                            .channel(item.getChannel())
                            .agent(item.getAgent())
                            .productType(item.getProductType())
                            .comercialOperationType(item.getComercialOperationType())
                            .estimatedRevenue(item.getEstimatedRevenue())
                            .paymentType(item.getPaymentType())
                            .prospectContact(item.getProspectContact())
                            .relatedParty(item.getRelatedParty())
                            .status(item.getStatus())
                            .statusChangeDate(item.getStatusChangeDate())
                            .statusChangeReason(item.getStatusChangeReason())
                            .audioStatus(item.getAudioStatus())
                            .identityValidations(item.getIdentityValidations())
                            .audioUrl(item.getAudioUrl())
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

    @Override
    public Mono<SalesResponse> put(SalesRequest request) {
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
                        .agent(request.getAgent())
                        .comercialOperationType(request.getComercialOperationType())
                        .estimatedRevenue(request.getEstimatedRevenue())
                        .paymentType(request.getPaymentType())
                        .name(request.getName())
                        .priority(request.getPriority())
                        .productType(request.getProductType())
                        .prospectContact(request.getProspectContact())
                        .relatedParty(request.getRelatedParty())
                        .saleCreationDate(request.getSaleCreationDate())
                        .status(request.getStatus())
                        .statusChangeDate(request.getStatusChangeDate())
                        .statusChangeReason(request.getStatusChangeReason())
                        .audioStatus(request.getAudioStatus())
                        .identityValidations(request.getIdentityValidations())
                        .audioUrl(request.getAudioUrl())
                        .validFor(request.getValidFor())
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
                                .salesId("FE-" + salesId)
                                .description(updateSaleItem.getDescription())
                                .priority(updateSaleItem.getPriority())
                                .channel(updateSaleItem.getChannel())
                                .agent(updateSaleItem.getAgent())
                                .comercialOperationType(updateSaleItem.getComercialOperationType())
                                .estimatedRevenue(updateSaleItem.getEstimatedRevenue())
                                .paymentType(updateSaleItem.getPaymentType())
                                .validFor(updateSaleItem.getValidFor())
                                .name(updateSaleItem.getName())
                                .productType(updateSaleItem.getProductType())
                                .prospectContact(updateSaleItem.getProspectContact())
                                .relatedParty(updateSaleItem.getRelatedParty())
                                .saleCreationDate(updateSaleItem.getSaleCreationDate())
                                .audioStatus(updateSaleItem.getAudioStatus())
                                .status(updateSaleItem.getStatus())
                                .statusChangeDate(updateSaleItem.getStatusChangeDate())
                                .statusChangeReason(updateSaleItem.getStatusChangeReason())
                                .identityValidations(updateSaleItem.getIdentityValidations())
                                .audioUrl(updateSaleItem.getAudioUrl())
                                .additionalData(updateSaleItem.getAdditionalData())
                                .build();

                        return Mono.just(response);
                    });
        });

    }

    @Override
    public Mono<SalesResponse> confirmationSalesLead(SalesResponse request, Map<String, String> headersMap) {

        //TODO: Esto es un mock, se ha replanteado la estructura para el siguiente sprint
        // Buscar el sale por su id
        Mono<Sale> inputSale = salesRepository.findById(request.getId());
        return inputSale.flatMap(item -> put(SalesRequest
                .builder()
                .additionalData(item.getAdditionalData())
                .agent(item.getAgent())
                .audioStatus(item.getAudioStatus())
                .audioUrl(item.getAudioUrl())
                .channel(item.getChannel())
                .comercialOperationType(item.getComercialOperationType())
                .description(item.getDescription())
                .estimatedRevenue(item.getEstimatedRevenue())
                .id(item.getId())
                .identityValidations(item.getIdentityValidations())
                .name(item.getName())
                .paymentType(item.getPaymentType())
                .priority(item.getPriority())
                .productType(item.getProductType())
                .prospectContact(item.getProspectContact())
                .relatedParty(item.getRelatedParty())
                .saleCreationDate(item.getSaleCreationDate())
                .salesId(item.getSalesId().toString())
                .status(item.getStatus())
                .statusChangeDate(item.getStatusChangeDate())
                .statusChangeReason(item.getStatusChangeReason())
                .validFor(item.getValidFor())
                .build()));
    }
}
