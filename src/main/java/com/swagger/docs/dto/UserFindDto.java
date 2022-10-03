package com.swagger.docs.dto;

import com.swagger.docs.domain.user.Account;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;
import org.springframework.security.core.userdetails.User;

@Data
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserFindDto {
    @Range(min =1, max = 50)
    private Long year;

    private String userName;

    public Account toEntity(){
        return Account.builder()
                .year(year)
                .userName(userName).
                build();
    }
}
