package com.econovation.idpapi.application.service;


import com.econovation.idp.redis.RedisService;
import com.econovation.idpapi.application.port.in.AccountUseCase;
import com.econovation.idpapi.config.security.AuthDetails;
import com.econovation.idpapi.utils.EntityMapper;
import com.econovation.idpcommon.dto.AccessTokenInfo;
import com.econovation.idpcommon.exception.BadRequestException;
import com.econovation.idpcommon.exception.GetExpiredTimeException;
import com.econovation.idpcommon.jwt.JwtProvider;
import com.econovation.idpdomain.domains.dto.LoginResponseDto;
import com.econovation.idpdomain.domains.dto.UserResponseMatchedTokenDto;
import com.econovation.idpdomain.domains.users.domain.AccountRepository;
import com.econovation.idpdomain.domains.users.domain.Accounts;
import com.econovation.idpdomain.domains.users.port.LoadAccountPort;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
@Service
public class AccountJwtService implements AccountUseCase {
    private static final String NO_ACCOUNT_MESSAGE = "존재하지 않는 유저입니다.";
    private final LoadAccountPort loadAccountPort;
    private final AccountRepository accountRepository;
    private final EntityMapper entityMapper;
    private final JwtProvider jwtProvider;
    private final RedisService redisService;

    @Value("${auth.jwt.blacklist.prefix}")
    private String blackListATPrefix;

    @Override
    @Transactional
    public LoginResponseDto reIssueAccessToken(HttpServletRequest request, String refreshedToken) {
        Long idpId = jwtProvider.getIdpId(refreshedToken);
        Accounts account =
                accountRepository
                        .findById(idpId)
                        .orElseThrow(() -> new IllegalArgumentException(NO_ACCOUNT_MESSAGE));
        // refreshToken 검증 절차
        Authentication authentication = validateToken(request, refreshedToken);
        if (authentication.isAuthenticated()) {

            String refreshToken = jwtProvider.generateRefreshToken(idpId);
            String accessToken =
                    jwtProvider.generateAccessToken(idpId, account.getAccountRole().name());
            return new LoginResponseDto(accessToken, refreshToken);
        } else {
            throw new BadRequestException("유효하지 않은 토큰입니다");
        }
    }

    @Override
    public UserResponseMatchedTokenDto findByAccessToken(String accessToken) {
        Long idpId = jwtProvider.getIdpId(accessToken);
        Accounts account =
                accountRepository
                        .findById(idpId)
                        .orElseThrow(() -> new IllegalArgumentException(NO_ACCOUNT_MESSAGE));
        return entityMapper.toUserResponseMatchedTokenDto(account);
    }

    @Transactional
    public LoginResponseDto createToken(Accounts account) {
        String accessToken =
                jwtProvider.generateAccessToken(account.getId(), account.getAccountRole().name());
        String refreshToken = jwtProvider.generateRefreshToken(account.getId());
        return new LoginResponseDto(accessToken, refreshToken);
    }

    @Override
    @Transactional
    public LoginResponseDto login(String email, String password) {
        log.info("email : " + email + " / password : " + password);
        Accounts account =
                loadAccountPort
                        .loadByUserEmail(email)
                        .orElseThrow(() -> new BadRequestException("해당하는 이메일이 존재하지 않습니다"));
        //        checkPassword(password, account.getPassword());
        return createToken(account);
    }

    @Override
    public void logout(String refreshToken) throws GetExpiredTimeException {
        jwtProvider.logout(refreshToken);
    }

    /*    private void checkPassword(String password, String encodePassword) {
        boolean isMatch = passwordEncoder.matches(password, encodePassword);
        if (!isMatch) {
            throw new BadRequestException("비밀번호를 확인하세요");
        }
    }*/

    public Authentication validateToken(HttpServletRequest request, String token) {
        String exception = "exception";
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

    public Authentication getAuthentication(String token) {
        AccessTokenInfo accessTokenInfo = jwtProvider.parseAccessToken(token);

        UserDetails userDetails =
                new AuthDetails(accessTokenInfo.getUserId().toString(), accessTokenInfo.getRole());
        return new UsernamePasswordAuthenticationToken(
                userDetails, "user", userDetails.getAuthorities());
    }
}
