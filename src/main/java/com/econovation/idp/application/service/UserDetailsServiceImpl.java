package com.econovation.idp.application.service;

import com.econovation.idp.domain.user.Account;
import com.econovation.idp.domain.user.AccountRepository;
import com.econovation.idp.global.common.exception.BaseErrorCode;
import com.econovation.idp.global.common.exception.GlobalErrorCode;
import com.econovation.idp.global.common.exception.IdpCodeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class UserDetailsServiceImpl implements UserDetailsService {
    private final AccountRepository accountRepository;
    @Override
    public UserDetails loadUserByUsername(String idpId) throws UsernameNotFoundException {

        Account user = accountRepository.findById(Long.valueOf(idpId)).orElseThrow(() -> new IdpCodeException(GlobalErrorCode.INTERNAL_SERVER_ERROR));

        if (user == null) {
            throw new UsernameNotFoundException("user not found.");
        }
        return user;
    }
}
