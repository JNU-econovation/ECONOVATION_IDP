package com.swagger.docs.domain.user.sevice;


import com.swagger.docs.domain.user.Account;
import com.swagger.docs.domain.user.AccountRepository;
import com.swagger.docs.domain.user.controller.LoginResponseDto;
import com.swagger.docs.global.common.exception.BadRequestException;
import com.swagger.docs.global.config.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountService {
    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;
    private final JwtProvider jwtProvider;

    //   회원가입
    @Transactional
    public void signUp(String email, String nickname, String password) {
        // 중복검증
        isDuplicateEmail(email);
        String encodePassword = passwordEncoder.encode(password);
        Account newAccount = Account.of(email, nickname, encodePassword);
        accountRepository.save(newAccount);
    }

    // 중복된 이메일 확인
    public void isDuplicateEmail(String email) {
        boolean isDuplicate = accountRepository.existsByEmail(email);
        if(isDuplicate){
            throw new BadRequestException("이미 존재하는 회원입니다");
        }
    }
    public void logout(String email, String accessToken) {
        jwtProvider.logout(email, accessToken);
    }

    private void checkPassword(String password, String encodePassword) {
        boolean isMatch = passwordEncoder.matches(password, encodePassword);
        if(!isMatch){
            throw new BadRequestException("아이디 혹은 비밀번호를 확인하세요");
        }
    }

    public LoginResponseDto reIssueAccessToken(String email, String refreshToken) {
        Account account = accountRepository.findByEmail(email).orElseThrow(() -> new BadRequestException("존재하지 않는 유저입니다."));
        jwtProvider.createRefreshToken(email,refreshToken);
        String accessToken = jwtProvider.createAccessToken(account.getEmail(), account.getRole());
        return new LoginResponseDto(accessToken, refreshToken);
    }

    public LoginResponseDto login(String email, String password) {
        Account account = accountRepository
                .findByEmail(email).orElseThrow(() -> new BadRequestException("아이디 혹은 비밀번호를 확인하세요"));
        checkPassword(password, account.getPassword());
        String accessToken = jwtProvider.createAccessToken(account.getEmail(), account.getRole());
        String refreshToken = jwtProvider.createRefreshToken(account.getEmail(), account.getRole());
        return new LoginResponseDto(accessToken, refreshToken);
    }
}
