package com.swagger.docs.dto;


import lombok.Getter;

@Getter
public class LoginRequestDto {
    private String userEmail;
    private String password;
    private String redirectUrl;
}
