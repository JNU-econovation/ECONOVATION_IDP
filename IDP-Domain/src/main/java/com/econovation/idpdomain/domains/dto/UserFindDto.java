package com.econovation.idpdomain.domains.dto;


import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserFindDto {
    private Integer year;

    private String userName;
}
