package com.pointtils.pointtils.src.application.dto.requests;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddUserSpecialtiesRequestDTO {

    @JsonProperty("specialty_ids")
    private List<UUID> specialtyIds;
    @JsonProperty("replace_existing")
    private boolean replaceExisting = false;
}
