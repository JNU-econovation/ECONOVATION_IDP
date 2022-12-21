package com.econovation.idp.application.service;

import com.econovation.idp.domain.user.Account;
import com.econovation.idp.domain.user.AccountRepository;
import com.econovation.idp.global.common.exception.BadRequestException;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class AccountSignUpService implements UserDetailsService {
    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;
    private final ConfirmationTokenService confirmationTokenService;
    //   회원가입
    @Transactional
    public void signUp(String userName, Long year, String userEmail, String password) {
        // 중복검증
        isDuplicateEmail(userEmail);
        String encodePassword = passwordEncoder.encode(password);
        Account newAccount = Account.of(year,userName,encodePassword,userEmail);
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
        List<Account> byUserName = accountRepository.findByUserName(name).stream().filter(u->u.getYear().equals(year))
                .collect(Collectors.toList());
        Optional<Account> first = byUserName.stream().findFirst();
        if(first.isEmpty()){
            throw new BadRequestException("잘못된 이름과 기수를 입력했습니다.");
        }
        String userEmail = first.get().getUserEmail();
        return confirmationTokenService.createEmailConfirmationToken(userEmail);
    }

    @Override
    public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
        //adminUser 정보 조회
        Optional<Account> adminUser = accountRepository.findAccountByUserEmail(userEmail);

        if(adminUser.isPresent()) {
            Account admin = adminUser.get();

            Account authAdmin = Account.builder()
                    .id(admin.getId())
                    .userName(admin.getUsername())
                    .password(admin.getPassword())
                    .role(admin.getRole())
                    .userEmail(admin.getUserEmail())
                    .createdAt(admin.getCreatedAt())
                    .updatedAt(admin.getUpdatedAt())
                    .build();

            log.info("authAdmin : {}", authAdmin);
            return authAdmin;
    }
}
