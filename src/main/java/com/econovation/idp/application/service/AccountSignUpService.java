package com.econovation.idp.application.service;

import com.econovation.idp.application.port.in.AccountSignUpUseCase;
import com.econovation.idp.domain.user.Account;
import com.econovation.idp.domain.user.AccountRepository;
import com.econovation.idp.global.common.BasicResponse;
import com.econovation.idp.global.common.exception.BadRequestException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class AccountSignUpService implements AccountSignUpUseCase {
    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;
    private final ConfirmationTokenService confirmationTokenService;
    //   회원가입
    @Override
    @Transactional
    public void signUp(String userName, Long year, String userEmail, String password) {
        // 중복검증
        BasicResponse duplicateEmail = isDuplicateEmail(userEmail);
        if (duplicateEmail.getStatus().equals(HttpStatus.CONFLICT)) {
            throw new BadRequestException("중복된 이메일입니다");
        }
        String encodePassword = passwordEncoder.encode(password);
        Account newAccount = new Account(year, userName, encodePassword, userEmail);
//        Account newAccount = Account.of(year,userName,encodePassword,userEmail);
        accountRepository.save(newAccount);
    }

    // 중복된 이메일 확인
    @Override
    public BasicResponse isDuplicateEmail(String email) {
        boolean isDuplicate = accountRepository.existsAccountByUserEmail(email);
        if(isDuplicate){
            return new BasicResponse("중복된 이메일입니다.", HttpStatus.CONFLICT);
        }
        return new BasicResponse("사용가능한 이메일입니다.", HttpStatus.OK);
    }

    /**이름, 기수를 받아 회원을 조회
     * 회원 이메일을 추출
     * 그 이메일로 난수 6글자를 보냄
     * 회원가입
     * */
    @Override
    @Transactional
    public String sendfindingPasswordConfirmationCode(String name, Long year) throws IllegalAccessException {
        List<Account> byUserName = accountRepository.findByUserName(name).stream().filter(u->u.getYear().equals(year))
                .collect(Collectors.toList());
        Optional<Account> first = byUserName.stream().findFirst();
        if(first.isEmpty()){
            throw new IllegalAccessException("잘못된 이름과 기수를 입력했습니다.");
        }
        String userEmail = first.get().getUserEmail();
        return confirmationTokenService.createEmailConfirmationToken(userEmail);
    }
}

