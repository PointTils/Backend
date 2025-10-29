package com.pointtils.pointtils.src.application.controllers;

import com.pointtils.pointtils.src.application.dto.requests.InterpreterDocumentRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.InterpreterDocumentResponseDTO;
import com.pointtils.pointtils.src.application.services.InterpreterDocumentService;
import com.pointtils.pointtils.src.core.domain.exceptions.FileUploadException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InterpreterDocumentControllerTest {

    @Mock
    private InterpreterDocumentService interpreterDocumentService;
    @InjectMocks
    private InterpreterDocumentController interpreterDocumentController;

    @Test
    void shouldSaveDocumentsSuccessfully() {
        // Arrange
        UUID interpreterId = UUID.randomUUID();
        MockMultipartFile file = new MockMultipartFile(
                "files",
                "test-document.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                "Test content".getBytes()
        );

        InterpreterDocumentResponseDTO responseDTO = new InterpreterDocumentResponseDTO();
        responseDTO.setSuccess(true);
        responseDTO.setMessage("Documento enviado com sucesso");
        responseDTO.setData(new InterpreterDocumentResponseDTO.DocumentData(
                UUID.randomUUID(),
                interpreterId,
                "https://s3.amazonaws.com/documents/test-document.pdf"
        ));

        when(interpreterDocumentService.saveDocuments(any(UUID.class), anyList()))
                .thenReturn(List.of(responseDTO));

        // Act
        ResponseEntity<List<InterpreterDocumentResponseDTO>> response = interpreterDocumentController.saveDocuments(interpreterId, List.of(file));

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("Documento enviado com sucesso", response.getBody().get(0).getMessage());
        verify(interpreterDocumentService, times(1)).saveDocuments(any(UUID.class), anyList());
    }

    @Test
    void shouldGetDocumentsByInterpreterSuccessfully() {
        // Arrange
        UUID interpreterId = UUID.randomUUID();
        InterpreterDocumentResponseDTO responseDTO = new InterpreterDocumentResponseDTO();
        responseDTO.setSuccess(true);
        responseDTO.setMessage("Documento encontrado");
        responseDTO.setData(new InterpreterDocumentResponseDTO.DocumentData(
                UUID.randomUUID(),
                interpreterId,
                "https://s3.amazonaws.com/documents/test-document.pdf"
        ));

        when(interpreterDocumentService.getDocumentsByInterpreter(any(UUID.class)))
                .thenReturn(List.of(responseDTO));

        // Act
        ResponseEntity<List<InterpreterDocumentResponseDTO>> response = interpreterDocumentController.getDocumentsByInterpreter(interpreterId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("Documento encontrado", response.getBody().get(0).getMessage());
        verify(interpreterDocumentService, times(1)).getDocumentsByInterpreter(any(UUID.class));
    }

    @Test
    void shouldUpdateDocumentSuccessfully() {
        // Arrange
        UUID interpreterId = UUID.randomUUID();
        UUID documentId = UUID.randomUUID();
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "updated-document.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                "Updated content".getBytes()
        );

        InterpreterDocumentResponseDTO responseDTO = new InterpreterDocumentResponseDTO();
        responseDTO.setSuccess(true);
        responseDTO.setMessage("Documento atualizado com sucesso");
        responseDTO.setData(new InterpreterDocumentResponseDTO.DocumentData(
                documentId,
                interpreterId,
                "https://s3.amazonaws.com/documents/updated-document.pdf"
        ));

        when(interpreterDocumentService.updateDocument(any(UUID.class), any(InterpreterDocumentRequestDTO.class)))
                .thenReturn(responseDTO);

        // Act
        ResponseEntity<InterpreterDocumentResponseDTO> response = interpreterDocumentController.uploadDocument(interpreterId, documentId, file);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Documento atualizado com sucesso", response.getBody().getMessage());
        verify(interpreterDocumentService, times(1)).updateDocument(any(UUID.class), any(InterpreterDocumentRequestDTO.class));
    }

    @Test
    void shouldHandleFileUploadExceptionWhenSavingDocuments() {
        // Arrange
        UUID interpreterId = UUID.randomUUID();
        MockMultipartFile file = new MockMultipartFile(
                "files",
                "test-document.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                "Test content".getBytes()
        );

        when(interpreterDocumentService.saveDocuments(any(UUID.class), anyList()))
                .thenThrow(new FileUploadException("test-document.pdf", new RuntimeException()));
        List<MultipartFile> fileList = List.of(file);

        // Act & Assert
        FileUploadException exception = assertThrows(FileUploadException.class,
                () -> interpreterDocumentController.saveDocuments(interpreterId, fileList));
        assertEquals("Erro ao fazer upload do arquivo test-document.pdf", exception.getMessage());
        verify(interpreterDocumentService, times(1)).saveDocuments(any(UUID.class), anyList());
    }

    @Test
    void shouldHandleFileUploadExceptionWhenUpdatingDocument() {
        // Arrange
        UUID interpreterId = UUID.randomUUID();
        UUID documentId = UUID.randomUUID();
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "updated-document.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                "Updated content".getBytes()
        );

        when(interpreterDocumentService.updateDocument(any(UUID.class), any(InterpreterDocumentRequestDTO.class)))
                .thenThrow(new FileUploadException("updated-document.pdf", new RuntimeException()));

        // Act & Assert
        FileUploadException exception = assertThrows(FileUploadException.class,
                () -> interpreterDocumentController.uploadDocument(interpreterId, documentId, file));
        assertEquals("Erro ao fazer upload do arquivo updated-document.pdf", exception.getMessage());
        verify(interpreterDocumentService, times(1)).updateDocument(any(UUID.class), any(InterpreterDocumentRequestDTO.class));
    }

    @Test
    void shouldHandleNoDocumentsFound() {
        // Arrange
        UUID interpreterId = UUID.randomUUID();

        when(interpreterDocumentService.getDocumentsByInterpreter(any(UUID.class)))
                .thenReturn(List.of());

        // Act
        ResponseEntity<List<InterpreterDocumentResponseDTO>> response = interpreterDocumentController.getDocumentsByInterpreter(interpreterId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody().size());
        verify(interpreterDocumentService, times(1)).getDocumentsByInterpreter(any(UUID.class));
    }

    @Test
    void shouldHandleUnsupportedOperationExceptionWhenSavingDocuments() {
        // Arrange
        UUID interpreterId = UUID.randomUUID();
        MockMultipartFile file = new MockMultipartFile(
                "files",
                "test-document.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                "Test content".getBytes()
        );

        when(interpreterDocumentService.saveDocuments(any(UUID.class), anyList()))
                .thenThrow(new UnsupportedOperationException("Upload de documentos está desabilitado."));
        List<MultipartFile> fileList = List.of(file);

        // Act & Assert
        UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class,
                () -> interpreterDocumentController.saveDocuments(interpreterId, fileList));
        assertEquals("Upload de documentos está desabilitado.", exception.getMessage());
    }

    @Test
    void shouldHandleUnsupportedOperationExceptionWhenUpdatingDocument() {
        // Arrange
        UUID interpreterId = UUID.randomUUID();
        UUID documentId = UUID.randomUUID();
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "updated-document.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                "Updated content".getBytes()
        );

        when(interpreterDocumentService.updateDocument(any(UUID.class), any(InterpreterDocumentRequestDTO.class)))
                .thenThrow(new UnsupportedOperationException("Upload de documentos está desabilitado."));

        // Act & Assert
        UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class,
                () -> interpreterDocumentController.uploadDocument(interpreterId, documentId, file));
        assertEquals("Upload de documentos está desabilitado.", exception.getMessage());
    }

    @Test
    void shouldHandleEntityNotFoundExceptionWhenGettingDocuments() {
        // Arrange
        UUID interpreterId = UUID.randomUUID();

        when(interpreterDocumentService.getDocumentsByInterpreter(any(UUID.class)))
                .thenThrow(new EntityNotFoundException("Intérprete não encontrado"));

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            interpreterDocumentController.getDocumentsByInterpreter(interpreterId);
        });
        assertEquals("Intérprete não encontrado", exception.getMessage());
    }

    @Test
    void shouldHandleEntityNotFoundExceptionWhenUpdatingDocument() {
        // Arrange
        UUID interpreterId = UUID.randomUUID();
        UUID documentId = UUID.randomUUID();
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "updated-document.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                "Updated content".getBytes()
        );

        when(interpreterDocumentService.updateDocument(any(UUID.class), any(InterpreterDocumentRequestDTO.class)))
                .thenThrow(new EntityNotFoundException("Documento não encontrado"));

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> interpreterDocumentController.uploadDocument(interpreterId, documentId, file));
        assertEquals("Documento não encontrado", exception.getMessage());
    }

    @Test
    void shouldHandleEntityNotFoundExceptionWhenSavingDocuments() {
        // Arrange
        UUID interpreterId = UUID.randomUUID();
        MockMultipartFile file = new MockMultipartFile(
                "files",
                "test-document.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                "Test content".getBytes()
        );

        when(interpreterDocumentService.saveDocuments(any(UUID.class), anyList()))
                .thenThrow(new EntityNotFoundException("Intérprete não encontrado"));
        List<MultipartFile> fileList = List.of(file);

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> interpreterDocumentController.saveDocuments(interpreterId, fileList));
        assertEquals("Intérprete não encontrado", exception.getMessage());
    }
}