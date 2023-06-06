package com.econovation.idp.application.port.in;


import com.econovation.idp.domain.dto.LoginResponseDto;
import com.econovation.idp.domain.dto.UserResponseMatchedTokenDto;
import com.econovation.idp.global.common.exception.GetExpiredTimeException;
import javax.servlet.http.HttpServletRequest;

public interface AccountUseCase {
    public LoginResponseDto reIssueAccessToken(HttpServletRequest request, String refreshedToken);

    public UserResponseMatchedTokenDto findByAccessToken(String accessToken);

    public LoginResponseDto login(String email, String password);

    public void logout(String refreshToken) throws GetExpiredTimeException;
}
