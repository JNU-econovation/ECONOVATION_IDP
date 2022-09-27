package com.swagger.docs.domain.account;

import com.swagger.docs.domain.account.dto.UserUpdateRequestDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.validator.constraints.Range;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@NoArgsConstructor
@Builder
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="USER_ID")
    private Long id;

    @Column(nullable = false, unique = true)
    @NotNull
    private String userEmail;

    @Column(nullable = false)
    @Range(min =1, max = 50)
    @NotNull
    private Long year;

    @Column(nullable = false)
    @NotNull
    private String userName;

    @Column(nullable = false)
    @NotNull
    private String password;

    @ColumnDefault("'USER'")
    @Column(nullable = false)
    private String role;

//  EconoBeep 도서 대여 서비스 이용 코드
    @Column(nullable = false)
    @NotNull
    private String pinCode;

    public void update(UserUpdateRequestDto userUpdateRequestDto){
        this.userEmail = userUpdateRequestDto.toEntity().getUserEmail();
        this.userName = userUpdateRequestDto.toEntity().getUserName();
        this.year = userUpdateRequestDto.toEntity().getYear();
        this.role = userUpdateRequestDto.toEntity().getRole();
    }

    private Account(String email, String userName, String password) {
        this.userEmail = email;
        this.userName = userName;
        this.password = password;
        role = "USER";
    }

    public static Account of(String email, String nickname, String password) {
        return new Account(email, nickname, password);
    }
}
