package com.pointtils.pointtils.src.application.mapper;

import com.pointtils.pointtils.src.application.dto.responses.UserAppResponseDTO;
import com.pointtils.pointtils.src.core.domain.entities.Person;
import com.pointtils.pointtils.src.core.domain.entities.UserApp;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserAppMapperTest {

    private final UserAppMapper userAppMapper = new UserAppMapper();

    @Test
    void shouldMapUserAppToResponse() {
        UUID userId = UUID.randomUUID();
        UUID userAppId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime modifiedAt = LocalDateTime.now();

        Person person = new Person();
        person.setId(userId);

        UserApp userApp = new UserApp();
        userApp.setId(userAppId);
        userApp.setDeviceId("device123");
        userApp.setToken("token123");
        userApp.setPlatform("Android");
        userApp.setUser(person);
        userApp.setCreatedAt(createdAt);
        userApp.setModifiedAt(modifiedAt);

        UserAppResponseDTO responseDTO = userAppMapper.toResponseDto(userApp);

        assertEquals(userAppId, responseDTO.getId());
        assertEquals("device123", responseDTO.getDeviceId());
        assertEquals("token123", responseDTO.getToken());
        assertEquals("Android", responseDTO.getPlatform());
        assertEquals(userId, responseDTO.getUserId());
        assertEquals(createdAt, responseDTO.getCreatedAt());
        assertEquals(modifiedAt, responseDTO.getModifiedAt());
    }
}
