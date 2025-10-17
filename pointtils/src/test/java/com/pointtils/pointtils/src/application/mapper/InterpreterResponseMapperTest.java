package com.pointtils.pointtils.src.application.mapper;

import com.pointtils.pointtils.src.application.dto.responses.InterpreterListResponseDTO;
import com.pointtils.pointtils.src.application.dto.responses.InterpreterResponseDTO;
import com.pointtils.pointtils.src.core.domain.entities.Interpreter;
import com.pointtils.pointtils.src.core.domain.entities.Location;
import com.pointtils.pointtils.src.core.domain.entities.Specialty;
import com.pointtils.pointtils.src.core.domain.entities.enums.Gender;
import com.pointtils.pointtils.src.core.domain.entities.enums.InterpreterModality;
import com.pointtils.pointtils.src.core.domain.entities.enums.UserStatus;
import com.pointtils.pointtils.src.core.domain.entities.enums.UserTypeE;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class InterpreterResponseMapperTest {

    @Spy
    private LocationMapper locationMapper = new LocationMapper();
    @Spy
    private UserSpecialtyMapper userSpecialtyMapper = new UserSpecialtyMapper();
    @InjectMocks
    private InterpreterResponseMapper mapper;

    @Test
    void shouldMapToResponseDTO() {
        // Arrange
        UUID id = UUID.randomUUID();
        Location location = Location.builder()
                .id(UUID.randomUUID())
                .uf("SP")
                .city("São Paulo")
                .neighborhood("Higienópolis")
                .build();

        List<Location> locations = new ArrayList<>();
        locations.add(location);
        Interpreter interpreter = Interpreter.builder()
                .id(id)
                .email("test@example.com")
                .type(UserTypeE.INTERPRETER)
                .status(UserStatus.ACTIVE)
                .phone("123456789")
                .picture("profile.jpg")
                .name("John Doe")
                .gender(Gender.MALE)
                .birthday(LocalDate.of(1990, 1, 1))
                .cpf("12345678901")
                .cnpj("12345678000199")
                .rating(BigDecimal.valueOf(4.5))
                .modality(InterpreterModality.ONLINE)
                .description("Experienced interpreter")
                .imageRights(true)
                .locations(locations)
                .specialties(Set.of(new Specialty("Libras")))
                .build();

        // Act
        InterpreterResponseDTO responseDTO = mapper.toResponseDTO(interpreter);

        // Assert
        assertThat(responseDTO.getId()).isEqualTo(id);
        assertThat(responseDTO.getEmail()).isEqualTo("test@example.com");
        assertThat(responseDTO.getName()).isEqualTo("John Doe");
        assertThat(responseDTO.getGender()).isEqualTo(Gender.MALE);
        assertThat(responseDTO.getCpf()).isEqualTo("123.***.***-01");
        assertThat(responseDTO.getProfessionalData().getRating()).isEqualTo(BigDecimal.valueOf(4.5));
        assertThat(responseDTO.getLocations()).hasSize(1);
        assertThat(responseDTO.getSpecialties()).hasSize(1);
    }

    @Test
    void shouldMapToListResponseDTO() {
        // Arrange
        UUID id = UUID.randomUUID();
        Location location = Location.builder()
                .id(UUID.randomUUID())
                .uf("SP")
                .city("São Paulo")
                .neighborhood("Higienópolis")
                .build();

        List<Location> locations = new ArrayList<>();
        locations.add(location);
        Interpreter interpreter = Interpreter.builder()
                .id(id)
                .name("John Doe")
                .rating(BigDecimal.valueOf(4.5))
                .modality(InterpreterModality.ONLINE)
                .picture("profile.jpg")
                .locations(locations)
                .build();

        // Act
        InterpreterListResponseDTO listResponseDTO = mapper.toListResponseDTO(interpreter);

        // Assert
        assertThat(listResponseDTO.getId()).isEqualTo(id);
        assertThat(listResponseDTO.getName()).isEqualTo("John Doe");
        assertThat(listResponseDTO.getProfessionalData().getRating()).isEqualTo(BigDecimal.valueOf(4.5));
        assertThat(listResponseDTO.getProfessionalData().getModality()).isEqualTo(InterpreterModality.ONLINE);
        assertThat(listResponseDTO.getPicture()).isEqualTo("profile.jpg");
        assertThat(listResponseDTO.getLocations()).hasSize(1);
    }
}