package com.econovation.idp.application.service;

import com.econovation.idp.application.port.in.AccountJwtUseCase;
import com.econovation.idp.application.port.in.JwtProviderUseCase;
import com.econovation.idp.application.port.out.LoadAccountPort;
import com.econovation.idp.domain.dto.LoginResponseDto;
import com.econovation.idp.domain.dto.NonAccountResponseDto;
import com.econovation.idp.domain.user.Account;
import com.econovation.idp.domain.user.AccountRepository;
import com.econovation.idp.global.common.exception.BadRequestException;
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
public class AccountJwtService implements AccountJwtUseCase {
    private static final String NO_ACCOUNT_MESSAGE = "존재하지 않는 유저입니다.";
    private final PasswordEncoder passwordEncoder;
    private final LoadAccountPort loadAccountPort;
    private final AccountRepository accountRepository;
    private final JwtProviderUseCase jwtProviderUseCase;

    @Override
    @Transactional
    public LoginResponseDto reIssueAccessToken(HttpServletRequest request, String refreshedToken) {
        String email = jwtProviderUseCase.getUserEmail(refreshedToken);
        Account account = loadAccountPort.loadByUserEmail(email).orElseThrow(() -> new IllegalArgumentException(NO_ACCOUNT_MESSAGE));
        // refreshToken 검증 절차
        Authentication authentication = jwtProviderUseCase.validateToken(request, refreshedToken);
        if (authentication.isAuthenticated()) {
            String refreshToken = jwtProviderUseCase.createRefreshToken(email,account.getRole());
            String accessToken = jwtProviderUseCase.createAccessToken(account.getUserEmail(), account.getRole());
            return new LoginResponseDto(accessToken, refreshToken);
        }
        else {
            throw new BadRequestException("유효하지 않은 토큰입니다");
        }
    }

    @Override
    public NonAccountResponseDto findByAccessToken(String accessToken) {
        String email = jwtProviderUseCase.getUserEmail(accessToken);
        Account account = loadAccountPort.loadByUserEmail(email).orElseThrow(() -> new IllegalArgumentException(NO_ACCOUNT_MESSAGE));
        return new NonAccountResponseDto(account.getYear(),account.getUsername(),account.getId());
    }

    @Transactional
    public LoginResponseDto createToken(Account account){
        String accessToken = jwtProviderUseCase.createAccessToken(account.getUserEmail(), account.getRole());
        String refreshToken = jwtProviderUseCase.createRefreshToken(account.getUserEmail(), account.getRole());
        return new LoginResponseDto(accessToken, refreshToken);
    }

    @Override
    @Transactional
    public LoginResponseDto login(String email, String password) {
        Account account = loadAccountPort
                .loadByUserEmail(email).orElseThrow(() -> new BadRequestException("아이디 혹은 비밀번호를 확인하세요"));
        checkPassword(password, account.getPassword());
        return createToken(account);
    }
    @Override
    public void logout(String refreshToken) {
        jwtProviderUseCase.logout(refreshToken);
    }


    private void checkPassword(String password, String encodePassword) {
        boolean isMatch = passwordEncoder.matches(password, encodePassword);
        log.info("맞았는데, password : " + password + "  /  encodePassword" + encodePassword);
        if(!isMatch){
            log.info("password : " + password + "  /  encodePassword" + encodePassword);
            throw new BadRequestException("아이디 혹은 비밀번호를 확인하세요");
        }
    }
}
