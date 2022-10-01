package com.swagger.docs.dto;

import com.swagger.docs.domain.user.Account;
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
