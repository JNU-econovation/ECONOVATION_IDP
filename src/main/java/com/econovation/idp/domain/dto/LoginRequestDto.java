package com.econovation.idp.domain.dto;

import com.econovation.idp.domain.auth.Password;
import lombok.Getter;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.Email;

@Getter
public class LoginRequestDto {
    @Email
    private String userEmail;
    @Password
    private String password;
    @URL
    private String redirectUrl;
}