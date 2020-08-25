package com.tdp.ms.sales.expose;

import com.tdp.ms.sales.business.SalesService;
import com.tdp.ms.sales.model.SalesRequest;
import com.tdp.ms.sales.model.SalesResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
 *         <li>Developer name</li>
 *         </ul>
 *         <u>Changes</u>:<br/>
 *         <ul>
 *         <li>YYYY-MM-DD Creaci&oacute;n del proyecto.</li>
 *         </ul>
 * @version 1.0
 */
@RestController
@RequestMapping("/sales/v1/greeting")
public class SalesController {

    @Autowired
    private SalesService salesService;

    @GetMapping
    public Mono<SalesResponse> indexGet() {
        return Mono.justOrEmpty(salesService.get());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<SalesResponse> indexPost(@RequestBody SalesRequest request) {
        return Mono.justOrEmpty(salesService.put(request.getName()));
    }



}
