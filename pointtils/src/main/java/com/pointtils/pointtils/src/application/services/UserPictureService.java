package com.pointtils.pointtils.src.application.services;

import java.io.IOException;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.pointtils.pointtils.src.application.dto.requests.UserPicturePostRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.UserResponseDTO;
import com.pointtils.pointtils.src.core.domain.entities.User;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserPictureService {

    private final UserService userService;
    private final S3Service s3Service;

    public UserResponseDTO updatePicture(UserPicturePostRequestDTO request) throws IOException {
        User user = userService.findById(request.getUserId());

        String url = s3Service.uploadFile(request.getFile(), request.getUserId().toString());
        user.setPicture(url);

        User savedUser = userService.updateUser(user);

        return UserResponseDTO.fromEntity(savedUser);
    }

    public void deletePicture(UUID id) {
        User user = userService.findById(id);
        
        if (user.getPicture() != null && !user.getPicture().isEmpty()) {
            try {
                s3Service.deleteFile(user.getPicture());
            } catch (RuntimeException e){
                // Ignora erros do S3 (incluindo UnsupportedOperationException) e continua com a atualização do banco
                // Isso permite que a foto seja removida do banco mesmo se S3 estiver desabilitado ou falhar
            }
        }
        
        user.setPicture(null);
        userService.updateUser(user);
    }
}
