package com.pointtils.pointtils.src.application.controllers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pointtils.pointtils.src.core.domain.entities.Person;
import com.pointtils.pointtils.src.infrastructure.repositories.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    @SuppressWarnings("unused")
    void up() {
        // Criar usuário de teste no banco H2
        Person person = new Person();
        person.setEmail("usuario@exemplo.com");
        person.setPassword("minhasenha123"); // alterar quando integrar com PasswordEncoder
        person.setName("João Silva");
        person.setPhone("51999999999");
        person.setPicture("picture_url");
        person.setStatus("active");

        userRepository.save(person);
    }

    @AfterEach
    @SuppressWarnings("unused")
    void down() {
        userRepository.deleteAll();
    }

    @Test
    void deveRetornar200QuandoLoginValido() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "usuario@exemplo.com",
                                    "password": "minhasenha123"
                                }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.user.email").value("usuario@exemplo.com"));
                // .andExpect(jsonPath("$.data.tokens.access_token").exists());
    }

    @Test
    void deveFalharComCredenciaisInvalidas() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "usuario@exemplo.com",
                                    "password": "senhaErrada"
                                }
                        """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Credenciais inválidas"));
    }
}
