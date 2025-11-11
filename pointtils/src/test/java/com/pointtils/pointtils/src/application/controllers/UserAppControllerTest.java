package com.pointtils.pointtils.src.application.controllers;

import com.pointtils.pointtils.src.application.dto.requests.UserAppPatchRequestDTO;
import com.pointtils.pointtils.src.application.dto.requests.UserAppRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.UserAppResponseDTO;
import com.pointtils.pointtils.src.application.services.UserAppService;
import com.pointtils.pointtils.src.infrastructure.configs.FirebaseConfig;
import com.pointtils.pointtils.src.infrastructure.configs.GlobalExceptionHandler;
import io.awspring.cloud.autoconfigure.s3.S3AutoConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import software.amazon.awssdk.services.s3.S3Client;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest(classes = UserAppController.class)
@EnableAutoConfiguration(exclude = S3AutoConfiguration.class)
class UserAppControllerTest {

    @MockitoBean
    private S3Client s3Client;
    @MockitoBean
    private FirebaseConfig firebaseConfig;
    @MockitoBean
    private UserAppService userAppService;
    @Autowired
    private UserAppController userAppController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userAppController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void createUserApp_ShouldReturnCreatedResponse() throws Exception {
        ArgumentCaptor<UserAppRequestDTO> requestArgumentCaptor = ArgumentCaptor.forClass(UserAppRequestDTO.class);
        when(userAppService.createUserApp(requestArgumentCaptor.capture())).thenReturn(buildResponse());

        String requestJson = "{\"token\":\"tokentoken\",\"platform\":\"android\",\"userId\":" +
                "\"991c7eb1-258a-497a-890e-fd794ab2d16b\",\"device_id\":\"3049ba07-f2b4-41c9-a689-740ec19668d0\"}";

        mockMvc.perform(post("/v1/user-apps")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Dados do aplicativo do usuário criados com sucesso"))
                .andExpect(jsonPath("$.data.token").value("tokentoken"))
                .andExpect(jsonPath("$.data.platform").value("android"))
                .andExpect(jsonPath("$.data.device_id").value("3049ba07-f2b4-41c9-a689-740ec19668d0"))
                .andExpect(jsonPath("$.data.user_id").value("991c7eb1-258a-497a-890e-fd794ab2d16b"))
                .andExpect(jsonPath("$.data.created_at").value("2025-10-31T09:30:30"))
                .andExpect(jsonPath("$.data.modified_at").value("2025-10-31T09:30:59"));

        assertEquals("tokentoken", requestArgumentCaptor.getValue().getToken());
        assertEquals("android", requestArgumentCaptor.getValue().getPlatform());
        assertEquals("991c7eb1-258a-497a-890e-fd794ab2d16b", requestArgumentCaptor.getValue().getUserId().toString());
        assertEquals("3049ba07-f2b4-41c9-a689-740ec19668d0", requestArgumentCaptor.getValue().getDeviceId());
    }

    @Test
    void getAllUserApps_ShouldReturnOkResponse() throws Exception {
        when(userAppService.getUserApps(any(), any())).thenReturn(Collections.singletonList(buildResponse()));

        mockMvc.perform(get("/v1/user-apps"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Dados de aplicativos dos usuários encontrados com sucesso"))
                .andExpect(jsonPath("$.data[0].token").value("tokentoken"))
                .andExpect(jsonPath("$.data[0].platform").value("android"))
                .andExpect(jsonPath("$.data[0].device_id").value("3049ba07-f2b4-41c9-a689-740ec19668d0"))
                .andExpect(jsonPath("$.data[0].user_id").value("991c7eb1-258a-497a-890e-fd794ab2d16b"))
                .andExpect(jsonPath("$.data[0].created_at").value("2025-10-31T09:30:30"))
                .andExpect(jsonPath("$.data[0].modified_at").value("2025-10-31T09:30:59"));
    }

    @Test
    void updateUserApp_ShouldReturnOkResponse() throws Exception {
        UUID userAppId = UUID.randomUUID();
        ArgumentCaptor<UserAppPatchRequestDTO> requestArgumentCaptor = ArgumentCaptor.forClass(UserAppPatchRequestDTO.class);
        when(userAppService.updateUserApp(eq(userAppId), requestArgumentCaptor.capture())).thenReturn(buildResponse());

        String requestJson = "{\"token\":\"tokentoken\",\"platform\":\"android\"," +
                "\"device_id\":\"3049ba07-f2b4-41c9-a689-740ec19668d0\"}";

        mockMvc.perform(patch("/v1/user-apps/{id}", userAppId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Dados do aplicativo do usuário atualizado com sucesso"))
                .andExpect(jsonPath("$.data.token").value("tokentoken"))
                .andExpect(jsonPath("$.data.platform").value("android"))
                .andExpect(jsonPath("$.data.device_id").value("3049ba07-f2b4-41c9-a689-740ec19668d0"))
                .andExpect(jsonPath("$.data.user_id").value("991c7eb1-258a-497a-890e-fd794ab2d16b"))
                .andExpect(jsonPath("$.data.created_at").value("2025-10-31T09:30:30"))
                .andExpect(jsonPath("$.data.modified_at").value("2025-10-31T09:30:59"));

        assertEquals("tokentoken", requestArgumentCaptor.getValue().getToken());
        assertEquals("android", requestArgumentCaptor.getValue().getPlatform());
        assertEquals("3049ba07-f2b4-41c9-a689-740ec19668d0", requestArgumentCaptor.getValue().getDeviceId());
    }

    @Test
    void deleteUserApps_ShouldReturnNoContentResponse() throws Exception {
        mockMvc.perform(delete("/v1/user-apps"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteUserAppById_ShouldReturnNoContentResponse() throws Exception {
        UUID userAppId = UUID.randomUUID();

        mockMvc.perform(delete("/v1/user-apps/{id}", userAppId))
                .andExpect(status().isNoContent());
    }

    private UserAppResponseDTO buildResponse() {
        UserAppResponseDTO response = new UserAppResponseDTO();
        response.setPlatform("android");
        response.setDeviceId("3049ba07-f2b4-41c9-a689-740ec19668d0");
        response.setToken("tokentoken");
        response.setUserId(UUID.fromString("991c7eb1-258a-497a-890e-fd794ab2d16b"));
        response.setCreatedAt(LocalDateTime.of(2025, 10, 31, 9, 30, 30));
        response.setModifiedAt(LocalDateTime.of(2025, 10, 31, 9, 30, 59));
        return response;
    }
}
