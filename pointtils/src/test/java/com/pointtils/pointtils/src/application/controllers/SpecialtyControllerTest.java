package com.pointtils.pointtils.src.application.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pointtils.pointtils.src.application.dto.requests.UpdateSpecialtyRequestDTO;
import com.pointtils.pointtils.src.application.services.SpecialtyService;
import com.pointtils.pointtils.src.core.domain.entities.Specialty;
import com.pointtils.pointtils.src.infrastructure.configs.GlobalExceptionHandler;
import jakarta.persistence.EntityNotFoundException;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class SpecialtyControllerTest {

    private MockMvc mockMvc;

    @Mock
    private SpecialtyService specialtyService;

    @InjectMocks
    private SpecialtyController specialtyController;

    private ObjectMapper objectMapper = new ObjectMapper();

    private Specialty specialty;
    private UUID specialtyId;

    @BeforeEach
    void setUp() {
        specialtyId = UUID.randomUUID();
        specialty = new Specialty("Test Specialty");
        specialty.setId(specialtyId);

        mockMvc = MockMvcBuilders.standaloneSetup(specialtyController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getAllSpecialties_ShouldReturnSpecialties() throws Exception {
        // Arrange
        when(specialtyService.getAllSpecialties()).thenReturn(List.of(specialty));

        // Act & Assert
        mockMvc.perform(get("/v1/specialties"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(specialtyId.toString()))
                .andExpect(jsonPath("$[0].name").value("Test Specialty"));

        verify(specialtyService).getAllSpecialties();
    }

    @Test
    void getSpecialtyById_WhenExists_ShouldReturnSpecialty() throws Exception {
        // Arrange
        when(specialtyService.getSpecialtyById(specialtyId)).thenReturn(specialty);

        // Act & Assert
        mockMvc.perform(get("/v1/specialties/{id}", specialtyId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(specialtyId.toString()))
                .andExpect(jsonPath("$.name").value("Test Specialty"));

        verify(specialtyService).getSpecialtyById(specialtyId);
    }

    @Test
    void getSpecialtyById_WhenNotExists_ShouldReturnNotFound() throws Exception {
        // Arrange
        when(specialtyService.getSpecialtyById(specialtyId))
                .thenThrow(new EntityNotFoundException("Specialty not found"));

        // Act & Assert
        mockMvc.perform(get("/v1/specialties/{id}", specialtyId))
                .andExpect(status().isNotFound());

        verify(specialtyService).getSpecialtyById(specialtyId);
    }

    @Test
    void searchSpecialtiesByName_ShouldReturnMatchingSpecialties() throws Exception {
        // Arrange
        when(specialtyService.searchSpecialtiesByName("test")).thenReturn(List.of(specialty));

        // Act & Assert
        mockMvc.perform(get("/v1/specialties/search")
                        .param("name", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(specialtyId.toString()))
                .andExpect(jsonPath("$[0].name").value("Test Specialty"));

        verify(specialtyService).searchSpecialtiesByName("test");
    }

    @Test
    void searchSpecialtiesByName_WhenServiceThrowsException_ShouldReturnInternalServerError() throws Exception {
        // Arrange
        when(specialtyService.searchSpecialtiesByName("test"))
                .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        mockMvc.perform(get("/v1/specialties/search")
                        .param("name", "test"))
                .andExpect(status().isInternalServerError());

        verify(specialtyService).searchSpecialtiesByName("test");
    }

    @Test
    void createSpecialty_ShouldCreateSpecialty() throws Exception {
        // Arrange
        when(specialtyService.createSpecialty("New Specialty")).thenReturn(specialty);

        // Act & Assert
        mockMvc.perform(post("/v1/specialties")
                        .param("name", "New Specialty"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(specialtyId.toString()))
                .andExpect(jsonPath("$.name").value("Test Specialty"));

        verify(specialtyService).createSpecialty("New Specialty");
    }

    @Test
    void updateSpecialty_ShouldUpdateSpecialty() throws Exception {
        // Arrange
        when(specialtyService.updateSpecialty(specialtyId, "Updated Name")).thenReturn(specialty);

        // Act & Assert
        mockMvc.perform(put("/v1/specialties/{id}", specialtyId)
                        .param("name", "Updated Name"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(specialtyId.toString()))
                .andExpect(jsonPath("$.name").value("Test Specialty"));

        verify(specialtyService).updateSpecialty(specialtyId, "Updated Name");
    }

    @Test
    void partialUpdateSpecialty_ShouldUpdateSpecialty() throws Exception {
        // Arrange
        UpdateSpecialtyRequestDTO request = new UpdateSpecialtyRequestDTO();
        request.setName("Updated Name");

        when(specialtyService.partialUpdateSpecialty(specialtyId, "Updated Name")).thenReturn(specialty);

        // Act & Assert
        mockMvc.perform(patch("/v1/specialties/{id}", specialtyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(specialtyId.toString()))
                .andExpect(jsonPath("$.name").value("Test Specialty"));

        verify(specialtyService).partialUpdateSpecialty(specialtyId, "Updated Name");
    }

    @Test
    void partialUpdateSpecialty_WhenInvalidRequest_ShouldReturnBadRequest() throws Exception {
        // Arrange
        UpdateSpecialtyRequestDTO request = new UpdateSpecialtyRequestDTO();
        request.setName("Updated Name");

        when(specialtyService.partialUpdateSpecialty(specialtyId, "Updated Name"))
                .thenThrow(new IllegalArgumentException("Invalid request"));

        // Act & Assert
        mockMvc.perform(patch("/v1/specialties/{id}", specialtyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(specialtyService).partialUpdateSpecialty(specialtyId, "Updated Name");
    }

    @Test
    void deleteSpecialty_ShouldDeleteSpecialty() throws Exception {
        // Arrange
        doNothing().when(specialtyService).deleteSpecialty(specialtyId);

        // Act & Assert
        mockMvc.perform(delete("/v1/specialties/{id}", specialtyId))
                .andExpect(status().isNoContent());

        verify(specialtyService).deleteSpecialty(specialtyId);
    }

    @Test
    void deleteSpecialty_WhenNotExists_ShouldReturnNotFound() throws Exception {
        // Arrange
        doThrow(new EntityNotFoundException("Specialty not found"))
                .when(specialtyService).deleteSpecialty(specialtyId);

        // Act & Assert
        mockMvc.perform(delete("/v1/specialties/{id}", specialtyId))
                .andExpect(status().isNotFound());

        verify(specialtyService).deleteSpecialty(specialtyId);
    }
}
