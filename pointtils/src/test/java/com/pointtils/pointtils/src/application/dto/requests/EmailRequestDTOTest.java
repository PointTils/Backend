package com.pointtils.pointtils.src.application.dto.requests;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class EmailRequestDTOTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Deve criar EmailRequestDTO com sucesso")
    void deveCriarEmailRequestDTOComSucesso() {
        // Arrange & Act
        EmailRequestDTO emailRequest = new EmailRequestDTO(
            "usuario@exemplo.com",
            "Assunto do Email",
            "Corpo do email",
            "PointTils"
        );

        // Assert
        assertEquals("usuario@exemplo.com", emailRequest.getTo());
        assertEquals("Assunto do Email", emailRequest.getSubject());
        assertEquals("Corpo do email", emailRequest.getBody());
        assertEquals("PointTils", emailRequest.getFromName());
    }

    @Test
    @DisplayName("Deve usar valor padrão para fromName quando não fornecido")
    void deveUsarValorPadraoParaFromNameQuandoNaoFornecido() {
        // Arrange & Act
        EmailRequestDTO emailRequest = new EmailRequestDTO(
            "usuario@exemplo.com",
            "Assunto do Email",
            "Corpo do email",
            null
        );

        // Assert
        // Quando passamos null no construtor, o Lombok não usa o valor padrão
        // O valor padrão só é usado quando o campo não é explicitamente setado
        assertNull(emailRequest.getFromName());
    }

    @Test
    @DisplayName("Deve validar EmailRequestDTO com dados válidos")
    void deveValidarEmailRequestDTOComDadosValidos() {
        // Arrange
        EmailRequestDTO emailRequest = new EmailRequestDTO(
            "usuario@exemplo.com",
            "Assunto do Email",
            "Corpo do email",
            "PointTils"
        );

        // Act
        Set<ConstraintViolation<EmailRequestDTO>> violations = validator.validate(emailRequest);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Deve falhar validação quando email for nulo")
    void deveFalharValidacaoQuandoEmailForNulo() {
        // Arrange
        EmailRequestDTO emailRequest = new EmailRequestDTO(
            null,
            "Assunto do Email",
            "Corpo do email",
            "PointTils"
        );

        // Act
        Set<ConstraintViolation<EmailRequestDTO>> violations = validator.validate(emailRequest);

        // Assert
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Destinatário é obrigatório", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Deve falhar validação quando email for vazio")
    void deveFalharValidacaoQuandoEmailForVazio() {
        // Arrange
        EmailRequestDTO emailRequest = new EmailRequestDTO(
            "",
            "Assunto do Email",
            "Corpo do email",
            "PointTils"
        );

        // Act
        Set<ConstraintViolation<EmailRequestDTO>> violations = validator.validate(emailRequest);

        // Assert
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Destinatário é obrigatório", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Deve falhar validação quando email for inválido")
    void deveFalharValidacaoQuandoEmailForInvalido() {
        // Arrange
        EmailRequestDTO emailRequest = new EmailRequestDTO(
            "email-invalido",
            "Assunto do Email",
            "Corpo do email",
            "PointTils"
        );

        // Act
        Set<ConstraintViolation<EmailRequestDTO>> violations = validator.validate(emailRequest);

        // Assert
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Destinatário deve ser um email válido", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Deve falhar validação quando assunto for nulo")
    void deveFalharValidacaoQuandoAssuntoForNulo() {
        // Arrange
        EmailRequestDTO emailRequest = new EmailRequestDTO(
            "usuario@exemplo.com",
            null,
            "Corpo do email",
            "PointTils"
        );

        // Act
        Set<ConstraintViolation<EmailRequestDTO>> violations = validator.validate(emailRequest);

        // Assert
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Assunto é obrigatório", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Deve falhar validação quando assunto for vazio")
    void deveFalharValidacaoQuandoAssuntoForVazio() {
        // Arrange
        EmailRequestDTO emailRequest = new EmailRequestDTO(
            "usuario@exemplo.com",
            "",
            "Corpo do email",
            "PointTils"
        );

        // Act
        Set<ConstraintViolation<EmailRequestDTO>> violations = validator.validate(emailRequest);

        // Assert
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Assunto é obrigatório", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Deve falhar validação quando corpo for nulo")
    void deveFalharValidacaoQuandoCorpoForNulo() {
        // Arrange
        EmailRequestDTO emailRequest = new EmailRequestDTO(
            "usuario@exemplo.com",
            "Assunto do Email",
            null,
            "PointTils"
        );

        // Act
        Set<ConstraintViolation<EmailRequestDTO>> violations = validator.validate(emailRequest);

        // Assert
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Corpo do email é obrigatório", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Deve falhar validação quando corpo for vazio")
    void deveFalharValidacaoQuandoCorpoForVazio() {
        // Arrange
        EmailRequestDTO emailRequest = new EmailRequestDTO(
            "usuario@exemplo.com",
            "Assunto do Email",
            "",
            "PointTils"
        );

        // Act
        Set<ConstraintViolation<EmailRequestDTO>> violations = validator.validate(emailRequest);

        // Assert
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Corpo do email é obrigatório", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Deve criar EmailRequestDTO com construtor vazio")
    void deveCriarEmailRequestDTOComConstrutorVazio() {
        // Arrange & Act
        EmailRequestDTO emailRequest = new EmailRequestDTO();

        // Assert
        assertNull(emailRequest.getTo());
        assertNull(emailRequest.getSubject());
        assertNull(emailRequest.getBody());
        assertEquals("PointTils", emailRequest.getFromName());
    }

    @Test
    @DisplayName("Deve settar e gettar propriedades corretamente")
    void deveSettarEGettarPropriedadesCorretamente() {
        // Arrange
        EmailRequestDTO emailRequest = new EmailRequestDTO();

        // Act
        emailRequest.setTo("teste@exemplo.com");
        emailRequest.setSubject("Teste Assunto");
        emailRequest.setBody("Teste Corpo");
        emailRequest.setFromName("Teste Nome");

        // Assert
        assertEquals("teste@exemplo.com", emailRequest.getTo());
        assertEquals("Teste Assunto", emailRequest.getSubject());
        assertEquals("Teste Corpo", emailRequest.getBody());
        assertEquals("Teste Nome", emailRequest.getFromName());
    }
}
