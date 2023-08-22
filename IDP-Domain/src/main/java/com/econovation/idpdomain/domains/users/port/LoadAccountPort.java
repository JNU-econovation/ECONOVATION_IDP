package com.econovation.idpdomain.domains.users.port;


import com.econovation.idpcommon.annotation.Port;
import com.econovation.idpdomain.domains.users.domain.AccountRole;
import com.econovation.idpdomain.domains.users.domain.Accounts;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Port
public interface LoadAccountPort {
    boolean existsAccountByUserEmail(String email);

    List<Accounts> loadByUserName(String userName);

    Optional<Accounts> loadByUserEmail(String userEmail);

    Page<Accounts> loadAllByPage(Pageable pageable);

    Long countAllByRole(AccountRole role);

    Accounts loadById(Long id);

    Optional<Accounts> loadUserByUserNameAndYear(String userName, Integer year);
}
