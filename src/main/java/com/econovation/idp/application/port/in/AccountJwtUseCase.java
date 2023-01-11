package com.econovation.idp.application.port.in;

import com.econovation.idp.domain.dto.LoginResponseDto;

import javax.servlet.http.HttpServletRequest;

public interface AccountJwtUseCase {
    LoginResponseDto reIssueAccessToken(HttpServletRequest request, String refreshedToken);
    public LoginResponseDto login(String email, String password);
    public void logout(String refreshToken);
}
