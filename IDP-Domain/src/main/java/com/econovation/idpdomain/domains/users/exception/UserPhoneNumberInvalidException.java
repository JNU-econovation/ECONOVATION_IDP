package com.econovation.idpdomain.domains.users.exception;


import com.econovation.idpcommon.exception.IdpCodeException;

public class UserPhoneNumberInvalidException extends IdpCodeException {

    public static final IdpCodeException EXCEPTION = new UserPhoneNumberInvalidException();

    private UserPhoneNumberInvalidException() {
        super(UserErrorCode.USER_PHONE_INVALID);
    }
}
