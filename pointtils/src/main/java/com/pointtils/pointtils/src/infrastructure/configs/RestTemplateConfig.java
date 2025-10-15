package com.pointtils.pointtils.src.infrastructure.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Value("${client.ibge.timeout:3000}")
    private Integer ibgeTimeoutInMs;

    @Bean
    public RestTemplate ibgeRestTemplate() {
        return new RestTemplate(buildClientHttpRequestFactory(ibgeTimeoutInMs));
    }

    private ClientHttpRequestFactory buildClientHttpRequestFactory(Integer timeoutInMs) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(timeoutInMs);
        factory.setReadTimeout(timeoutInMs);
        return factory;
    }
}