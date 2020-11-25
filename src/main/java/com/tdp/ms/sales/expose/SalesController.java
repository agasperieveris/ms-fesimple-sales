package com.tdp.ms.sales.expose;

import com.tdp.genesis.core.constants.HttpHeadersKey;
import com.tdp.ms.sales.business.SalesManagmentService;
import com.tdp.ms.sales.model.entity.Sale;
import com.tdp.ms.sales.model.request.PostSalesRequest;
import com.tdp.ms.sales.utils.Commons;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

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
    private SalesManagmentService salesManagementService;

    /**
     * Actualiza datos de la orden de Sales.
     *
     * @author @cesargomezeveris
     * @param sale Datos de la venta
     * @return Sale, datos de la venta con información de la orden creada
     */
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Sale> createdSales(@Valid @RequestBody Sale sale,
                                            @RequestHeader(HttpHeadersKey.UNICA_SERVICE_ID) String serviceId,
                                            @RequestHeader(HttpHeadersKey.UNICA_APPLICATION) String application,
                                            @RequestHeader(HttpHeadersKey.UNICA_PID) String pid,
                                            @RequestHeader(HttpHeadersKey.UNICA_USER) String user) {

        return salesManagementService.post(PostSalesRequest
                .builder()
                .sale(sale)
                .headersMap(Commons.fillHeaders(serviceId,
                        application,
                        pid,
                        user))
                .build());
    }

}
