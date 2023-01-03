package com.econovation.idp.adapter.in.controller;

import com.econovation.idp.domain.dto.LoginResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@AllArgsConstructor
@Data
public class LoginResponseDtoWithExpiredTime {
    private Date expiredTime;
    private String accessToken;
    private String refreshToken;
    public LoginResponseDtoWithExpiredTime(Date expiredTime, LoginResponseDto responseDto) {
    }
}
