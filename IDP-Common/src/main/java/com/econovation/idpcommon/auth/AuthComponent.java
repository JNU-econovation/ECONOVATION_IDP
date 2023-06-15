package com.econovation.idpcommon.auth;


import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class AuthComponent {
    private final JwtProvider jwtProvider;

    public void validateUser(HttpServletRequest request) {
        String accessToken = request.getHeader("Authorization").substring(7);
        if (!jwtProvider.validateToken(request, accessToken).isAuthenticated()) {
            log.info("[ " + LocalDateTime.now() + " ] not USER is Approach");
        }
    }

    public void validateAdmin(HttpServletRequest request) {
        String accessToken = request.getHeader("Authorization").substring(7);
        if (!jwtProvider.validateToken(request, accessToken).isAuthenticated()) {
            log.info("[ " + LocalDateTime.now() + " ] not ADMIN is Approach");
        }
    }
}
