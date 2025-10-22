package com.pointtils.pointtils.src.application.dto.requests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes Unitários do EmailRequestDTO")
class EmailRequestDTOTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @Test
    @DisplayName("Deve criar EmailRequestDTO com todos os campos válidos")
    void deveCriarEmailRequestDTOComTodosCamposValidos() {
        EmailRequestDTO dto = new EmailRequestDTO(
            "test@example.com",
            "Assunto Teste",
            "Corpo do email",
            "PointTils"
        );

        assertEquals("test@example.com", dto.getTo());
        assertEquals("Assunto Teste", dto.getSubject());
        assertEquals("Corpo do email", dto.getBody());
        assertEquals("PointTils", dto.getFromName());
    }

    @Test
    @DisplayName("Deve criar EmailRequestDTO com construtor padrão")
    void deveCriarEmailRequestDTOComConstrutorPadrao() {
        EmailRequestDTO dto = new EmailRequestDTO();

        assertNull(dto.getTo());
        assertNull(dto.getSubject());
        assertNull(dto.getBody());
        assertEquals("PointTils", dto.getFromName());
    }

    @Test
    @DisplayName("Deve alterar valores usando setters")
    void deveAlterarValoresUsandoSetters() {
        EmailRequestDTO dto = new EmailRequestDTO();

        dto.setTo("novo@email.com");
        dto.setSubject("Novo Assunto");
        dto.setBody("Novo corpo");
        dto.setFromName("Novo Remetente");

        assertEquals("novo@email.com", dto.getTo());
        assertEquals("Novo Assunto", dto.getSubject());
        assertEquals("Novo corpo", dto.getBody());
        assertEquals("Novo Remetente", dto.getFromName());
    }

    @Test
    @DisplayName("Deve validar email com formato válido")
    void deveValidarEmailComFormatoValido() {
        EmailRequestDTO dto = new EmailRequestDTO(
            "usuario@dominio.com",
            "Assunto",
            "Corpo",
            "PointTils"
        );

        Set<ConstraintViolation<EmailRequestDTO>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Deve retornar erro de validação quando email for inválido")
    void deveRetornarErroValidacaoQuandoEmailForInvalido() {
        EmailRequestDTO dto = new EmailRequestDTO(
            "email-invalido",
            "Assunto",
            "Corpo",
            "PointTils"
        );

        Set<ConstraintViolation<EmailRequestDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getMessage().contains("email válido")));
    }

    @Test
    @DisplayName("Deve retornar erro de validação quando destinatário for vazio")
    void deveRetornarErroValidacaoQuandoDestinatarioForVazio() {
        EmailRequestDTO dto = new EmailRequestDTO(
            "",
            "Assunto",
            "Corpo",
            "PointTils"
        );

        Set<ConstraintViolation<EmailRequestDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getMessage().contains("obrigatório")));
    }

    @Test
    @DisplayName("Deve retornar erro de validação quando destinatário for nulo")
    void deveRetornarErroValidacaoQuandoDestinatarioForNulo() {
        EmailRequestDTO dto = new EmailRequestDTO(
            null,
            "Assunto",
            "Corpo",
            "PointTils"
        );

        Set<ConstraintViolation<EmailRequestDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getMessage().contains("obrigatório")));
    }

    @Test
    @DisplayName("Deve retornar erro de validação quando assunto for vazio")
    void deveRetornarErroValidacaoQuandoAssuntoForVazio() {
        EmailRequestDTO dto = new EmailRequestDTO(
            "test@example.com",
            "",
            "Corpo",
            "PointTils"
        );

        Set<ConstraintViolation<EmailRequestDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getMessage().contains("obrigatório")));
    }

    @Test
    @DisplayName("Deve retornar erro de validação quando assunto for nulo")
    void deveRetornarErroValidacaoQuandoAssuntoForNulo() {
        EmailRequestDTO dto = new EmailRequestDTO(
            "test@example.com",
            null,
            "Corpo",
            "PointTils"
        );

        Set<ConstraintViolation<EmailRequestDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getMessage().contains("obrigatório")));
    }

    @Test
    @DisplayName("Deve retornar erro de validação quando corpo for vazio")
    void deveRetornarErroValidacaoQuandoCorpoForVazio() {
        EmailRequestDTO dto = new EmailRequestDTO(
            "test@example.com",
            "Assunto",
            "",
            "PointTils"
        );

        Set<ConstraintViolation<EmailRequestDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getMessage().contains("obrigatório")));
    }

    @Test
    @DisplayName("Deve retornar erro de validação quando corpo for nulo")
    void deveRetornarErroValidacaoQuandoCorpoForNulo() {
        EmailRequestDTO dto = new EmailRequestDTO(
            "test@example.com",
            "Assunto",
            null,
            "PointTils"
        );

        Set<ConstraintViolation<EmailRequestDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getMessage().contains("obrigatório")));
    }

    @Test
    @DisplayName("Deve aceitar fromName nulo com valor padrão")
    void deveAceitarFromNameNuloComValorPadrao() {
        EmailRequestDTO dto = new EmailRequestDTO(
            "test@example.com",
            "Assunto",
            "Corpo",
            null
        );

        Set<ConstraintViolation<EmailRequestDTO>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
        assertNull(dto.getFromName());
    }

    @Test
    @DisplayName("Deve ser igual quando todos os campos forem iguais")
    void deveSerIgualQuandoTodosCamposForemIguais() {
        EmailRequestDTO dto1 = new EmailRequestDTO(
            "test@example.com",
            "Assunto",
            "Corpo",
            "PointTils"
        );
        
        EmailRequestDTO dto2 = new EmailRequestDTO(
            "test@example.com",
            "Assunto",
            "Corpo",
            "PointTils"
        );

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    @DisplayName("Deve ser diferente quando algum campo for diferente")
    void deveSerDiferenteQuandoAlgumCampoForDiferente() {
        EmailRequestDTO dto1 = new EmailRequestDTO(
            "test@example.com",
            "Assunto",
            "Corpo",
            "PointTils"
        );
        
        EmailRequestDTO dto2 = new EmailRequestDTO(
            "outro@example.com",
            "Assunto",
            "Corpo",
            "PointTils"
        );

        assertNotEquals(dto1, dto2);
    }

    @Test
    @DisplayName("Deve retornar string representativa do objeto")
    void deveRetornarStringRepresentativaDoObjeto() {
        EmailRequestDTO dto = new EmailRequestDTO(
            "test@example.com",
            "Assunto",
            "Corpo",
            "PointTils"
        );

        String toString = dto.toString();

        assertNotNull(toString);
        assertTrue(toString.contains("test@example.com"));
        assertTrue(toString.contains("Assunto"));
        assertTrue(toString.contains("Corpo"));
        assertTrue(toString.contains("PointTils"));
    }
}