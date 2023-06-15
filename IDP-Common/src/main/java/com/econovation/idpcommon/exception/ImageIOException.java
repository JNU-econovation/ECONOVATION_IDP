package com.econovation.idpcommon.exception;

public class ImageIOException extends RuntimeException {
    public ImageIOException(String message) {
        super(ExceptionMessage.IMAGE_IO_EXCEPTION.getMessage() + message);
    }
}
