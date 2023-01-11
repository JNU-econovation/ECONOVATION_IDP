package com.econovation.idp.domain.dto;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class UserResponseMatchedTokenDto {
    private Long year;

    private String userName;

    private Long id;
}
