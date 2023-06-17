package com.econovation.idpdomain.domains.dto;


import com.econovation.idpdomain.domains.auth.Password;
import javax.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignUpRequestDto {
    @Email private String userEmail;
    private Integer year;

    private String userName;
    @Password private String password;
}
