package com.econovation.idp.application.port.in;

public interface AccountSignUpUseCase {
    public void signUp(String userName, Long year, String userEmail, String password);
    public String sendfindingPasswordConfirmationCode(String name, Long year) throws IllegalAccessException;
}
