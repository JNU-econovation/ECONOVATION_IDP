package com.swagger.docs.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpHeaders;

@Getter
@AllArgsConstructor
public class LoginResponseDto {
    private String accessToken;
    private String refreshToken;
}
