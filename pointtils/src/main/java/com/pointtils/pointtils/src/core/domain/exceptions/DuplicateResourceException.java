package com.pointtils.pointtils.src.core.domain.exceptions;

public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }
}