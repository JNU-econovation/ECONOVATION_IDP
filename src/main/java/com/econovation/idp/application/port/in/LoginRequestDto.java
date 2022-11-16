package com.econovation.idp.application.port.in;

import lombok.Getter;

@Getter
public class LoginRequestDto {
    private String userEmail;
    private String password;
    private String redirectUrl;
}