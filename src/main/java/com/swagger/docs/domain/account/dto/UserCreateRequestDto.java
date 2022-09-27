package com.swagger.docs.domain.account.dto;

import com.swagger.docs.domain.auth.Password;
import com.swagger.docs.domain.account.Account;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotEmpty;

@Data
@Getter
@NoArgsConstructor
public class UserCreateRequestDto {

    @NotEmpty
    private String userEmail;

    @NotEmpty
    @Password
    private String password;

    @Range(min =1, max = 50)
    private Long year;

    @NotEmpty
    private String userName;

    @NotEmpty
    private String pinCode;

    public Account toEntity(){
        return Account.builder()
                .userName(userName)
                .userEmail(userEmail)
                .password(password)
                .year(year)
                .pinCode(pinCode)
                .role("GUEST")
                .build();
    }
}
