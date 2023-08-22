package com.econovation.idpdomain.domains.users.adaptor;


import com.econovation.idpcommon.annotation.Adaptor;
import com.econovation.idpdomain.domains.users.domain.AccountRepository;
import com.econovation.idpdomain.domains.users.domain.AccountRole;
import com.econovation.idpdomain.domains.users.domain.Accounts;
import com.econovation.idpdomain.domains.users.port.LoadAccountPort;
import com.econovation.idpdomain.domains.users.port.RecordAccountPort;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Adaptor
@Slf4j
@RequiredArgsConstructor
public class AccountAdaptor implements LoadAccountPort, RecordAccountPort {
    private static final String NO_MATCH_ACCOUNT = "해당하는 ACCOUNT가 존재하지 않습니다";
    private final AccountRepository accountRepository;

    @Override
    public boolean existsAccountByUserEmail(String email) {
        return accountRepository.existsAccountsByUserEmail(email);
    }

    @Override
    public List<Accounts> loadByUserName(String userName) {
        return accountRepository.findByUserName(userName);
    }

    @Override
    public Optional<Accounts> loadByUserEmail(String email) {
        return accountRepository.findByUserEmail(email);
    }

    @Override
    public Page<Accounts> loadAllByPage(Pageable pageable) {
        return accountRepository.findAll(pageable);
    }

    @Override
    public Long countAllByRole(AccountRole role) {
        return accountRepository.countAllByRole(role);
    }

    @Override
    public Accounts loadById(Long id) {
        return accountRepository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException(NO_MATCH_ACCOUNT));
    }

    @Override
    public Optional<Accounts> loadUserByUserNameAndYear(String userName, Integer year) {
        return accountRepository.findUserByUserNameAndYear(userName, year);
    }

    @Override
    public Accounts save(Accounts account) {
        return accountRepository.save(account);
    }

    public Accounts loadAccountByUserEmail(String email) {
        return accountRepository
                .findByUserEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
    }
}
