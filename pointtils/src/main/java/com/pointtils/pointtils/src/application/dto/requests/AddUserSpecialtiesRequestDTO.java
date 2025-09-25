package com.pointtils.pointtils.src.application.dto.requests;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddUserSpecialtiesRequestDTO {
    private List<UUID> specialtyIds;
    private boolean replaceExisting = false;
}
