package com.pointtils.pointtils.src.application.services;

import com.pointtils.pointtils.src.application.dto.requests.InterpreterDocumentRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.InterpreterDocumentResponseDTO;
import com.pointtils.pointtils.src.core.domain.entities.Interpreter;
import com.pointtils.pointtils.src.core.domain.entities.InterpreterDocuments;
import com.pointtils.pointtils.src.core.domain.exceptions.FileUploadException;
import com.pointtils.pointtils.src.infrastructure.repositories.InterpreterDocumentsRepository;
import com.pointtils.pointtils.src.infrastructure.repositories.InterpreterRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InterpreterDocumentService {

    private final InterpreterRepository interpreterRepository;
    private final InterpreterDocumentsRepository interpreterDocumentsRepository;
    private final S3Service s3Service;

    @Transactional
    public List<InterpreterDocumentResponseDTO> saveDocuments(UUID interpreterId, List<MultipartFile> files) {
        Interpreter interpreter = getInterpreterById(interpreterId);
        if (!s3Service.isS3Enabled()) {
            throw new UnsupportedOperationException("Upload de documentos está desabilitado.");
        }

        // Processa cada arquivo e salva no banco de dados
        return files.stream().map(file -> {
            // Faz o upload do arquivo para o S3
            String documentUrl;
            try {
                documentUrl = s3Service.uploadFile(file, "documents/" + interpreterId);
            } catch (IOException e) {
                throw new FileUploadException(file.getOriginalFilename(), e);
            }

            // Cria uma nova instância de InterpreterDocuments
            InterpreterDocuments document = new InterpreterDocuments();
            document.setInterpreter(interpreter);
            document.setDocument(documentUrl);

            InterpreterDocuments savedDocument = interpreterDocumentsRepository.save(document);

            return InterpreterDocumentResponseDTO.fromEntity(savedDocument);
        }).toList();
    }

    public boolean isDocumentUploadEnabled() {
        return s3Service.isS3Enabled();
    }

    @Transactional(readOnly = true)
    public List<InterpreterDocumentResponseDTO> getDocumentsByInterpreter(UUID interpreterId) {
        Interpreter interpreter = getInterpreterById(interpreterId);

        List<InterpreterDocuments> documents = interpreterDocumentsRepository.findByInterpreter(interpreter);

        return documents.stream()
                .map(InterpreterDocumentResponseDTO::fromEntity)
                .toList();
    }

    @Transactional
    public InterpreterDocumentResponseDTO updateDocument(UUID documentId, InterpreterDocumentRequestDTO request) {
        InterpreterDocuments existingDocument = interpreterDocumentsRepository.findById(documentId)
                .orElseThrow(() -> new EntityNotFoundException("Documento não encontrado"));

        Interpreter interpreter = getInterpreterById(request.getInterpreterId());

        if (!s3Service.isS3Enabled()) {
            throw new UnsupportedOperationException("Upload de documentos está desabilitado. Configure spring.cloud.aws.s3.enabled=true para habilitar o upload para S3.");
        }

        try {
            String updatedDocumentUrl = s3Service.uploadFile(request.getFile(), "documents/" + request.getInterpreterId());

            existingDocument.setInterpreter(interpreter);
            existingDocument.setDocument(updatedDocumentUrl);

            InterpreterDocuments updatedDocument = interpreterDocumentsRepository.save(existingDocument);
            return InterpreterDocumentResponseDTO.fromEntity(updatedDocument);
        } catch (IOException e) {
            throw new FileUploadException(request.getFile().getOriginalFilename(), e);
        }
    }

    private Interpreter getInterpreterById(UUID interpreterId) {
        return interpreterRepository.findById(interpreterId)
                .orElseThrow(() -> new EntityNotFoundException("Intérprete não encontrado"));
    }
}