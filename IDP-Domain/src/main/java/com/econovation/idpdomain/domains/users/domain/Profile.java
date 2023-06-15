package com.econovation.idpdomain.domains.users.domain;


import javax.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Profile {
    private String name;
    private String email;
    private Integer year;

    public void withdraw() {
        this.name = "탈퇴한 유저";
        this.email = null;
        this.year = null;
    }

    @Builder
    public Profile(String name, String email, Integer year) {
        this.name = name;
        this.email = email;
        this.year = year;
    }
}
