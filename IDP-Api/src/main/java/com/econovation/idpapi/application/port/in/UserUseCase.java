package com.econovation.idpapi.application.port.in;


import com.econovation.idpdomain.domains.dto.UserPasswordUpdateDto;
import com.econovation.idpdomain.domains.dto.UserUpdateRequestDto;
import com.econovation.idpdomain.domains.users.domain.Accounts;
import java.util.List;
import java.util.Map;

public interface UserUseCase {
    List<Accounts> findAll(Integer page);

    Accounts findUserById(Long userId);

    Long countUserByRole(String role);

    Long countAllUser();

    List<Accounts> findUserByUserName(String userName);

    List<Accounts> findUserByRole(int page, String role);

    Accounts findUserByYearAndUserName(String userName, Integer year);

    Accounts findUserByUserEmail(String userEmail);

    Accounts updateUser(UserUpdateRequestDto userUpdateRequestDto);

    void deleteUserById(Long userId);

    Accounts setPassword(UserPasswordUpdateDto userPasswordUpdateDto);

    Map findAllWithLastPageInPage(Integer page);
}
