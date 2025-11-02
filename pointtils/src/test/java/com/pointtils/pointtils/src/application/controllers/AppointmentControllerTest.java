package com.pointtils.pointtils.src.application.controllers;

import com.pointtils.pointtils.src.application.dto.requests.AppointmentPatchRequestDTO;
import com.pointtils.pointtils.src.application.dto.requests.AppointmentRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.ApiResponseDTO;
import com.pointtils.pointtils.src.application.dto.responses.AppointmentFilterResponseDTO;
import com.pointtils.pointtils.src.application.dto.responses.AppointmentResponseDTO;
import com.pointtils.pointtils.src.application.services.AppointmentService;
import com.pointtils.pointtils.src.core.domain.entities.enums.AppointmentModality;
import com.pointtils.pointtils.src.core.domain.entities.enums.AppointmentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários do AppointmentController")
class AppointmentControllerTest {

    @Mock
    private AppointmentService appointmentService;

    @InjectMocks
    private AppointmentController appointmentController;

    private AppointmentRequestDTO appointmentRequestDTO;
    private AppointmentResponseDTO appointmentResponseDTO;
    private UUID appointmentId;
    private UUID userId;

    @BeforeEach
    void setUp() {
        appointmentId = UUID.randomUUID();
        userId = UUID.randomUUID();

        appointmentRequestDTO = new AppointmentRequestDTO();
        appointmentRequestDTO.setUf("SP");
        appointmentRequestDTO.setCity("São Paulo");
        appointmentRequestDTO.setDate(LocalDate.of(2024, 6, 15));
        appointmentRequestDTO.setStartTime(LocalTime.of(9, 0));
        appointmentRequestDTO.setEndTime(LocalTime.of(10, 0));
        appointmentRequestDTO.setModality(AppointmentModality.ONLINE);
        appointmentRequestDTO.setUserId(userId);

        appointmentResponseDTO = new AppointmentResponseDTO();
        appointmentResponseDTO.setId(appointmentId);
        appointmentResponseDTO.setUf("SP");
        appointmentResponseDTO.setCity("São Paulo");
        appointmentResponseDTO.setDate("2024-06-15");
        appointmentResponseDTO.setStartTime(LocalTime.of(9, 0));
        appointmentResponseDTO.setEndTime(LocalTime.of(10, 0));
        appointmentResponseDTO.setModality("ONLINE");
        appointmentResponseDTO.setStatus("PENDING");
        appointmentResponseDTO.setUserId(userId);
    }

    @Test
    @DisplayName("Deve verificar se os valores do enum AppointmentModality funcionam corretamente")
    void shouldVerifyAppointmentModalityEnumValues() {
        assertNotNull(AppointmentModality.ONLINE);
        assertNotNull(AppointmentModality.PERSONALLY);

        appointmentRequestDTO.setModality(AppointmentModality.ONLINE);
        assertEquals(AppointmentModality.ONLINE, appointmentRequestDTO.getModality());

        appointmentRequestDTO.setModality(AppointmentModality.PERSONALLY);
        assertEquals(AppointmentModality.PERSONALLY, appointmentRequestDTO.getModality());
    }

    @Test
    @DisplayName("Deve verificar se os valores do enum AppointmentStatus funcionam corretamente")
    void shouldVerifyAppointmentStatusEnumValues() {
        assertNotNull(AppointmentStatus.PENDING);
        assertNotNull(AppointmentStatus.ACCEPTED);
        assertNotNull(AppointmentStatus.CANCELED);
        assertNotNull(AppointmentStatus.COMPLETED);

        for (AppointmentStatus status : AppointmentStatus.values()) {
            assertNotNull(status.name());
            assertEquals(status, AppointmentStatus.valueOf(status.name()));
        }
    }

    @Test
    @DisplayName("Deve verificar se os métodos de conversão JSON dos enums funcionam corretamente")
    void shouldVerifyEnumJsonConversionMethods() {
        assertEquals(AppointmentModality.ONLINE, AppointmentModality.fromJson("ONLINE"));
        assertEquals(AppointmentModality.PERSONALLY, AppointmentModality.fromJson("PERSONALLY"));
        assertEquals("ONLINE", AppointmentModality.ONLINE.toJson());
        assertEquals("PERSONALLY", AppointmentModality.PERSONALLY.toJson());

        assertEquals(AppointmentStatus.PENDING, AppointmentStatus.fromJson("PENDING"));
        assertEquals(AppointmentStatus.ACCEPTED, AppointmentStatus.fromJson("ACCEPTED"));
        assertEquals(AppointmentStatus.CANCELED, AppointmentStatus.fromJson("CANCELED"));
        assertEquals(AppointmentStatus.COMPLETED, AppointmentStatus.fromJson("COMPLETED"));

        assertEquals("PENDING", AppointmentStatus.PENDING.toJson());
        assertEquals("ACCEPTED", AppointmentStatus.ACCEPTED.toJson());
        assertEquals("CANCELED", AppointmentStatus.CANCELED.toJson());
        assertEquals("COMPLETED", AppointmentStatus.COMPLETED.toJson());
    }

