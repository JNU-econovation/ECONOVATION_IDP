package com.econovation.idpcommon.exception;

public class InvalidTokenException extends IdpCodeException {
    public static final IdpCodeException EXCEPTION = new InvalidTokenException();

    private InvalidTokenException() {
        super(GlobalErrorCode.INVALID_TOKEN);
    }
}
