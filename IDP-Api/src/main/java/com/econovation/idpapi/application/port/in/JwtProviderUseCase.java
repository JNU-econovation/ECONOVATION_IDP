package com.econovation.idpapi.application.port.in;


import com.econovation.idpcommon.exception.GetExpiredTimeException;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;

public interface JwtProviderUseCase {
    public void logout(String refreshToken) throws GetExpiredTimeException;

    public String createAccessToken(Long userId, String role);

    public String createRefreshToken(Long userId, String role);

    public Date getExpiredTime(String token) throws GetExpiredTimeException;

    public Authentication validateToken(HttpServletRequest request, String token);

    public Long getIdpId(String token);
}
