package com.econovation.idpdomain.domains.dto;


import com.econovation.idpdomain.domains.auth.Password;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserPasswordUpdateDto {
    @Range(min = 1, max = 50)
    private Integer year;

    @NotNull private String userName;
    @Password private String password;
}
