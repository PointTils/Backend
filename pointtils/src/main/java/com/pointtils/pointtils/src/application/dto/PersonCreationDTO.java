package com.pointtils.pointtils.src.application.dto;

import java.time.LocalDate;
import java.util.List;

import com.pointtils.pointtils.src.core.domain.entities.enums.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class PersonCreationDTO {
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @Size(max = 11, message = "Phone must be up to 11 digits")
    private String phone;

    private String picture;

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Gender is required")
    private Gender gender;

    @NotNull(message = "Birthday is required")
    private LocalDate birthday;

    @NotBlank(message = "CPF is required")
    @Size(min = 11, max = 11, message = "CPF must be 11 digits")
    private String cpf;

    private List<UserSpecialtyDTO> specialties;

}