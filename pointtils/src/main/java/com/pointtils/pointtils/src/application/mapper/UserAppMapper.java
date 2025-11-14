package com.pointtils.pointtils.src.application.mapper;

import com.pointtils.pointtils.src.application.dto.responses.UserAppResponseDTO;
import com.pointtils.pointtils.src.core.domain.entities.UserApp;
import org.springframework.stereotype.Component;

@Component
public class UserAppMapper {

    public UserAppResponseDTO toResponseDto(UserApp userApp) {
        return new UserAppResponseDTO(
                userApp.getId(),
                userApp.getDeviceId(),
                userApp.getToken(),
                userApp.getPlatform(),
                userApp.getUser().getId(),
                userApp.getCreatedAt(),
                userApp.getModifiedAt()
        );
    }
}
