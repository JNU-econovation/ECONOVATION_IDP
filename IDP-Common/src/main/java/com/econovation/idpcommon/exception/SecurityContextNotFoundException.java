package com.econovation.idpcommon.exception;

public class SecurityContextNotFoundException extends IdpCodeException {
    public static final IdpCodeException EXCEPTION = new SecurityContextNotFoundException();

    private SecurityContextNotFoundException() {
        super(GlobalErrorCode.SECURITY_CONTEXT_NOT_FOUND);
    }
}
