package com.pointtils.pointtils.src.application.services;

import com.pointtils.pointtils.src.application.dto.requests.UserPicturePatchRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.UserResponseDTO;
import com.pointtils.pointtils.src.core.domain.entities.User;
import com.pointtils.pointtils.src.infrastructure.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.cloud.aws.s3.enabled", havingValue = "true")
public class UserPicturePatchService {

    private final UserRepository userRepository;
    private final S3Service s3Service;

    public UserResponseDTO updatePicture(UserPicturePatchRequestDTO request) throws IOException {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        String url = s3Service.uploadFile(request.getFile(), request.getUserId().toString());
        user.setPicture(url);

        User savedUser = userRepository.save(user);

        return UserResponseDTO.fromEntity(savedUser);
    }
}