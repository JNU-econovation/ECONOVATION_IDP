package com.swagger.docs.controller;

import com.swagger.docs.domain.user.Account;
import com.swagger.docs.sevice.*;
import com.swagger.docs.dto.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;


@Slf4j
@RestController
@AllArgsConstructor
public class UserController {
    private final UserService userService;

//    @GetMapping("/api/user/all/{page}")
//    public List<Account> findUserAll(@PathVariable int page){return userService.findAll(page);}

    @GetMapping("/api/user/{userId}")
    public Account findUserById(@PathVariable Long userId) { return userService.findUserById(userId);}

    @GetMapping("/api/user/pinCode/{pinCode}")
    public Account findUserBypinCode(@PathVariable String pinCode) { return userService.findUserByPinCode(pinCode);}

    @GetMapping("/api/user/count/{role}")
    public Long countUserByRole(@PathVariable String role){return userService.countUserByRole(role);}

    @GetMapping("/api/user/count")
    public Long countAllUser(){return userService.countAllUser();}

    @GetMapping("/api/usernames/{userName}")
    public String findUserByUserName(@PathVariable String userName){
            List<Account> findUser = userService.findUserByUserName(userName);
            return userName;
            }

    @GetMapping("/api/user/role/{page}/{role}")
    public List<Account> findUserByRole(@PathVariable int page, @PathVariable String role){ return userService.findUserByRole(page, role); }

    @GetMapping("/api/user/find-email/")
    public Account findEmail(@Valid @ModelAttribute UserFindDto userFindDto){
            return userService.findUserByYearAndUserName(userFindDto);
            }

    @GetMapping("/api/user/email/{userEmail}")
    public Account findUserByEmail(@PathVariable String userEmail) { return userService.findUserByUserEmail(userEmail);}

    @PostMapping("/api/user/{userId}")
    public Account updateUser(@PathVariable Long userId, @ModelAttribute UserUpdateRequestDto userUpdateRequestDto) {
            return userService.updateUser(userId, userUpdateRequestDto);
            }

    @DeleteMapping("/api/user/{userId}")
    public Long deleteUser(@PathVariable Long userId) {
            userService.deleteUserById(userId);
            return userId;
            }

    //  비밀번호 수정을 위한 인증번호 출력.
    @PostMapping("/api/find-password/")
    public String findPassword(@Valid @ModelAttribute UserFindDto userFindDto){
    //        Code를 이메일로 보내기
            return userService.sendfindingPasswordConfirmationCode(userFindDto.getUserName(),userFindDto.getYear());
        }

    @PostMapping("/api/user/set-password/")
    public String setPassword(@Valid @ModelAttribute UserPasswordUpdateDto userPasswordUpdateDto){
        return userService.setPassword(userPasswordUpdateDto);
    }

}