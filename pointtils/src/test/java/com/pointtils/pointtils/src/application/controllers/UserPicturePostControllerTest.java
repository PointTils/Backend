package com.pointtils.pointtils.src.application.controllers;

import com.pointtils.pointtils.src.application.dto.requests.UserPicturePostRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.UserResponseDTO;
import com.pointtils.pointtils.src.application.services.UserPicturePostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserPicturePostController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserPicturePostControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private UserPicturePostService userService;

        @MockBean
        private com.pointtils.pointtils.src.infrastructure.configs.JwtService jwtService;

        @MockBean
        private com.pointtils.pointtils.src.infrastructure.configs.MemoryBlacklistService memoryBlacklistService;

        private UUID userId;
        private UserResponseDTO userResponse;

        @BeforeEach
        void setUp() {
                userId = UUID.randomUUID();
                userResponse = UserResponseDTO.builder()
                                .id(userId)
                                .email("john.doe@example.com")
                                .type("INTERPRETER")
                                .status("ACTIVE")
                                .phone("123456789")
                                .picture("http://example.com/avatar.png")
                                .specialties(List.of())
                                .build();
        }

        @Test
        @DisplayName("POST /v1/users/{id}/picture deve atualizar a foto com sucesso")
        void uploadPicture_ShouldReturnOk() throws Exception {
                MockMultipartFile file = new MockMultipartFile(
                                "file",
                                "avatar.png",
                                MediaType.IMAGE_PNG_VALUE,
                                "fake-image-content".getBytes());

                when(userService.updatePicture(any(UserPicturePostRequestDTO.class)))
                                .thenReturn(userResponse);

                mockMvc.perform(multipart("/v1/users/{id}/picture", userId)
                                .file(file)
                                .contentType(MediaType.MULTIPART_FORM_DATA))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(userId.toString()))
                                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                                .andExpect(jsonPath("$.type").value("INTERPRETER"))
                                .andExpect(jsonPath("$.status").value("ACTIVE"))
                                .andExpect(jsonPath("$.phone").value("123456789"))
                                .andExpect(jsonPath("$.picture").value("http://example.com/avatar.png"))
                                .andExpect(jsonPath("$.specialties").isArray());

                ArgumentCaptor<UserPicturePostRequestDTO> captor = ArgumentCaptor
                                .forClass(UserPicturePostRequestDTO.class);
                verify(userService).updatePicture(captor.capture());

                assertThat(captor.getValue().getUserId()).isEqualTo(userId);
                assertThat(captor.getValue().getFile().getOriginalFilename()).isEqualTo("avatar.png");
        }

        @Test
        @DisplayName("POST /v1/users/{id}/picture deve retornar 503 quando upload estiver desabilitado")
        void uploadPicture_ShouldReturnServiceUnavailable_WhenUploadDisabled() throws Exception {
                MockMultipartFile file = new MockMultipartFile(
                                "file",
                                "avatar.png",
                                MediaType.IMAGE_PNG_VALUE,
                                "fake-image-content".getBytes());

                when(userService.updatePicture(any(UserPicturePostRequestDTO.class)))
                                .thenThrow(new UnsupportedOperationException("Upload de fotos desabilitado"));

                mockMvc.perform(multipart("/v1/users/{id}/picture", userId)
                                .file(file)
                                .contentType(MediaType.MULTIPART_FORM_DATA))
                                .andExpect(status().isServiceUnavailable());
        }

}
