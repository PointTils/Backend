package com.pointtils.pointtils.src.application.mapper;

import com.pointtils.pointtils.src.core.domain.entities.Person;
import com.pointtils.pointtils.src.core.domain.entities.enums.Gender;
import com.pointtils.pointtils.src.core.domain.entities.enums.UserStatus;
import com.pointtils.pointtils.src.core.domain.entities.enums.UserTypeE;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PersonResponseMapperTest {

    private PersonResponseMapper personResponseMapper;

    @BeforeEach
    void setUp() {
        personResponseMapper = new PersonResponseMapper();
    }

    @Test
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
}
