package com.pointtils.pointtils.src.infrastructure.configs;

import com.fasterxml.jackson.databind.exc.ValueInstantiationException;
import com.pointtils.pointtils.src.core.domain.exceptions.AuthenticationException;
import com.pointtils.pointtils.src.core.domain.exceptions.ClientTimeoutException;
import com.pointtils.pointtils.src.core.domain.exceptions.UserSpecialtyException;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.pointtils.pointtils.src.core.domain.entities.enums.Gender;
import com.pointtils.pointtils.src.core.domain.entities.enums.InterpreterModality;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void handleEntityNotFoundException_ShouldReturnNotFoundResponse() {
        // Arrange
        EntityNotFoundException ex = new EntityNotFoundException("Entity not found");

        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = globalExceptionHandler
                .handleEntityNotFoundException(ex);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getBody().getStatus());
        assertEquals("Entity not found", response.getBody().getMessage());
        assertTrue(response.getBody().getTimestamp() > 0);
    }

    @Test
    void handleGlobalException_ShouldReturnInternalServerErrorResponse() {
        // Arrange
        Exception ex = new Exception("Unexpected error");

        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = globalExceptionHandler
                .handleGlobalException(ex);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getBody().getStatus());
        assertEquals("Ocorreu um erro inesperado. Tente novamente mais tarde.", response.getBody().getMessage());
        assertTrue(response.getBody().getTimestamp() > 0);
    }

    @Test
    void handleAuthenticationException_WithRequiredFieldMessage_ShouldReturnBadRequest() {
        // Arrange
        AuthenticationException ex = new AuthenticationException("O campo email é obrigatório");

        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = globalExceptionHandler.handleAuthentication(ex);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("O campo email é obrigatório", response.getBody().getMessage());
    }

    @Test
    void handleAuthenticationException_WithInvalidFormatMessage_ShouldReturnUnprocessableEntity() {
        // Arrange
        AuthenticationException ex = new AuthenticationException("Formato de e-mail inválido");

        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = globalExceptionHandler.handleAuthentication(ex);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertEquals("Formato de e-mail inválido", response.getBody().getMessage());
    }

    @Test
    void handleAuthenticationException_WithUserNotFoundMessage_ShouldReturnNotFound() {
        // Arrange
        AuthenticationException ex = new AuthenticationException("Usuário não encontrado");

        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = globalExceptionHandler.handleAuthentication(ex);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Usuário não encontrado", response.getBody().getMessage());
    }

    @Test
    void handleAuthenticationException_WithTooManyAttemptsMessage_ShouldReturnTooManyRequests() {
        // Arrange
        AuthenticationException ex = new AuthenticationException("Muitas tentativas de login");

        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = globalExceptionHandler.handleAuthentication(ex);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.TOO_MANY_REQUESTS, response.getStatusCode());
        assertEquals("Muitas tentativas de login", response.getBody().getMessage());
    }

    @Test
    void handleAuthenticationException_WithInvalidCredentialsMessage_ShouldReturnUnauthorized() {
        // Arrange
        AuthenticationException ex = new AuthenticationException("Credenciais inválidas");

        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = globalExceptionHandler.handleAuthentication(ex);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Credenciais inválidas", response.getBody().getMessage());
    }

    @Test
    void handleAuthenticationException_WithUserBlockedMessage_ShouldReturnForbidden() {
        // Arrange
        AuthenticationException ex = new AuthenticationException("Usuário bloqueado");

        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = globalExceptionHandler.handleAuthentication(ex);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Usuário bloqueado", response.getBody().getMessage());
    }

    @Test
    void handleAuthenticationException_WithRefreshTokenNotProvidedMessage_ShouldReturnBadRequest() {
        // Arrange
        AuthenticationException ex = new AuthenticationException("Refresh token não fornecido");

        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = globalExceptionHandler.handleAuthentication(ex);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Refresh token não fornecido", response.getBody().getMessage());
    }

    @Test
    void handleAuthenticationException_WithRefreshTokenInvalidMessage_ShouldReturnUnauthorized() {
        // Arrange
        AuthenticationException ex = new AuthenticationException("Refresh token inválido ou expirado");

        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = globalExceptionHandler.handleAuthentication(ex);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Refresh token inválido ou expirado", response.getBody().getMessage());
    }

    @Test
    void handleAuthenticationException_WithUnknownMessage_ShouldReturnInternalServerError() {
        // Arrange
        AuthenticationException ex = new AuthenticationException("Unknown error");

        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = globalExceptionHandler.handleAuthentication(ex);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Unknown error", response.getBody().getMessage());
    }

    @Test
    void handleUserSpecialtyException_ShouldReturnBadRequest() {
        // Arrange
        UserSpecialtyException ex = new UserSpecialtyException("User specialty error");

        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = globalExceptionHandler.handleUserSpecialty(ex);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("User specialty error", response.getBody().getMessage());
    }

    @Test
    void errorResponseConstructor_ShouldSetAllFields() {
        // Arrange
        int status = 400;
        String message = "Error message";
        long timestamp = System.currentTimeMillis();

        // Act
        GlobalExceptionHandler.ErrorResponse errorResponse = new GlobalExceptionHandler.ErrorResponse(status, message,
                timestamp);

        // Assert
        assertEquals(status, errorResponse.getStatus());
        assertEquals(message, errorResponse.getMessage());
        assertEquals(timestamp, errorResponse.getTimestamp());
    }

    @Test
    void handleInternalErrorException_ShouldReturnInternalServerError() {
        // Arrange
        InternalError ex = new InternalError("Internal error");

        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = globalExceptionHandler.handleInternalError(ex);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Internal error", response.getBody().getMessage());
    }

    @Test
    void handleClientTimeoutException_ShouldReturnGatewayTimeout() {
        // Arrange
        ClientTimeoutException ex = new ClientTimeoutException("Timeout");

        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = globalExceptionHandler
                .handleClientTimeoutException(ex);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.GATEWAY_TIMEOUT, response.getStatusCode());
        assertEquals("Timeout", response.getBody().getMessage());
    }

    @Test
    void handleConstraintViolationException_ShouldReturnBadRequest_WithoutConstraintViolationMessages() {
        // Arrange
        ConstraintViolationException ex = new ConstraintViolationException("Error", Set.of());

        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = globalExceptionHandler
                .handleConstraintViolationException(ex);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Error", response.getBody().getMessage());
    }

    @Test
    void handleConstraintViolationException_ShouldReturnBadRequest_WithConstraintViolationMessages() {
        // Arrange
        ConstraintViolation<?> mockViolation = mock(ConstraintViolation.class);
        when(mockViolation.getMessage()).thenReturn("Violation message");
        ConstraintViolationException ex = new ConstraintViolationException("Error", Set.of(mockViolation));

        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = globalExceptionHandler
                .handleConstraintViolationException(ex);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Violation message", response.getBody().getMessage());
    }

    @Test
    void handleHttpMessageNotReadableException_ShouldReturnBadRequest_WithIllegalArgumentExceptionCause() {
        // Arrange
        IllegalArgumentException cause = new IllegalArgumentException("Argument message");
        ValueInstantiationException valueEx = mock(ValueInstantiationException.class);
        when(valueEx.getCause()).thenReturn(cause);
        HttpMessageNotReadableException thrownException = mock(HttpMessageNotReadableException.class);
        when(thrownException.getCause()).thenReturn(valueEx);

        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = globalExceptionHandler
                .handleHttpMessageNotReadable(thrownException);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Dados inválidos: Argument message", response.getBody().getMessage());
    }

    @Test
    void handleHttpMessageNotReadableException_ShouldReturnBadRequest_WithUnexpectedCause() {
        // Arrange
        ValueInstantiationException valueEx = mock(ValueInstantiationException.class);
        when(valueEx.getCause()).thenReturn(new RuntimeException());
        HttpMessageNotReadableException thrownException = mock(HttpMessageNotReadableException.class);
        when(thrownException.getCause()).thenReturn(valueEx);

        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = globalExceptionHandler
                .handleHttpMessageNotReadable(thrownException);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Ocorreu um erro inesperado. Tente novamente mais tarde.", response.getBody().getMessage());
    }

    @Test
    void handleMethodArgumentTypeMismatch_ShouldReturnBadRequest_WithInterpreterModality() {
        String paramName = "modality";
        Class<?> requiredType = InterpreterModality.class;
        MethodArgumentTypeMismatchException ex = new MethodArgumentTypeMismatchException("value", requiredType,
                paramName, null, null);
                
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = globalExceptionHandler
                .handleMethodArgumentTypeMismatch(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Modalidade inválida", response.getBody().getMessage());
    }

    @Test
    void handleMethodArgumentTypeMismatch_ShouldReturnBadRequest_WithGender() {
        String paramName = "gender";
        Class<?> requiredType = Gender.class;
        MethodArgumentTypeMismatchException ex = new MethodArgumentTypeMismatchException("value", requiredType,
                paramName, null, null);

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = globalExceptionHandler
                .handleMethodArgumentTypeMismatch(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Gênero inválido", response.getBody().getMessage());
    }
}
