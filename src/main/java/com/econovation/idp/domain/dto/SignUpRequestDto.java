package com.econovation.idp.domain.dto;

import com.econovation.idp.domain.auth.Password;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.Email;

@Getter
@AllArgsConstructor
public class SignUpRequestDto {
    @Email
    private String userEmail;
    private Long year;

    private String userName;
    @Password
    private String password;
}
