package com.econovation.idpapi.common.handler;


import com.econovation.idpapi.application.port.out.LoadAccountPort;
import com.econovation.idpapi.application.service.ConfirmationTokenService;
import com.econovation.idpdomain.common.events.user.AccountRegisterEvent;
import com.econovation.idpdomain.domains.users.domain.Account;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccountRegisterEventHandler {
    private final LoadAccountPort loadAccountPort;
    private final ConfirmationTokenService mailService;

    @Async
    @TransactionalEventListener(
            classes = AccountRegisterEvent.class,
            phase = TransactionPhase.AFTER_COMPLETION)
    public void handleAccountRegisterEvent(AccountRegisterEvent accountRegisterEvent) {
        log.info(accountRegisterEvent.toString());
        Account account = loadAccountPort.loadById(accountRegisterEvent.getUserId());
        mailService.createEmailConfirmationToken(account.getId(), account.getProfile().getEmail());
    }
}
