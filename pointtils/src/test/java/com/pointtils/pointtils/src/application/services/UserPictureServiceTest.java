package com.pointtils.pointtils.src.application.services;

import com.pointtils.pointtils.src.application.dto.requests.UserPicturePostRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.UserResponseDTO;
import com.pointtils.pointtils.src.core.domain.entities.User;
import com.pointtils.pointtils.src.infrastructure.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserPictureServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private S3Service s3Service;

    @InjectMocks
    private UserPictureService userPictureService;

    private User user;
    private UUID userId;
    private String pictureUrl;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        pictureUrl = "https://test-bucket.s3.amazonaws.com/users/123/test.jpg";
        
        user = new User() {
            @Override
            public String getDisplayName() { return "Test User"; }
            @Override
            public String getDocument() { return "123456789"; }
        };
        user.setId(userId);
        user.setEmail("test@example.com");
        user.setPicture(pictureUrl);
        user.setPassword("password");
        user.setPhone("1234567890");
    }

    @Test
    @DisplayName("Deve deletar foto com sucesso quando S3 está habilitado")
    void shouldDeletePictureSuccessfully() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(s3Service.deleteFile(pictureUrl)).thenReturn(true);
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        userPictureService.deletePicture(userId);

        // Assert
        verify(userRepository, times(1)).findById(userId);
        verify(s3Service, times(1)).deleteFile(pictureUrl);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Deve deletar foto do banco mesmo quando S3 está desabilitado")
    void shouldDeletePictureFromDatabaseWhenS3IsDisabled() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(s3Service.deleteFile(pictureUrl)).thenThrow(new UnsupportedOperationException("S3 desabilitado"));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        userPictureService.deletePicture(userId);

        // Assert - Não deve lançar exceção e deve atualizar o banco
        verify(userRepository, times(1)).findById(userId);
        verify(s3Service, times(1)).deleteFile(pictureUrl);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Deve deletar foto do banco mesmo quando S3 falha com RuntimeException")
    void shouldDeletePictureFromDatabaseWhenS3Fails() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(s3Service.deleteFile(pictureUrl)).thenThrow(new RuntimeException("Erro no S3"));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        userPictureService.deletePicture(userId);

        // Assert - Não deve lançar exceção e deve atualizar o banco
        verify(userRepository, times(1)).findById(userId);
        verify(s3Service, times(1)).deleteFile(pictureUrl);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Deve deletar foto quando usuário não tem foto")
    void shouldDeletePictureWhenUserHasNoPicture() {
        // Arrange
        user.setPicture(null);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        userPictureService.deletePicture(userId);

        // Assert - Não deve chamar s3Service.deleteFile
        verify(userRepository, times(1)).findById(userId);
        verify(s3Service, never()).deleteFile(anyString());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não existe")
    void shouldThrowExceptionWhenUserNotFound() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            userPictureService.deletePicture(userId);
        });

        assertEquals("Usuário não encontrado", exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
        verify(s3Service, never()).deleteFile(anyString());
    }

    @Test
    @DisplayName("Deve retornar true quando S3 está habilitado")
    void shouldReturnTrueWhenS3IsEnabled() {
        // Arrange
        when(s3Service.isS3Enabled()).thenReturn(true);

        // Act
        boolean result = userPictureService.isPictureUploadEnabled();

        // Assert
        assertTrue(result);
        verify(s3Service, times(1)).isS3Enabled();
    }

    @Test
    @DisplayName("Deve retornar false quando S3 está desabilitado")
    void shouldReturnFalseWhenS3IsDisabled() {
        // Arrange
        when(s3Service.isS3Enabled()).thenReturn(false);

        // Act
        boolean result = userPictureService.isPictureUploadEnabled();

        // Assert
        assertFalse(result);
        verify(s3Service, times(1)).isS3Enabled();
    }

    @Test
    @DisplayName("Deve atualizar foto quando S3 está habilitado")
    void shouldUpdatePictureSuccessfully() throws IOException {
        // Arrange
        MultipartFile file = mock(MultipartFile.class);
        UserPicturePostRequestDTO request = new UserPicturePostRequestDTO(userId, file);
        String newPictureUrl = "https://test-bucket.s3.amazonaws.com/users/123/new-picture.jpg";
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(s3Service.isS3Enabled()).thenReturn(true);
        when(s3Service.uploadFile(file, userId.toString())).thenReturn(newPictureUrl);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            assertEquals(newPictureUrl, savedUser.getPicture());
            return savedUser;
        });

        // Act
        UserResponseDTO result = userPictureService.updatePicture(request);

        // Assert
        assertNotNull(result);
        verify(userRepository, times(1)).findById(userId);
        verify(s3Service, times(1)).isS3Enabled();
        verify(s3Service, times(1)).uploadFile(file, userId.toString());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar foto quando S3 está desabilitado")
    void shouldThrowExceptionWhenUpdatingPictureWithS3Disabled() throws IOException {
        // Arrange
        MultipartFile file = mock(MultipartFile.class);
        UserPicturePostRequestDTO request = new UserPicturePostRequestDTO(userId, file);
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(s3Service.isS3Enabled()).thenReturn(false);

        // Act & Assert
        UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class, () -> {
            userPictureService.updatePicture(request);
        });

        assertTrue(exception.getMessage().contains("Upload de fotos está desabilitado"));
        verify(userRepository, times(1)).findById(userId);
        verify(s3Service, times(1)).isS3Enabled();
        verify(s3Service, never()).uploadFile(any(), anyString());
        verify(userRepository, never()).save(any(User.class));
    }
}

