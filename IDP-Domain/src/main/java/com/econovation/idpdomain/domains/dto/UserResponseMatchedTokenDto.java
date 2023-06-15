package com.econovation.idpdomain.domains.dto;


import com.econovation.idpdomain.domains.users.domain.Profile;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
@Builder
public class UserResponseMatchedTokenDto {

    private Profile profile;
    private Long id;
}
