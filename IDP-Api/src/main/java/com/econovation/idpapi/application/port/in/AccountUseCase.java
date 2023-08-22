package com.econovation.idpapi.application.port.in;


import com.econovation.idpcommon.exception.GetExpiredTimeException;
import com.econovation.idpdomain.domains.dto.LoginResponseDto;
import com.econovation.idpdomain.domains.dto.UserResponseMatchedTokenDto;
import javax.servlet.http.HttpServletRequest;

public interface AccountUseCase {
    public LoginResponseDto reIssueAccessToken(HttpServletRequest request, String refreshedToken);

    public UserResponseMatchedTokenDto findByAccessToken(String accessToken);

    public LoginResponseDto login(String email, String password);

    public void logout(String refreshToken) throws GetExpiredTimeException;
}
