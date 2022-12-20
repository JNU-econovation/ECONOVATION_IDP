package com.econovation.idp.domain.user;

import com.econovation.idp.application.port.in.UserUpdateRequestDto;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.validator.constraints.Range;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@DynamicInsert
@Builder
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ACCOUNT_ID")
    private Long id;


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

    @Column(nullable = false)
    @NotNull
    private String userEmail;
    @Column(nullable = false)
    private String role;

    public void setPassword(String password){
        this.password = password;
    }

    public Account(Long year, String userName, String password, String userEmail) {
        this.year = year;
        this.userName = userName;
        this.password = password;
        this.userEmail = userEmail;
        this.role = "USER";
    }

    public void update(UserUpdateRequestDto userUpdateRequestDto){
        this.userEmail = userUpdateRequestDto.toEntity().getUserEmail();
        this.userName = userUpdateRequestDto.toEntity().getUserName();
        this.year = userUpdateRequestDto.toEntity().getYear();
    }


    public static Account of(Long year, String userName, String password, String userEmail) {
        return new Account(year, userName, password, userEmail);
    }
}