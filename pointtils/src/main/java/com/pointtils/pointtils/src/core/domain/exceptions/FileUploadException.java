package com.pointtils.pointtils.src.core.domain.exceptions;

public class FileUploadException extends RuntimeException {

    public FileUploadException(String filename, Throwable cause) {
        super(String.format("Erro ao fazer upload do arquivo %s", filename), cause);
    }
}
