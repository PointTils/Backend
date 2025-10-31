package com.pointtils.pointtils.src.application.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pointtils.pointtils.src.application.dto.requests.ScheduleListRequestDTO;
import com.pointtils.pointtils.src.application.dto.requests.SchedulePatchRequestDTO;
import com.pointtils.pointtils.src.application.dto.requests.ScheduleRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.AvailableTimeSlotsResponseDTO;
import com.pointtils.pointtils.src.application.dto.responses.PaginatedScheduleResponseDTO;
import com.pointtils.pointtils.src.application.dto.responses.ScheduleResponseDTO;
import com.pointtils.pointtils.src.application.services.ScheduleService;
import com.pointtils.pointtils.src.core.domain.entities.enums.DayOfWeek;
import com.pointtils.pointtils.src.infrastructure.configs.JwtService;
import com.pointtils.pointtils.src.infrastructure.configs.MemoryBlacklistService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ScheduleController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration.class
})
@AutoConfigureMockMvc(addFilters = false)

class ScheduleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ScheduleService service;

    @MockBean
    private MemoryBlacklistService memoryBlacklistService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtService jwtService;

    private UUID scheduleId;
    private ScheduleResponseDTO scheduleResponse;

    @BeforeEach
    void setUp() {
        scheduleId = UUID.randomUUID();
        scheduleResponse = ScheduleResponseDTO.builder()
                .id(scheduleId)
                .interpreterId(UUID.randomUUID())
                .day(DayOfWeek.MON)
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(10, 0))
                .build();
    }

    @Test
    @DisplayName("POST /v1/schedules/register deve cadastrar um horário com sucesso")
    void registerSchedule_ShouldReturnCreated() throws Exception {
        ScheduleRequestDTO request = ScheduleRequestDTO.builder().interpreterId(UUID.randomUUID()).day(DayOfWeek.MON)
                .startTime(LocalTime.of(9, 0)).endTime(LocalTime.of(10, 0)).build();
        when(service.registerSchedule(any(ScheduleRequestDTO.class))).thenReturn(scheduleResponse);
        mockMvc.perform(post("/v1/schedules/register").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))).andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Horário cadastrado com sucesso"))
                .andExpect(jsonPath("$.data.id").value(scheduleId.toString()));
        verify(service).registerSchedule(any(ScheduleRequestDTO.class));
    }

    @Test
    @DisplayName("GET /v1/schedules/{id} deve retornar um horário existente")
    void getScheduleById_ShouldReturnOk() throws Exception {
        when(service.findById(eq(scheduleId))).thenReturn(scheduleResponse);
        mockMvc.perform(get("/v1/schedules/{id}", scheduleId)).andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Detalhes do horário obtidos com sucesso"))
                .andExpect(jsonPath("$.data.id").value(scheduleId.toString()));
        verify(service).findById(scheduleId);
    }

    @Test
    @DisplayName("GET /v1/schedules deve listar horários com paginação")
    void listSchedules_ShouldReturnPaginatedList() throws Exception {
        ScheduleListRequestDTO query = new ScheduleListRequestDTO();
        query.setPage(0);
        query.setSize(10);

        PaginatedScheduleResponseDTO paginated = PaginatedScheduleResponseDTO.builder()
                .page(0)
                .size(10)
                .total(1)
                .items(List.of(scheduleResponse))
                .build();

        when(service.findAll(any(ScheduleListRequestDTO.class), any(PageRequest.class)))
                .thenReturn(paginated);

        mockMvc.perform(get("/v1/schedules")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Horários obtidos com sucesso"))
                .andExpect(jsonPath("$.data.page").value(0))
                .andExpect(jsonPath("$.data.size").value(10))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.items[0].id").value(scheduleId.toString()));

        verify(service).findAll(any(ScheduleListRequestDTO.class), any(PageRequest.class));
    }

    @Test
    @DisplayName("PATCH /v1/schedules/{id} deve atualizar um horário com sucesso")
    void updateSchedule_ShouldReturnUpdated() throws Exception {
        SchedulePatchRequestDTO patch = SchedulePatchRequestDTO.builder()
                .interpreterId(UUID.randomUUID())
                .day(DayOfWeek.TUE)
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(11, 0))
                .build();

        when(service.updateSchedule(eq(scheduleId), eq(patch))).thenReturn(scheduleResponse);

        mockMvc.perform(patch("/v1/schedules/{id}", scheduleId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patch)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Horário atualizado com sucesso"))
                .andExpect(jsonPath("$.data.id").value(scheduleId.toString()));

        verify(service).updateSchedule(scheduleId, patch);
    }

    @Test
    @DisplayName("DELETE /v1/schedules/{id} deve excluir um horário com sucesso")
    void deleteSchedule_ShouldReturnOk() throws Exception {
        doNothing().when(service).deleteById(scheduleId);

        mockMvc.perform(delete("/v1/schedules/{id}", scheduleId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Horário excluído com sucesso"));

        verify(service).deleteById(scheduleId);
    }

    @Test
    @DisplayName("GET /v1/schedules/available deve listar horários disponíveis")
    void listAvailableSchedules_ShouldReturnAvailableSlots() throws Exception {
        UUID interpreterId = UUID.randomUUID();
        LocalDate dateFrom = LocalDate.now();
        LocalDate dateTo = dateFrom.plusDays(5);

        AvailableTimeSlotsResponseDTO slot = new AvailableTimeSlotsResponseDTO();
        when(service.findAvailableSchedules(eq(interpreterId), eq(dateFrom), eq(dateTo)))
                .thenReturn(List.of(slot));

        mockMvc.perform(get("/v1/schedules/available")
                .param("interpreterId", interpreterId.toString())
                .param("dateFrom", dateFrom.toString())
                .param("dateTo", dateTo.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Horários disponíveis obtidos com sucesso"));

        verify(service).findAvailableSchedules(interpreterId, dateFrom, dateTo);
    }
}
