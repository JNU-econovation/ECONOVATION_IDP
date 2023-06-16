package com.econovation.idpapi.application.port.out;


import com.econovation.idpdomain.domains.users.domain.Account;
import com.econovation.idpdomain.domains.users.domain.AccountRole;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LoadAccountPort {
    boolean existsAccountByUserEmail(String email);

    List<Account> loadByUserName(String userName);

    Optional<Account> loadByUserEmail(String userEmail);

    Page<Account> loadAllByPage(Pageable pageable);

    Long countAllByRole(AccountRole role);

    Account loadById(Long id);

    Optional<Account> loadUserByUserNameAndYear(String userName, Integer year);
}
