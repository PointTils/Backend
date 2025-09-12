package com.pointtils.pointtils.src.core.domain.exceptions;

public class UserSpecialtyException extends RuntimeException {
    
    public UserSpecialtyException(String message) {
        super(message);
    }
    
    public UserSpecialtyException(String message, Throwable cause) {
        super(message, cause);
    }
}
