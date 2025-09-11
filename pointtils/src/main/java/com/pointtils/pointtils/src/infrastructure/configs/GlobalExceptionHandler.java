
package com.pointtils.pointtils.src.infrastructure.configs;

import com.pointtils.pointtils.src.core.domain.exceptions.AuthenticationException;
import com.pointtils.pointtils.src.core.domain.exceptions.ClientTimeoutException;
import com.pointtils.pointtils.src.core.domain.exceptions.UserSpecialtyException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Optional;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(
            EntityNotFoundException ex, WebRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                System.currentTimeMillis());

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Dados inválidos: " + ex.getMessage(),
                System.currentTimeMillis());

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        Optional<String> fieldErrorMessage = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .findAny();

        String errorMessage = fieldErrorMessage.orElse(exception.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                errorMessage,
                System.currentTimeMillis());

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException exception) {
        Optional<String> fieldErrorMessage = exception.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .findAny();

        String errorMessage = fieldErrorMessage.orElse(exception.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                errorMessage,
                System.currentTimeMillis());

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        if (ex.getMessage().contains("duplicate key") && StringUtils.containsAny(ex.getMessage(), "person", "user", "enterprise")) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.CONFLICT.value(),
                    "Já existe um usuário cadastrado com estes dados",
                    System.currentTimeMillis());

            return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
        }

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected error occurred",
                System.currentTimeMillis());

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, WebRequest request) {
        ex.printStackTrace();
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected error occurred",
                System.currentTimeMillis());

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthentication(AuthenticationException ex) {
        String message = ex.getMessage();

        if ("O campo email é obrigatório".equals(message) || "O campo senha é obrigatório".equals(message)) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    message,
                    System.currentTimeMillis());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
        if ("Formato de e-mail inválido".equals(message) || "Formato de senha inválida".equals(message)) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.UNPROCESSABLE_ENTITY.value(),
                    message,
                    System.currentTimeMillis());
            return new ResponseEntity<>(errorResponse, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        if ("Usuário não encontrado".equals(message)) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.NOT_FOUND.value(),
                    message,
                    System.currentTimeMillis());
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
        if ("Muitas tentativas de login".equals(message)) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.TOO_MANY_REQUESTS.value(),
                    message,
                    System.currentTimeMillis());
            return new ResponseEntity<>(errorResponse, HttpStatus.TOO_MANY_REQUESTS);
        }
        if ("Credenciais inválidas".equals(message)) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.UNAUTHORIZED.value(),
                    message,
                    System.currentTimeMillis());
            return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
        }
        if ("Usuário bloqueado".equals(message)) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.FORBIDDEN.value(),
                    message,
                    System.currentTimeMillis());
            return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
        }
        if ("Refresh token não fornecido".equals(message)) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    message,
                    System.currentTimeMillis());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
        if ("Refresh token inválido ou expirado".equals(message)) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.UNAUTHORIZED.value(),
                    message,
                    System.currentTimeMillis());
            return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
        }
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                message,
                System.currentTimeMillis());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ClientTimeoutException.class)
    public ResponseEntity<ErrorResponse> handleClientTimeoutException(ClientTimeoutException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.GATEWAY_TIMEOUT.value(),
                ex.getMessage(),
                System.currentTimeMillis());

        return new ResponseEntity<>(errorResponse, HttpStatus.GATEWAY_TIMEOUT);
    }

    @ExceptionHandler(UserSpecialtyException.class)
    public ResponseEntity<ErrorResponse> handleUserSpecialty(UserSpecialtyException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                System.currentTimeMillis());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @Data
    @AllArgsConstructor
    public static class ErrorResponse {
        private int status;
        private String message;
        private long timestamp;
    }
}
