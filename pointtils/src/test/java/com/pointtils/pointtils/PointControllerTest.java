package com.pointtils.pointtils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pointtils.pointtils.src.application.controllers.PointController;
import com.pointtils.pointtils.src.application.dto.PointRequestDTO;
import com.pointtils.pointtils.src.application.dto.PointResponseDTO;
import com.pointtils.pointtils.src.application.services.PointService;

import jakarta.persistence.EntityNotFoundException;

@WebMvcTest(PointController.class)
class PointControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PointService pointService;

    private PointRequestDTO requestDTO;
    private PointResponseDTO responseDTO;
    private List<PointResponseDTO> responseList;

    @BeforeEach
    void setUp() {
        // Configurar objetos de teste
        requestDTO = new PointRequestDTO();
        requestDTO.setUserId("user123");
        requestDTO.setDescription("Morning entry");
        requestDTO.setType("ENTRY");

        responseDTO = new PointResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setUserId("user123");
        responseDTO.setDescription("Morning entry");
        responseDTO.setType("ENTRY");
        responseDTO.setTimestamp(LocalDateTime.now());

        responseList = Arrays.asList(responseDTO);
    }

    @Test
    void findAllShouldReturnListOfPoints() throws Exception {
        // Given
        when(pointService.findAll()).thenReturn(responseList);

        // When & Then
        mockMvc.perform(get("/api/points")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].userId").value("user123"))
                .andExpect(jsonPath("$[0].description").value("Morning entry"))
                .andExpect(jsonPath("$[0].type").value("ENTRY"));
    }

    @Test
    void findByIdShouldReturnPoint() throws Exception {
        // Given
        when(pointService.findById(1L)).thenReturn(responseDTO);

        // When & Then
        mockMvc.perform(get("/api/points/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.userId").value("user123"))
                .andExpect(jsonPath("$.description").value("Morning entry"))
                .andExpect(jsonPath("$.type").value("ENTRY"));
    }

    @Test
    void findByIdShouldReturnNotFoundWhenPointDoesNotExist() throws Exception {
        // Given
        when(pointService.findById(999L)).thenThrow(new EntityNotFoundException("Point not found"));

        // When & Then
        mockMvc.perform(get("/api/points/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void createShouldReturnCreatedPoint() throws Exception {
        // Given
        when(pointService.create(any(PointRequestDTO.class))).thenReturn(responseDTO);

        // When & Then
        mockMvc.perform(post("/api/points")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.userId").value("user123"))
                .andExpect(jsonPath("$.description").value("Morning entry"))
                .andExpect(jsonPath("$.type").value("ENTRY"));
    }

    @Test
    void updateShouldReturnUpdatedPoint() throws Exception {
        // Given
        when(pointService.update(eq(1L), any(PointRequestDTO.class))).thenReturn(responseDTO);

        // When & Then
        mockMvc.perform(put("/api/points/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.userId").value("user123"))
                .andExpect(jsonPath("$.description").value("Morning entry"))
                .andExpect(jsonPath("$.type").value("ENTRY"));
    }

    @Test
    void updateShouldReturnNotFoundWhenPointDoesNotExist() throws Exception {
        // Given
        when(pointService.update(eq(999L), any(PointRequestDTO.class)))
                .thenThrow(new EntityNotFoundException("Point not found"));

        // When & Then
        mockMvc.perform(put("/api/points/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteShouldReturnNoContent() throws Exception {
        // Given
        doNothing().when(pointService).delete(1L);

        // When & Then
        mockMvc.perform(delete("/api/points/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteShouldReturnNotFoundWhenPointDoesNotExist() throws Exception {
        // Given
        doThrow(new EntityNotFoundException("Point not found")).when(pointService).delete(999L);

        // When & Then
        mockMvc.perform(delete("/api/points/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void createShouldHandleNullUserId() throws Exception {
        // Given - Request with null userId (should be handled by service layer)
        PointRequestDTO requestWithNullUserId = new PointRequestDTO();
        requestWithNullUserId.setUserId(null);
        requestWithNullUserId.setDescription("Test");
        requestWithNullUserId.setType("ENTRY");
        
        when(pointService.create(any(PointRequestDTO.class))).thenReturn(responseDTO);

        // When & Then - The service should handle validation, controller just passes through
        mockMvc.perform(post("/api/points")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestWithNullUserId)))
                .andExpect(status().isCreated());
    }
}
