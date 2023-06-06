package com.econovation.idp.domain.dto;


import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
@Builder
public class UserResponseMatchedTokenDto {
    private Long year;

    private String userName;

    private Long id;
    private String userEmail;
}
