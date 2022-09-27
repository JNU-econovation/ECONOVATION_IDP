package com.swagger.docs.domain.account.controller;


import lombok.Getter;

@Getter
public class LoginRequestDto {
    private String email;
    private String password;
}
