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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

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
    public InterpreterDocumentResponseDTO saveDocuments(UUID interpreterId, List<MultipartFile> files,
                                                              Boolean replaceExisting) {
        Interpreter interpreter = getInterpreterById(interpreterId);
        if (!s3Service.isS3Enabled()) {
            throw new UnsupportedOperationException("Upload de documentos está desabilitado.");
        }

        List<InterpreterDocuments> existingDocuments = null;
        if (Boolean.TRUE.equals(replaceExisting)) {
            existingDocuments = interpreterDocumentsRepository.findByInterpreter(interpreter);
        }

        // Processa cada arquivo e salva no banco de dados
        List<InterpreterDocuments> savedDocuments = files.stream().map(file -> {
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

            return interpreterDocumentsRepository.save(document);
        }).toList();

        // Enviar email para o administrador após cadastro
        sendInterpreterRegistrationEmail(interpreter, files);

        if (Objects.nonNull(existingDocuments)) {
            existingDocuments.forEach(existingDocument -> {
                s3Service.deleteFile(existingDocument.getDocument());
                interpreterDocumentsRepository.delete(existingDocument);
            });
        }

        return InterpreterDocumentResponseDTO.fromEntity(savedDocuments);
    }

    public boolean isDocumentUploadEnabled() {
        return s3Service.isS3Enabled();
    }

    @Transactional(readOnly = true)
    public InterpreterDocumentResponseDTO getDocumentsByInterpreter(UUID interpreterId) {
        Interpreter interpreter = getInterpreterById(interpreterId);

        List<InterpreterDocuments> documents = interpreterDocumentsRepository.findByInterpreter(interpreter);

        return InterpreterDocumentResponseDTO.fromEntity(documents);
    }

    @Transactional
    public InterpreterDocumentResponseDTO updateDocument(UUID documentId, InterpreterDocumentRequestDTO request) {
        InterpreterDocuments existingDocument = interpreterDocumentsRepository.findById(documentId)
                .orElseThrow(() -> new EntityNotFoundException("Documento não encontrado"));

        Interpreter interpreter = getInterpreterById(request.getInterpreterId());

        if (!s3Service.isS3Enabled()) {
            throw new UnsupportedOperationException(
                    "Upload de documentos está desabilitado. Configure spring.cloud.aws.s3.enabled=true para habilitar o upload para S3.");
        }

        try {
            String updatedDocumentUrl = s3Service.uploadFile(request.getFile(), "documents/" + request.getInterpreterId());

            existingDocument.setInterpreter(interpreter);
            existingDocument.setDocument(updatedDocumentUrl);

            InterpreterDocuments updatedDocument = interpreterDocumentsRepository.save(existingDocument);
            return InterpreterDocumentResponseDTO.fromEntity(List.of(updatedDocument));
        } catch (IOException e) {
            throw new FileUploadException(request.getFile().getOriginalFilename(), e);
        }
    }

    private Interpreter getInterpreterById(UUID interpreterId) {
        return interpreterRepository.findById(interpreterId)
                .orElseThrow(() -> new EntityNotFoundException("Intérprete não encontrado"));
    }

    /**
     * Envia email para o administrador com os dados de cadastro do intérprete
     *
     * @param interpreter Intérprete cadastrado
     */
    @Value("${app.api.base-url}")
    private String apiBaseUrl;

    private void sendInterpreterRegistrationEmail(Interpreter interpreter, List<MultipartFile> files) {
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
                    rejectLink,
                    files);

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