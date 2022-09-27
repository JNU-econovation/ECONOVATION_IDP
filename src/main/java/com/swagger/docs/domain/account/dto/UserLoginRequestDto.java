package com.swagger.docs.domain.account.dto;

import com.swagger.docs.domain.account.Account;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Data
@Getter
@Setter
@NoArgsConstructor
public class UserLoginRequestDto {
    @NotEmpty
    private String userEmail;
    @NotEmpty
    private String password;

    public Account toEntity(){
        return Account.builder()
                .userEmail(userEmail)
                .password(password).build();
    }
}
