package com.econovation.idp.application.service;

import com.econovation.idp.application.port.in.AccountUseCase;
import com.econovation.idp.application.port.in.JwtProviderUseCase;
import com.econovation.idp.application.port.out.LoadAccountPort;
import com.econovation.idp.domain.dto.LoginResponseDto;
import com.econovation.idp.domain.dto.UserResponseMatchedTokenDto;
import com.econovation.idp.domain.user.Account;
import com.econovation.idp.domain.user.AccountRepository;
import com.econovation.idp.global.common.exception.BadRequestException;
import com.econovation.idp.global.utils.EntityMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@Slf4j
@Service
public class AccountJwtService implements AccountUseCase {
    private static final String NO_ACCOUNT_MESSAGE = "존재하지 않는 유저입니다.";
    private final PasswordEncoder passwordEncoder;
    private final LoadAccountPort loadAccountPort;
    private final AccountRepository accountRepository;
    private final JwtProviderUseCase jwtProviderUseCase;
    private final EntityMapper entityMapper;

    @Override
    @Transactional
    public LoginResponseDto reIssueAccessToken(HttpServletRequest request, String refreshedToken) {
        Long idpId = jwtProviderUseCase.getIdpId(refreshedToken);
        Account account = accountRepository.findById(idpId).orElseThrow(() -> new IllegalArgumentException(NO_ACCOUNT_MESSAGE));
        // refreshToken 검증 절차
        Authentication authentication = jwtProviderUseCase.validateToken(request, refreshedToken);
        if (authentication.isAuthenticated()) {
            String refreshToken = jwtProviderUseCase.createRefreshToken(idpId,account.getRole().name());
            String accessToken = jwtProviderUseCase.createAccessToken(idpId, account.getRole().name());
            return new LoginResponseDto(accessToken, refreshToken);
        }
        else {
            throw new BadRequestException("유효하지 않은 토큰입니다");
        }
    }

    @Override
    public UserResponseMatchedTokenDto findByAccessToken(String accessToken) {
        Long idpId = jwtProviderUseCase.getIdpId(accessToken);
        Account account = accountRepository.findById(idpId).orElseThrow(() -> new IllegalArgumentException(NO_ACCOUNT_MESSAGE));
        return entityMapper.toUserResponseMatchedTokenDto(account);
    }

    @Transactional
    public LoginResponseDto createToken(Account account){
        String accessToken = jwtProviderUseCase.createAccessToken(account.getId(), account.getRole().name());
        String refreshToken = jwtProviderUseCase.createRefreshToken(account.getId(), account.getRole().name());
        return new LoginResponseDto(accessToken, refreshToken);
    }

    @Override
    @Transactional
    public LoginResponseDto login(String email, String password) {
        Account account = loadAccountPort
                .loadByUserEmail(email).orElseThrow(() -> new BadRequestException("해당하는 이메일이 존재하지 않습니다"));
        checkPassword(password, account.getPassword());
        return createToken(account);
    }
    @Override
    public void logout(String refreshToken) {
        jwtProviderUseCase.logout(refreshToken);
    }

    private void checkPassword(String password, String encodePassword) {
        boolean isMatch = passwordEncoder.matches(password, encodePassword);
        if(!isMatch){
            throw new BadRequestException("비밀번호를 확인하세요");
        }
    }
}
