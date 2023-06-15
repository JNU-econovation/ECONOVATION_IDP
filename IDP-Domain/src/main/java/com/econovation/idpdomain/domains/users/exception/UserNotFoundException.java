package com.econovation.idpdomain.domains.users.exception;


import com.econovation.idpcommon.exception.IdpCodeException;

public class UserNotFoundException extends IdpCodeException {

    public static final IdpCodeException EXCEPTION = new UserNotFoundException();

    private UserNotFoundException() {
        super(UserErrorCode.USER_NOT_FOUND);
    }
}
