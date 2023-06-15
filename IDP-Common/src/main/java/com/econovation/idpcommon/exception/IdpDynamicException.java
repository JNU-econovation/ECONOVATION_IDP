package com.econovation.idpcommon.exception;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class IdpDynamicException extends RuntimeException {
    private final int status;
    private final String code;
    private final String reason;
}
