package com.pointtils.pointtils.src.application.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pointtils.pointtils.src.application.dto.AddUserSpecialtiesRequestDTO;
import com.pointtils.pointtils.src.application.dto.UserSpecialtyResponseDTO;
import com.pointtils.pointtils.src.application.services.UserSpecialtyService;
import com.pointtils.pointtils.src.core.domain.entities.Specialty;
import com.pointtils.pointtils.src.core.domain.entities.User;
import com.pointtils.pointtils.src.core.domain.entities.UserSpecialty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserSpecialtyControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserSpecialtyService userSpecialtyService;

    @InjectMocks
    private UserSpecialtyController userSpecialtyController;

    private ObjectMapper objectMapper = new ObjectMapper();

    private UUID userId;
    private UUID specialtyId;
    private UserSpecialty userSpecialty;
    private UserSpecialtyResponseDTO userSpecialtyResponseDTO;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        specialtyId = UUID.randomUUID();

        User user = new User() {
            @Override
            public String getDisplayName() {
                return "Test User";
            }

            @Override
            public String getType() {
                return "CLIENT";
            }
        };
        user.setId(userId);

        Specialty specialty = new Specialty("Test Specialty");
        specialty.setId(specialtyId);

        userSpecialty = new UserSpecialty(specialty, user);
        userSpecialty.setId(UUID.randomUUID());

        userSpecialtyResponseDTO = new UserSpecialtyResponseDTO(
                userSpecialty.getId(),
                userId,
                specialtyId,
                "Test Specialty"
        );

        // Setup MockMvc
        mockMvc = MockMvcBuilders.standaloneSetup(userSpecialtyController).build();
    }

    @Test
    void getUserSpecialties_ShouldReturnUserSpecialties() throws Exception {
        // Arrange
        when(userSpecialtyService.getUserSpecialties(userId)).thenReturn(List.of(userSpecialty));

        // Act & Assert
        mockMvc.perform(get("/v1/users/{userId}/specialties", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Especialidades do usuário obtidas com sucesso"))
                .andExpect(jsonPath("$.data.userSpecialties[0].id").value(userSpecialtyResponseDTO.getId().toString()));

        verify(userSpecialtyService).getUserSpecialties(userId);
    }

    @Test
    void getUserSpecialties_WhenUserNotExists_ShouldReturnNotFound() throws Exception {
        // Arrange
        when(userSpecialtyService.getUserSpecialties(userId))
                .thenThrow(new RuntimeException("User not found"));

        // Act & Assert
        mockMvc.perform(get("/v1/users/{userId}/specialties", userId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));

        verify(userSpecialtyService).getUserSpecialties(userId);
    }

    @Test
    void addUserSpecialties_ShouldAddSpecialties() throws Exception {
        // Arrange
        AddUserSpecialtiesRequestDTO request = new AddUserSpecialtiesRequestDTO(
                List.of(specialtyId), false
        );

        when(userSpecialtyService.addUserSpecialties(userId, List.of(specialtyId), false))
                .thenReturn(List.of(userSpecialty));
        when(userSpecialtyService.countUserSpecialties(userId)).thenReturn(1L);

        // Act & Assert
        mockMvc.perform(post("/v1/users/{userId}/specialties", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Especialidades adicionadas com sucesso"))
                .andExpect(jsonPath("$.data.userSpecialties[0].id").value(userSpecialtyResponseDTO.getId().toString()));

        verify(userSpecialtyService).addUserSpecialties(userId, List.of(specialtyId), false);
    }

    @Test
    void addUserSpecialties_WhenInvalidRequest_ShouldReturnBadRequest() throws Exception {
        // Arrange
        AddUserSpecialtiesRequestDTO request = new AddUserSpecialtiesRequestDTO(
                List.of(specialtyId), false
        );

        when(userSpecialtyService.addUserSpecialties(userId, List.of(specialtyId), false))
                .thenThrow(new RuntimeException("Invalid specialty IDs"));

        // Act & Assert
        mockMvc.perform(post("/v1/users/{userId}/specialties", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));

        verify(userSpecialtyService).addUserSpecialties(userId, List.of(specialtyId), false);
    }

    @Test
    void replaceUserSpecialties_ShouldReplaceSpecialties() throws Exception {
        // Arrange
        when(userSpecialtyService.replaceUserSpecialties(userId, List.of(specialtyId)))
                .thenReturn(List.of(userSpecialty));

        // Act & Assert
        mockMvc.perform(put("/v1/users/{userId}/specialties", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(specialtyId))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Especialidades do usuário atualizadas com sucesso"))
                .andExpect(jsonPath("$.data.userSpecialties[0].id").value(userSpecialtyResponseDTO.getId().toString()));

        verify(userSpecialtyService).replaceUserSpecialties(userId, List.of(specialtyId));
    }

    @Test
    void removeUserSpecialty_ShouldRemoveSpecialty() throws Exception {
        // Arrange
        doNothing().when(userSpecialtyService).removeUserSpecialty(userId, specialtyId);

        // Act & Assert
        mockMvc.perform(delete("/v1/users/{userId}/specialties/{specialtyId}", userId, specialtyId))
                .andExpect(status().isNoContent());

        verify(userSpecialtyService).removeUserSpecialty(userId, specialtyId);
    }

    @Test
    void removeUserSpecialty_WhenNotExists_ShouldReturnNotFound() throws Exception {
        // Arrange
        doThrow(new RuntimeException("User specialty not found"))
                .when(userSpecialtyService).removeUserSpecialty(userId, specialtyId);

        // Act & Assert
        mockMvc.perform(delete("/v1/users/{userId}/specialties/{specialtyId}", userId, specialtyId))
                .andExpect(status().isNotFound());

        verify(userSpecialtyService).removeUserSpecialty(userId, specialtyId);
    }

    @Test
    void removeUserSpecialties_ShouldRemoveSpecialties() throws Exception {
        // Arrange
        doNothing().when(userSpecialtyService).removeUserSpecialties(userId, List.of(specialtyId));
        when(userSpecialtyService.countUserSpecialties(userId)).thenReturn(0L);

        // Act & Assert
        mockMvc.perform(delete("/v1/users/{userId}/specialties", userId)
                        .param("specialtyIds", specialtyId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Especialidades removidas com sucesso"));

        verify(userSpecialtyService).removeUserSpecialties(userId, List.of(specialtyId));
    }
}
