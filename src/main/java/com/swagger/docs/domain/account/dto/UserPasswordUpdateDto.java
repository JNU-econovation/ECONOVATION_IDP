package com.swagger.docs.domain.account.dto;


import com.swagger.docs.domain.account.Account;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@Data
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserPasswordUpdateDto {
    @Range(min =1, max = 50)
    private Long year;

    private String userName;

    private String password;

    public Account toEntity(){
        return Account.builder()
                .year(year)
                .userName(userName)
                .password(password).
                build();
    }
}

