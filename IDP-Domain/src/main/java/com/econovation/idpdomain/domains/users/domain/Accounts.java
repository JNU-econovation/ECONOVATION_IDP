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
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@Slf4j
@Builder
public class Accounts extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "accounts_id")
    private Long id;

    private String password;

    @Embedded private Profile profile;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private AccountState accountState = AccountState.NORMAL;

    @Column(nullable = false)
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private AccountRole accountRole = AccountRole.GUEST;

    // 이메일 수신 여부
    @Builder.Default private Boolean receiveMail = Boolean.FALSE;
    @Builder.Default private LocalDateTime lastLoginAt = LocalDateTime.now();

    @PostPersist
    public void registerEvent() {
        AccountRegisterEvent userRegisterEvent =
                AccountRegisterEvent.builder().userId(id).year(profile.getYear()).build();
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

    public Accounts(Profile profile, String password) {
        this.profile = profile;
        this.password = password;
    }

    public void update(UserUpdateRequestDto userUpdateRequestDto) {
        this.profile = userUpdateRequestDto.toProfile(userUpdateRequestDto);
    }

    public NonAccountResponseDto toNonLoginUser(Accounts account) {
        return new NonAccountResponseDto(
                account.profile.getYear(), account.profile.getName(), account.getId());
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

    @Override
    public String toString() {
        return "Account{"
                + "id="
                + id
                + ", password='"
                + password
                + '\''
                + ", profile="
                + profile
                + ", accountState="
                + accountState
                + ", accountRole="
                + accountRole
                + ", receiveMail="
                + receiveMail
                + ", lastLoginAt="
                + lastLoginAt
                + '}';
    }
}
