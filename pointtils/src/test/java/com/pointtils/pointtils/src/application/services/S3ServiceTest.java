package com.pointtils.pointtils.src.application.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.http.SdkHttpResponse;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class),
                any(software.amazon.awssdk.core.sync.RequestBody.class));
    }

    @Test
    void shouldThrowExceptionWhenS3IsDisabled() {
        // Arrange
        S3Service disabledS3Service = new S3Service("test-bucket", false, null);
        MultipartFile file = mock(MultipartFile.class);

        // Act & Assert
        UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class,
                () -> disabledS3Service.uploadFile(file, "user123"));
        assertEquals("Upload de arquivos para S3 está desabilitado. Configure spring.cloud.aws.s3.enabled=true para habilitar.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenGettingFileAndS3IsDisabled() {
        // Arrange
        S3Service disabledS3Service = new S3Service("test-bucket", false, null);
        String key = "some-key";
        // Act & Assert
        UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class,
                () -> disabledS3Service.getFile(key));
        assertEquals("Download de arquivos está desabilitado.", exception.getMessage());
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldGetFileSuccessfully() throws IOException {
        // Arrange
        String key = "some-key";
        byte[] expectedBytes = "file-content".getBytes();

        ResponseBytes<GetObjectResponse> responseBytes = mock(ResponseBytes.class);
        when(responseBytes.asByteArray()).thenReturn(expectedBytes);

        when(s3Client.getObject(any(GetObjectRequest.class), eq(ResponseTransformer.toBytes())))
                .thenReturn(responseBytes);

        // Act
        byte[] result = s3Service.getFile(key);

        // Assert
        assertNotNull(result);
        assertEquals(new String(expectedBytes), new String(result));
        verify(s3Client, times(1)).getObject(any(GetObjectRequest.class), eq(ResponseTransformer.toBytes()));
    }

    @Test
    void shouldThrowIOExceptionWhenGetFileFails() {
        // Arrange
        String key = "some-key";

        when(s3Client.getObject(any(GetObjectRequest.class), eq(ResponseTransformer.toBytes())))
                .thenThrow(new RuntimeException("S3 error"));
        // Act & Assert
        IOException exception = assertThrows(IOException.class, () -> s3Service.getFile(key));
        assertEquals("Falha inesperada ao baixar arquivo do S3", exception.getMessage());
    }

    @Test
    void shouldThrowIOExceptionWhenS3ExceptionOccurs() {
        // Arrange
        String key = "some-key";
        AwsServiceException s3Exception = S3Exception
                .builder()
                .awsErrorDetails(AwsErrorDetails.builder()
                        .errorMessage("Not Found")
                        .build())
                .build();

        when(s3Client.getObject(any(GetObjectRequest.class), eq(ResponseTransformer.toBytes())))
                .thenThrow(s3Exception);
        // Act & Assert
        IOException exception = assertThrows(IOException.class, () -> s3Service.getFile(key));
        assertEquals("Erro ao obter arquivo do S3: Not Found", exception.getMessage());
    }

    @Test
    void shouldDeleteFileSuccessfully() {
        // Arrange
        String documentUrl = "https://test-bucket.s3.amazonaws.com/users/user123/test-file.txt";
        DeleteObjectResponse mockResponse = mock(DeleteObjectResponse.class);
        SdkHttpResponse mockSdkHttpResponse = mock(SdkHttpResponse.class);
        when(mockResponse.sdkHttpResponse()).thenReturn(mockSdkHttpResponse);
        when(mockSdkHttpResponse.isSuccessful()).thenReturn(Boolean.TRUE);
        ArgumentCaptor<DeleteObjectRequest> responseArgumentCaptor = ArgumentCaptor.forClass(DeleteObjectRequest.class);
        when(s3Client.deleteObject(responseArgumentCaptor.capture())).thenReturn(mockResponse);

        // Act
        s3Service.deleteFile(documentUrl);

        // Assert
        verify(s3Client).deleteObject(any(DeleteObjectRequest.class));
        assertEquals("test-bucket", responseArgumentCaptor.getValue().bucket());
        assertEquals("users/user123/test-file.txt", responseArgumentCaptor.getValue().key());
    }

    @Test
    void deleteShouldThrowExceptionIfS3IsDisabled() {
        // Arrange
        S3Service disabledS3Service = new S3Service("test-bucket", false, null);
        String documentUrl = "https://test-bucket.s3.amazonaws.com/users/user123/test-file.txt";

        // Act & Assert
        UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class,
                () -> disabledS3Service.deleteFile(documentUrl));
        assertEquals("Delete de arquivos está desabilitado.", exception.getMessage());
    }
}