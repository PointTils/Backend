
package com.pointtils.pointtils.src.infrastructure.configs;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Data;

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

    // @ExceptionHandler(AccessDeniedException.class)
    // public ResponseEntity<ErrorResponse> accessDeniedException(
    //         AccessDeniedException ex, WebRequest request) {
    //     ErrorResponse errorResponse = new ErrorResponse(
    //             HttpStatus.FORBIDDEN.value(),
    //             "Acesso negado. Você não tem permissão para acessar este recurso.",
    //             System.currentTimeMillis());
    //     return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    // }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex, WebRequest request) {
        
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected error occurred",
                System.currentTimeMillis());
        
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Data
    @AllArgsConstructor
    public static class ErrorResponse {
        private int status;
        private String message;
        private long timestamp;
    }
}
