package com.pointtils.pointtils.src.application.services;

import java.io.IOException;
import java.time.Instant;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Slf4j
@Service
public class S3Service {

    private final S3Client s3Client;
    private final String bucketName;
    private final boolean s3Enabled;

    public S3Service(@Value("${cloud.aws.bucket-name:pointtils-api-tests-d9396dcc}") String bucketName,
            @Value("${spring.cloud.aws.s3.enabled:false}") boolean s3Enabled,
            S3Client s3Client) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
        this.s3Enabled = s3Enabled;
    }

    public String uploadFile(MultipartFile file, String userId) throws IOException {
        // Se S3 está desabilitado, retorna null ou lança exceção
        if (!s3Enabled || s3Client == null) {
            throw new UnsupportedOperationException(
                    "Upload de arquivos para S3 está desabilitado. Configure spring.cloud.aws.s3.enabled=true para habilitar.");
        }

        String key = "users/" + userId + "/" + Instant.now().toEpochMilli() + "-" + file.getOriginalFilename();

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(putObjectRequest, software.amazon.awssdk.core.sync.RequestBody.fromBytes(file.getBytes()));

        return String.format("https://%s.s3.amazonaws.com/%s", bucketName, key);
    }

    public void deleteFile(String documentUrl) {
        // Se S3 está desabilitado, retorna null ou lança exceção
        if (!s3Enabled || s3Client == null) {
            throw new UnsupportedOperationException(
                    "Delete de arquivos no S3 está desabilitado. Configure spring.cloud.aws.s3.enabled=true para habilitar.");
        }

        String fileKey = documentUrl.replaceAll(String.format("https://%s.s3.amazonaws.com", bucketName), "");

        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(fileKey)
                .build();
        var response = s3Client.deleteObject(deleteObjectRequest);
        if (Boolean.FALSE.equals(response.deleteMarker())) {
            log.error("Erro ao deletar arquivo {}", documentUrl);
        }
    }

    /**
     * Baixa um arquivo do S3 pelo key (o mesmo key retornado em uploadFile).
     * Retorna os bytes do objeto ou lança IOException em caso de falha.
     */
    public byte[] getFile(String key) throws IOException {
        if (!isS3Enabled()) {
            throw new UnsupportedOperationException(
                    "Download de arquivos do S3 está desabilitado. Configure spring.cloud.aws.s3.enabled=true para habilitar.");
        }

        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            ResponseBytes<GetObjectResponse> objectBytes = s3Client
                    .getObject(getObjectRequest, ResponseTransformer.toBytes());

            return objectBytes.asByteArray();
        } catch (S3Exception e) {
            throw new IOException("Erro ao obter arquivo do S3: " + e.awsErrorDetails().errorMessage(), e);
        } catch (Exception e) {
            throw new IOException("Falha inesperada ao baixar arquivo do S3", e);
        }
    }

    /**
     * Verifica se o serviço S3 está habilitado
     */
    public boolean isS3Enabled() {
        return s3Enabled && s3Client != null;
    }
}
