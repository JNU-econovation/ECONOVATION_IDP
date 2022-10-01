package com.swagger.docs.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignUpRequestDto {
    private String userEmail;
    private Long year;
    private String userName;
    private String password;
    private String pinCode;
}
