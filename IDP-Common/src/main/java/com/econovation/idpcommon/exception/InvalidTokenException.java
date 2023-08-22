package com.econovation.idpcommon.exception;

public class InvalidTokenException extends IdpCodeException {
    public static final IdpCodeException EXCEPTION = new InvalidTokenException();

    public InvalidTokenException() {
        super(GlobalErrorCode.INVALID_TOKEN);
    }
}
