package com.swagger.docs.sevice;


import com.swagger.docs.domain.user.Account;
import com.swagger.docs.domain.user.AccountRepository;
import com.swagger.docs.dto.LoginResponseDto;
import com.swagger.docs.global.common.exception.BadRequestException;
import com.swagger.docs.global.config.jwt.JwtProvider;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
//@RequiredArgsConstructor
@AllArgsConstructor
//@Transactional(readOnly = true)
@Transactional(rollbackFor = Exception.class)
public class AccountService {
    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;
    private final JwtProvider jwtProvider;

    //   회원가입
    @Transactional
    public void signUp(String userName,Long year,String userEmail,String pinCode, String password) {
        // 중복검증
        isDuplicateEmail(userEmail);
        String encodePassword = passwordEncoder.encode(password);
        Account newAccount = Account.of(year,userName,password,userEmail,pinCode);
        accountRepository.save(newAccount);
    }

    // 중복된 이메일 확인
    public void isDuplicateEmail(String email) {
        boolean isDuplicate = accountRepository.existsAccountByUserEmail(email);
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
        Account account = accountRepository.findAccountByUserEmail(email).orElseThrow(() -> new BadRequestException("존재하지 않는 유저입니다."));
        jwtProvider.createRefreshToken(email,refreshToken);
        String accessToken = jwtProvider.createAccessToken(account.getUserEmail(), account.getRole());
        return new LoginResponseDto(accessToken, refreshToken);
    }

    public LoginResponseDto login(String email, String password) {
        Account account = accountRepository
                .findAccountByUserEmail(email).orElseThrow(() -> new BadRequestException("아이디 혹은 비밀번호를 확인하세요"));
        checkPassword(password, account.getPassword());
        String accessToken = jwtProvider.createAccessToken(account.getUserEmail(), account.getRole());
        String refreshToken = jwtProvider.createRefreshToken(account.getUserEmail(), account.getRole());
        return new LoginResponseDto(accessToken, refreshToken);
    }
}
