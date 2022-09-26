package com.swagger.docs.domain.user.controller;


import lombok.Getter;

@Getter
public class LoginRequestDto {
    private String email;
    private String password;
}
