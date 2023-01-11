package com.econovation.idp.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@AllArgsConstructor
@Data
public class LoginResponseDtoWithExpiredTime {
    private Date expiredTime;
    private String accessToken;
    private String refreshToken;

    public LoginResponseDtoWithExpiredTime(Date expiredTime, LoginResponseDto loginResponseDto) {
        this.expiredTime = expiredTime;
        this.accessToken = loginResponseDto.getAccessToken();
        this.refreshToken = loginResponseDto.getRefreshToken();
    }
}
