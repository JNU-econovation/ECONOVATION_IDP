package com.econovation.idp.global.utils;

import com.econovation.idp.domain.dto.UserResponseMatchedTokenDto;
import com.econovation.idp.domain.user.Account;
import org.springframework.stereotype.Component;

@Component
public class EntityMapper {
    public UserResponseMatchedTokenDto toUserResponseMatchedTokenDto(Account account) {
        return UserResponseMatchedTokenDto.builder()
                .id(account.getId())
                .userEmail(account.getUserEmail())
                .userName(account.getUsername())
                .year(account.getYear())
                .build();
    }
}
