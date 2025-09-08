package com.pointtils.pointtils.src.core.domain.exceptions;

public class ClientTimeoutException extends RuntimeException {
    public ClientTimeoutException(String message) {
        super(message);
    }
}