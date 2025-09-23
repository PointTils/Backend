package com.pointtils.pointtils.src.application.mapper;

import com.pointtils.pointtils.src.core.domain.entities.Person;
import com.pointtils.pointtils.src.core.domain.entities.enums.Gender;
import com.pointtils.pointtils.src.core.domain.entities.enums.UserStatus;
import com.pointtils.pointtils.src.core.domain.entities.enums.UserTypeE;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class PersonResponseMapperTest {

    private PersonResponseMapper personResponseMapper;

    @BeforeEach
    void setUp() {
        personResponseMapper = new PersonResponseMapper();
    }

    @Test
    @DisplayName("Deve mapear dados da pessoa para DTO e mascarar CPF")
    void shouldMapPersonToDtoWithMaskedCPF() {
        UUID id = UUID.randomUUID();
        LocalDate birthDate = LocalDate.of(2000, 10, 26);
        Person person = Person.builder()
                .id(id)
                .name("Meu Nome")
                .cpf("04884554774")
                .email("meuemail@gmail.com")
                .password("minhasenha123")
                .birthday(birthDate)
                .gender(Gender.MALE)
                .phone("54963636363")
                .picture("minhafoto")
                .status(UserStatus.ACTIVE)
                .type(UserTypeE.PERSON)
                .build();

        var actualDTO = personResponseMapper.toResponseDTO(person);
        assertEquals(id, actualDTO.getId());
        assertEquals("Meu Nome", actualDTO.getName());
        assertEquals("048.***.***-74", actualDTO.getCpf());
        assertEquals("meuemail@gmail.com", actualDTO.getEmail());
        assertEquals(Gender.MALE, actualDTO.getGender());
        assertEquals("54963636363", actualDTO.getPhone());
        assertEquals("minhafoto", actualDTO.getPicture());
        assertEquals(birthDate, actualDTO.getBirthday());
        assertEquals(UserStatus.ACTIVE, actualDTO.getStatus());
        assertEquals(UserTypeE.PERSON, actualDTO.getType());
    }

    @Test
    @DisplayName("Deve mapear dados da pessoa para DTO sem mascarar CPF se for nulo")
    void shouldNotMaskNullCPF() {
        UUID id = UUID.randomUUID();
        Person person = Person.builder().id(id).build();

        var actualDTO = personResponseMapper.toResponseDTO(person);
        assertEquals(id, actualDTO.getId());
        assertNull(actualDTO.getCpf());
    }

    @Test
    @DisplayName("Deve mapear dados da pessoa para DTO sem mascarar CPF não tiver 11 dígitos")
    void shouldNotMaskInvalidCPF() {
        UUID id = UUID.randomUUID();
        Person person = Person.builder().id(id).cpf("123").build();

        var actualDTO = personResponseMapper.toResponseDTO(person);
        assertEquals(id, actualDTO.getId());
        assertEquals("123", actualDTO.getCpf());
    }
}
