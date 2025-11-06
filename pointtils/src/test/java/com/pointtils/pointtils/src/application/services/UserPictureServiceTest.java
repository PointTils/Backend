package com.pointtils.pointtils.src.application.services;

import com.pointtils.pointtils.src.application.dto.requests.UserPicturePostRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.UserResponseDTO;
import com.pointtils.pointtils.src.core.domain.entities.User;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserPictureServiceTest {

    @Mock
    private UserService userService;

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
        when(userService.findById(userId)).thenReturn(user);
        doNothing().when(s3Service).deleteFile(pictureUrl);
        when(userService.updateUser(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        userPictureService.deletePicture(userId);

        // Assert
        verify(userService, times(1)).findById(userId);
        verify(s3Service, times(1)).deleteFile(pictureUrl);
        verify(userService, times(1)).updateUser(any(User.class));
    }

    @Test
    @DisplayName("Deve deletar foto do banco mesmo quando S3 está desabilitado")
    void shouldDeletePictureFromDatabaseWhenS3IsDisabled() {
        // Arrange
        when(userService.findById(userId)).thenReturn(user);
        doThrow(new UnsupportedOperationException("S3 desabilitado")).when(s3Service).deleteFile(pictureUrl);
        when(userService.updateUser(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        userPictureService.deletePicture(userId);

        // Assert - Não deve lançar exceção e deve atualizar o banco
        verify(userService, times(1)).findById(userId);
        verify(s3Service, times(1)).deleteFile(pictureUrl);
        verify(userService, times(1)).updateUser(any(User.class));
    }

    @Test
    @DisplayName("Deve deletar foto do banco mesmo quando S3 falha com RuntimeException")
    void shouldDeletePictureFromDatabaseWhenS3Fails() {
        // Arrange
        when(userService.findById(userId)).thenReturn(user);
        doThrow(new RuntimeException("Erro no S3")).when(s3Service).deleteFile(pictureUrl);
        when(userService.updateUser(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        userPictureService.deletePicture(userId);

        // Assert - Não deve lançar exceção e deve atualizar o banco
        verify(userService, times(1)).findById(userId);
        verify(s3Service, times(1)).deleteFile(pictureUrl);
        verify(userService, times(1)).updateUser(any(User.class));
    }

    @Test
    @DisplayName("Deve deletar foto quando usuário não tem foto")
    void shouldDeletePictureWhenUserHasNoPicture() {
        // Arrange
        user.setPicture(null);
        when(userService.findById(userId)).thenReturn(user);
        when(userService.updateUser(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        userPictureService.deletePicture(userId);

        // Assert - Não deve chamar s3Service.deleteFile
        verify(userService, times(1)).findById(userId);
        verify(s3Service, never()).deleteFile(anyString());
        verify(userService, times(1)).updateUser(any(User.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não existe")
    void shouldThrowExceptionWhenUserNotFound() {
        // Arrange
        when(userService.findById(userId)).thenThrow(new EntityNotFoundException("Usuário não encontrado"));

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            userPictureService.deletePicture(userId);
        });

        assertEquals("Usuário não encontrado", exception.getMessage());
        verify(userService, times(1)).findById(userId);
        verify(s3Service, never()).deleteFile(anyString());
        verify(userService, never()).updateUser(any(User.class));
    }

    @Test
    @DisplayName("Deve atualizar foto quando upload para S3 ocorre com sucesso")
    void shouldUpdatePictureSuccessfully() throws IOException {
        // Arrange
        MultipartFile file = mock(MultipartFile.class);
        UserPicturePostRequestDTO request = new UserPicturePostRequestDTO(userId, file);
        String newPictureUrl = "https://test-bucket.s3.amazonaws.com/users/123/new-picture.jpg";

        when(userService.findById(userId)).thenReturn(user);
        when(s3Service.uploadFile(file, userId.toString())).thenReturn(newPictureUrl);
        when(userService.updateUser(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            assertEquals(newPictureUrl, savedUser.getPicture());
            return savedUser;
        });

        // Act
        UserResponseDTO result = userPictureService.updatePicture(request);

        // Assert
        assertNotNull(result);
        verify(userService, times(1)).findById(userId);
        verify(s3Service, times(1)).uploadFile(file, userId.toString());
        verify(userService, times(1)).updateUser(any(User.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar foto quando upload para S3 falha/desabilitado")
    void shouldThrowExceptionWhenUpdatingPictureWithS3Failure() {
        // Arrange
        MultipartFile file = mock(MultipartFile.class);
        UserPicturePostRequestDTO request = new UserPicturePostRequestDTO(userId, file);

        when(userService.findById(userId)).thenReturn(user);
        try {
            when(s3Service.uploadFile(file, userId.toString())).thenThrow(new UnsupportedOperationException("Upload de fotos está desabilitado"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Act & Assert
        UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class, () -> {
            userPictureService.updatePicture(request);
        });

        assertTrue(exception.getMessage().contains("desabilitado"));
        verify(userService, times(1)).findById(userId);
        try {
            verify(s3Service, times(1)).uploadFile(file, userId.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        verify(userService, never()).updateUser(any(User.class));
    }

    @Test
    @DisplayName("updatePicture - Deve enviar arquivo ao S3, atualizar foto e retornar UserResponseDTO com URL")
    void updatePicture_success() throws IOException {
        UUID uid = UUID.randomUUID();
        MultipartFile file = new MockMultipartFile("file", "foto.png", "image/png", "conteudo".getBytes());
        String s3Url = "https://test-bucket.s3.amazonaws.com/users/" + uid + "/test.jpg";

        User u = new User() {
            @Override
            public String getDisplayName() { return "Test User"; }
            @Override
            public String getDocument() { return "123456789"; }
        };
        u.setId(uid);
        u.setEmail("test@example.com");

        when(userService.findById(uid)).thenReturn(u);
        when(s3Service.uploadFile(file, uid.toString())).thenReturn(s3Url);
        when(userService.updateUser(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponseDTO response = userPictureService.updatePicture(
                UserPicturePostRequestDTO.builder()
                        .userId(uid)
                        .file(file)
                        .build()
        );

        assertNotNull(response);
        assertEquals(uid, response.getId());
        assertEquals(s3Url, response.getPicture());

        verify(userService).findById(uid);
        verify(s3Service).uploadFile(file, uid.toString());
        verify(userService).updateUser(any(User.class));
    }

    @Test
    @DisplayName("updatePicture - Deve lançar EntityNotFoundException quando usuário não existe")
    void updatePicture_userNotFound() {
        UUID uid = UUID.randomUUID();
        MultipartFile file = new MockMultipartFile("file", "foto.png", "image/png", "conteudo".getBytes());

        when(userService.findById(uid)).thenThrow(new EntityNotFoundException("Usuário não encontrado"));

        assertThrows(EntityNotFoundException.class, () ->
                userPictureService.updatePicture(
                        UserPicturePostRequestDTO.builder()
                                .userId(uid)
                                .file(file)
                                .build()
                )
        );

        verify(userService).findById(uid);
        verifyNoInteractions(s3Service);
        verify(userService, never()).updateUser(any());
    }
}

