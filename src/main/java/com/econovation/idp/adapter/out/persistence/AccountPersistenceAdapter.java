package com.econovation.idp.adapter.out.persistence;

import com.econovation.idp.application.port.out.LoadAccountPort;
import com.econovation.idp.application.port.out.RecordAccountPort;
import com.econovation.idp.domain.user.Account;
import com.econovation.idp.domain.user.AccountRepository;
import com.econovation.idp.global.common.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class AccountPersistenceAdapter implements LoadAccountPort, RecordAccountPort {
    private final AccountRepository accountRepository;


    @Override
    public boolean existsAccountByUserEmail(String email) {
        return accountRepository.existsAccountByUserEmail(email);
    }

    @Override
    public List<Account> loadByUserName(String userName) {
        return accountRepository.findByUserName(userName);
    }

    @Override
    public Optional<Account> loadByUserEmail(String email) {
        return accountRepository.findByUserEmail(email);
    }

    @Override
    public Page<Account> loadAllByPage(Pageable pageable) {
        return accountRepository.findAll(pageable);
    }

    @Override
    public Long countAllByRole(String role) {
        return accountRepository.countAllByRole(role);
    }

    @Override
    public Optional<Account> loadUserByUserNameAndYear(String userName, Long year) {
        return accountRepository.findUserByUserNameAndYear(userName,year);
    }

    @Override
    public Account save(Account account) {
        return accountRepository.save(account);
    }

    public Account loadAccountByUserEmail(String email) {
        return accountRepository.findByUserEmail(email).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
    }
}
