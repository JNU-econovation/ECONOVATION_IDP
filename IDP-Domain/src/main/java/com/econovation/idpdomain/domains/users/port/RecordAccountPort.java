package com.econovation.idpdomain.domains.users.port;


import com.econovation.idpcommon.annotation.Port;
import com.econovation.idpdomain.domains.users.domain.Accounts;

@Port
public interface RecordAccountPort {
    Accounts save(Accounts account);
}
