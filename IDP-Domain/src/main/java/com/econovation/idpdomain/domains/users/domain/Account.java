package com.econovation.idpdomain.domains.users.domain;


import com.econovation.idpdomain.common.aop.domainEvent.Events;
import com.econovation.idpdomain.common.events.user.AccountRegisterEvent;
import com.econovation.idpdomain.domains.dto.NonAccountResponseDto;
import com.econovation.idpdomain.domains.dto.UserUpdateRequestDto;
import com.econovation.idpdomain.domains.users.exception.ForbiddenUserException;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PostPersist;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@DynamicInsert
@Builder
public class Account extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long id;
    private String password;

    @Embedded
    private Profile profile;

    @Enumerated(EnumType.STRING)
    private AccountState accountState = AccountState.NORMAL;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountRole accountRole = AccountRole.USER;

    // 이메일 수신 여부
    private Boolean receiveMail = Boolean.TRUE;

    private LocalDateTime lastLoginAt = LocalDateTime.now();


    @PostPersist
    public void registerEvent() {
        AccountRegisterEvent userRegisterEvent = AccountRegisterEvent.builder().userId(id).build();
        Events.raise(userRegisterEvent);
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void login() {
        if (!AccountState.NORMAL.equals(this.accountState)) {
            throw ForbiddenUserException.EXCEPTION;
        }
        lastLoginAt = LocalDateTime.now();
    }

    public Account(Profile profile, String password) {
        this.profile = profile;
        this.password = password;
    }

    public void update(UserUpdateRequestDto userUpdateRequestDto) {
        this.profile = userUpdateRequestDto.toProfile(userUpdateRequestDto);
    }

    public NonAccountResponseDto toNonLoginUser(Account account) {
        return new NonAccountResponseDto(account.profile.getYear(), account.profile.getName(), account.getId());
    }

    public Boolean isReceiveEmail() {
        return receiveMail;
    }


    public void toggleReceiveEmail() {
        receiveMail = !receiveMail;
    }


    public Boolean isDeletedUser() {
        return accountState == AccountState.DELETED;
    }
}
