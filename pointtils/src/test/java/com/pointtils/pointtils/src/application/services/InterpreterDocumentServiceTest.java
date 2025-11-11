package com.pointtils.pointtils.src.application.services;

import com.pointtils.pointtils.src.application.dto.requests.InterpreterDocumentRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.InterpreterDocumentResponseDTO;
import com.pointtils.pointtils.src.application.dto.responses.InterpreterRegistrationEmailDTO;
import com.pointtils.pointtils.src.core.domain.entities.Interpreter;
import com.pointtils.pointtils.src.core.domain.entities.InterpreterDocuments;
import com.pointtils.pointtils.src.core.domain.exceptions.FileUploadException;
import com.pointtils.pointtils.src.infrastructure.repositories.InterpreterDocumentsRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InterpreterDocumentServiceTest {

    @Mock
    private InterpreterService interpreterService;
    @Mock
    private InterpreterDocumentsRepository interpreterDocumentsRepository;
    @Mock
    private S3Service s3Service;
    @Mock
    private EmailService emailService;
    @InjectMocks
    private InterpreterDocumentService interpreterDocumentService;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(interpreterDocumentService, "adminEmail", "admin@email.com");
        ReflectionTestUtils.setField(interpreterDocumentService, "apiBaseUrl", "http://localhost:8080");
    }

    @Test
    void shouldSaveDocumentsSuccessfully() throws IOException {
        // Arrange
        UUID interpreterId = UUID.fromString("b56e2062-6dba-4f6a-bc2f-655ba8ba5cd3");
        MultipartFile file = mock(MultipartFile.class);
        when(s3Service.uploadFile(any(MultipartFile.class), anyString()))
                .thenReturn("https://s3.amazonaws.com/documents/test-document.pdf");

        Interpreter interpreter = new Interpreter();
        interpreter.setId(interpreterId);
        interpreter.setName("Nome Mock");
        interpreter.setEmail("nome.mock@email.com");
        interpreter.setCpf("1112222333344");
        interpreter.setCnpj("12345678984561");
        interpreter.setPhone("51984848484");
        when(interpreterService.findInterpreterById(interpreterId)).thenReturn(interpreter);

        InterpreterDocuments savedDocument = new InterpreterDocuments();
        savedDocument.setDocument("https://s3.amazonaws.com/documents/test-document.pdf");
        savedDocument.setInterpreter(interpreter);
        when(interpreterDocumentsRepository.save(any(InterpreterDocuments.class))).thenReturn(savedDocument);

        // Act
        List<MultipartFile> fileList = List.of(file); // <-- Corrigido
        InterpreterDocumentResponseDTO result = interpreterDocumentService.saveDocuments(interpreterId, fileList, false);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getData().size());
        assertEquals("https://s3.amazonaws.com/documents/test-document.pdf", result.getData().get(0).getDocument());
        verify(s3Service).uploadFile(any(MultipartFile.class), anyString());
        verify(interpreterDocumentsRepository).save(any(InterpreterDocuments.class));
        verify(emailService).sendInterpreterRegistrationRequestEmail(any(InterpreterRegistrationEmailDTO.class)); // <-- Corrigido
    }


    @Test
    void shouldReplaceExistingDocumentsSuccessfully() throws IOException {
        // Arrange
        UUID interpreterId = UUID.fromString("b56e2062-6dba-4f6a-bc2f-655ba8ba5cd3");
        MultipartFile file = mock(MultipartFile.class);
        when(s3Service.uploadFile(any(MultipartFile.class), anyString()))
                .thenReturn("https://s3.amazonaws.com/documents/test-document.pdf");

        Interpreter interpreter = new Interpreter();
        interpreter.setId(interpreterId); // Certifique-se de que o ID está preenchido
        interpreter.setName("Nome Mock");
        interpreter.setEmail("nome.mock@email.com");
        interpreter.setCpf("1112222333344");
        interpreter.setCnpj("12345678984561");
        interpreter.setPhone("51984848484");
        when(interpreterService.findInterpreterById(interpreterId)).thenReturn(interpreter);

        InterpreterDocuments oldDocument = new InterpreterDocuments();
        oldDocument.setDocument("https://s3.amazonaws.com/documents/old-document.pdf");
        oldDocument.setInterpreter(interpreter);
        when(interpreterDocumentsRepository.findByInterpreter(interpreter)).thenReturn(List.of(oldDocument));

        InterpreterDocuments savedDocument = new InterpreterDocuments();
        savedDocument.setDocument("https://s3.amazonaws.com/documents/test-document.pdf");
        savedDocument.setInterpreter(interpreter); // Associe o Interpreter ao documento
        when(interpreterDocumentsRepository.save(any(InterpreterDocuments.class))).thenReturn(savedDocument);

        // Act
        List<MultipartFile> fileList = List.of(file);
        InterpreterDocumentResponseDTO result = interpreterDocumentService.saveDocuments(interpreterId, fileList, true);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getData().size());
        assertEquals("https://s3.amazonaws.com/documents/test-document.pdf", result.getData().get(0).getDocument());
        verify(s3Service).uploadFile(any(MultipartFile.class), anyString());
        verify(s3Service).deleteFile(oldDocument.getDocument());
        verify(interpreterDocumentsRepository).findByInterpreter(interpreter);
        verify(interpreterDocumentsRepository).save(any(InterpreterDocuments.class));
        verify(interpreterDocumentsRepository).delete(oldDocument);
        verifyNoInteractions(emailService);
    }

    @Test
    void shouldThrowExceptionWhenInterpreterNotFound() {
        // Arrange
        UUID interpreterId = UUID.randomUUID();
        when(interpreterService.findInterpreterById(interpreterId))
                .thenThrow(new EntityNotFoundException("Intérprete não encontrado"));
        List<MultipartFile> fileList = List.of(mock(MultipartFile.class));

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> interpreterDocumentService.saveDocuments(interpreterId, fileList, false));
        assertEquals("Intérprete não encontrado", exception.getMessage());
        verifyNoInteractions(s3Service);
    }

    @Test
    void saveShouldThrowExceptionWhenS3IsDisabled() throws IOException {
        // Arrange
        UUID interpreterId = UUID.randomUUID();
        when(interpreterService.findInterpreterById(interpreterId)).thenReturn(new Interpreter());
        when(s3Service.uploadFile(any(), anyString()))
                .thenThrow(new UnsupportedOperationException("Upload de documentos está desabilitado."));
        List<MultipartFile> fileList = List.of(mock(MultipartFile.class));

        // Act & Assert
        UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class,
                () -> interpreterDocumentService.saveDocuments(interpreterId, fileList, false));
        assertEquals("Upload de documentos está desabilitado.", exception.getMessage());
        verifyNoInteractions(interpreterDocumentsRepository);
    }

    @Test
    void shouldThrowExceptionWhenUploadFailed() throws IOException {
        // Arrange
        UUID interpreterId = UUID.randomUUID();
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("test-document.pdf");
        when(s3Service.uploadFile(any(MultipartFile.class), anyString()))
                .thenThrow(new IOException("Falha no upload"));

        Interpreter interpreter = new Interpreter();
        interpreter.setId(interpreterId);
        when(interpreterService.findInterpreterById(interpreterId)).thenReturn(interpreter);

        List<MultipartFile> fileList = List.of(file);

        // Act & Assert
        FileUploadException exception = assertThrows(FileUploadException.class,
                () -> interpreterDocumentService.saveDocuments(interpreterId, fileList, false));
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
        when(interpreterService.findInterpreterById(interpreterId)).thenReturn(interpreter);

        InterpreterDocuments document = new InterpreterDocuments();
        document.setDocument("https://s3.amazonaws.com/documents/test-document.pdf");
        document.setInterpreter(interpreter); // Associe o Interpreter ao documento
        when(interpreterDocumentsRepository.findByInterpreter(interpreter)).thenReturn(List.of(document));

        // Act
        InterpreterDocumentResponseDTO result = interpreterDocumentService.getDocumentsByInterpreter(interpreterId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getData().size());
        assertEquals("https://s3.amazonaws.com/documents/test-document.pdf", result.getData().get(0).getDocument());
        verify(interpreterDocumentsRepository).findByInterpreter(interpreter);
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

        when(interpreterService.findInterpreterById(interpreterId)).thenReturn(interpreter);

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
        assertEquals("https://s3.amazonaws.com/documents/updated-document.pdf", result.getData().get(0).getDocument());
        verify(s3Service).uploadFile(any(MultipartFile.class), anyString());
        verify(interpreterDocumentsRepository).save(any(InterpreterDocuments.class));
    }

    @Test
    void updateShouldThrowExceptionWhenS3IsDisabled() throws IOException {
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
        when(interpreterService.findInterpreterById(interpreterId)).thenReturn(interpreter);
        when(s3Service.uploadFile(any(), anyString()))
                .thenThrow(new UnsupportedOperationException("Upload de documentos está desabilitado."));

        // Act & Assert
        UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class,
                () -> interpreterDocumentService.updateDocument(documentId, request));
        assertEquals("Upload de documentos está desabilitado.", exception.getMessage());
        verify(interpreterDocumentsRepository).findById(documentId);
        verifyNoMoreInteractions(interpreterDocumentsRepository);
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
        when(interpreterService.findInterpreterById(interpreterId)).thenReturn(interpreter);

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
        verify(s3Service).uploadFile(any(MultipartFile.class), anyString());
        verify(interpreterDocumentsRepository).findById(documentId);
        verifyNoMoreInteractions(interpreterDocumentsRepository);
    }
}
