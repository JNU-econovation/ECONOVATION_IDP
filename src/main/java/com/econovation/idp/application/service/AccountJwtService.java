package com.econovation.idp.application.service;

import com.econovation.idp.adapter.out.persistence.AccountPersistenceAdapter;
import com.econovation.idp.application.port.in.AccountJwtUseCase;
import com.econovation.idp.application.port.in.JwtProviderUseCase;
import com.econovation.idp.domain.dto.LoginResponseDto;
import com.econovation.idp.domain.user.Account;
import com.econovation.idp.domain.user.AccountRepository;
import com.econovation.idp.global.common.exception.BadRequestException;
import com.econovation.idp.global.config.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AccountJwtService implements AccountJwtUseCase {
    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;
    private final AccountPersistenceAdapter accountPersistenceAdapter;
    private final JwtProviderUseCase jwtProviderUseCase;

    @Override
    @Transactional
    public LoginResponseDto reIssueAccessToken(String email, String refreshedToken) {
//        Account account = accountRepository.findAccountByUserEmail(email).orElseThrow(() -> new BadRequestException("존재하지 않는 유저입니다."));
        Account account = accountPersistenceAdapter.loadAccountByUserEmail(email);
        String refreshToken = jwtProviderUseCase.createRefreshToken(email,account.getRole());
        String accessToken = jwtProviderUseCase.createAccessToken(account.getUserEmail(), account.getRole());
        return new LoginResponseDto(accessToken, refreshToken);
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
        Account account = accountRepository
                .findAccountByUserEmail(email).orElseThrow(() -> new BadRequestException("아이디 혹은 비밀번호를 확인하세요"));
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
            throw new BadRequestException("아이디 혹은 비밀번호를 확인하세요");
        }
    }
}
