package com.econovation.idpapi.application.service;


import com.econovation.idpapi.application.port.in.AccountUseCase;
import com.econovation.idpapi.application.port.in.JwtProviderUseCase;
import com.econovation.idpapi.application.port.out.LoadAccountPort;
import com.econovation.idpapi.config.jwt.JwtProvider;
import com.econovation.idpapi.config.security.SecurityUtils;
import com.econovation.idpapi.utils.EntityMapper;
import com.econovation.idpcommon.exception.BadRequestException;
import com.econovation.idpcommon.exception.GetExpiredTimeException;
import com.econovation.idpdomain.domains.dto.LoginResponseDto;
import com.econovation.idpdomain.domains.dto.UserResponseMatchedTokenDto;
import com.econovation.idpdomain.domains.users.domain.Account;
import com.econovation.idpdomain.domains.users.domain.AccountRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
@Service
@Tag(name = "1. 회원 인증(로그인, 로그아웃, 회원 가입)")
public class AccountJwtService implements AccountUseCase {
    private static final String NO_ACCOUNT_MESSAGE = "존재하지 않는 유저입니다.";
    private final PasswordEncoder passwordEncoder;
    private final LoadAccountPort loadAccountPort;
    private final AccountRepository accountRepository;
    private final JwtProviderUseCase jwtProviderUseCase;
    private final EntityMapper entityMapper;
    private final JwtProvider jwtProvider;

    @Override
    @Transactional
    public LoginResponseDto reIssueAccessToken(HttpServletRequest request, String refreshedToken) {
        Long idpId = jwtProviderUseCase.getIdpId(refreshedToken);
        Account account =
                accountRepository
                        .findById(idpId)
                        .orElseThrow(() -> new IllegalArgumentException(NO_ACCOUNT_MESSAGE));
        // refreshToken 검증 절차
        Authentication authentication = jwtProviderUseCase.validateToken(request, refreshedToken);
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
        Long idpId = jwtProviderUseCase.getIdpId(accessToken);
        Account account =
                accountRepository
                        .findById(idpId)
                        .orElseThrow(() -> new IllegalArgumentException(NO_ACCOUNT_MESSAGE));
        return entityMapper.toUserResponseMatchedTokenDto(account);
    }

    @Transactional
    public LoginResponseDto createToken(Account account) {
        String accessToken =
                jwtProvider.generateAccessToken(account.getId(), account.getAccountRole().name());
        String refreshToken = jwtProvider.generateRefreshToken(account.getId());
        return new LoginResponseDto(accessToken, refreshToken);
    }

    @Override
    @Transactional
    public LoginResponseDto login(String email, String password) {
        log.info("email : " + email + " / password : " + password);
        Account account =
                loadAccountPort
                        .loadByUserEmail(email)
                        .orElseThrow(() -> new BadRequestException("해당하는 이메일이 존재하지 않습니다"));
        checkPassword(password, account.getPassword());
        return createToken(account);
    }

    @Override
    public void logout(String refreshToken) throws GetExpiredTimeException {
        jwtProviderUseCase.logout(refreshToken);
    }

    private void checkPassword(String password, String encodePassword) {
        boolean isMatch = passwordEncoder.matches(password, encodePassword);
        if (!isMatch) {
            throw new BadRequestException("비밀번호를 확인하세요");
        }
    }

    @Override
    public Optional<Account> findByMe() {
        Long currentId = SecurityUtils.getCurrentUserId();
        return accountRepository.findById(currentId);
    }
}
