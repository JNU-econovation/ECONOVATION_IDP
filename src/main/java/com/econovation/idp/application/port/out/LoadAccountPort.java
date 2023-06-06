package com.econovation.idp.application.port.out;


import com.econovation.idp.domain.user.Account;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LoadAccountPort {
    boolean existsAccountByUserEmail(String email);

    List<Account> loadByUserName(String userName);

    Optional<Account> loadByUserEmail(String userEmail);

    Page<Account> loadAllByPage(Pageable pageable);

    Long countAllByRole(String role);

    Account loadById(Long id);

    Optional<Account> loadUserByUserNameAndYear(String userName, Long Year);
}
