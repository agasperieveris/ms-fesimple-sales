package com.tdp.ms.sales.expose;

import com.tdp.genesis.core.constants.HttpHeadersKey;
import com.tdp.ms.sales.business.SalesService;
import com.tdp.ms.sales.model.entity.Sale;
import com.tdp.ms.sales.model.response.SalesResponse;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * Class: SalesController. <br/>
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

@RestController
@RequestMapping("/fesimple/v1/sales")
@CrossOrigin
public class SalesController {
    @Autowired
    private SalesService salesService;

    /**
     * se listan las ventas de la BBDD.
     *
     */

    @GetMapping
    public Flux<SalesResponse> getSales(@RequestHeader(HttpHeadersKey.UNICA_SERVICE_ID) String serviceId,
            @RequestHeader(HttpHeadersKey.UNICA_APPLICATION) String application,
            @RequestHeader(HttpHeadersKey.UNICA_PID) String pid,
            @RequestHeader(HttpHeadersKey.UNICA_USER) String user) {

        // TODO: Esto es un mock - método por definir

        SalesResponse salesResponse = SalesResponse
                .builder()
                .id("1")
                .name("Pedro")
                .description("descripcion")
                .build();

        return Flux.just(salesResponse);
    }

    /**
     * Registra los datos de un Sale en la BBDD de la Web Convergente.
     *
     * @author @srivasme
     * @param request Datos de la venta
     * @return SalesResponse, datos de la venta registrada en la BBDD de la Web
     *         Convergente
     */

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<SalesResponse> createdSales(@Valid @RequestBody Sale request,
            @RequestHeader(HttpHeadersKey.UNICA_SERVICE_ID) String serviceId,
            @RequestHeader(HttpHeadersKey.UNICA_APPLICATION) String application,
            @RequestHeader(HttpHeadersKey.UNICA_PID) String pid,
            @RequestHeader(HttpHeadersKey.UNICA_USER) String user) {

        return salesService.post(request);
    }

    /**
     * ACtualiza los datos de un Sale en la BBDD de la Web Convergente.
     *
     * @author @srivasme
     * @param request Datos de la venta
     * @return SalesResponse, datos de la venta actualizada en la BBDD de la Web
     *         Convergente
     */

    @PutMapping
    public Mono<SalesResponse> updateSales(@Valid @RequestBody Sale request,
            @RequestHeader(HttpHeadersKey.UNICA_SERVICE_ID) String serviceId,
            @RequestHeader(HttpHeadersKey.UNICA_APPLICATION) String application,
            @RequestHeader(HttpHeadersKey.UNICA_PID) String pid,
            @RequestHeader(HttpHeadersKey.UNICA_USER) String user) {

        return salesService.put(request);
    }

    /**
     * Registra los datos de un Sale en la BBDD de la Web Convergente.
     *
     * @author @srivasme
     * @param request Datos de la venta
     * @return SalesResponse, datos de la venta registrada en la BBDD de la Web
     *         Convergente
     */

    @PostMapping("/confirmation")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<SalesResponse> confirmationSalesLead(@Valid @RequestBody SalesResponse request,
                                            @RequestHeader(HttpHeadersKey.UNICA_SERVICE_ID) String serviceId,
                                            @RequestHeader(HttpHeadersKey.UNICA_APPLICATION) String application,
                                            @RequestHeader(HttpHeadersKey.UNICA_PID) String pid,
                                            @RequestHeader(HttpHeadersKey.UNICA_USER) String user,
                                            @RequestHeader("ufxauthorization") String ufxauthorization) {

        Map<String, String> headersMap = new HashMap<String, String>();
        headersMap.put(HttpHeadersKey.UNICA_SERVICE_ID, serviceId);
        headersMap.put(HttpHeadersKey.UNICA_APPLICATION, application);
        headersMap.put(HttpHeadersKey.UNICA_PID, pid);
        headersMap.put(HttpHeadersKey.UNICA_USER, user);
        headersMap.put("ufxauthorization", ufxauthorization);

        return salesService.confirmationSalesLead(request, headersMap);
    }

}
