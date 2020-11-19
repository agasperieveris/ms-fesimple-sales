package com.tdp.ms.sales.business.impl;

import com.tdp.ms.commons.util.DateUtils;
import com.tdp.ms.sales.business.SalesManagementService;
import com.tdp.ms.sales.client.WebClientBusinessParameters;
import com.tdp.ms.sales.client.WebClientReceptor;
import com.tdp.ms.sales.model.dto.KeyValueType;
import com.tdp.ms.sales.model.entity.Sale;
import com.tdp.ms.sales.model.request.ReceptorRequest;
import com.tdp.ms.sales.model.response.BusinessParametersResponse;
import com.tdp.ms.sales.repository.SalesRepository;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

/**
 * Interface: SalesManagementServiceImpl. <br/>
 * <b>Copyright</b>: &copy; 2020 Telef&oacute;nica del Per&uacute;<br/>
 * <b>Company</b>: Telef&oacute;nica del Per&uacute;<br/>
 *
 * @author Telef&oacute;nica del Per&uacute; (TDP) <br/>
 *         <u>Service Provider</u>: Everis Per&uacute; SAC (EVE) <br/>
 *         <u>Developed by</u>: <br/>
 *         <ul>
 *         <li>Ingrid Mendoza</li>
 *         </ul>
 *         <u>Changes</u>:<br/>
 *         <ul>
 *         <li>2020-11-13 Creaci&oacute;n del proyecto.</li>
 *         </ul>
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class SalesManagementServiceImpl implements SalesManagementService {
    @Autowired
    private SalesRepository salesRepository;
    
    private final WebClientBusinessParameters webClient;
    private final WebClientReceptor webClientReceptor;
    
    private static final String FLOW_SALE_POST = "01";
    
    @Value("${application.endpoints.url.business_parameters.seq_number}")
    private String seqNumber;
    
    /**
     * Registra los datos de un nueva venta en la BBDD de la Web Convergente.
     *
     * @author @srivasme
     * @param request Datos de la nueva venta
     * @return SalesResponse, datos de la nueva venta registrada en la BBDD de la Web Convergente
     */
    @Override
    public Mono<Sale> post(Sale request, Map<String, String> headersMap) {
        String uuid = UUID.randomUUID().toString();

        request.setId(uuid);

        Mono<BusinessParametersResponse> saleSequential =
                webClient.getNewSaleSequential(seqNumber, headersMap);

        return saleSequential
                .flatMap(saleSequentialItem -> {
            request.setSalesId(saleSequentialItem.getData().get(0).getValue());

            ZoneId zone = ZoneId.of("America/Lima");
            ZonedDateTime date = ZonedDateTime.now(zone);


            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy'T'HH:mm:ss");
            request.setSaleCreationDate(date.format(formatter));

            return salesRepository.save(request)
                    .map(r -> {
                        if(request.getAdditionalData()!=null) {
                            request.getAdditionalData().add(
                                    KeyValueType
                                        .builder()
                                        .key("initialProcessDate")
                                        .value(DateUtils.getDatetimeNowCosmosDbFormat())
                                        .build()
                                );
                        }
                        else {
                            request.setAdditionalData(new ArrayList<>());
                            request.getAdditionalData().add(
                                    KeyValueType
                                        .builder()
                                        .key("initialProcessDate")
                                        .value(DateUtils.getDatetimeNowCosmosDbFormat())
                                        .build()
                                );
                        }

                        // Llamada a receptor
                        webClientReceptor
                            .register(
                                    ReceptorRequest
                                    .builder()
                                    .businessId(request.getSalesId())
                                    .typeEventFlow(FLOW_SALE_POST)
                                    .message(request)
                                    .build(),
                                    headersMap
                            )
                            .subscribe();
                        return r;
                    });
        });
    }

}
