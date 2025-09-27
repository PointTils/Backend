package com.pointtils.pointtils.src.application.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.time.Instant;

@Service
@ConditionalOnProperty(name = "spring.cloud.aws.s3.enabled", havingValue = "true")
public class S3Service {

    private final S3Client s3Client;
    private final String bucketName;

    public S3Service(@Value("${cloud.aws.bucket-name:pointtils-api-tests-f28f947a}") String bucketName,
                     S3Client s3Client) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
    }

    public String uploadFile(MultipartFile file, String userId) throws IOException {
        String key = "users/" + userId + "/" + Instant.now().toEpochMilli() + "-" + file.getOriginalFilename();

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(putObjectRequest, software.amazon.awssdk.core.sync.RequestBody.fromBytes(file.getBytes()));

        return String.format("https://%s.s3.amazonaws.com/%s", bucketName, key);
    }
}

