package com.econovation.idpdomain.domains.dto;


import com.econovation.idpdomain.domains.users.domain.Profile;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import org.hibernate.validator.constraints.Range;

@Data
@Getter
public class UserUpdateRequestDto {

    @NotNull
    @Range(min = 1, max = 50)
    private Integer year;

    @NotNull private String userName;

    public UserUpdateRequestDto(Integer year, String userName) {
        this.year = year;
        this.userName = userName;
    }

    public Profile toProfile(UserUpdateRequestDto userUpdateRequestDto) {
        return Profile.builder().email(userName).build();
    }
}
