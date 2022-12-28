package com.econovation.idp.domain.dto;

import com.econovation.idp.domain.auth.Password;
import com.econovation.idp.domain.user.Account;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

import jakarta.validation.constraints.NotNull;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserPasswordUpdateDto {
    @Range(min =1, max = 50)
    private Long year;
    @NotNull
    private String userName;
    @Password
    private String password;

    public Account toEntity(){
        return Account.builder()
                .year(year)
                .userName(userName)
                .password(password).
                build();
    }
}

