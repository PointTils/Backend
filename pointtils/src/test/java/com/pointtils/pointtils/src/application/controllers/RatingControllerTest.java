package com.pointtils.pointtils.src.application.controllers;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pointtils.pointtils.src.application.dto.requests.RatingPatchRequestDTO;
import com.pointtils.pointtils.src.application.dto.requests.RatingRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.RatingResponseDTO;
import com.pointtils.pointtils.src.application.dto.responses.RatingUserResponseDTO;
import com.pointtils.pointtils.src.application.services.RatingService;
import com.pointtils.pointtils.src.core.domain.exceptions.RatingException;

import io.awspring.cloud.autoconfigure.s3.S3AutoConfiguration;
import jakarta.persistence.EntityNotFoundException;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@EnableAutoConfiguration(exclude = S3AutoConfiguration.class)
@ActiveProfiles("test")
class RatingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @SuppressWarnings("removal")
    @MockBean
    private RatingService ratingService;

    @Autowired
    private ObjectMapper objectMapper;

    private RatingResponseDTO ratingResponseDTO;
    private RatingUserResponseDTO ratingUserResponseDTO;
    private RatingRequestDTO ratingRequestDTO;
    private RatingPatchRequestDTO ratingPatchRequestDTO;
    private UUID appointmentId;
    private UUID ratingId;
    private UUID userId;

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() {
        userId = UUID.randomUUID();
        appointmentId = UUID.randomUUID();
        ratingId = UUID.randomUUID();

        ratingUserResponseDTO = RatingUserResponseDTO.builder()
                .id(userId)
                .name("João da Silva")
                .picture("https://example.com/picture.jpg")
                .build();

        ratingResponseDTO = RatingResponseDTO.builder()
                .id(ratingId)
                .stars(BigDecimal.valueOf(4))
                .description("Ótimo intérprete!")
                .date("2025-10-07")
                .user(ratingUserResponseDTO)
                .build();

        ratingRequestDTO = RatingRequestDTO.builder()
                .stars(BigDecimal.valueOf(4))
                .description("Ótimo intérprete!")
                .userId(userId)
                .build();

        ratingPatchRequestDTO = RatingPatchRequestDTO.builder()
                .stars(BigDecimal.valueOf(5))
                .description("Excelente atendimento!")
                .build();
    }

    @Test
    @DisplayName("Deve criar uma nova avaliação com sucesso")
    void shouldCreateRating() throws Exception {
        when(ratingService.createRating(any(RatingRequestDTO.class), eq(appointmentId)))
                .thenReturn(ratingResponseDTO);

        mockMvc.perform(post("/v1/ratings/{appointmentId}", appointmentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ratingRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Avaliação adicionada com sucesso"))
                .andExpect(jsonPath("$.data.stars").value(4))
                .andExpect(jsonPath("$.data.description").value("Ótimo intérprete!"));

        verify(ratingService).createRating(any(RatingRequestDTO.class), eq(appointmentId));
    }

    @Test
    @DisplayName("Deve retornar todas as avaliações de um intérprete")
    void shouldGetAllRatingsByInterpreterId() throws Exception {
        when(ratingService.getRatingsByInterpreterId(eq(userId)))
                .thenReturn(List.of(ratingResponseDTO));

        mockMvc.perform(get("/v1/ratings")
                .param("interpreterId", userId.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Avaliações obtidas com sucesso"))
                .andExpect(jsonPath("$.data[0].id").value(ratingId.toString()))
                .andExpect(jsonPath("$.data[0].stars").value(4));

        verify(ratingService).getRatingsByInterpreterId(eq(userId));
    }

    @Test
    @DisplayName("Deve atualizar uma avaliação existente")
    void shouldPatchRating() throws Exception {
        when(ratingService.patchRating(any(RatingPatchRequestDTO.class), eq(ratingId)))
                .thenReturn(ratingResponseDTO);

        mockMvc.perform(patch("/v1/ratings/{id}", ratingId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ratingPatchRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Avaliação atualizada com sucesso"))
                .andExpect(jsonPath("$.data.id").value(ratingId.toString()))
                .andExpect(jsonPath("$.data.stars").value(4));

        verify(ratingService).patchRating(any(RatingPatchRequestDTO.class), eq(ratingId));
    }

    @Test
    @DisplayName("Deve deletar uma avaliação existente")
    void shouldDeleteRating() throws Exception {
        mockMvc.perform(delete("/v1/ratings/{id}", ratingId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(ratingService).deleteRating(eq(ratingId));
    }

    @Test
    @DisplayName("Deve retornar 404 quando agendamento ou usuário não for encontrado")
    void shouldReturnNotFoundWhenAppointmentOrUserNotFound() throws Exception {
        when(ratingService.createRating(any(RatingRequestDTO.class), eq(appointmentId)))
                .thenThrow(new EntityNotFoundException("Agendamento ou usuário não encontrado"));

        mockMvc.perform(post("/v1/ratings/{appointmentId}", appointmentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ratingRequestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Agendamento ou usuário não encontrado"));
    }

    @Test
    @DisplayName("Deve retornar 404 quando o intérprete não for encontrado")
    void shouldReturnNotFoundWhenInterpreterNotFound() throws Exception {
        when(ratingService.getRatingsByInterpreterId(eq(userId)))
                .thenThrow(new EntityNotFoundException("Intérprete não encontrado"));

        mockMvc.perform(get("/v1/ratings")
                .param("interpreterId", userId.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Intérprete não encontrado"));
    }

    @Test
    @DisplayName("Deve retornar 400 quando parâmetros de entrada forem inválidos")
    void shouldReturnBadRequestWhenInvalidParameters() throws Exception {
        when(ratingService.createRating(any(RatingRequestDTO.class), eq(appointmentId)))
                .thenThrow(new RatingException("Parâmetros de entrada inválidos"));

        mockMvc.perform(post("/v1/ratings/{appointmentId}", appointmentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ratingRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Parâmetros de entrada inválidos"));
    }

    @Test
    @DisplayName("Deve retornar 409 quando o agendamento ainda não foi concluído")
    void shouldReturnUnprocessableWhenAppointmentNotCompleted() throws Exception {
        when(ratingService.createRating(any(RatingRequestDTO.class), eq(appointmentId)))
                .thenThrow(new RatingException(
                        "Agendamento ainda não foi concluído (só posso avaliar depois de status ser encerrado)"));

        mockMvc.perform(post("/v1/ratings/{appointmentId}", appointmentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ratingRequestDTO)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value(
                        "Agendamento ainda não foi concluído (só posso avaliar depois de status ser encerrado)"));
    }

    @Test
    @DisplayName("Deve retornar 500 para erros inesperados")
    void shouldReturnInternalServerErrorForUnexpectedErrors() throws Exception {
        when(ratingService.createRating(any(RatingRequestDTO.class), eq(appointmentId)))
                .thenThrow(new RatingException("Erro inesperado no sistema"));

        mockMvc.perform(post("/v1/ratings/{appointmentId}", appointmentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ratingRequestDTO)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("Erro inesperado no sistema"));
    }
}