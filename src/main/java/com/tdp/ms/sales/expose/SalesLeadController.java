package com.tdp.ms.sales.expose;

import com.tdp.genesis.core.constants.HttpHeadersKey;
import com.tdp.genesis.core.exception.GenesisException;
import com.tdp.ms.sales.business.SalesService;
import com.tdp.ms.sales.model.dto.KeyValueType;
import com.tdp.ms.sales.model.entity.Sale;
import com.tdp.ms.sales.model.request.GetSalesRequest;
import com.tdp.ms.sales.utils.Commons;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
         * @param serviceId   header Identificador único de cada ejecución.
         * @param application header Identificador del sistema que origina la solicitud.
         * @param pid         header Identificador de un grupo de ejecuciones, que
         *                    tienen en común estar en el mismo proceso del negocio.
         * @param user        header Identificador del usuario del sistema y/o
         *                    subsistema que inicia la petición.
         * @return Sale
         */

        @GetMapping(value = "/{id}")
        @ApiOperation(produces = MediaType.APPLICATION_JSON_VALUE, value = "Obtiene por id la venta", notes = "Se debe enviar id como parámetro, se conulta la colección sales de Cosmos DB", response = Sale.class)
        @ApiResponses(value = {
                        @ApiResponse(code = 404, message = "El id no se encuentra registrado en BD.", response = GenesisException.class),
                        @ApiResponse(code = 500, message = "Internal Server Error", response = GenesisException.class), })
        public Mono<Sale> getSales(@PathVariable("id") String id,
                        @RequestHeader(HttpHeadersKey.UNICA_SERVICE_ID) String serviceId,
                        @RequestHeader(HttpHeadersKey.UNICA_APPLICATION) String application,
                        @RequestHeader(HttpHeadersKey.UNICA_PID) String pid,
                        @RequestHeader(HttpHeadersKey.UNICA_USER) String user) {

                Map<String, String> headersMap = new HashMap<>();
                headersMap.put(HttpHeadersKey.UNICA_SERVICE_ID, serviceId);
                headersMap.put(HttpHeadersKey.UNICA_APPLICATION, application);
                headersMap.put(HttpHeadersKey.UNICA_PID, pid);
                headersMap.put(HttpHeadersKey.UNICA_USER, user);

                return salesService.getSale(GetSalesRequest.builder().id(id).headersMap(headersMap).build());
        }

        /**
         * Registra los datos de un Sale en la BBDD de la Web Convergente.
         *
         * @author @srivasme
         * @param serviceId   header Identificador único de cada ejecución.
         * @param application header Identificador del sistema que origina la solicitud.
         * @param pid         header Identificador de un grupo de ejecuciones, que
         *                    tienen en común estar en el mismo proceso del negocio.
         * @param user        header Identificador del usuario del sistema y/o
         *                    subsistema que inicia la petición.
         * @param request     Datos de la venta
         * @return SalesResponse, datos de la venta registrada en la BBDD de la Web
         *         Convergente
         */

        @PostMapping()
        @ResponseStatus(HttpStatus.CREATED)
        @ApiOperation(produces = MediaType.APPLICATION_JSON_VALUE, value = "Registra los datos de un Sale en la BBDD de la Web Convergente.", notes = "Se debe enviar Datos de la venta, se guarda la colección sales en Cosmos DB", response = Sale.class)
        @ApiResponses(value = { @ApiResponse(code = 401, message = "Unauthorized", response = GenesisException.class),
                        @ApiResponse(code = 500, message = "Internal Server Error", response = GenesisException.class), })
        public Mono<Sale> createdSales(@Valid @RequestBody Sale request,
                        @RequestHeader(HttpHeadersKey.UNICA_SERVICE_ID) String serviceId,
                        @RequestHeader(HttpHeadersKey.UNICA_APPLICATION) String application,
                        @RequestHeader(HttpHeadersKey.UNICA_PID) String pid,
                        @RequestHeader(HttpHeadersKey.UNICA_USER) String user) {

                return salesService.post(request, Commons.fillHeaders(serviceId, application, pid, user));
        }

        /**
         * Actualiza los datos de un Sale en la BBDD de la Web Convergente.
         *
         * @author @srivasme
         * @param serviceId   header Identificador único de cada ejecución.
         * @param application header Identificador del sistema que origina la solicitud.
         * @param pid         header Identificador de un grupo de ejecuciones, que
         *                    tienen en común estar en el mismo proceso del negocio.
         * @param user        header Identificador del usuario del sistema y/o
         *                    subsistema que inicia la petición.
         * @param request     Datos de la venta
         * @return SalesResponse, datos de la venta actualizada en la BBDD de la Web
         *         Convergente
         */
        @PutMapping("/{id}")
        @ApiOperation(produces = MediaType.APPLICATION_JSON_VALUE, value = "Actualiza los datos de un Sale en la BBDD de la Web Convergente", notes = "Se debe enviar Datos existente de la venta, se actualiza en la colección sales en Cosmos DB", response = Sale.class)
        @ApiResponses(value = { @ApiResponse(code = 401, message = "Unauthorized", response = GenesisException.class),
                        @ApiResponse(code = 500, message = "Internal Server Error", response = GenesisException.class), })
        public Mono<Sale> updateSales(@PathVariable("id") String salesId, @RequestBody Sale request,
                        @RequestHeader(HttpHeadersKey.UNICA_SERVICE_ID) String serviceId,
                        @RequestHeader(HttpHeadersKey.UNICA_APPLICATION) String application,
                        @RequestHeader(HttpHeadersKey.UNICA_PID) String pid,
                        @RequestHeader(HttpHeadersKey.UNICA_USER) String user) {

                String audioFileName = request.getAdditionalData().stream()
                                .filter(keyValue -> keyValue.getKey().equalsIgnoreCase("filename")
                                                && keyValue.getValue() != null)
                                .findFirst().orElse(KeyValueType.builder().value(null).build()).getValue();

                boolean salesApprove = Commons
                                .getStringValueByKeyFromAdditionalDataList(request.getAdditionalData(), "salesApprove")
                                .equalsIgnoreCase(Constants.STRING_TRUE);

                // Validation to avoid put salesLead loop
                boolean isSalesFromEventFlow = false;
                int saleAdditionalDataLastPosition = request.getAdditionalData().size() - 1;
                if (request.getAdditionalData() != null && !request.getAdditionalData().isEmpty()
                                && request.getAdditionalData().get(saleAdditionalDataLastPosition).getKey()
                                                .equalsIgnoreCase(Constants.SALES_FROM_EVENT_FLOW)) {
                        // Entra aquí cuando se ha invocado el PUT de saleslead desde el flujo asíncrono
                        request.getAdditionalData().remove(saleAdditionalDataLastPosition);
                        isSalesFromEventFlow = true;
                }

                if (isSalesFromEventFlow) { // put simple
                        return salesService.put(salesId, request,
                                        Commons.fillHeaders(serviceId, application, pid, user));
                } else if (audioFileName != null && !salesApprove) { // put para ejecutar flujo asíncrono 2
                        return salesService.putEvent(salesId, request,
                                        Commons.fillHeaders(serviceId, application, pid, user));
                } else if (salesApprove) {
                        // put para ejecutar flujo asíncrono 1
                        return salesService.putEventFlow1(salesId, request,
                                        Commons.fillHeaders(serviceId, application, pid, user));
                }
                return salesService.put(salesId, request, Commons.fillHeaders(serviceId, application, pid, user));
        }

        /**
         * se listan las ventas que cumplan con los campos solicitados.
         * 
         * @param serviceId   header Identificador único de cada ejecución.
         * @param application header Identificador del sistema que origina la solicitud.
         * @param pid         header Identificador de un grupo de ejecuciones, que
         *                    tienen en común estar en el mismo proceso del negocio.
         * @param user        header Identificador del usuario del sistema y/o
         *                    subsistema que inicia la petición.
         * @return Lista de Sale
         */

        @GetMapping
        @ApiOperation(produces = MediaType.APPLICATION_JSON_VALUE, value = "Obtiene por id la venta", notes = "Se debe enviar id como parámetro para conseguir la colección sale de Cosmos DB", responseContainer = "List", response = Sale.class)
        @ApiResponses(value = { @ApiResponse(code = 401, message = "Unauthorized", response = GenesisException.class),
                        @ApiResponse(code = 500, message = "Internal Server Error", response = GenesisException.class), })
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
                        @RequestParam(value = "nationalIDType", required = false, defaultValue = "") String nationalIdType,
                        @RequestParam(value = "status", required = false, defaultValue = "") String status,
                        @RequestParam(value = "channelId", required = false, defaultValue = "") String channelId,
                        @RequestParam(value = "storeId", required = false, defaultValue = "") String storeId,
                        @RequestParam(value = "orderId", required = false, defaultValue = "") String orderId,
                        @RequestParam(value = "startDateTime", required = false, defaultValue = "") String startDateTime,
                        @RequestParam(value = "endDateTime", required = false, defaultValue = "") String endDateTime,
                        // Los parámetros de paginación aun no será utilizados
                        @RequestParam(value = "paginationInfo.size", required = false, defaultValue = "") String size,
                        @RequestParam(value = "paginationInfo.pageCount", required = false, defaultValue = "") String pageCount,
                        @RequestParam(value = "paginationInfo.page", required = false, defaultValue = "") String page,
                        @RequestParam(value = "paginationInfo.maxResultCount", required = false, defaultValue = "") String maxResultCount) {

                return salesService.getSaleList(saleId, dealerId, idAgent, customerId, nationalID, nationalIdType,
                                status, channelId, storeId, orderId, startDateTime, endDateTime, size, pageCount, page,
                                maxResultCount);
        }

}
