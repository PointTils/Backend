package com.pointtils.pointtils.src.infrastructure.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class S3Config {

    @Value("${spring.cloud.aws.s3.enabled:false}")
    private boolean s3Enabled;

    /**
     * Bean principal para S3Client - só é criado quando S3 está habilitado
     */
    @Bean
    @Primary
    @ConditionalOnProperty(name = "spring.cloud.aws.s3.enabled", havingValue = "true")
    public S3Client s3Client() {
        return S3Client.create();
    }

    /**
     * Bean alternativo que cria um S3Client mock quando S3 está desabilitado
     * Evita que serviços dependentes falhem ao injetar S3Client
     */
    @Bean
    @ConditionalOnProperty(name = "spring.cloud.aws.s3.enabled", havingValue = "false", matchIfMissing = true)
    public S3Client s3ClientDisabled() {
        // Retorna um S3Client mock que não faz nada
        // Isso evita a falha de injeção de dependência
        return new S3Client() {
            @Override
            public String serviceName() {
                return "S3Client-Disabled";
            }

            @Override
            public void close() {
                // Não faz nada
            }
        };
    }
}
