package com.tdp.ms.sales.config;

import com.tdp.genesis.core.starter.reactive.webclient.filters.LogWebClientFilters;
import io.netty.channel.ChannelOption;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import javax.net.ssl.SSLException;

import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

/**
 * Class: WebClientConfig. <br/>
 * <b>Copyright</b>: &copy; 2020 Telef&oacute;nica del Per&uacute;<br/>
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
 *         <li>2020-07-29 Creaci&oacute;n del proyecto.</li>
 *         </ul>
 * @version 1.0
 */
@Configuration
public class WebClientConfig {

    //@Value("${application.security.token.url}")
    private static String tokenUrl = "";
    @Value("${application.endpoints.receptor.register_url}")
    private String urlReceptorRegister;

    @Value("${application.timeout.get_order_timeout}")
    private int timeout;
    @Value("${application.timeout.get_order_timeout_read}")
    private int readTimeout;
    @Value("${application.timeout.get_order_timeout_write}")
    private int writeTimeout;

    /**
     * Bean to config Webclient.
     *
     * @return      WebClient
     */
    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .filter(LogWebClientFilters.logResponse())
                .filter(LogWebClientFilters.logRequest())
                .filter(LogWebClientFilters.mdcFilter)
                .build();
    }

    /**
     * Bean to config Webclient to manage Geocoding Token.
     *
     * @return      WebClient
     * @throws javax.net.ssl.SSLException
     */
    @Bean
    public WebClient webClientAccountToken() throws SSLException {
        //TODO Agregar configuración para enviar un certificado seguro
        SslContext sslContext = SslContextBuilder
                .forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE)
                .build();
        HttpClient httpClient = HttpClient.create().secure(t -> t.sslContext(sslContext));
        return WebClient
                .builder()
                .filter(LogWebClientFilters.logResponse())
                .filter(LogWebClientFilters.logRequest())
                .filter(LogWebClientFilters.mdcFilter)
                .baseUrl(tokenUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    /**
     * Bean to config Webclient add for account.
     *
     * @return WebClient
     */
    @Bean
    public WebClient webClientInsecure() throws SSLException {
        SslContext sslContext = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE)
                .build();
        HttpClient httpClient = HttpClient.create()
                .secure(t -> t.sslContext(sslContext))
                .tcpConfiguration(client ->
                        client.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, timeout)
                                .doOnConnected(conn -> conn
                                        .addHandlerLast(new ReadTimeoutHandler(readTimeout / 1000))
                                        .addHandlerLast(new WriteTimeoutHandler(writeTimeout / 1000))));
        return WebClient.builder()
                .filter(LogWebClientFilters.logResponse())
                .filter(LogWebClientFilters.logRequest())
                .filter(LogWebClientFilters.mdcFilter)
                .clientConnector(new ReactorClientHttpConnector(httpClient)).build();


    }

    /**
     * Bean to config Webclient add for geocoding.
     *
     * @return WebClient
     */
    @Bean
    public WebClient webClientSecure() {
        return WebClient.builder()
                .filter(LogWebClientFilters.logResponse())
                .filter(LogWebClientFilters.logRequest())
                .filter(LogWebClientFilters.mdcFilter)
                .build();
    }
    
    /**
     * Bean para configurar la conexión contra el receptor de colas.
     * 
     * @return WebClient.
     * @throws SSLException SSLException.
     */
    @Bean
    public WebClient webClientInsecureReceptor() throws SSLException {
        SslContext sslContext = SslContextBuilder
                .forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE)
                .build();
        
        HttpClient httpClient = HttpClient
                .create().secure(t -> t.sslContext(sslContext));
        
        return WebClient
                .builder()
                .filter(LogWebClientFilters.logResponse())
                .filter(LogWebClientFilters.logRequest())
                .filter(LogWebClientFilters.mdcFilter)
                .baseUrl(urlReceptorRegister)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}

