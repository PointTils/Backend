package com.pointtils.pointtils.src.application.services;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pointtils.pointtils.src.application.dto.requests.InterpreterDocumentRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.InterpreterDocumentResponseDTO;
import com.pointtils.pointtils.src.core.domain.entities.Interpreter;
import com.pointtils.pointtils.src.core.domain.entities.InterpreterDocuments;
import com.pointtils.pointtils.src.infrastructure.repositories.InterpreterRepository;
import com.pointtils.pointtils.src.infrastructure.repositories.InterpreterDocumentsRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InterpreterDocumentService {

    private final InterpreterRepository interpreterRepository;
    private final InterpreterDocumentsRepository interpreterDocumentsRepository;
    private final S3Service s3Service;

    public InterpreterDocumentResponseDTO saveDocument(InterpreterDocumentRequestDTO request) throws IOException {
        Interpreter interpreter = interpreterRepository.findById(request.getInterpreterId())
                .orElseThrow(() -> new EntityNotFoundException("Intérprete não encontrado"));

        if (!s3Service.isS3Enabled()) {
            throw new UnsupportedOperationException("Upload de documentos está desabilitado. Configure spring.cloud.aws.s3.enabled=true para habilitar o upload para S3.");
        }

        String documentUrl = s3Service.uploadFile(request.getFile(), "documents/" + request.getInterpreterId());

        InterpreterDocuments document = new InterpreterDocuments();
        document.setInterpreter(interpreter);
        document.setDocument(documentUrl);

        InterpreterDocuments savedDocument = interpreterDocumentsRepository.save(document);

        return InterpreterDocumentResponseDTO.fromEntity(savedDocument);
    }

    public boolean isDocumentUploadEnabled() {
        return s3Service.isS3Enabled();
    }

    @Transactional(readOnly = true)
    public List<InterpreterDocumentResponseDTO> getDocumentsByInterpreter(UUID interpreterId) {
        Interpreter interpreter = interpreterRepository.findById(interpreterId)
                .orElseThrow(() -> new EntityNotFoundException("Intérprete não encontrado"));

        List<InterpreterDocuments> documents = interpreterDocumentsRepository.findByInterpreter(interpreter);

        return documents.stream()
                .map(InterpreterDocumentResponseDTO::fromEntity)
                .toList();
    }

    @Transactional
    public InterpreterDocumentResponseDTO updateDocument(UUID documentId, InterpreterDocumentRequestDTO request) throws IOException {
        InterpreterDocuments existingDocument = interpreterDocumentsRepository.findById(documentId)
                .orElseThrow(() -> new EntityNotFoundException("Documento não encontrado"));

        Interpreter interpreter = interpreterRepository.findById(request.getInterpreterId())
                .orElseThrow(() -> new EntityNotFoundException("Intérprete não encontrado"));

        if (!s3Service.isS3Enabled()) {
            throw new UnsupportedOperationException("Upload de documentos está desabilitado. Configure spring.cloud.aws.s3.enabled=true para habilitar o upload para S3.");
        }

        String updatedDocumentUrl = s3Service.uploadFile(request.getFile(), "documents/" + request.getInterpreterId());

        existingDocument.setInterpreter(interpreter);
        existingDocument.setDocument(updatedDocumentUrl);

        InterpreterDocuments updatedDocument = interpreterDocumentsRepository.save(existingDocument);

        return InterpreterDocumentResponseDTO.fromEntity(updatedDocument);
    }
}