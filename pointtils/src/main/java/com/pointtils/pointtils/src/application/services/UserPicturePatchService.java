package com.pointtils.pointtils.src.application.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.pointtils.pointtils.src.application.dto.requests.UserPicturePatchRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.UserResponseDTO;
import com.pointtils.pointtils.src.core.domain.entities.User;
import com.pointtils.pointtils.src.infrastructure.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import java.io.IOException;
import java.util.UUID;
import jakarta.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
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