package com.swagger.docs.domain.account.dto;

import com.swagger.docs.domain.account.Account;
import lombok.*;

@Data
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserResponseDto {
    private Long year;
    private String userName;
    private String password;
    private String userEmail;

    @Builder
    public UserResponseDto(Long year, String userName, String password, String userEmail) {
        this.year = year;
        this.userName = userName;
        this.password = password;
        this.userEmail = userEmail;
    }

    public Account toEntity(){
        return Account.builder()
                .userEmail(userEmail)
                .year(year)
                .userName(userEmail).build();
    }
}
