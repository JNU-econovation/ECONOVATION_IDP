package com.econovation.idpdomain.domains.users.exception;


import com.econovation.idpcommon.exception.IdpCodeException;

public class AlreadySignUpUserException extends IdpCodeException {

    public static final IdpCodeException EXCEPTION = new AlreadySignUpUserException();

    private AlreadySignUpUserException() {
        super(UserErrorCode.USER_ALREADY_SIGNUP);
    }
}
