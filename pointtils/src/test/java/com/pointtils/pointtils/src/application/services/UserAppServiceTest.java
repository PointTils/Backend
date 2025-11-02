package com.pointtils.pointtils.src.application.services;

import com.pointtils.pointtils.src.application.dto.requests.UserAppPatchRequestDTO;
import com.pointtils.pointtils.src.application.dto.requests.UserAppRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.UserAppResponseDTO;
import com.pointtils.pointtils.src.application.mapper.UserAppMapper;
import com.pointtils.pointtils.src.core.domain.entities.Person;
import com.pointtils.pointtils.src.core.domain.entities.UserApp;
import com.pointtils.pointtils.src.infrastructure.repositories.UserAppRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserAppServiceTest {

    @Mock
    private UserAppRepository userAppRepository;
    @Mock
    private UserService userService;
    @Mock
    private UserAppMapper userAppMapper;
    @InjectMocks
    private UserAppService userAppService;

    @Test
    void createUserApp_ShouldReturnResponseDTO() {
        UserAppRequestDTO request = new UserAppRequestDTO();
        request.setUserId(UUID.randomUUID());
        request.setToken("token");
        request.setDeviceId("deviceId");
        request.setPlatform("platform");

        Person person = new Person();
        UserApp userApp = new UserApp();
        UserAppResponseDTO response = new UserAppResponseDTO();

        when(userService.findById(request.getUserId())).thenReturn(person);
        when(userAppRepository.save(any(UserApp.class))).thenReturn(userApp);
        when(userAppMapper.toResponseDto(userApp)).thenReturn(response);

        UserAppResponseDTO result = userAppService.createUserApp(request);

        assertNotNull(result);
        verify(userService).findById(request.getUserId());
        verify(userAppRepository).save(any(UserApp.class));
        verify(userAppMapper).toResponseDto(userApp);
    }

    @Test
    void getUserApps_ShouldReturnListOfResponseDTOs() {
        UUID userId = UUID.randomUUID();
        String deviceId = "deviceId";
        UserApp userApp = new UserApp();
        UserAppResponseDTO response = new UserAppResponseDTO();

        when(userAppRepository.findAllByFilters(userId, deviceId)).thenReturn(List.of(userApp));
        when(userAppMapper.toResponseDto(userApp)).thenReturn(response);

        List<UserAppResponseDTO> result = userAppService.getUserApps(userId, deviceId);

        assertEquals(1, result.size());
        verify(userAppRepository).findAllByFilters(userId, deviceId);
        verify(userAppMapper).toResponseDto(userApp);
    }

    @Test
    void updateUserApp_ShouldUpdateAndReturnResponseDTO() {
        UUID userAppId = UUID.randomUUID();
        UserAppPatchRequestDTO request = new UserAppPatchRequestDTO();
        request.setDeviceId("newDeviceId");
        request.setToken("newToken");
        request.setPlatform("newPlatform");

        UserApp userApp = new UserApp();
        UserAppResponseDTO response = new UserAppResponseDTO();

        when(userAppRepository.findById(userAppId)).thenReturn(Optional.of(userApp));
        when(userAppRepository.save(userApp)).thenReturn(userApp);
        when(userAppMapper.toResponseDto(userApp)).thenReturn(response);

        UserAppResponseDTO result = userAppService.updateUserApp(userAppId, request);

        assertNotNull(result);
        assertEquals("newDeviceId", userApp.getDeviceId());
        assertEquals("newToken", userApp.getToken());
        assertEquals("newPlatform", userApp.getPlatform());
        verify(userAppRepository).findById(userAppId);
        verify(userAppRepository).save(userApp);
        verify(userAppMapper).toResponseDto(userApp);
    }

    @Test
    void deleteUserApps_ShouldDeleteAllMatchingApps() {
        UUID userId = UUID.randomUUID();
        String deviceId = "deviceId";
        UserApp userApp = new UserApp();

        when(userAppRepository.findAllByFilters(userId, deviceId)).thenReturn(List.of(userApp));

        userAppService.deleteUserApps(userId, deviceId);

        verify(userAppRepository).findAllByFilters(userId, deviceId);
        verify(userAppRepository).deleteAll(List.of(userApp));
    }

    @Test
    void deleteUserAppById_ShouldDeleteApp() {
        UUID userAppId = UUID.randomUUID();
        UserApp userApp = new UserApp();
        when(userAppRepository.findById(userAppId)).thenReturn(Optional.of(userApp));

        userAppService.deleteUserAppById(userAppId);

        verify(userAppRepository).findById(userAppId);
        verify(userAppRepository).delete(userApp);
    }

    @Test
    void getUserAppById_ShouldThrowExceptionIfNotFound() {
        UUID userAppId = UUID.randomUUID();
        when(userAppRepository.findById(userAppId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userAppService.deleteUserAppById(userAppId));
        verify(userAppRepository).findById(userAppId);
    }
}
