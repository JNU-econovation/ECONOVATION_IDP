package com.econovation.idp.adapter.out.persistence;


import com.econovation.idp.application.port.out.LoadAccountPort;
import com.econovation.idp.application.port.out.RecordAccountPort;
import com.econovation.idp.domain.user.Account;
import com.econovation.idp.domain.user.AccountRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class AccountPersistenceAdapter implements LoadAccountPort, RecordAccountPort {
    private static final String NO_MATCH_ACCOUNT = "해당하는 ACCOUNT가 존재하지 않습니다";
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
    public Account loadById(Long id) {
        return accountRepository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException(NO_MATCH_ACCOUNT));
    }

    @Override
    public Optional<Account> loadUserByUserNameAndYear(String userName, Long year) {
        return accountRepository.findUserByUserNameAndYear(userName, year);
    }

    @Override
    public Account save(Account account) {
        return accountRepository.save(account);
    }

    public Account loadAccountByUserEmail(String email) {
        return accountRepository
                .findByUserEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
    }
}
