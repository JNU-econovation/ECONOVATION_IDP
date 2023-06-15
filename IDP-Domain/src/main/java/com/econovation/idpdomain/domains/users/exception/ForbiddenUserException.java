package com.econovation.idpdomain.domains.users.exception;


import com.econovation.idpcommon.exception.IdpCodeException;

public class ForbiddenUserException extends IdpCodeException {

    public static final IdpCodeException EXCEPTION = new ForbiddenUserException();

    private ForbiddenUserException() {
        super(UserErrorCode.USER_FORBIDDEN);
    }
}
