package com.econovation.idpdomain.domains.users.exception;


import com.econovation.idpcommon.exception.IdpCodeException;

public class AlreadyDeletedUserException extends IdpCodeException {

    public static final IdpCodeException EXCEPTION = new AlreadyDeletedUserException();

    private AlreadyDeletedUserException() {
        super(UserErrorCode.USER_ALREADY_DELETED);
    }
}
