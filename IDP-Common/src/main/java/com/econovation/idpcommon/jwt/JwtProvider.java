package com.econovation.idpcommon.jwt;

import static com.econovation.idpcommon.consts.IdpStatic.*;

import com.econovation.idp.redis.RedisService;
import com.econovation.idpcommon.dto.AccessTokenInfo;
import com.econovation.idpcommon.exception.ExpiredTokenException;
import com.econovation.idpcommon.exception.GetExpiredTimeException;
import com.econovation.idpcommon.exception.InvalidTokenException;
import com.econovation.idpcommon.properties.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Duration;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtProvider {
    private final RedisService redisService;
    private final JwtProperties jwtProperties;

    public Jws<Claims> getJws(String token) {
        try {
            return Jwts.parserBuilder().setSigningKey(getSecretKey()).build().parseClaimsJws(token);
        } catch (ExpiredJwtException e) {
            throw ExpiredTokenException.EXCEPTION;
        } catch (Exception e) {
            throw InvalidTokenException.EXCEPTION;
        }
    }

    private Key getSecretKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8));
    }

    public void logout(String refreshToken) throws GetExpiredTimeException {
        long expiredAccessTokenTime = getExpiredTime(refreshToken).getTime() - new Date().getTime();
        //        이메일 조회
        //        accessToken To userEmail
        Long idpId = getIdpId(refreshToken);
        redisService.setValues(
                jwtProperties.getSecretKey() + refreshToken,
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
    public String createAccessToken(Long idpId, String role) {
        long tokenInvalidTime = 1000L * 60 * 60; // 1h
        return this.createToken(Math.toIntExact(idpId), role, tokenInvalidTime);
    }

    // refreshToken 은 redis 에 저장해야한다.
    public String createRefreshToken(Long idpId, String role) {
        //        String refreshToken = this.createToken(Math.toIntExact(idpId), role,
        // tokenInvalidTime);

        final Date issuedAt = new Date();
        //        integer MILLT_TOSECOND to Long
        final Date refreshTokenExpiresIn =
                new Date(issuedAt.getTime() + jwtProperties.getRefreshExp() * MILLI_TO_SECOND);
        String refreshToken = buildRefreshToken(idpId, issuedAt, refreshTokenExpiresIn);

        redisService.setValues(
                String.valueOf(idpId),
                refreshToken,
                Duration.ofMillis(jwtProperties.getRefreshExp() * MILLI_TO_SECOND));
        return refreshToken;
    }

    public String generateAccessToken(Long id, String role) {
        final Date issuedAt = new Date();

        final Date accessTokenExpiresIn =
                new Date(issuedAt.getTime() + jwtProperties.getAccessExp() * MILLI_TO_SECOND);

        return buildAccessToken(id, issuedAt, accessTokenExpiresIn, role);
    }

    public String generateRefreshToken(Long id) {
        final Date issuedAt = new Date();
        final Date refreshTokenExpiresIn =
                new Date(issuedAt.getTime() + jwtProperties.getRefreshExp() * MILLI_TO_SECOND);
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

    public Date getExpiredTime(String token) throws GetExpiredTimeException {
        final Key secretKey = getSecretKey();
        try {
            return (Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody())
                    .getExpiration();
        } catch (Exception e) {
            throw new GetExpiredTimeException("토큰의 만료시간을 조회할 수 없습니다.");
        }
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

    public boolean isAccessToken(String token) {
        // npe
        log.info(getJws(token).getBody().get(TOKEN_TYPE).toString());
        return getJws(token).getBody().get(TOKEN_TYPE).equals(ACCESS_TOKEN);
    }

    public boolean isRefreshToken(String token) {
        return getJws(token).getBody().get(TOKEN_TYPE).equals(REFRESH_TOKEN);
    }
}
