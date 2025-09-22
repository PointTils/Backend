package com.pointtils.pointtils.src.application.services;


import org.flywaydb.core.api.configuration.S3ClientFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;
    private final String bucketName = "pointtils_api_tests"; //tem que trocar para o nosso bucket(não sei qual eh...)

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

