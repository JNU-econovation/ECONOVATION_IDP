package com.econovation.idp.application.service;

import com.econovation.idp.application.port.out.LoginResponseDto;

public interface AccountJwtService {
    public LoginResponseDto
    reIssueAccessToken(String email, String refreshedToken);
    public LoginResponseDto login(String email, String password);
    public void logout(String refreshToken);
}
