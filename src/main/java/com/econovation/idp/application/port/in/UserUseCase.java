package com.econovation.idp.application.port.in;

import com.econovation.idp.domain.dto.UserPasswordUpdateDto;
import com.econovation.idp.domain.dto.UserUpdateRequestDto;
import com.econovation.idp.domain.user.Account;

import java.util.List;
import java.util.Map;

public interface UserUseCase {
    List<Account> findAll(Integer page);
    Account findUserById(Long userId);
    Long countUserByRole(String role);
    Long countAllUser();
    List<Account> findUserByUserName(String userName);
    List<Account> findUserByRole(int page, String role);
    Account findUserByYearAndUserName(String userName,Long year);
    Account findUserByUserEmail(String userEmail);
    Account updateUser(UserUpdateRequestDto userUpdateRequestDto);
    void deleteUserById(Long userId);
    Account setPassword(UserPasswordUpdateDto userPasswordUpdateDto);
    Map findAllWithLastPageInPage(Integer page);
}
