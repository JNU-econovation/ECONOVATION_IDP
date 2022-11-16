package com.econovation.idp.application.port.in;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@AllArgsConstructor
public class SignUpRequestDto {
    @NotNull
    private String userEmail;
    @NotNull
    private Long year;
    @NotNull
    private String userName;
    @NotNull
    private String password;
    @NotNull
    private String pinCode;
}
