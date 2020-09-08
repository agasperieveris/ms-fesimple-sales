package com.tdp.ms.sales.repository;

import com.microsoft.azure.spring.data.cosmosdb.repository.ReactiveCosmosRepository;
import com.tdp.ms.sales.model.entity.Sale;
import org.springframework.stereotype.Repository;

/**
 * Class: SalesRepository. <br/>
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
 *         <li>2020-07-20 Creaci&oacute;n del proyecto.</li>
 *         </ul>
 * @version 1.0
 */


@Repository
public interface SalesRepository extends ReactiveCosmosRepository<Sale, String> {

}
