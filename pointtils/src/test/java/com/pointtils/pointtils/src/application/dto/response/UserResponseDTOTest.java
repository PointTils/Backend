package com.pointtils.pointtils.src.application.dto.response;

import com.pointtils.pointtils.src.application.dto.responses.UserResponseDTO;
import com.pointtils.pointtils.src.core.domain.entities.Person;
import com.pointtils.pointtils.src.core.domain.entities.enums.UserStatus;
import com.pointtils.pointtils.src.core.domain.entities.enums.UserTypeE;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserResponseDTOTest {

    @Test
    void shouldMapResponseFromEntity() {
        UUID personId = UUID.randomUUID();
        Person mockPerson = Person.builder()
                .id(personId)
                .status(UserStatus.ACTIVE)
                .type(UserTypeE.PERSON)
                .email("person1@email.com")
                .password("password")
                .picture("my_picture_url")
                .phone("54963636363")
                .build();
        UserResponseDTO actualResponse = UserResponseDTO.fromEntity(mockPerson);
        assertEquals(personId, actualResponse.getId());
        assertEquals("person1@email.com", actualResponse.getEmail());
        assertEquals("54963636363", actualResponse.getPhone());
        assertEquals("my_picture_url", actualResponse.getPicture());
        assertEquals(UserStatus.ACTIVE.name(), actualResponse.getStatus());
        assertEquals(UserTypeE.PERSON.name(), actualResponse.getType());
    }
}
