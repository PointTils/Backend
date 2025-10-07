package com.pointtils.pointtils.src.application.services;

import java.io.IOException;

import org.springframework.stereotype.Service;

import com.pointtils.pointtils.src.application.dto.requests.InterpreterDocumentRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.InterpreterResponseDTO;
import com.pointtils.pointtils.src.core.domain.entities.Interpreter;
import com.pointtils.pointtils.src.infrastructure.repositories.InterpreterRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InterpreterDocumentService {

    private final InterpreterRepository userRepository;
    private final S3Service s3Service;

    public InterpreterResponseDTO updatePicture(InterpreterDocumentRequestDTO request) throws IOException {
        Interpreter interpreter = InterpreterRepository.findById(request.getInterpreterId())
                .orElseThrow() -> new EntityNotFoundException("Interprete não encontrado"));

        // Verifica se o S3 está habilitado antes de tentar fazer upload
        if (!s3Service.isS3Enabled()) {
            throw new UnsupportedOperationException("Upload de documentos está desabilitado. Configure spring.cloud.aws.s3.enabled=true para habilitar o upload para S3.");
        }

        String url = s3Service.uploadFile(request.getFile(), request.getInterpreterId().toString());
        interpreter.setDocument(url);

        Interpreter savedInterpreter = InterpreterRepository.save(user);

        return InterpreterResponseDTO.fromEntity(savedInterpreter);
    }

    /**
     * Verifica se o serviço de upload de fotos está disponível
     */
    public boolean isDocumentUploadEnabled() {
        return s3Service.isS3Enabled();
    }
}