    @Test
    @DisplayName("Deve tratar entradas inválidas de enum corretamente")
    void shouldHandleInvalidEnumInputsCorrectly() {
        assertThrows(IllegalArgumentException.class, () ->
                AppointmentModality.fromJson("INVALID"));
        assertThrows(IllegalArgumentException.class, () ->
                AppointmentModality.fromJson("hybrid"));
        assertThrows(IllegalArgumentException.class, () ->
                AppointmentModality.fromJson(""));

        assertThrows(IllegalArgumentException.class, () ->
                AppointmentStatus.fromJson("INVALID"));
        assertThrows(IllegalArgumentException.class, () ->
                AppointmentStatus.fromJson("processing"));
        assertThrows(IllegalArgumentException.class, () ->
                AppointmentStatus.fromJson(""));
    }

    @Test
    @DisplayName("Deve verificar a sensibilidade a maiúsculas/minúsculas dos enums")
    void shouldVerifyEnumCaseSensitivity() {
        assertEquals(AppointmentModality.ONLINE, AppointmentModality.fromJson("online"));
        assertEquals(AppointmentModality.ONLINE, AppointmentModality.fromJson("Online"));
        assertEquals(AppointmentModality.PERSONALLY, AppointmentModality.fromJson("personally"));
        assertEquals(AppointmentModality.PERSONALLY, AppointmentModality.fromJson("Personally"));

        assertEquals(AppointmentStatus.PENDING, AppointmentStatus.fromJson("pending"));
        assertEquals(AppointmentStatus.PENDING, AppointmentStatus.fromJson("Pending"));
        assertEquals(AppointmentStatus.ACCEPTED, AppointmentStatus.fromJson("accepted"));
        assertEquals(AppointmentStatus.ACCEPTED, AppointmentStatus.fromJson("Accepted"));
    }

    @Test
    @DisplayName("Deve verificar se os aliases dos enums funcionam corretamente")
    void shouldVerifyEnumAliasesWorkCorrectly() {
        assertEquals(AppointmentModality.ONLINE, AppointmentModality.fromJson("ONLINE"));
        assertEquals(AppointmentModality.PERSONALLY, AppointmentModality.fromJson("PERSONALLY"));
    }

    @Test
    @DisplayName("Deve verificar se os enums funcionam corretamente com service mockado")
    void shouldVerifyEnumsWorkWithMockedService() {
        when(appointmentService.createAppointment(any(AppointmentRequestDTO.class)))
                .thenReturn(appointmentResponseDTO);

        appointmentRequestDTO.setModality(AppointmentModality.ONLINE);
        ResponseEntity<ApiResponseDTO<AppointmentResponseDTO>> response1 =
                appointmentController.createAppointment(appointmentRequestDTO);

        assertEquals(HttpStatus.CREATED, response1.getStatusCode());

        appointmentRequestDTO.setModality(AppointmentModality.PERSONALLY);
        ResponseEntity<ApiResponseDTO<AppointmentResponseDTO>> response2 =
                appointmentController.createAppointment(appointmentRequestDTO);

        assertEquals(HttpStatus.CREATED, response2.getStatusCode());

        verify(appointmentService, times(2)).createAppointment(any(AppointmentRequestDTO.class));
    }

    @Test
    @DisplayName("Deve verificar todos os valores dos enums em iteração")
    void shouldVerifyAllEnumValuesInIteration() {
        for (AppointmentModality modality : AppointmentModality.values()) {
            assertNotNull(modality);
            assertNotNull(modality.toJson());
            assertEquals(modality, AppointmentModality.fromJson(modality.toJson()));
        }

        for (AppointmentStatus status : AppointmentStatus.values()) {
            assertNotNull(status);
            assertNotNull(status.toJson());
            assertEquals(status, AppointmentStatus.fromJson(status.toJson()));
        }
    }

    @Test
    @DisplayName("Deve detectar potenciais problemas de serialização com enums")
    void shouldDetectPotentialSerializationIssuesWithEnums() {
        String modalityJson = AppointmentModality.ONLINE.toJson();
        AppointmentModality deserializedModality = AppointmentModality.fromJson(modalityJson);
        assertEquals(AppointmentModality.ONLINE, deserializedModality);

        String statusJson = AppointmentStatus.PENDING.toJson();
        AppointmentStatus deserializedStatus = AppointmentStatus.fromJson(statusJson);
        assertEquals(AppointmentStatus.PENDING, deserializedStatus);

        assertEquals("ONLINE", modalityJson);
        assertEquals("PENDING", statusJson);
    }

