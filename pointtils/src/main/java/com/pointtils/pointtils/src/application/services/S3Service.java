package com.pointtils.pointtils.src.application.services;

import java.io.IOException;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

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
            throw new UnsupportedOperationException("Upload de arquivos para S3 está desabilitado. Configure spring.cloud.aws.s3.enabled=true para habilitar.");
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

    /**
     * Verifica se o serviço S3 está habilitado
     */
    public boolean isS3Enabled() {
        return s3Enabled && s3Client != null;
    }

    /**
     * Deleta um arquivo do S3 usando a URL completa
     * @param fileUrl URL completa do arquivo (ex: https://bucket.s3.amazonaws.com/key)
     * @return true se a deleção foi bem-sucedida
     * @throws UnsupportedOperationException se S3 estiver desabilitado ou URL inválida
     * @throws RuntimeException se houver erro ao deletar o arquivo
     */
    public boolean deleteFile(String fileUrl) {
        if (!isS3Enabled() || fileUrl == null || fileUrl.isEmpty()) {
            throw new UnsupportedOperationException("Deleção de arquivos para S3 está desabilitado. Configure spring.cloud.aws.s3.enabled=true para habilitar.");
        }

        try {
            String key = extractKeyFromUrl(fileUrl);
            if (key == null) {
                throw new UnsupportedOperationException("URL do arquivo inválida para deleção.");
            }

            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build());

            return true;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao deletar arquivo do S3", e);
        }
    }

    /**
     * Extrai a chave (key) de uma URL do S3
     * Suporta formatos:
     * - https://bucket.s3.amazonaws.com/key
     * - https://bucket.s3.region.amazonaws.com/key
     */
    private String extractKeyFromUrl(String url) {
        if (url == null || url.isEmpty()) {
            return null;
        }

        // Remove protocolo
        url = url.replaceFirst("^https?://", "");
        
        // Extrai tudo depois de .amazonaws.com/
        int index = url.indexOf(".amazonaws.com/");
        if (index != -1) {
            return url.substring(index + ".amazonaws.com/".length());
        }
        
        // Caso o formato seja s3.amazonaws.com/bucket/key, remove o bucket
        if (url.startsWith("s3.amazonaws.com/")) {
            String[] parts = url.substring("s3.amazonaws.com/".length()).split("/", 2);
            return parts.length > 1 ? parts[1] : null;
        }
        
        return null;
    }
}
