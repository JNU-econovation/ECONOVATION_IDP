package com.econovation.idp.global.common.auth;

import com.econovation.idp.global.config.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Component
@Slf4j
public class AuthComponent {
    private final JwtProvider jwtProvider;

    public void validateUser(HttpServletRequest request) {
        String accessToken = request.getHeader("Authorization").substring(7);
        if(!jwtProvider.validateToken(request,accessToken).isAuthenticated()){
            log.info("[ " + LocalDateTime.now() + " ] not USER is Approach");
        }
    }

    public void validateAdmin(HttpServletRequest request) {
        String accessToken = request.getHeader("Authorization").substring(7);
        if(!jwtProvider.validateToken(request,accessToken).isAuthenticated()){
            log.info("[ " + LocalDateTime.now() + " ] not ADMIN is Approach");
        }
    }
}
