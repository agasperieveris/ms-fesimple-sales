package com.tdp.ms.sales.expose;

import com.tdp.genesis.core.constants.HttpHeadersKey;
import com.tdp.ms.sales.business.SalesService;
import com.tdp.ms.sales.model.entity.Sale;
import com.tdp.ms.sales.model.response.SalesResponse;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
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
    private SalesService salesService;

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

        //TODO: Por ahora solo se hace lo mismo que el post de salesLead
        return salesService.post(request);
    }
}
