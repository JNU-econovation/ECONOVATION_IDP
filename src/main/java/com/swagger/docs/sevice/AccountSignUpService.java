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

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class AccountSignUpService {
    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;
    private final ConfirmationTokenService confirmationTokenService;
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

    /**이름, 기수를 받아 회원을 조회
     * 회원 이메일을 추출
     * 그 이메일로 난수 6글자를 보냄
     * 회원가입
     * */
    @Transactional
    public String sendfindingPasswordConfirmationCode(String name, Long year){
        List<Account> byUserName = accountRepository.findByUserName(name).stream().filter(u->u.getYear() == year)
                .collect(Collectors.toList());
        String userEmail = byUserName.stream().findFirst().get().getUserEmail();
//        String userEmail = first.getUserEmail();
        return confirmationTokenService.createEmailConfirmationToken(userEmail);
    }

}
