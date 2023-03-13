package com.econovation.idp.application.port.in;

import com.econovation.idp.global.common.exception.GetExpiredTimeException;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

public interface JwtProviderUseCase {
    public void logout(String refreshToken) throws GetExpiredTimeException;
    public String createAccessToken(Long userId, String role);
    public String createRefreshToken(Long userId, String role);
    public Date getExpiredTime(String token) throws GetExpiredTimeException;
    public Authentication validateToken(HttpServletRequest request, String token);
    public Long getIdpId(String token);
}
