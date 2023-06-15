package com.econovation.idpdomain.domains.dto;


import lombok.Data;
import lombok.Getter;

@Getter
@Data
public class LoginRequestDto {
    private String userEmail;
    private String password;
    private String redirectUrl;
}
