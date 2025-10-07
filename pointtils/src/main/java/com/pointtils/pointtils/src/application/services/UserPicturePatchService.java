package com.pointtils.pointtils.src.application.services;

import java.io.IOException;

import org.springframework.stereotype.Service;

import com.pointtils.pointtils.src.application.dto.requests.UserPicturePatchRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.UserResponseDTO;
import com.pointtils.pointtils.src.core.domain.entities.User;
import com.pointtils.pointtils.src.infrastructure.repositories.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserPicturePatchService {

    private final UserRepository userRepository;
    private final S3Service s3Service;

    public UserResponseDTO updatePicture(UserPicturePatchRequestDTO request) throws IOException {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        // Verifica se o S3 está habilitado antes de tentar fazer upload
        if (!s3Service.isS3Enabled()) {
            throw new UnsupportedOperationException("Upload de fotos está desabilitado. Configure spring.cloud.aws.s3.enabled=true para habilitar o upload para S3.");
        }

        String url = s3Service.uploadFile(request.getFile(), request.getUserId().toString());
        user.setPicture(url);

        User savedUser = userRepository.save(user);

        return UserResponseDTO.fromEntity(savedUser);
    }

    /**
     * Verifica se o serviço de upload de fotos está disponível
     */
    public boolean isPictureUploadEnabled() {
        return s3Service.isS3Enabled();
    }
}
