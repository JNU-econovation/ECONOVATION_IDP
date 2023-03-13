package com.econovation.idp.global.config.jwt;

import com.econovation.idp.application.port.in.JwtProviderUseCase;
import com.econovation.idp.global.common.exception.GetExpiredTimeException;
import com.econovation.idp.global.common.redis.RedisService;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.Base64;
import java.util.Date;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtProvider implements JwtProviderUseCase {
    private final UserDetailsService customAccountDetailsService;
    private final RedisService redisService;
    @Value("${spring.jwt.secret-key}")
    private String secretKey;

    @Value("${spring.jwt.blacklist.access-token}")
    private String blackListATPrefix;

    // 의존성 주입 후, 초기화를 수행
    // 객체 초기화, secretKey Base64로 인코딩한다.
    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    @Override
    public void logout(String refreshToken) throws GetExpiredTimeException {
        long expiredAccessTokenTime = getExpiredTime(refreshToken).getTime() - new Date().getTime();
//        이메일 조회
//        accessToken To userEmail
        Long idpId = getIdpId(refreshToken);
        redisService.setValues(blackListATPrefix + refreshToken, String.valueOf(idpId), Duration.ofMillis(expiredAccessTokenTime));
        redisService.deleteValues(String.valueOf(idpId)); // Delete RefreshToken In Redis
    }

    private String createToken(Integer idpId, String role, long tokenInvalidTime) {
        Claims claims = Jwts.claims().setSubject(String.valueOf(idpId));
        claims.put("roles", role);
        Date date = new Date();
        log.info(secretKey);
        return Jwts.builder()
                .setClaims(claims) // 발행유저 정보 저장
                .setIssuedAt(date) // 발행 시간 저장
                .setExpiration(new Date(date.getTime() + tokenInvalidTime)) // 토큰 유효 시간 저장
                .signWith(SignatureAlgorithm.HS256, secretKey) // 해싱 알고리즘 및 키 설정
                .compact();
    }

// accessToken 은 redis에 저장하지 않는다.
    @Override
    public String createAccessToken(Long idpId, String role) {
        long tokenInvalidTime = 1000L * 60 * 60; //1h
        return this.createToken(Math.toIntExact(idpId), role, tokenInvalidTime);
    }

// refreshToken 은 redis 에 저장해야한다.
    @Override
    public String createRefreshToken(Long idpId, String role) {
        Long tokenInvalidTime = 1000L * 60 * 60 * 24; // 1d
        String refreshToken = this.createToken(Math.toIntExact(idpId), role, tokenInvalidTime);
        redisService.setValues(String.valueOf(idpId), refreshToken, Duration.ofMillis(tokenInvalidTime));
        return refreshToken;
    }


    @Override
    public Long getIdpId(String token) {
        Long aLong = Long.valueOf(Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject());
        log.info("테스트중입니다 따란 : " + String.valueOf(aLong));
        return aLong;
    }

    @Override
    public Date getExpiredTime(String token) throws GetExpiredTimeException {
        try {
            return (Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody()).getExpiration();
        }catch (Exception e) {
            log.warn(e.getMessage());
            throw new GetExpiredTimeException("토큰의 만료시간을 조회할 수 없습니다.");
        }
    }

    @Override
    public Authentication validateToken(HttpServletRequest request, String token) {
        String exception = "exception";
        try {
            String expiredAT = redisService.getValues(blackListATPrefix + token);
            if (expiredAT != null) {
                throw new ExpiredJwtException(null, null, null);
            }
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return getAuthentication(token);
        } catch (MalformedJwtException  | SignatureException | UnsupportedJwtException e) {
            request.setAttribute(exception, "토큰의 형식을 확인하세요");
        } catch (ExpiredJwtException e) {
            request.setAttribute(exception, "토큰이 만료되었습니다.");
        } catch (IllegalArgumentException e) {
            request.setAttribute(exception, "JWT compact of handler are invalid");
        }
        return null;
    }
    private Authentication getAuthentication(String token) {
        UserDetails userDetails = customAccountDetailsService.loadUserByUsername(String.valueOf(getIdpId(token)));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }
}
