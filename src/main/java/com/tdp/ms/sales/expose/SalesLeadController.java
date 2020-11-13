package com.tdp.ms.sales.expose;

import com.tdp.genesis.core.constants.HttpHeadersKey;
import com.tdp.ms.sales.business.SalesService;
import com.tdp.ms.sales.model.entity.Sale;
import com.tdp.ms.sales.model.request.GetSalesRequest;
import com.tdp.ms.sales.model.response.SalesResponse;
import io.swagger.annotations.ApiOperation;
import java.util.HashMap;
import java.util.Map;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Class: SalesLeadController. <br/>
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
@RequestMapping("/fesimple/v1/saleslead")
@CrossOrigin
public class SalesLeadController {
    @Autowired
    private SalesService salesService;

    /**
     * se listan las ventas de la BBDD.
     *
     * @return
     */

    @GetMapping(value = "/{id}")
    @ApiOperation(produces = MediaType.APPLICATION_JSON_VALUE,
            value = "Obtiene por id la venta",
            notes = "Se debe enviar id como parámetro",
            response = SalesResponse.class)
    public Mono<Sale> getSales(@PathVariable("id") String id,
                                        @RequestHeader(HttpHeadersKey.UNICA_SERVICE_ID) String serviceId,
                                        @RequestHeader(HttpHeadersKey.UNICA_APPLICATION) String application,
                                        @RequestHeader(HttpHeadersKey.UNICA_PID) String pid,
                                        @RequestHeader(HttpHeadersKey.UNICA_USER) String user) {

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
    public Mono<Sale> createdSales(@Valid @RequestBody Sale request,
            @RequestHeader(HttpHeadersKey.UNICA_SERVICE_ID) String serviceId,
            @RequestHeader(HttpHeadersKey.UNICA_APPLICATION) String application,
            @RequestHeader(HttpHeadersKey.UNICA_PID) String pid,
            @RequestHeader(HttpHeadersKey.UNICA_USER) String user) {

        return salesService.post(request, fillHeaders(serviceId, application, pid, user));
    }

    /**
     * ACtualiza los datos de un Sale en la BBDD de la Web Convergente.
     *
     * @author @srivasme
     * @param request Datos de la venta
     * @return SalesResponse, datos de la venta actualizada en la BBDD de la Web
     *         Convergente
     */

    @PutMapping("/{id}")
    public Mono<Sale> updateSales(@PathVariable("id") String salesId,
                                  @RequestBody Sale request,
                                  @RequestHeader(HttpHeadersKey.UNICA_SERVICE_ID) String serviceId,
                                  @RequestHeader(HttpHeadersKey.UNICA_APPLICATION) String application,
                                  @RequestHeader(HttpHeadersKey.UNICA_PID) String pid,
                                  @RequestHeader(HttpHeadersKey.UNICA_USER) String user) {

        return salesService.put(salesId, request, fillHeaders(serviceId, application, pid, user));
    }

    /**
     * se listan las ventas que cumplan con los campos solicitados.
     *
     * @return
     */

    @GetMapping
    @ApiOperation(produces = MediaType.APPLICATION_JSON_VALUE,
            value = "Obtiene por id la venta",
            notes = "Se debe enviar id como parámetro",
            response = SalesResponse.class)
    public Flux<Sale> getSalesList(@RequestHeader(HttpHeadersKey.UNICA_SERVICE_ID) String serviceId,
                        @RequestHeader(HttpHeadersKey.UNICA_APPLICATION) String application,
                        @RequestHeader(HttpHeadersKey.UNICA_PID) String pid,
                        @RequestHeader(HttpHeadersKey.UNICA_USER) String user,
                        @RequestHeader("ufxauthorization") String ufxauthorization,
                        @RequestParam(value = "id", required = false, defaultValue = "") String saleId,
                        @RequestParam(value = "dealerId", required = false, defaultValue = "") String dealerId,
                        @RequestParam(value = "idAgent", required = false, defaultValue = "") String idAgent,
                        @RequestParam(value = "customerId", required = false, defaultValue = "") String customerId,
                        @RequestParam(value = "nationalID", required = false, defaultValue = "") String nationalID,
                        @RequestParam(value = "nationalIDType", required = false,
                                defaultValue = "") String nationalIdType,
                        @RequestParam(value = "status", required = false, defaultValue = "") String status,
                        @RequestParam(value = "channelId", required = false, defaultValue = "") String channelId,
                        @RequestParam(value = "storeId", required = false, defaultValue = "") String storeId,
                        @RequestParam(value = "orderId", required = false, defaultValue = "") String orderId,
                        @RequestParam(value = "startDateTime", required = false,
                                defaultValue = "") String startDateTime,
                        @RequestParam(value = "endDateTime", required = false, defaultValue = "") String endDateTime,
                        // Los parámetros de paginación aun no será utilizados
                        @RequestParam(value = "paginationInfo.size", required = false, defaultValue = "") String size,
                        @RequestParam(value = "paginationInfo.pageCount", required = false,
                                defaultValue = "") String pageCount,
                        @RequestParam(value = "paginationInfo.page", required = false, defaultValue = "") String page,
                        @RequestParam(value = "paginationInfo.maxResultCount", required = false,
                                defaultValue = "") String maxResultCount) {


        return salesService.getSaleList(saleId, dealerId, idAgent, customerId, nationalID,
                nationalIdType, status, channelId, storeId, orderId, startDateTime, endDateTime, size, pageCount, page,
                maxResultCount);
    }

    private Map<String, String> fillHeaders(String serviceId, String application, String pid, String user) {
        Map<String, String> headersMap = new HashMap();
        headersMap.put(HttpHeadersKey.UNICA_SERVICE_ID, serviceId);
        headersMap.put(HttpHeadersKey.UNICA_APPLICATION, application);
        headersMap.put(HttpHeadersKey.UNICA_PID, pid);
        headersMap.put(HttpHeadersKey.UNICA_USER, user);

        return headersMap;
    }

}
