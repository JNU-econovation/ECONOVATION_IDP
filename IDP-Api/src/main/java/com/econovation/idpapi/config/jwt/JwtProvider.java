package com.econovation.idpapi.config.jwt;

import static com.econovation.idpcommon.consts.IdpStatic.*;

import com.econovation.idpapi.application.port.in.JwtProviderUseCase;
import com.econovation.idpapi.common.redis.RedisService;
import com.econovation.idpapi.config.security.AuthDetails;
import com.econovation.idpcommon.dto.AccessTokenInfo;
import com.econovation.idpcommon.exception.ExpiredTokenException;
import com.econovation.idpcommon.exception.GetExpiredTimeException;
import com.econovation.idpcommon.exception.InvalidTokenException;
import com.econovation.idpcommon.properties.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Duration;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtProvider implements JwtProviderUseCase {
    private final RedisService redisService;

    private final JwtProperties jwtProperties;

    @Value("${auth.jwt.blacklist.prefix}")
    private String blackListATPrefix;

    private Jws<Claims> getJws(String token) {
        try {
            return Jwts.parserBuilder().setSigningKey(getSecretKey()).build().parseClaimsJws(token);
        } catch (ExpiredJwtException e) {
            throw ExpiredTokenException.EXCEPTION;
        } catch (Exception e) {
            throw InvalidTokenException.EXCEPTION;
        }
    }

    private Key getSecretKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public void logout(String refreshToken) throws GetExpiredTimeException {
        long expiredAccessTokenTime = getExpiredTime(refreshToken).getTime() - new Date().getTime();
        //        이메일 조회
        //        accessToken To userEmail
        Long idpId = getIdpId(refreshToken);
        redisService.setValues(
                blackListATPrefix + refreshToken,
                String.valueOf(idpId),
                Duration.ofMillis(expiredAccessTokenTime));
        redisService.deleteValues(String.valueOf(idpId)); // Delete RefreshToken In Redis
    }

    private String createToken(Integer idpId, String role, long tokenInvalidTime) {
        Claims claims = Jwts.claims().setSubject(String.valueOf(idpId));
        claims.put("roles", role);
        Date date = new Date();
        final Key secretKey = getSecretKey();
        return Jwts.builder()
                .setIssuer(TOKEN_ISSUER)
                .setIssuedAt(date)
                .claim(TOKEN_TYPE, REFRESH_TOKEN) // 발행유저 정보 저장
                .setExpiration(new Date(date.getTime() + tokenInvalidTime)) // 토큰 유효 시간 저장
                .signWith(SignatureAlgorithm.HS256, secretKey) // 해싱 알고리즘 및 키 설정
                .compact();
    }

    // accessToken 은 redis에 저장하지 않는다.
    @Override
    public String createAccessToken(Long idpId, String role) {
        long tokenInvalidTime = 1000L * 60 * 60; // 1h
        return this.createToken(Math.toIntExact(idpId), role, tokenInvalidTime);
    }

    // refreshToken 은 redis 에 저장해야한다.
    @Override
    public String createRefreshToken(Long idpId, String role) {
        //        String refreshToken = this.createToken(Math.toIntExact(idpId), role,
        // tokenInvalidTime);

        final Date issuedAt = new Date();
        //        integer MILLT_TOSECOND to Long
        final Date refreshTokenExpiresIn =
                new Date(issuedAt.getTime() + jwtProperties.getRefresh() * MILLI_TO_SECOND);
        String refreshToken = buildRefreshToken(idpId, issuedAt, refreshTokenExpiresIn);

        redisService.setValues(
                String.valueOf(idpId),
                refreshToken,
                Duration.ofMillis(jwtProperties.getRefresh() * MILLI_TO_SECOND));
        return refreshToken;
    }

    public String generateAccessToken(Long id, String role) {
        final Date issuedAt = new Date();
        final Date accessTokenExpiresIn =
                new Date(issuedAt.getTime() + jwtProperties.getAccess() * MILLI_TO_SECOND);

        return buildAccessToken(id, issuedAt, accessTokenExpiresIn, role);
    }

    public String generateRefreshToken(Long id) {
        final Date issuedAt = new Date();
        final Date refreshTokenExpiresIn =
                new Date(issuedAt.getTime() + jwtProperties.getRefresh() * MILLI_TO_SECOND);
        return buildRefreshToken(id, issuedAt, refreshTokenExpiresIn);
    }

    private String buildAccessToken(
            Long id, Date issuedAt, Date accessTokenExpiresIn, String role) {
        final Key encodedKey = getSecretKey();
        return Jwts.builder()
                .setIssuer(TOKEN_ISSUER)
                .setIssuedAt(issuedAt)
                .setSubject(id.toString())
                .claim(TOKEN_TYPE, ACCESS_TOKEN)
                .claim(TOKEN_ROLE, role)
                .setExpiration(accessTokenExpiresIn)
                .signWith(encodedKey)
                .compact();
    }

    private String buildRefreshToken(Long id, Date issuedAt, Date accessTokenExpiresIn) {
        final Key encodedKey = getSecretKey();
        return Jwts.builder()
                .setIssuer(TOKEN_ISSUER)
                .setIssuedAt(issuedAt)
                .setSubject(id.toString())
                .claim(TOKEN_TYPE, REFRESH_TOKEN)
                .setExpiration(accessTokenExpiresIn)
                .signWith(encodedKey)
                .compact();
    }

    @Override
    public Long getIdpId(String token) {
        final Key secretKey = getSecretKey();
        Long aLong =
                Long.valueOf(
                        Jwts.parser()
                                .setSigningKey(secretKey)
                                .parseClaimsJws(token)
                                .getBody()
                                .getSubject());
        return aLong;
    }

    @Override
    public Date getExpiredTime(String token) throws GetExpiredTimeException {
        final Key secretKey = getSecretKey();
        try {
            return (Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody())
                    .getExpiration();
        } catch (Exception e) {
            throw new GetExpiredTimeException("토큰의 만료시간을 조회할 수 없습니다.");
        }
    }

    @Override
    public Authentication validateToken(HttpServletRequest request, String token) {
        String exception = "exception";
        final Key secretKey = getSecretKey();
        try {
            String expiredAT = redisService.getValues(blackListATPrefix + token);
            if (expiredAT != null) {
                throw new ExpiredJwtException(null, null, null);
            }
            //            Jws<Claims> claimsJws =
            // Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            log.info("validateToken : {}", token);
            return getAuthentication(token);
        } catch (MalformedJwtException | UnsupportedJwtException e) {
            request.setAttribute(exception, "토큰의 형식을 확인하세요");
        } catch (ExpiredJwtException e) {
            request.setAttribute(exception, "토큰이 만료되었습니다.");
        } catch (IllegalArgumentException e) {
            request.setAttribute(exception, "JWT compact of handler are invalid");
        }
        return null;
    }

    public boolean isAccessToken(String token) {
        // npe
        log.info(getJws(token).getBody().get(TOKEN_TYPE).toString());
        return getJws(token).getBody().get(TOKEN_TYPE).equals(ACCESS_TOKEN);
    }

    public boolean isRefreshToken(String token) {
        return getJws(token).getBody().get(TOKEN_TYPE).equals(REFRESH_TOKEN);
    }

    public AccessTokenInfo parseAccessToken(String token) {
        if (isAccessToken(token)) {
            Claims claims = getJws(token).getBody();
            return AccessTokenInfo.builder()
                    .userId(Long.parseLong(claims.getSubject()))
                    .role((String) claims.get(TOKEN_ROLE))
                    .build();
        }
        throw InvalidTokenException.EXCEPTION;
    }

    public Authentication getAuthentication(String token) {
        AccessTokenInfo accessTokenInfo = parseAccessToken(token);

        UserDetails userDetails =
                new AuthDetails(accessTokenInfo.getUserId().toString(), accessTokenInfo.getRole());
        return new UsernamePasswordAuthenticationToken(
                userDetails, "user", userDetails.getAuthorities());
    }
}
