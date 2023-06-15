package com.econovation.idpdomain.common.events.user;


import com.econovation.idpdomain.common.aop.domainEvent.DomainEvent;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class AccountRegisterEvent extends DomainEvent {
    private final Long userId;

    @Builder
    public AccountRegisterEvent(Long userId) {
        this.userId = userId;
    }
}
