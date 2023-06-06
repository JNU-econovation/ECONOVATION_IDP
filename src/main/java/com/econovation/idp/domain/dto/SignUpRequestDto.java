package com.econovation.idp.domain.dto;


import com.econovation.idp.domain.auth.Password;
import javax.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignUpRequestDto {
    @Email private String userEmail;
    private Long year;

    private String userName;
    @Password private String password;
}
