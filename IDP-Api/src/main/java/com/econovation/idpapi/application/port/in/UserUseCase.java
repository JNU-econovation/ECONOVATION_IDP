package com.econovation.idpapi.application.port.in;


import com.econovation.idpdomain.domains.dto.UserPasswordUpdateDto;
import com.econovation.idpdomain.domains.dto.UserUpdateRequestDto;
import com.econovation.idpdomain.domains.users.domain.Account;
import java.util.List;
import java.util.Map;

public interface UserUseCase {
    List<Account> findAll(Integer page);

    Account findUserById(Long userId);

    Long countUserByRole(String role);

    Long countAllUser();

    List<Account> findUserByUserName(String userName);

    List<Account> findUserByRole(int page, String role);

    Account findUserByYearAndUserName(String userName, Integer year);

    Account findUserByUserEmail(String userEmail);

    Account updateUser(UserUpdateRequestDto userUpdateRequestDto);

    void deleteUserById(Long userId);

    Account setPassword(UserPasswordUpdateDto userPasswordUpdateDto);

    Map findAllWithLastPageInPage(Integer page);
}
