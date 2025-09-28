package com.pointtils.pointtils.src.application.dto.responses;

import com.pointtils.pointtils.src.application.dto.UserSpecialtyDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSpecialtiesResponseDTO {
    private boolean success;
    private String message;
    private List<UserSpecialtyDTO> data;
}
