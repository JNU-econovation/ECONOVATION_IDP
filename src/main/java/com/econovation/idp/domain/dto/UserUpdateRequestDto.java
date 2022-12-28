package com.econovation.idp.domain.dto;

import com.econovation.idp.domain.user.Account;
import lombok.Data;
import lombok.Getter;
import org.hibernate.validator.constraints.Range;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@Getter
public class UserUpdateRequestDto {

    @NotNull
    @Range(min =1, max = 50)
    private Long year;

    @NotNull
    private String userName;


    public UserUpdateRequestDto(Long year,String userName) {
        this.year = year;
        this.userName = userName;
    }

    public Account toEntity(){
        return Account.builder()
                .year(year)
                .userName(userName)
                .build();
    }
}
