package com.econovation.idp.application.port.in;

import com.econovation.idp.domain.dto.LoginResponseDto;
import com.econovation.idp.domain.dto.NonAccountResponseDto;

import javax.servlet.http.HttpServletRequest;

public interface AccountJwtUseCase {
    public LoginResponseDto reIssueAccessToken(HttpServletRequest request, String refreshedToken);

    public NonAccountResponseDto findByAccessToken(String accessToken);

    public LoginResponseDto login(String email, String password);
    public void logout(String refreshToken);
}
