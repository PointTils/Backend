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
        // Busca o intérprete pelo ID
        Interpreter interpreter = interpreterRepository.findById(request.getInterpreterId())
                .orElseThrow(() -> new EntityNotFoundException("Intérprete não encontrado"));

        // Verifica se o S3 está habilitado antes de tentar fazer upload
        if (!s3Service.isS3Enabled()) {
            throw new UnsupportedOperationException("Upload de documentos está desabilitado. Configure spring.cloud.aws.s3.enabled=true para habilitar o upload para S3.");
        }

        // Faz o upload do arquivo para o S3
        String documentUrl = s3Service.uploadFile(request.getFile(), "documents/" + request.getInterpreterId());

        // Cria uma nova instância de InterpreterDocuments
        InterpreterDocuments document = new InterpreterDocuments();
        document.setInterpreter(interpreter);
        document.setDocument(documentUrl);

        // Salva o documento no banco de dados
        InterpreterDocuments savedDocument = interpreterDocumentsRepository.save(document);

        // Retorna o DTO de resposta
        return InterpreterDocumentResponseDTO.fromEntity(savedDocument);
    }

    /**
     * Verifica se o serviço de upload de documentos está disponível
     */
    public boolean isDocumentUploadEnabled() {
        return s3Service.isS3Enabled();
    }

    @Transactional(readOnly = true)
    public List<InterpreterDocumentResponseDTO> getDocumentsByInterpreter(UUID interpreterId) {
        // Verifica se o intérprete existe
        Interpreter interpreter = interpreterRepository.findById(interpreterId)
                .orElseThrow(() -> new EntityNotFoundException("Intérprete não encontrado"));

        // Busca os documentos associados ao intérprete
        List<InterpreterDocuments> documents = interpreterDocumentsRepository.findByInterpreter(interpreter);

        // Converte os documentos para DTOs
        return documents.stream()
                .map(InterpreterDocumentResponseDTO::fromEntity)
                .toList();
    }
}