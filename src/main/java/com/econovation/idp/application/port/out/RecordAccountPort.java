package com.econovation.idp.application.port.out;

import com.econovation.idp.domain.user.Account;

public interface RecordAccountPort {
    Account save(Account account);

}
