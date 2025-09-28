package com.pointtils.pointtils.src.application.dto.responses;

import com.pointtils.pointtils.src.core.domain.entities.enums.UserStatus;
import com.pointtils.pointtils.src.core.domain.entities.enums.UserTypeE;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginResponseDTO {

    private UUID id;
    private String name;
    private String email;
    private String phone;
    private String picture;
    private UserTypeE type;
    private UserStatus status;
}
