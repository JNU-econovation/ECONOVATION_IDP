package com.econovation.idp.global.common.exception;

import lombok.Getter;

@Getter
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
