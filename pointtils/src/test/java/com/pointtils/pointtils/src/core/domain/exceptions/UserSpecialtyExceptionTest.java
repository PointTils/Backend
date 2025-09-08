package com.pointtils.pointtils.src.core.domain.exceptions;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class UserSpecialtyExceptionTest {

    @Test
    void constructorWithMessage_ShouldSetMessage() {
        // Arrange
        String message = "Test exception message";

        // Act
        UserSpecialtyException exception = new UserSpecialtyException(message);

        // Assert
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void constructorWithMessageAndCause_ShouldSetMessageAndCause() {
        // Arrange
        String message = "Test exception message";
        Throwable cause = new RuntimeException("Root cause");

        // Act
        UserSpecialtyException exception = new UserSpecialtyException(message, cause);

        // Assert
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void exceptionInheritance_ShouldBeRuntimeException() {
        // Arrange
        UserSpecialtyException exception = new UserSpecialtyException("Test");

        // Assert
        assertTrue(exception instanceof RuntimeException);
    }
}
