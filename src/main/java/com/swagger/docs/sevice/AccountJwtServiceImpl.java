package com.swagger.docs.sevice;

import com.swagger.docs.domain.user.Account;
import com.swagger.docs.domain.user.AccountRepository;
import com.swagger.docs.dto.LoginResponseDto;
import com.swagger.docs.global.common.exception.BadRequestException;
import com.swagger.docs.global.config.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AccountJwtServiceImpl implements AccountJwtService{
    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;
    private final JwtProvider jwtProvider;

    @Override
    @Transactional
    public LoginResponseDto reIssueAccessToken(String email, String refreshedToken) {
        Account account = accountRepository.findAccountByUserEmail(email).orElseThrow(() -> new BadRequestException("존재하지 않는 유저입니다."));
        String refreshToken = jwtProvider.createRefreshToken(email,refreshedToken);
        String accessToken = jwtProvider.createAccessToken(account.getUserEmail(), account.getRole());
        return new LoginResponseDto(accessToken, refreshToken);
    }

    @Transactional
    public LoginResponseDto getToken(Account account){
        String accessToken = jwtProvider.createAccessToken(account.getUserEmail(), account.getRole());
        String refreshToken = jwtProvider.createRefreshToken(account.getUserEmail(), account.getRole());
        return new LoginResponseDto(accessToken, refreshToken);
    }

    @Override
    @Transactional
    public LoginResponseDto login(String email, String password) {
        Account account = accountRepository
                .findAccountByUserEmail(email).orElseThrow(() -> new BadRequestException("아이디 혹은 비밀번호를 확인하세요"));
        checkPassword(password, account.getPassword());
        return getToken(account);
    }
    @Override
    public void logout(String refreshToken) {
        jwtProvider.logout(refreshToken);
    }


    private void checkPassword(String password, String encodePassword) {
        boolean isMatch = passwordEncoder.matches(password, encodePassword);
        if(!isMatch){
            throw new BadRequestException("아이디 혹은 비밀번호를 확인하세요");
        }
    }

}
