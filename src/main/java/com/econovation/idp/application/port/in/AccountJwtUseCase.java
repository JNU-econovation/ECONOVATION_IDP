package com.econovation.idp.application.port.in;

import com.econovation.idp.domain.dto.LoginResponseDto;

public interface AccountJwtUseCase {
    public LoginResponseDto
    reIssueAccessToken(String email, String refreshedToken);
    public LoginResponseDto login(String email, String password);
    public void logout(String refreshToken);
}
