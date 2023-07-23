package com.econovation.idpdomain.domains.users.domain;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AccountRole {
    GUEST("GUEST"),
    USER("USER"),
    TF("TF"),
    ADMIN("ADMIN"),
    SUPER_ADMIN("SUPER_ADMIN");

    private String value;
}
