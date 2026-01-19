package com.plaiaundi.sepe.seid.infrastructure;

import java.net.http.HttpClient;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import javax.net.ssl.SSLContext;
import java.security.cert.X509Certificate;
import java.time.Duration;

@Configuration
public class OpenDataEuskadiConfig {

    @Bean
    public RestClient restClient() throws NoSuchAlgorithmException, KeyManagementException {
        // 1. Crear un TrustManager que NO verifique nada (acepta todo)
        TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() { return null; }
                public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                public void checkServerTrusted(X509Certificate[] certs, String authType) {}
            }
        };

        // 2. Configurar el contexto SSL con ese TrustManager
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

        // 3. Crear el cliente HTTP nativo de Java usando ese contexto SSL
        HttpClient httpClient = HttpClient.newBuilder()
                .sslContext(sslContext)
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        // 4. Construir el RestClient usando ese cliente HTTP modificado
        return RestClient.builder()
                .baseUrl("https://api.euskadi.eus/traffic")
                .requestFactory(new JdkClientHttpRequestFactory(httpClient))
                .build();
    }

}
