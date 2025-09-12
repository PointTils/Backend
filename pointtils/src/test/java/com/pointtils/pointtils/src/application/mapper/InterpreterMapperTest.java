package com.pointtils.pointtils.src.application.mapper;

import com.pointtils.pointtils.src.application.dto.requests.ProfessionalRequestDTO;
import com.pointtils.pointtils.src.core.domain.entities.Interpreter;
import com.pointtils.pointtils.src.core.domain.entities.enums.InterpreterModality;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class InterpreterMapperTest {

    @Test
    void shouldMapProfessionalDataDtoToDomain() {
        Interpreter interpreter = new Interpreter();
        ProfessionalRequestDTO dto = ProfessionalRequestDTO.builder()
                .cnpj("12345678000190")
                .minValue(BigDecimal.valueOf(100.0))
                .maxValue(BigDecimal.valueOf(500.0))
                .modality("online")
                .description("Interprete de Libras com experiencia em palestras")
                .imageRights(true)
                .build();

        try (MockedStatic<InterpreterModality> utilities = Mockito.mockStatic(InterpreterModality.class)) {
            utilities.when(() -> InterpreterModality.fromString("online"))
                    .thenReturn(InterpreterModality.ONLINE);
            InterpreterMapper.toDomain(dto, interpreter);
        }

        assertEquals("12345678000190", interpreter.getCnpj());
        assertEquals(BigDecimal.valueOf(100.0), interpreter.getMinValue());
        assertEquals(BigDecimal.valueOf(500.0), interpreter.getMaxValue());
        assertEquals(InterpreterModality.ONLINE, interpreter.getModality());
        assertEquals("Interprete de Libras com experiencia em palestras", interpreter.getDescription());
        assertEquals(true, interpreter.getImageRights());
    }

    @Test
    void shouldMapModalityToEnum() {
        try (MockedStatic<InterpreterModality> utilities = Mockito.mockStatic(InterpreterModality.class)) {
            utilities.when(() -> InterpreterModality.fromString("online"))
                    .thenReturn(InterpreterModality.ONLINE);
            assertEquals(InterpreterModality.ONLINE, InterpreterMapper.toInterpreterModality("online"));
        }
    }

    @Test
    void shouldMapDomainToProfessionalDataDto() {
        Interpreter interpreter = Interpreter.builder()
                .cnpj("12345678000190")
                .minValue(BigDecimal.valueOf(100.0))
                .maxValue(BigDecimal.valueOf(500.0))
                .modality(InterpreterModality.ONLINE)
                .description("Interprete de Libras com experiencia em palestras")
                .imageRights(true)
                .build();
        ProfessionalRequestDTO dto = InterpreterMapper.toDto(interpreter);

        assertEquals("12345678000190", dto.getCnpj());
        assertEquals(BigDecimal.valueOf(100.0), dto.getMinValue());
        assertEquals(BigDecimal.valueOf(500.0), dto.getMaxValue());
        assertEquals("online", dto.getModality());
        assertEquals("Interprete de Libras com experiencia em palestras", dto.getDescription());
        assertEquals(true, dto.getImageRights());
    }

    @Test
    void shouldMapDomainWithNullModalityToProfessionalDataDto() {
        Interpreter interpreter = Interpreter.builder()
                .cnpj("12345678000190")
                .minValue(BigDecimal.valueOf(100.0))
                .maxValue(BigDecimal.valueOf(500.0))
                .modality(null)
                .description("Interprete de Libras com experiencia em palestras")
                .imageRights(true)
                .build();
        ProfessionalRequestDTO dto = InterpreterMapper.toDto(interpreter);

        assertEquals("12345678000190", dto.getCnpj());
        assertEquals(BigDecimal.valueOf(100.0), dto.getMinValue());
        assertEquals(BigDecimal.valueOf(500.0), dto.getMaxValue());
        assertNull(dto.getModality());
        assertEquals("Interprete de Libras com experiencia em palestras", dto.getDescription());
        assertEquals(true, dto.getImageRights());
    }
}
