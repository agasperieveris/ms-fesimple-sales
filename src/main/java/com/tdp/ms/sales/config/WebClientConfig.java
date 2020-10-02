package com.tdp.ms.sales.config;

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

    @Value("${application.endpoints.url.business_parameters.timeout.timeout}")
    private String timeout;
    @Value("${application.endpoints.url.business_parameters.timeout.timeout_read}")
    private String readTimeout;
    @Value("${application.endpoints.url.business_parameters.timeout.timeout_write}")
    private String writeTimeout;


    /**
     * Bean to config Webclient.
     *
     * @return      WebClient
     */
    @Bean
    public WebClient webClient() {
        return WebClient.builder().build();
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
        SslContext sslContext = sslContext();
        HttpClient httpClient =
                httpClient(Integer.parseInt(timeout),
                        Integer.parseInt(readTimeout),
                        Integer.parseInt(writeTimeout))
                        .secure(t -> t.sslContext(sslContext));

        return createWebclient(tokenUrl,httpClient);

    }

    /**
     * Bean to config Webclient add for geocoding.
     *
     * @return WebClient
     */
    @Bean
    public WebClient webClientSecure() {
        return WebClient.builder().build();
    }

    private HttpClient httpClient(int connectionTimeOut, int readTimeout, int writeTimeout) {
        return HttpClient.create()
                .tcpConfiguration(client ->
                        client.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectionTimeOut)
                                .doOnConnected(conn -> conn
                                        .addHandlerLast(new ReadTimeoutHandler(readTimeout / 1000))
                                        .addHandlerLast(new WriteTimeoutHandler(writeTimeout / 1000))));
    }

    private SslContext sslContext() throws SSLException {
        return SslContextBuilder
                .forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE)
                .build();
    }

    private WebClient createWebclient(String url, HttpClient httpClient) {
        return WebClient
                .builder()
                .baseUrl(url)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

}

