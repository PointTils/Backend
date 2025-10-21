package com.pointtils.pointtils.src.application.services;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.pointtils.pointtils.src.application.dto.requests.InterpreterDocumentRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.InterpreterDocumentResponseDTO;
import com.pointtils.pointtils.src.core.domain.entities.Interpreter;
import com.pointtils.pointtils.src.core.domain.entities.InterpreterDocuments;
import com.pointtils.pointtils.src.infrastructure.repositories.InterpreterDocumentsRepository;
import com.pointtils.pointtils.src.infrastructure.repositories.InterpreterRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class InterpreterDocumentService {

    private final InterpreterRepository interpreterRepository;
    private final InterpreterDocumentsRepository interpreterDocumentsRepository;
    private final S3Service s3Service;
    private final EmailService emailService;

    @Value("${app.mail.admin:admin@pointtils.com}")
    private String adminEmail;

    @Transactional
    public List<InterpreterDocumentResponseDTO> saveDocuments(UUID interpreterId, List<MultipartFile> files)
            throws IOException {
        Interpreter interpreter = interpreterRepository.findById(interpreterId)
                .orElseThrow(() -> new EntityNotFoundException("Intérprete não encontrado"));
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
                throw new RuntimeException("Erro ao fazer upload do arquivo: " + file.getOriginalFilename(), e);
            }

            // Cria uma nova instância de InterpreterDocuments
            InterpreterDocuments document = new InterpreterDocuments();
            document.setInterpreter(interpreter);
            document.setDocument(documentUrl);

            InterpreterDocuments savedDocument = interpreterDocumentsRepository.save(document);

            // Enviar email para o administrador após cadastro
            sendInterpreterRegistrationEmail(interpreter);

            return InterpreterDocumentResponseDTO.fromEntity(savedDocument);
        }).toList();
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
    public InterpreterDocumentResponseDTO updateDocument(UUID documentId, InterpreterDocumentRequestDTO request)
            throws IOException {
        InterpreterDocuments existingDocument = interpreterDocumentsRepository.findById(documentId)
                .orElseThrow(() -> new EntityNotFoundException("Documento não encontrado"));

        Interpreter interpreter = interpreterRepository.findById(request.getInterpreter_Id())
                .orElseThrow(() -> new EntityNotFoundException("Intérprete não encontrado"));

        if (!s3Service.isS3Enabled()) {
            throw new UnsupportedOperationException(
                    "Upload de documentos está desabilitado. Configure spring.cloud.aws.s3.enabled=true para habilitar o upload para S3.");
        }

        String updatedDocumentUrl = s3Service.uploadFile(request.getFile(), "documents/" + request.getInterpreter_Id());

        existingDocument.setInterpreter(interpreter);
        existingDocument.setDocument(updatedDocumentUrl);

        InterpreterDocuments updatedDocument = interpreterDocumentsRepository.save(existingDocument);

        return InterpreterDocumentResponseDTO.fromEntity(updatedDocument);
    }

    /**
     * Envia email para o administrador com os dados de cadastro do intérprete
     * 
     * @param interpreter Intérprete cadastrado
     */
    @Value("${app.api.base-url}")
    private String apiBaseUrl;

    private void sendInterpreterRegistrationEmail(Interpreter interpreter) {
        try {
            String acceptLink = String.format("%s/v1/email/interpreter/%s/approve", apiBaseUrl, interpreter.getId());
            String rejectLink = String.format("%s/v1/email/interpreter/%s/reject", apiBaseUrl, interpreter.getId());

            // Enviar email usando o template do banco de dados
            boolean emailSent = emailService.sendInterpreterRegistrationRequestEmail(
                    adminEmail,
                    interpreter.getName(),
                    interpreter.getCpf(),
                    interpreter.getCnpj(),
                    interpreter.getEmail(),
                    interpreter.getPhone(),
                    acceptLink,
                    rejectLink);

            if (emailSent) {
                log.info("Email de solicitação de cadastro enviado com sucesso para: {}", adminEmail);
            } else {
                log.error("Falha ao enviar email de solicitação de cadastro para: {}", adminEmail);
            }

        } catch (Exception e) {
            log.error("Erro ao enviar email de solicitação de cadastro: {}", e.getMessage());
        }
    }
}