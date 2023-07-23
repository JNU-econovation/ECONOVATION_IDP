package com.econovation.idpdomain.common.events.user;


import com.econovation.idpdomain.common.aop.domainEvent.DomainEvent;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class AccountRegisterEvent extends DomainEvent {
    private final Long userId;
    private final Integer year;

    @Builder
    public AccountRegisterEvent(Long userId, Integer year) {
        this.userId = userId;
        this.year = year;
    }
    // toString
    @Override
    public String toString() {
        return "AccountRegisterEvent{" + "userId=" + userId + ", year=" + year + '}';
    }
}
