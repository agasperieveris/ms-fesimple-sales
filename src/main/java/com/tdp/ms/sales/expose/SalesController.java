package com.tdp.ms.sales.expose;

import com.tdp.genesis.core.constants.HttpHeadersKey;
import com.tdp.genesis.core.exception.GenesisException;
import com.tdp.ms.sales.business.SalesService;
import com.tdp.ms.sales.model.entity.Sale;
import com.tdp.ms.sales.model.request.GetSalesRequest;
import com.tdp.ms.sales.model.response.SalesResponse;
import javax.validation.Valid;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
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
     * @return
     */

    @GetMapping(value ="/{id}")
    @ApiOperation(produces = MediaType.APPLICATION_JSON_VALUE,
            value = "Obtiene por id la venta",
            notes = "Se debe enviar id como parámetro",
            response = SalesResponse.class)
    public Mono<SalesResponse> getSales(@PathVariable("id") String id, @RequestHeader(HttpHeadersKey.UNICA_SERVICE_ID) String serviceId,
                                        @RequestHeader(HttpHeadersKey.UNICA_APPLICATION) String application,
                                        @RequestHeader(HttpHeadersKey.UNICA_PID) String pid,
                                        @RequestHeader(HttpHeadersKey.UNICA_USER) String user) throws GenesisException {

        Map<String,String> headersMap = new HashMap<>();
        headersMap.put(HttpHeadersKey.UNICA_SERVICE_ID, serviceId);
        headersMap.put(HttpHeadersKey.UNICA_APPLICATION, application);
        headersMap.put(HttpHeadersKey.UNICA_PID, pid);
        headersMap.put(HttpHeadersKey.UNICA_USER, user);

        return salesService.getSale(GetSalesRequest
                .builder()
                .id(id)
                .headersMap(headersMap)
                .build());
  /*      // TODO: Esto es un mock - método por definir

        SalesResponse salesResponse = SalesResponse
                .builder()
                .id("1")
                .name("Pedro")
                .description("descripcion")
                .build();

        return Mono.just(salesResponse);*/
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

}
