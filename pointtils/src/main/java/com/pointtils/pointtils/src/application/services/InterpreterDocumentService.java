package com.pointtils.pointtils.src.application.services;

import com.pointtils.pointtils.src.application.dto.requests.InterpreterDocumentRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.InterpreterDocumentResponseDTO;
import com.pointtils.pointtils.src.application.dto.responses.InterpreterRegistrationEmailDTO;
import com.pointtils.pointtils.src.core.domain.entities.Interpreter;
import com.pointtils.pointtils.src.core.domain.entities.InterpreterDocuments;
import com.pointtils.pointtils.src.core.domain.exceptions.FileUploadException;
import com.pointtils.pointtils.src.infrastructure.repositories.InterpreterDocumentsRepository;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class InterpreterDocumentService {

    private final InterpreterDocumentsRepository interpreterDocumentsRepository;
    private final InterpreterService interpreterService;
    private final S3Service s3Service;
    private final EmailService emailService;

    @Value("${app.mail.admin:admin@pointtils.com}")
    private String adminEmail;
    @Value("${app.api.base-url}")
    private String apiBaseUrl;

    @Transactional
    public InterpreterDocumentResponseDTO saveDocuments(UUID interpreterId, List<MultipartFile> files,
                                                        Boolean replaceExisting) {
        Interpreter interpreter = interpreterService.findInterpreterById(interpreterId);

        List<InterpreterDocuments> existingDocuments = null;
        if (Boolean.TRUE.equals(replaceExisting)) {
            existingDocuments = interpreterDocumentsRepository.findByInterpreter(interpreter);
        }

        // Processa cada arquivo e salva no banco de dados
        List<InterpreterDocuments> savedDocuments = files.stream()
                .map(file -> uploadDocument(interpreter, file, new InterpreterDocuments()))
                .toList();

        if (Objects.isNull(existingDocuments)) {
            // Enviar email para o administrador após cadastro do usuario
            sendInterpreterRegistrationEmail(interpreter, files);
        } else {
            // Deleta documentos antigos em caso de atualização dos documentos
            existingDocuments.forEach(existingDocument -> {
                s3Service.deleteFile(existingDocument.getDocument());
                interpreterDocumentsRepository.delete(existingDocument);
            });
        }

        return InterpreterDocumentResponseDTO.fromEntity(savedDocuments);
    }

    @Transactional(readOnly = true)
    public InterpreterDocumentResponseDTO getDocumentsByInterpreter(UUID interpreterId) {
        Interpreter interpreter = interpreterService.findInterpreterById(interpreterId);
        List<InterpreterDocuments> documents = interpreterDocumentsRepository.findByInterpreter(interpreter);

        return InterpreterDocumentResponseDTO.fromEntity(documents);
    }

    @Transactional
    public InterpreterDocumentResponseDTO updateDocument(UUID documentId, InterpreterDocumentRequestDTO request) {
        InterpreterDocuments existingDocument = interpreterDocumentsRepository.findById(documentId)
                .orElseThrow(() -> new EntityNotFoundException("Documento não encontrado"));
        Interpreter interpreter = interpreterService.findInterpreterById(request.getInterpreterId());

        InterpreterDocuments updatedDocument = uploadDocument(interpreter, request.getFile(), existingDocument);
        return InterpreterDocumentResponseDTO.fromEntity(List.of(updatedDocument));
    }

    private InterpreterDocuments uploadDocument(Interpreter interpreter, MultipartFile file, InterpreterDocuments interpreterDocument) {
        // Faz o upload do arquivo para o S3
        String documentUrl;
        try {
            documentUrl = s3Service.uploadFile(file, "documents/" + interpreter.getId());
        } catch (IOException ex) {
            log.error("Erro ao realizar upload de documento {} para S3", file.getOriginalFilename(), ex);
            throw new FileUploadException(file.getOriginalFilename(), ex);
        }

        // Cria uma nova instância de InterpreterDocuments
        interpreterDocument.setInterpreter(interpreter);
        interpreterDocument.setDocument(documentUrl);

        return interpreterDocumentsRepository.save(interpreterDocument);
    }

    /**
     * Envia email para o administrador com os dados de cadastro do intérprete
     *
     * @param interpreter Intérprete cadastrado
     */
    private void sendInterpreterRegistrationEmail(Interpreter interpreter, List<MultipartFile> files) {
        try {
            String acceptLink = String.format("%s/v1/email/interpreter/%s/approve", apiBaseUrl, interpreter.getId());
            String rejectLink = String.format("%s/v1/email/interpreter/%s/reject", apiBaseUrl, interpreter.getId());

            InterpreterRegistrationEmailDTO emailDTO = InterpreterRegistrationEmailDTO.builder()
                    .adminEmail(adminEmail)
                    .interpreterName(interpreter.getName())
                    .cpf(interpreter.getCpf())
                    .cnpj(interpreter.getCnpj())
                    .email(interpreter.getEmail())
                    .phone(interpreter.getPhone())
                    .acceptLink(acceptLink)
                    .rejectLink(rejectLink)
                    .files(files)
                    .build();

            boolean emailSent = emailService.sendInterpreterRegistrationRequestEmail(emailDTO);

            if (emailSent) {
                log.info("Email de solicitação de cadastro enviado com sucesso para: {}", adminEmail);
            } else {
                log.error("Falha ao enviar email de solicitação de cadastro para: {}", adminEmail);
            }

        } catch (Exception ex) {
            log.error("Erro ao enviar email de solicitação de cadastro: {}", ex.getMessage());
        }
    }

}