package com.econovation.idp.application.port.in;

import com.econovation.idp.domain.user.Account;
import lombok.Data;
import lombok.Getter;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Getter
public class UserUpdateRequestDto {
    @Email
    private String userEmail;

    @NotNull
    @Range(min =1, max = 50)
    private Long year;

    @NotNull
    private String userName;


    public UserUpdateRequestDto(String userEmail, Long year,String userName) {
        this.userEmail = userEmail;
        this.year = year;
        this.userName = userName;
    }

    public Account toEntity(){
        return Account.builder()
                .userEmail(userEmail)
                .year(year)
                .userName(userName)
                .build();
    }
}
