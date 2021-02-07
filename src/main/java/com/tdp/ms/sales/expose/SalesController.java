package com.tdp.ms.sales.expose;

import com.tdp.genesis.core.constants.HttpHeadersKey;
import com.tdp.genesis.core.exception.GenesisException;
import com.tdp.ms.sales.business.SalesManagmentService;
import com.tdp.ms.sales.model.entity.Sale;
import com.tdp.ms.sales.model.request.PostSalesRequest;
import com.tdp.ms.sales.utils.Commons;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
    private SalesManagmentService salesManagementService;

    /**
     * Actualiza datos de la orden de Sales.
     *
     * @author @cesargomezeveris
     * @param serviceId     header     Identificador único de cada ejecución.
	 * @param application   header     Identificador del sistema que origina la solicitud.
	 * @param pid           header     Identificador de un grupo de ejecuciones, que tienen en
	 *                                 común estar en el mismo proceso del negocio.
	 * @param user          header     Identificador del usuario del sistema y/o subsistema
	 *                                 que inicia la petición.
     * @param sale Datos de la venta
     * @return Sale, datos de la venta con información de la orden creada
     */
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
	@ApiOperation(produces = MediaType.APPLICATION_JSON_VALUE, value = "Actualiza datos de la orden de Sales", notes = "Se guarda información en la colección Sale de ComosDB : \n"
			+ "API Berserkers - Backend : \n"
			+ "1.get_parameters_financiamiento_fija: https://aks-berserkers-ingress-dev.eastus2.cloudapp.azure.com/bussinesparameters/v2/parameters/054/dataparams? "
			+ "\n"
			+ "2.get_risk_domain_url: https://aks-berserkers-ingress-dev.eastus2.cloudapp.azure.com/bussinesparameters/v2/parameters/034/dataparams?value={domain} "
			+ "\n"
			+ "3.get_sales_characteristics_url: https://aks-berserkers-ingress-dev.eastus2.cloudapp.azure.com/bussinesparameters/v2/parameters/033/dataparams?codComercialOperationType={commercialOperationType}"
			+ "\n"
			+ "4.get_bonificacion_simcard: https://aks-berserkers-ingress-dev.eastus2.cloudapp.azure.com/bussinesparameters/v2/parameters/045/dataparams?key={channelId}"
			+ "\n"
			+ "5.get_parameters_simcard: https://aks-berserkers-ingress-dev.eastus2.cloudapp.azure.com/bussinesparameters/v2/parameters/051/dataparams"
			+ "\n", responseContainer = "Object", response = Sale.class)
	@ApiResponses(value = { @ApiResponse(code = 500, message = "id is mandatory.", response = GenesisException.class),
			@ApiResponse(code = 500, message = "salesId is mandatory.", response = GenesisException.class), })
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
