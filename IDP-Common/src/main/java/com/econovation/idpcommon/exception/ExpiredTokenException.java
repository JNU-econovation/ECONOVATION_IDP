package com.econovation.idpcommon.exception;

public class ExpiredTokenException extends IdpCodeException {
    public static final IdpCodeException EXCEPTION = new ExpiredTokenException();

    private ExpiredTokenException() {
        super(GlobalErrorCode.TOKEN_EXPIRED);
    }
}
