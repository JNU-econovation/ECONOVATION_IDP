package com.econovation.idpapi.application.port.out;


import com.econovation.idpdomain.domains.users.domain.Account;

public interface RecordAccountPort {
    Account save(Account account);
}
