package com.econovation.idp.domain.dto;

import com.econovation.idp.domain.auth.Password;
import lombok.AllArgsConstructor;
import lombok.Getter;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

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
