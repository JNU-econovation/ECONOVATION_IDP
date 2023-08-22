package com.econovation.idpapi.utils;


import com.econovation.idpdomain.domains.dto.UserResponseMatchedTokenDto;
import com.econovation.idpdomain.domains.users.domain.Accounts;
import org.springframework.stereotype.Component;

@Component
public class EntityMapper {
    public UserResponseMatchedTokenDto toUserResponseMatchedTokenDto(Accounts account) {
        return UserResponseMatchedTokenDto.builder()
                .id(account.getId())
                .profile(account.getProfile())
                .build();
    }
}
