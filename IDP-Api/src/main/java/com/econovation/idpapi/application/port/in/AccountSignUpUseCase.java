package com.econovation.idpapi.application.port.in;


import com.econovation.idpapi.common.BasicResponse;

public interface AccountSignUpUseCase {
    void signUp(String userName, Integer year, String userEmail, String password);

    // 중복된 이메일 확인
    BasicResponse isDuplicateEmail(String email);

    String sendfindingPasswordConfirmationCode(String name, Integer year)
            throws IllegalAccessException;
}
