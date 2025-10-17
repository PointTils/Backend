package com.pointtils.pointtils.src.application.controllers;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import com.pointtils.pointtils.src.application.dto.requests.InterpreterDocumentRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.InterpreterDocumentResponseDTO;
import com.pointtils.pointtils.src.application.services.InterpreterDocumentService;

import jakarta.persistence.EntityNotFoundException;

class InterpreterDocumentControllerTest {

    private InterpreterDocumentService interpreterDocumentService;
    private InterpreterDocumentController interpreterDocumentController;

    @BeforeEach
    void setUp() {
        interpreterDocumentService = mock(InterpreterDocumentService.class);
        interpreterDocumentController = new InterpreterDocumentController(interpreterDocumentService);
    }

    @Test
    void shouldSaveDocumentsSuccessfully() throws IOException {
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
        assertEquals(200, response.getStatusCodeValue());
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
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        assertEquals("Documento encontrado", response.getBody().get(0).getMessage());
        verify(interpreterDocumentService, times(1)).getDocumentsByInterpreter(any(UUID.class));
    }

    @Test
    void shouldUpdateDocumentSuccessfully() throws IOException {
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
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Documento atualizado com sucesso", response.getBody().getMessage());
        verify(interpreterDocumentService, times(1)).updateDocument(any(UUID.class), any(InterpreterDocumentRequestDTO.class));
    }

    @Test
    void shouldHandleIOExceptionWhenSavingDocuments() throws IOException {
        // Arrange
        UUID interpreterId = UUID.randomUUID();
        MockMultipartFile file = new MockMultipartFile(
                "files",
                "test-document.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                "Test content".getBytes()
        );

        when(interpreterDocumentService.saveDocuments(any(UUID.class), anyList()))
                .thenThrow(new IOException("Erro ao salvar documento"));

        // Act & Assert
        try {
            interpreterDocumentController.saveDocuments(interpreterId, List.of(file));
        } catch (IOException e) {
            assertEquals("Erro ao salvar documento", e.getMessage());
        }
        verify(interpreterDocumentService, times(1)).saveDocuments(any(UUID.class), anyList());
    }

    @Test
    void shouldHandleIOExceptionWhenUpdatingDocument() throws IOException {
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
                .thenThrow(new IOException("Erro ao atualizar documento"));

        // Act & Assert
        try {
            interpreterDocumentController.uploadDocument(interpreterId, documentId, file);
        } catch (IOException e) {
            assertEquals("Erro ao atualizar documento", e.getMessage());
        }
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
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(0, response.getBody().size());
        verify(interpreterDocumentService, times(1)).getDocumentsByInterpreter(any(UUID.class));
    }

    @Test
    void shouldHandleUnsupportedOperationExceptionWhenSavingDocuments() throws IOException {
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

        // Act & Assert
        UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class, () -> {
            interpreterDocumentController.saveDocuments(interpreterId, List.of(file));
        });
        assertEquals("Upload de documentos está desabilitado.", exception.getMessage());
    }

    @Test
    void shouldHandleUnsupportedOperationExceptionWhenUpdatingDocument() throws IOException {
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
        UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class, () -> {
            interpreterDocumentController.uploadDocument(interpreterId, documentId, file);
        });
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
    void shouldHandleEntityNotFoundExceptionWhenUpdatingDocument() throws IOException {
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
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            interpreterDocumentController.uploadDocument(interpreterId, documentId, file);
        });
        assertEquals("Documento não encontrado", exception.getMessage());
    }

    @Test
    void shouldHandleEntityNotFoundExceptionWhenSavingDocuments() throws IOException {
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

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            interpreterDocumentController.saveDocuments(interpreterId, List.of(file));
        });
        assertEquals("Intérprete não encontrado", exception.getMessage());
    }
}