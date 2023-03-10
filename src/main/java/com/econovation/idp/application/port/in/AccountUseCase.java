package com.econovation.idp.application.port.in;

import com.econovation.idp.domain.dto.LoginResponseDto;
import com.econovation.idp.domain.user.Account;

import javax.servlet.http.HttpServletRequest;

public interface AccountUseCase {
    public LoginResponseDto reIssueAccessToken(HttpServletRequest request, String refreshedToken);
    public Account findByAccessToken(String accessToken);
    public LoginResponseDto login(String email, String password);
    public void logout(String refreshToken);
}
