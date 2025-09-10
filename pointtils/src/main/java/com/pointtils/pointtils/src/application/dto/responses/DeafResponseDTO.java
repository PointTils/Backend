package com.pointtils.pointtils.src.application.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pointtils.pointtils.src.application.dto.LocationDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DeafResponseDTO {

    private UUID id;
    private String email;
    private String phone;
    private String picture;
    private String status;
    private String type;
    private String name;
    private String gender;
    private LocalDate birthday;
    private String cpf;
    private LocationDTO location;
    @JsonProperty("created_at")
    private LocalDate createdAt = LocalDate.now();
}
