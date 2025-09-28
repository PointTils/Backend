package com.pointtils.pointtils.src.application.dto.responses;

import com.pointtils.pointtils.src.core.domain.entities.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    private UUID id;
    private String email;
    private String type;
    private String status;
    private String phone;
    private String picture;
    private List<SpecialtyResponseDTO> specialties;

    public static UserResponseDTO fromEntity(User user) {
        if (user == null) return null;
        return UserResponseDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .type(user.getType() != null ? user.getType().name() : null)
                .status(user.getStatus() != null ? user.getStatus().name() : null)
                .phone(user.getPhone())
                .picture(user.getPicture())
                .build();
    }
}