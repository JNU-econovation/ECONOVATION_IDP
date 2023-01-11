package com.econovation.idp.application.port.in;

import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

public interface JwtProviderUseCase {
    public void logout(String refreshToken);
    public String createAccessToken(String userId, String role);
    public String createRefreshToken(String userId, String role);
    public Date getExpiredTime(String token);
    public Authentication validateToken(HttpServletRequest request, String token);
    public String getUserEmail(String token);
}
