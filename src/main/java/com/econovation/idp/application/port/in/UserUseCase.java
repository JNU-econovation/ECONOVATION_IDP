package com.econovation.idp.application.port.in;

import com.econovation.idp.domain.dto.UserPasswordUpdateDto;
import com.econovation.idp.domain.dto.UserUpdateRequestDto;
import com.econovation.idp.domain.user.Account;

import java.util.List;

public interface UserUseCase {
    List<Account> findAll(int page);
    Account findUserById(Long userId);
    Integer countUserByRole(String role);
    Long countAllUser();
    List<Account> findUserByUserName(String userName);
    List<Account> findUserByRole(int page, String role);
    Account indUserByYearAndUserName(String userName,Long year);
    Account findUserByUserEmail(String userEmail);
    Account updateUser(Long userId, UserUpdateRequestDto userUpdateRequestDto);
    void deleteUserById(Long userId);
    Account setPassword(UserPasswordUpdateDto userPasswordUpdateDto);
}
