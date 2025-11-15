package com.pointtils.pointtils.src.application.services;

import com.pointtils.pointtils.src.application.dto.requests.UserAppPatchRequestDTO;
import com.pointtils.pointtils.src.application.dto.requests.UserAppRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.UserAppResponseDTO;
import com.pointtils.pointtils.src.application.mapper.UserAppMapper;
import com.pointtils.pointtils.src.core.domain.entities.User;
import com.pointtils.pointtils.src.core.domain.entities.UserApp;
import com.pointtils.pointtils.src.infrastructure.repositories.UserAppRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserAppService {

    private final UserAppRepository userAppRepository;
    private final UserService userService;
    private final UserAppMapper userAppMapper;

    public UserAppResponseDTO createUserApp(UserAppRequestDTO appRequestData) {
        User foundUser = userService.findById(appRequestData.getUserId());
        var userApp = UserApp.builder()
                .user(foundUser)
                .token(appRequestData.getToken())
                .deviceId(appRequestData.getDeviceId())
                .platform(appRequestData.getPlatform())
                .build();
        var createdUserApp = userAppRepository.save(userApp);
        return userAppMapper.toResponseDto(createdUserApp);
    }

    public List<UserAppResponseDTO> getUserApps(UUID userId, String deviceId) {
        List<UserApp> userApps = userAppRepository.findAllByFilters(userId, deviceId);
        return userApps.stream()
                .map(userAppMapper::toResponseDto)
                .toList();
    }

    public UserAppResponseDTO updateUserApp(UUID userAppId, UserAppPatchRequestDTO requestData) {
        UserApp foundUserApp = getUserAppById(userAppId);

        if (Objects.nonNull(requestData.getDeviceId())) {
            foundUserApp.setDeviceId(requestData.getDeviceId());
        }

        if (Objects.nonNull(requestData.getToken())) {
            foundUserApp.setToken(requestData.getToken());
        }

        if (Objects.nonNull(requestData.getPlatform())) {
            foundUserApp.setPlatform(requestData.getPlatform());
        }

        var updatedUserApp = userAppRepository.save(foundUserApp);
        return userAppMapper.toResponseDto(updatedUserApp);
    }

    public void deleteUserApps(UUID userId, String deviceId) {
        if (Objects.isNull(userId) && StringUtils.isBlank(deviceId)) {
            throw new IllegalArgumentException("Identificador do usuário ou do dispositivo devem ser informados");
        }
        List<UserApp> userApps = userAppRepository.findAllByFilters(userId, deviceId);
        userAppRepository.deleteAll(userApps);
        log.info("Foram deletados {} dados de aplicativos de usuários com sucesso", userApps.size());
    }

    public void deleteUserAppById(UUID userAppId) {
        UserApp foundUserApp = getUserAppById(userAppId);
        userAppRepository.delete(foundUserApp);
    }

    protected List<UserApp> getUserAppsByUserId(UUID userId) {
        return userAppRepository.findAllByUserId(userId);
    }

    private UserApp getUserAppById(UUID userAppId) {
        return userAppRepository.findById(userAppId)
                .orElseThrow(() -> new EntityNotFoundException("Dados do aplicativo do usuário não encontrados"));
    }
}
