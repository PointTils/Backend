package com.pointtils.pointtils.src.application.services;

import com.pointtils.pointtils.src.application.dto.requests.UserPicturePostRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.UserResponseDTO;
import com.pointtils.pointtils.src.core.domain.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class UserPicturePostService {

    private final UserService userService;
    private final S3Service s3Service;

    public UserResponseDTO updatePicture(UserPicturePostRequestDTO request) throws IOException {
        User user = userService.findById(request.getUserId());

        String url = s3Service.uploadFile(request.getFile(), request.getUserId().toString());
        user.setPicture(url);

        User savedUser = userService.updateUser(user);

        return UserResponseDTO.fromEntity(savedUser);
    }
}
