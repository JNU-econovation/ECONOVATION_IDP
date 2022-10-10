package com.swagger.docs.controller;

import com.swagger.docs.domain.user.Account;
import com.swagger.docs.sevice.*;
import com.swagger.docs.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@Slf4j
@RestController
@AllArgsConstructor
@Tag(name = "WebApplication User 제공 서비스", description = "유저 정보 조회")
public class UserController {
    private final UserService userService;
    @Deprecated
    @GetMapping("/api/user/all/{page}")
    public List<Account> findUserAll(@PathVariable int page){return userService.findAll();}

    @Operation(summary = "findUserById", description = "Id로 회원조회")
    @ApiResponses({
            @ApiResponse(responseCode = "Account Object", description = "검색 유저 return")
    })
    @GetMapping("/api/user/{userId}")
    public Account findUserById(@PathVariable Long userId) { return userService.findUserById(userId);}

    @Operation(summary = "findUserByPinCode", description = "PinCode로 회원조회")
    @ApiResponses({
            @ApiResponse(responseCode = "Account Object", description = "검색 유저 return")
    })
    @GetMapping("/api/user/pinCode/{pinCode}")
    public Account findUserBypinCode(@PathVariable String pinCode) { return userService.findUserByPinCode(pinCode);}

    @Operation(summary = "countUserByRole", description = "Role(USER,GUEST,ADMIN)로 회원수 조회")
    @ApiResponses({
            @ApiResponse(description = "Role에 따른 회원 조횐 return")
    })
    @GetMapping("/api/user/count/{role}")
    public Long countUserByRole(@PathVariable String role){return userService.countUserByRole(role);}


    @Operation(summary = "전체 회원 수 조회", description = "전체 회원 수 조회")
    @ApiResponses({
            @ApiResponse(description = "Role에 따른 회원 조횐 return")
    })
    @GetMapping("/api/user/count")
    public Long countAllUser(){return userService.countAllUser();}


    @Operation(summary = "이름으로 회원 조회", description = "동명이인 포함 회원 정보 조회")
    @ApiResponses({
            @ApiResponse(description = "이름으로 회원 조회")
    })
    @GetMapping("/api/usernames/{userName}")
    public List<Account> findUserByUserName(@PathVariable String userName){
        return userService.findUserByUserName(userName);
    }


    @Operation(summary = "Role으로 회원 조회", description = "Role(USER,GUEST,ADMIN)로 회원정보 조회")
    @ApiResponses({
            @ApiResponse(description = "Role에 따른 회원 조횐 return")
    })
    @GetMapping("/api/user/role/{page}/{role}")
    public List<Account> findUserByRole(@PathVariable int page, @PathVariable String role){ return userService.findUserByRole(page, role); }


    @Operation(summary = "Email 찾기 기능", description = "Email 찾기 ( 기수, 이름 ) 으로 조회")
    @ApiResponses({
            @ApiResponse(description = "Email에 따른 회원 조횐 return")
    })
    @GetMapping("/api/user/find-email/")
    public Account findEmail(@Valid UserFindDto userFindDto){
            return userService.findUserByYearAndUserName(userFindDto.getUserName(),userFindDto.getYear());
            }

    @Operation(summary = "Email로 회원 조회", description = "이메일로 회원 조회")
    @ApiResponses({
            @ApiResponse(description = "Role에 따른 회원 조횐 return")
    })
    @GetMapping("/api/user/email/{userEmail}")
    public Account findUserByEmail(@PathVariable String userEmail) { return userService.findUserByUserEmail(userEmail);}

    @Operation(summary = "회원정보 수정", description = "회원정보 수정")
    @ApiResponses({
            @ApiResponse(description = "Role에 따른 회원 조회 return")
    })
    @PostMapping("/api/user/{userId}")
    public Account updateUser(@PathVariable Long userId, UserUpdateRequestDto userUpdateRequestDto) {
            return userService.updateUser(userId, userUpdateRequestDto);
            }

    @Operation(summary = "회원삭제", description = "회원 삭제")
    @ApiResponses({
            @ApiResponse(description = "회원 삭제")
    })
    @DeleteMapping("/api/user/{userId}")
    public Long deleteUser(@PathVariable Long userId) {
            userService.deleteUserById(userId);
            return userId;
            }

    @Operation(summary = "비밀번호 수정전 인증 이메일 보내기", description = "비밀번호수정 이메일 보내기 ")
    @ApiResponses({
            @ApiResponse(description = "이메일 보낸 인증 Code")
    })
    @PostMapping("/api/user/set-password/")
    public Account setPassword(@Valid UserPasswordUpdateDto userPasswordUpdateDto){
        return userService.setPassword(userPasswordUpdateDto);
    }
}