    @Test
    @DisplayName("Deve tratar valores null corretamente nos enums - CORRIGIDO!")
    void shouldHandleNullValuesCorrectlyInEnums() {

        assertThrows(IllegalArgumentException.class, () ->
                AppointmentModality.fromJson(null));

        assertThrows(IllegalArgumentException.class, () ->
                AppointmentStatus.fromJson(null));

    }

    @Test
    @DisplayName("Deve verificar se os enums podem ser usados em DTOs sem problemas")
    void shouldVerifyEnumsCanBeUsedInDtosWithoutIssues() {
        for (AppointmentModality modality : AppointmentModality.values()) {
            appointmentRequestDTO.setModality(modality);
            assertEquals(modality, appointmentRequestDTO.getModality());
        }

        appointmentResponseDTO.setModality("ONLINE");
        assertEquals("ONLINE", appointmentResponseDTO.getModality());

        appointmentResponseDTO.setModality("PERSONALLY");
        assertEquals("PERSONALLY", appointmentResponseDTO.getModality());

        appointmentResponseDTO.setStatus("PENDING");
        assertEquals("PENDING", appointmentResponseDTO.getStatus());

        appointmentResponseDTO.setStatus("ACCEPTED");
        assertEquals("ACCEPTED", appointmentResponseDTO.getStatus());
    }

    @Test
@DisplayName("Deve retornar lista ao chamar findAll")
void shouldCallFindAllAndReturnOk() {
    when(appointmentService.findAll()).thenReturn(List.of(appointmentResponseDTO));

    ResponseEntity<?> response = appointmentController.findAll();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    verify(appointmentService, times(1)).findAll();
}

@Test
@DisplayName("Deve retornar item ao chamar findById")
void shouldCallFindByIdAndReturnOk() {
    when(appointmentService.findById(appointmentId)).thenReturn(appointmentResponseDTO);

    ResponseEntity<?> response = appointmentController.findById(appointmentId);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    verify(appointmentService, times(1)).findById(appointmentId);
}

@Test
@DisplayName("Deve atualizar parcialmente chamando updatePartial")
void shouldCallUpdatePartialAndReturnOk() {
    // cria um DTO mínimo para patch (não precisa preencher tudo)
    var patchDto = new AppointmentPatchRequestDTO();
    when(appointmentService.updatePartial(appointmentId, patchDto)).thenReturn(appointmentResponseDTO);

    ResponseEntity<?> response = appointmentController.updatePartial(appointmentId, patchDto);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    verify(appointmentService, times(1)).updatePartial(appointmentId, patchDto);
}

@Test
@DisplayName("Deve chamar delete e retornar No Content")
void shouldCallDeleteAndReturnNoContent() {
    doNothing().when(appointmentService).delete(appointmentId);

    ResponseEntity<Void> response = appointmentController.delete(appointmentId);

    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    verify(appointmentService, times(1)).delete(appointmentId);
}

@Test
@DisplayName("Deve chamar searchAppointments cobrindo branches do fromDateTime")
void shouldCallSearchAppointmentsCoveringFromDateTimeBranches() {
    List<AppointmentFilterResponseDTO> mockResponse = List.of(new AppointmentFilterResponseDTO());
    UUID interpreterId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    AppointmentStatus status = AppointmentStatus.PENDING;
    AppointmentModality modality = AppointmentModality.ONLINE;
    Boolean hasRating = true;
    int dayLimit = 5;

    // Caso 1: fromDateTime válido (já existente)
    String validDate = "2024-06-15T09:00:00";
    when(appointmentService.searchAppointments(
            interpreterId, userId, status, modality, LocalDateTime.parse(validDate), hasRating, dayLimit))
        .thenReturn(mockResponse);
    appointmentController.searchAppointments(interpreterId, userId, status, modality, validDate, hasRating, dayLimit);

    // Caso 2: fromDateTime nulo
    when(appointmentService.searchAppointments(
            interpreterId, userId, status, modality, null, hasRating, dayLimit))
        .thenReturn(mockResponse);
    appointmentController.searchAppointments(interpreterId, userId, status, modality, null, hasRating, dayLimit);

    // Caso 3: fromDateTime vazio
    when(appointmentService.searchAppointments(
            interpreterId, userId, status, modality, null, hasRating, dayLimit))
        .thenReturn(mockResponse);
    appointmentController.searchAppointments(interpreterId, userId, status, modality, "   ", hasRating, dayLimit);

    // Aqui você pode apenas verificar se o service foi chamado corretamente
    verify(appointmentService, times(1))
        .searchAppointments(interpreterId, userId, status, modality, LocalDateTime.parse(validDate), hasRating, dayLimit);
    verify(appointmentService, times(2))
        .searchAppointments(interpreterId, userId, status, modality, null, hasRating, dayLimit);
}


}