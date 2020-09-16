package com.tdp.ms.sales.business.impl;

import com.azure.cosmos.implementation.NotFoundException;
import com.tdp.genesis.core.exception.GenesisException;
import com.tdp.ms.sales.business.SalesService;
import com.tdp.ms.sales.model.dto.ValidFor;
import com.tdp.ms.sales.model.entity.Sale;
import com.tdp.ms.sales.model.request.GetSalesRequest;
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

    @Override
    public Mono<SalesResponse> confirmationSalesLead(SalesResponse request, Map<String, String> headersMap) {

        //TODO: Esto es un mock, se ha replanteado la estructura para el siguiente sprint
        // Buscar el sale por su id
        Mono<Sale> inputSale = salesRepository.findById(request.getId());
        return inputSale.flatMap(item -> {
            return put(item);
        });

        /*
        // TODO: crear objecto para llamar a create product order
        return inputSale
                .switchIfEmpty(Mono.error(new NotFoundException("El id solicitado no se encuentra registrado.")))
                .flatMap(existingSale -> {

                    return createOrder(existingSale.getComercialOperationType().get(0), existingSale, headersMap)
                            .flatMap(res -> {
                                System.out.println("====actualizar existingSale===");
                                //actualizar existingSale
                                existingSale.setDescription(res.getNewProductsInNewOfferings().get(0).getProductCatalogId());
                                System.out.println(existingSale);

                                // Guardar los cambios de existingSale
                                salesRepository.save(existingSale);
                                return Mono.empty();
                            });

                    /*for (ComercialOperationType comercialOperationType:existingSale.getComercialOperationType()) {
                        createOrder(comercialOperationType, existingSale, headersMap).map(item -> {
                            //actualizar existingSale
                            existingSale.setDescription(item.getNewProductsInNewOfferings().get(0).getProductCatalogId());
                            return item;
                        });

                        // Recorrer additionalData de comercialOperation
                    }

                    // Guardar los cambios de existingSale
                    salesRepository.save(existingSale);

                    // TODO: quitar Mono.empty()
                    //return Mono.empty();
                });
        */
    }

    /*private Mono<ProductorderResponse> createOrder(ComercialOperationType comercialOperationType, Sale existingSale,
                             Map<String, String> headersMap) {

        System.out.println("====Entro a CreateOrder===");

        // Analizar el campo reason y llamar al post para crear orden
        Customer customer = Customer
                .builder()
                .customerId(existingSale.getRelatedParty().get(0).getCustomerId()) //TODO: verificar el arreglo
                .build();

        ProductOrderRequest productOrderRequest = ProductOrderRequest
                .builder()
                .salesChannel(existingSale.getChannel().getName())
                .customer(customer)
                .productOfferingID("4417988") //TODO: verificar el arreglo para saber qué pasar aquí
                .onlyValidationIndicator(true) //TODO: en el Excel decía que debía ser false pero el api pide true
                .build();

        if (comercialOperationType.getReason().compareTo(alta) == 0) {
            // ALTA
            productOrderRequest.setActionType("PR");

            OrderAttributes orderAttributes_deliveryMethod = OrderAttributes
                    .builder()
                    .attrName("DELIVERY_METHOD")
                    .flexAttrValue(null)
                    .build();

            OrderAttributes orderAttributes_paymentMethod = OrderAttributes
                    .builder()
                    .attrName("PAYMENT_METHOD")
                    .flexAttrValue(null)
                    .build();

            List<OrderAttributes> orderAttributesList = new ArrayList<OrderAttributes>();
            orderAttributesList.add(orderAttributes_deliveryMethod);
            orderAttributesList.add(orderAttributes_paymentMethod);

            NewAssignedBillingOffers newAssignedBillingOffers = NewAssignedBillingOffers
                    .builder()
                    .productSpecPricingID(null)
                    .parentProductCatalogID("7491")
                    .build();

            List<NewAssignedBillingOffers> newAssignedBillingOffersList = new ArrayList<NewAssignedBillingOffers>();
            newAssignedBillingOffersList.add(newAssignedBillingOffers);

            ChangedContainedProducts changedContainedProducts = ChangedContainedProducts
                    .builder()
                    .temporaryId("TEMP2")
                    .productCatalogID("7411")
                    .changedCharacteristics(null) //TODO: Falta recibir respuesta de validación
                    .build();
            List<ChangedContainedProducts> changedContainedProductsList = new ArrayList<ChangedContainedProducts>();
            changedContainedProductsList.add(changedContainedProducts);

            //TODO: lanza error cuando se pasa productChanges
            ProductChanges productChanges = ProductChanges
                    .builder()
                    .newAssignedBillingOffers(newAssignedBillingOffersList)
                    .changedContainedProducts(changedContainedProductsList)
                    .build();

            NewProducts newProducts = NewProducts
                    .builder()
                    .productID("8091614409") //TODO: saber de donde sale
                    .productCatalogId("4418018") //TODO: saber de donde sale
                    .temporaryId("TEMP1")
                    .baId(null) //TODO: dice como necesario en el Excel pero no te lo pide el api
                    .accountId(null) //TODO: dice como necesario en el Excel pero no te lo pide el api
                    .invoiceCompany("TEF")
                    .productChanges(null) //TODO: lanza error cuando se pasa esto
                    .build();

            List<NewProducts> newProductsList = new ArrayList<NewProducts>();
            newProductsList.add(newProducts);

            Request request = Request
                    .builder()
                    .sourceApp("FE")
                    .orderAttributes(null) //TODO: lanza error cuando se pasa orderAttributes (Excel)
                    .newProducts(newProductsList)
                    .build();

            productOrderRequest.setRequest(request);

        } else if (comercialOperationType.getReason().compareTo(porta) == 0) {
            // PORTA
            productOrderRequest.setActionType("PR");
        } else if (comercialOperationType.getReason().compareTo(caeq) == 0) {
            // CAEQ
            productOrderRequest.setActionType("CH");
        } else if (comercialOperationType.getReason().compareTo(capl) == 0) {
            // CAPL
            productOrderRequest.setActionType("CH");
        } else if (comercialOperationType.getReason().compareTo(casi) == 0) {
            // CASI
            productOrderRequest.setActionType("CH");
        }

        // Llamar al servicio para crear un Product Order
        return productOrder.createOrder(productOrderRequest, headersMap);
    }*/
}
