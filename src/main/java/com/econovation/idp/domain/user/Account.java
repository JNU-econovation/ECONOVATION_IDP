package com.econovation.idp.domain.user;

import com.econovation.idp.domain.dto.NonAccountResponseDto;
import com.econovation.idp.domain.dto.UserUpdateRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.validator.constraints.Range;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@DynamicInsert
@Builder
public class Account extends BaseTimeEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    private boolean isEnabled;
    public void setPassword(String password){
        this.password = password;
    }

    public Account(Long year, String userName, String password, String userEmail) {
        this.year = year;
        this.userName = userName;
        this.password = password;
        this.userEmail = userEmail;
        this.role = "USER";
        this.isEnabled = true;
    }

    public void update(UserUpdateRequestDto userUpdateRequestDto){
        this.userEmail = userUpdateRequestDto.toEntity().getUserEmail();
        this.userName = userUpdateRequestDto.toEntity().getUsername();
        this.year = userUpdateRequestDto.toEntity().getYear();
    }


    public NonAccountResponseDto toNonLoginUser(Account account) {
        return new NonAccountResponseDto(account.getYear(), account.getUsername(), account.getUserEmail());
    }
    public static Account of(Long year, String userName, String password, String userEmail) {
        return new Account(year, userName, password, userEmail);
    }

    public void notEnabled(Account account){
        this.isEnabled = false;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();

        for(String role : role.split(",")){
            authorities.add(new SimpleGrantedAuthority(role));
        }
        return authorities;
    }


    @Override
    public String getUsername() {
        return userName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}