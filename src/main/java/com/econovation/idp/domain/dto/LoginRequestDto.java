package com.econovation.idp.domain.dto;

import lombok.Data;
import lombok.Getter;

@Getter
@Data
public class LoginRequestDto {
    private String userEmail;
    private String password;
//    private String redirectUrl;
}