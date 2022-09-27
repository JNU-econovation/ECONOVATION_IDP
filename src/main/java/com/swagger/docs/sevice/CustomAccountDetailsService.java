package com.swagger.docs.sevice;


import com.swagger.docs.domain.account.Account;
import com.swagger.docs.domain.account.AccountRepository;
import com.swagger.docs.global.common.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomAccountDetailsService implements UserDetailsService {
    private final AccountRepository accountRepository;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("토큰을 확인해보세요"));
        return new AuthAccount(account);
    }
}
