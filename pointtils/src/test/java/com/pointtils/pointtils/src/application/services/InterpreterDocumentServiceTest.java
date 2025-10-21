package com.pointtils.pointtils.src.application.services;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.web.multipart.MultipartFile;

import com.pointtils.pointtils.src.application.dto.requests.InterpreterDocumentRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.InterpreterDocumentResponseDTO;
import com.pointtils.pointtils.src.core.domain.entities.Interpreter;
import com.pointtils.pointtils.src.core.domain.entities.InterpreterDocuments;
import com.pointtils.pointtils.src.infrastructure.repositories.InterpreterDocumentsRepository;
import com.pointtils.pointtils.src.infrastructure.repositories.InterpreterRepository;

import jakarta.persistence.EntityNotFoundException;

class InterpreterDocumentServiceTest {

    private InterpreterRepository interpreterRepository;
    private InterpreterDocumentsRepository interpreterDocumentsRepository;
    private S3Service s3Service;
    private EmailService emailService;
    private InterpreterDocumentService interpreterDocumentService;
    @BeforeEach
    void setUp() {
        interpreterRepository = mock(InterpreterRepository.class);
        interpreterDocumentsRepository = mock(InterpreterDocumentsRepository.class);
        s3Service = mock(S3Service.class);
        emailService = mock(EmailService.class);
        interpreterDocumentService = new InterpreterDocumentService(interpreterRepository,
                interpreterDocumentsRepository, s3Service, emailService);
    }

    @Test
    void shouldSaveDocumentsSuccessfully() throws IOException {
        // Arrange
        UUID interpreterId = UUID.randomUUID();
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("test-document.pdf");
        when(s3Service.isS3Enabled()).thenReturn(true);
        when(s3Service.uploadFile(any(MultipartFile.class), anyString()))
                .thenReturn("https://s3.amazonaws.com/documents/test-document.pdf");

        Interpreter interpreter = new Interpreter();
        interpreter.setId(interpreterId); // Certifique-se de que o ID está preenchido
        when(interpreterRepository.findById(interpreterId)).thenReturn(Optional.of(interpreter));

        InterpreterDocuments savedDocument = new InterpreterDocuments();
        savedDocument.setDocument("https://s3.amazonaws.com/documents/test-document.pdf");
        savedDocument.setInterpreter(interpreter); // Associe o Interpreter ao documento
        when(interpreterDocumentsRepository.save(any(InterpreterDocuments.class))).thenReturn(savedDocument);

        // Act
        List<InterpreterDocumentResponseDTO> result = interpreterDocumentService.saveDocuments(interpreterId,
                List.of(file));

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("https://s3.amazonaws.com/documents/test-document.pdf", result.get(0).getData().getDocument());
        verify(s3Service, times(1)).uploadFile(any(MultipartFile.class), anyString());
        verify(interpreterDocumentsRepository, times(1)).save(any(InterpreterDocuments.class));
    }

    @Test
    void shouldThrowExceptionWhenInterpreterNotFound() {
        // Arrange
        UUID interpreterId = UUID.randomUUID();
        when(interpreterRepository.findById(interpreterId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            interpreterDocumentService.saveDocuments(interpreterId, List.of(mock(MultipartFile.class)));
        });
        assertEquals("Intérprete não encontrado", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenS3IsDisabled() {
        // Arrange
        UUID interpreterId = UUID.randomUUID();
        when(interpreterRepository.findById(interpreterId)).thenReturn(Optional.of(new Interpreter()));
        when(s3Service.isS3Enabled()).thenReturn(false);

        // Act & Assert
        UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class, () -> {
            interpreterDocumentService.saveDocuments(interpreterId, List.of(mock(MultipartFile.class)));
        });
        assertEquals("Upload de documentos está desabilitado.", exception.getMessage());
    }

    @Test
    void shouldGetDocumentsByInterpreter() {
        // Arrange
        UUID interpreterId = UUID.randomUUID();
        Interpreter interpreter = new Interpreter();
        interpreter.setId(interpreterId); // Certifique-se de que o ID está preenchido
        when(interpreterRepository.findById(interpreterId)).thenReturn(Optional.of(interpreter));

        InterpreterDocuments document = new InterpreterDocuments();
        document.setDocument("https://s3.amazonaws.com/documents/test-document.pdf");
        document.setInterpreter(interpreter); // Associe o Interpreter ao documento
        when(interpreterDocumentsRepository.findByInterpreter(interpreter)).thenReturn(List.of(document));

        // Act
        List<InterpreterDocumentResponseDTO> result = interpreterDocumentService
                .getDocumentsByInterpreter(interpreterId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("https://s3.amazonaws.com/documents/test-document.pdf", result.get(0).getData().getDocument());
        verify(interpreterDocumentsRepository, times(1)).findByInterpreter(interpreter);
    }

    @Test
    void shouldUpdateDocumentSuccessfully() throws IOException {
        // Arrange
        UUID documentId = UUID.randomUUID();
        UUID interpreterId = UUID.randomUUID();
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("updated-document.pdf");

        Interpreter interpreter = new Interpreter();
        interpreter.setId(interpreterId); // Certifique-se de que o ID está preenchido

        InterpreterDocuments existingDocument = new InterpreterDocuments();
        existingDocument.setInterpreter(interpreter); // Associe o Interpreter ao documento
        when(interpreterDocumentsRepository.findById(documentId)).thenReturn(Optional.of(existingDocument));

        when(interpreterRepository.findById(interpreterId)).thenReturn(Optional.of(interpreter));

        when(s3Service.isS3Enabled()).thenReturn(true);
        when(s3Service.uploadFile(any(MultipartFile.class), anyString()))
                .thenReturn("https://s3.amazonaws.com/documents/updated-document.pdf");

        InterpreterDocuments updatedDocument = new InterpreterDocuments();
        updatedDocument.setDocument("https://s3.amazonaws.com/documents/updated-document.pdf");
        updatedDocument.setInterpreter(interpreter); // Associe o Interpreter ao documento atualizado
        when(interpreterDocumentsRepository.save(any(InterpreterDocuments.class))).thenReturn(updatedDocument);

        InterpreterDocumentRequestDTO request = new InterpreterDocumentRequestDTO(interpreterId, file);

        // Act
        InterpreterDocumentResponseDTO result = interpreterDocumentService.updateDocument(documentId, request);

        // Assert
        assertNotNull(result);
        assertEquals("https://s3.amazonaws.com/documents/updated-document.pdf", result.getData().getDocument());
        verify(s3Service, times(1)).uploadFile(any(MultipartFile.class), anyString());
        verify(interpreterDocumentsRepository, times(1)).save(any(InterpreterDocuments.class));
    }
}
