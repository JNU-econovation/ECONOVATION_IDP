package com.econovation.idpapi.utils;


import com.econovation.idpdomain.domains.dto.UserResponseMatchedTokenDto;
import com.econovation.idpdomain.domains.users.domain.Account;
import org.springframework.stereotype.Component;

@Component
public class EntityMapper {
    public UserResponseMatchedTokenDto toUserResponseMatchedTokenDto(Account account) {
        return UserResponseMatchedTokenDto.builder()
                .id(account.getId())
                .profile(account.getProfile())
                .build();
    }
}
