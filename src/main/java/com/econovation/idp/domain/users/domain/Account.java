package com.econovation.idp.domain.user;


import com.econovation.idp.domain.dto.NonAccountResponseDto;
import com.econovation.idp.domain.dto.UserUpdateRequestDto;
import com.econovation.idp.domain.events.AccountRegisterEvent;
import com.econovation.idp.domain.events.Events;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.validator.constraints.Range;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

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
        this.profile. = password;
    }

    public void login() {
        if (!AccountState.NORMAL.equals(this.accountState)) {
            throw ForbiddenUserException.EXCEPTION;
        }
        lastLoginAt = LocalDateTime.now();
    }

    public Account(Long year, String userName, String password, String userEmail) {
        this.year = year;
        this.userName = userName;
        this.password = password;
        this.userEmail = userEmail;
        this.role = Role.GUEST;
        this.isEnabled = true;
    }

    public void update(UserUpdateRequestDto userUpdateRequestDto) {
        this.userEmail = userUpdateRequestDto.toEntity().profile.getUserEmail();
        this.userName = userUpdateRequestDto.toEntity().profile.getUserName();
        this.year = userUpdateRequestDto.toEntity().profile.getYear();
    }

    public NonAccountResponseDto toNonLoginUser(Account account) {
        return new NonAccountResponseDto(account.profile.getYear(), account.profile.getUserName(), account.getId());
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
