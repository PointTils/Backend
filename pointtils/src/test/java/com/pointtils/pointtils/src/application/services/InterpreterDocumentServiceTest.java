package com.pointtils.pointtils.src.application.services;

import com.pointtils.pointtils.src.application.dto.requests.InterpreterDocumentRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.InterpreterDocumentResponseDTO;
import com.pointtils.pointtils.src.core.domain.entities.Interpreter;
import com.pointtils.pointtils.src.core.domain.entities.InterpreterDocuments;
import com.pointtils.pointtils.src.core.domain.exceptions.FileUploadException;
import com.pointtils.pointtils.src.infrastructure.repositories.InterpreterDocumentsRepository;
import com.pointtils.pointtils.src.infrastructure.repositories.InterpreterRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InterpreterDocumentServiceTest {

    @Mock
    private InterpreterRepository interpreterRepository;
    @Mock
    private InterpreterDocumentsRepository interpreterDocumentsRepository;
    @Mock
    private S3Service s3Service;
    @InjectMocks
    private InterpreterDocumentService interpreterDocumentService;

    @Test
    void shouldSaveDocumentsSuccessfully() throws IOException {
        // Arrange
        UUID interpreterId = UUID.randomUUID();
        MultipartFile file = mock(MultipartFile.class);
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
        List<MultipartFile> fileList = List.of(mock(MultipartFile.class));

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> interpreterDocumentService.saveDocuments(interpreterId, fileList));
        assertEquals("Intérprete não encontrado", exception.getMessage());
        verifyNoInteractions(s3Service);
    }

    @Test
    void saveShouldThrowExceptionWhenS3IsDisabled() {
        // Arrange
        UUID interpreterId = UUID.randomUUID();
        when(interpreterRepository.findById(interpreterId)).thenReturn(Optional.of(new Interpreter()));
        when(s3Service.isS3Enabled()).thenReturn(false);
        List<MultipartFile> fileList = List.of(mock(MultipartFile.class));

        // Act & Assert
        UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class,
                () -> interpreterDocumentService.saveDocuments(interpreterId, fileList));
        assertEquals("Upload de documentos está desabilitado.", exception.getMessage());
        verify(s3Service).isS3Enabled();
        verifyNoMoreInteractions(s3Service);
    }

    @Test
    void shouldThrowExceptionWhenUploadFailed() throws IOException {
        // Arrange
        UUID interpreterId = UUID.randomUUID();
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("test-document.pdf");
        when(s3Service.isS3Enabled()).thenReturn(true);
        when(s3Service.uploadFile(any(MultipartFile.class), anyString()))
                .thenThrow(new IOException("Falha no upload"));

        Interpreter interpreter = new Interpreter();
        interpreter.setId(interpreterId);
        when(interpreterRepository.findById(interpreterId)).thenReturn(Optional.of(interpreter));

        List<MultipartFile> fileList = List.of(file);

        // Act & Assert
        FileUploadException exception = assertThrows(FileUploadException.class,
                () -> interpreterDocumentService.saveDocuments(interpreterId, fileList));
        assertEquals("Erro ao fazer upload do arquivo test-document.pdf", exception.getMessage());
        assertEquals(IOException.class, exception.getCause().getClass());
        assertEquals("Falha no upload", exception.getCause().getMessage());
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

    @Test
    void documentUploadShouldBeEnabledIfS3IsEnabled() {
        when(s3Service.isS3Enabled()).thenReturn(true);
        assertTrue(interpreterDocumentService.isDocumentUploadEnabled());
    }

    @Test
    void updateShouldThrowExceptionWhenS3IsDisabled() {
        // Arrange
        UUID documentId = UUID.randomUUID();
        UUID interpreterId = UUID.randomUUID();
        MultipartFile file = mock(MultipartFile.class);
        InterpreterDocumentRequestDTO request = new InterpreterDocumentRequestDTO(interpreterId, file);

        Interpreter interpreter = new Interpreter();
        interpreter.setId(interpreterId);

        InterpreterDocuments existingDocument = new InterpreterDocuments();
        existingDocument.setInterpreter(interpreter);
        when(interpreterDocumentsRepository.findById(documentId)).thenReturn(Optional.of(existingDocument));
        when(interpreterRepository.findById(interpreterId)).thenReturn(Optional.of(interpreter));

        when(s3Service.isS3Enabled()).thenReturn(false);

        // Act & Assert
        UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class,
                () -> interpreterDocumentService.updateDocument(documentId, request));
        assertEquals("Upload de documentos está desabilitado. Configure spring.cloud.aws.s3.enabled=true para habilitar o upload para S3.",
                exception.getMessage());
        verify(s3Service).isS3Enabled();
        verifyNoMoreInteractions(s3Service);
    }

    @Test
    void shouldThrowExceptionWhenUploadFailedDuringDocumentUpdate() throws IOException {
        // Arrange
        UUID documentId = UUID.randomUUID();
        UUID interpreterId = UUID.randomUUID();
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("updated-document.pdf");

        Interpreter interpreter = new Interpreter();
        interpreter.setId(interpreterId);

        InterpreterDocuments existingDocument = new InterpreterDocuments();
        existingDocument.setInterpreter(interpreter);
        when(interpreterDocumentsRepository.findById(documentId)).thenReturn(Optional.of(existingDocument));

        when(interpreterRepository.findById(interpreterId)).thenReturn(Optional.of(interpreter));

        when(s3Service.isS3Enabled()).thenReturn(true);
        when(s3Service.uploadFile(any(MultipartFile.class), anyString()))
                .thenThrow(new IOException("Falha no upload"));
        InterpreterDocumentRequestDTO request = new InterpreterDocumentRequestDTO(interpreterId, file);

        // Act
        FileUploadException exception = assertThrows(FileUploadException.class,
                () -> interpreterDocumentService.updateDocument(documentId, request));

        // Assert
        assertEquals("Erro ao fazer upload do arquivo updated-document.pdf", exception.getMessage());
        assertEquals(IOException.class, exception.getCause().getClass());
        assertEquals("Falha no upload", exception.getCause().getMessage());
        verify(s3Service, times(1)).uploadFile(any(MultipartFile.class), anyString());
        verify(interpreterDocumentsRepository).findById(documentId);
        verifyNoMoreInteractions(interpreterDocumentsRepository);
    }
}
