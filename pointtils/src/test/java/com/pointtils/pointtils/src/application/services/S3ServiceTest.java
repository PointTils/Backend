package com.pointtils.pointtils.src.application.services;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mockito;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

class S3ServiceTest {

    private S3Client s3Client;
    private S3Service s3Service;

    @BeforeEach 
    void setUp() {
        s3Client = Mockito.mock(S3Client.class);
        s3Service = new S3Service("test-bucket", true, s3Client);
    }

    @Test
    void shouldUploadFileSuccessfully() throws IOException {
        // Arrange
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("test-file.txt");
        when(file.getContentType()).thenReturn("text/plain");
        when(file.getBytes()).thenReturn("test-content".getBytes());

        // Act
        String result = s3Service.uploadFile(file, "user123");

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("https://test-bucket.s3.amazonaws.com/users/user123/"));
        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(software.amazon.awssdk.core.sync.RequestBody.class));
    }

    @Test
    void shouldThrowExceptionWhenS3IsDisabled() {
        // Arrange
        S3Service disabledS3Service = new S3Service("test-bucket", false, null);
        MultipartFile file = mock(MultipartFile.class);

        // Act & Assert
        UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class, () -> {
            disabledS3Service.uploadFile(file, "user123");
        });
        assertEquals("Upload de arquivos para S3 está desabilitado. Configure spring.cloud.aws.s3.enabled=true para habilitar.", exception.getMessage());
    }

    @Test
    void shouldReturnTrueWhenS3IsEnabled() {
        // Act
        boolean result = s3Service.isS3Enabled();

        // Assert
        assertTrue(result);
    }

    @Test
    void shouldReturnFalseWhenS3IsDisabled() {
        // Arrange
        S3Service disabledS3Service = new S3Service("test-bucket", false, null);

        // Act
        boolean result = disabledS3Service.isS3Enabled();

        // Assert
        assertFalse(result);
    }

    @Test
    void shouldDeleteFileSuccessfully() {
        // Arrange
        String fileUrl = "https://test-bucket.s3.amazonaws.com/users/123/test.jpg";

        // Act
        boolean result = s3Service.deleteFile(fileUrl);

        // Assert
        assertTrue(result);
        verify(s3Client, times(1)).deleteObject(any(DeleteObjectRequest.class));
    }

    @Test
    void shouldThrowExceptionWhenDeletingFileWithS3Disabled() {
        // Arrange
        S3Service disabledS3Service = new S3Service("test-bucket", false, null);
        String fileUrl = "https://test-bucket.s3.amazonaws.com/users/123/test.jpg";

        // Act & Assert
        UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class, () -> {
            disabledS3Service.deleteFile(fileUrl);
        });
        assertEquals("Deleção de arquivos para S3 está desabilitado. Configure spring.cloud.aws.s3.enabled=true para habilitar.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenDeletingFileWithInvalidUrl() {
        // Arrange
        String invalidUrl = "invalid-url";

        // Act & Assert
        UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class, () -> {
            s3Service.deleteFile(invalidUrl);
        });
        assertEquals("URL do arquivo inválida para deleção.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenDeletingFileWithNullUrl() {
        // Act & Assert
        UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class, () -> {
            s3Service.deleteFile(null);
        });
        assertTrue(exception.getMessage().contains("Deleção de arquivos para S3 está desabilitado"));
    }

    @Test
    void shouldThrowExceptionWhenDeletingFileWithEmptyUrl() {
        // Act & Assert
        UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class, () -> {
            s3Service.deleteFile("");
        });
        assertTrue(exception.getMessage().contains("Deleção de arquivos para S3 está desabilitado"));
    }

    @Test
    void shouldExtractKeyFromRegionalUrl() {
        // Arrange
        String regionalUrl = "https://test-bucket.s3.us-east-1.amazonaws.com/users/123/test.jpg";

        // Act
        boolean result = s3Service.deleteFile(regionalUrl);

        // Assert
        assertTrue(result);
        verify(s3Client, times(1)).deleteObject(any(DeleteObjectRequest.class));
    }

    @Test
    void shouldExtractKeyFromS3AmazonawsUrl() {
        // Arrange
        String s3Url = "https://s3.amazonaws.com/test-bucket/users/123/test.jpg";

        // Act
        boolean result = s3Service.deleteFile(s3Url);

        // Assert
        assertTrue(result);
        verify(s3Client, times(1)).deleteObject(any(DeleteObjectRequest.class));
    }
}
