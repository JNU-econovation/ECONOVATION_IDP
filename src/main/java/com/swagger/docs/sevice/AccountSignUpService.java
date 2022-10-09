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
@AllArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class AccountSignUpService {
    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;
    //   회원가입
    @Transactional
    public void signUp(String userName,Long year,String userEmail,String pinCode, String password) {
        // 중복검증
        isDuplicateEmail(userEmail);
        String encodePassword = passwordEncoder.encode(password);
        Account newAccount = Account.of(year,userName,encodePassword,userEmail,pinCode);
        accountRepository.save(newAccount);
    }

    // 중복된 이메일 확인
    public void isDuplicateEmail(String email) {
        boolean isDuplicate = accountRepository.existsAccountByUserEmail(email);
        if(isDuplicate){
            throw new BadRequestException("이미 존재하는 회원입니다");
        }
    }

}
