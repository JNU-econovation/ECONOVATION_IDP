package com.econovation.idp.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LoginResponseDtoWithRedirectUrl {
    private String accessToken;
    private String refreshToken;
    private String redirectUrl;
}
