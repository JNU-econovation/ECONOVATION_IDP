package com.swagger.docs.controller;

import com.swagger.docs.domain.user.Account;
import com.swagger.docs.global.common.BasicResponse;
import com.swagger.docs.sevice.*;
import com.swagger.docs.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.Basic;
import javax.validation.Valid;
import java.nio.charset.Charset;
import java.util.List;


@Slf4j
@RestController
@AllArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "WebApplication User 제공 서비스", description = "유저 정보 조회")
public class UserController {
    private final UserService userService;
    @Deprecated
    @GetMapping("/api/user/all/{page}")
    public ResponseEntity<List<Account>> findUserAll(@PathVariable int page){
        List<Account> listAccount = userService.findAll();
        return new ResponseEntity<>(listAccount, HttpStatus.OK);
    }

    @Operation(summary = "findUserById", description = "Id로 회원조회")
    @ApiResponses({
            @ApiResponse(responseCode = "Account Object", description = "검색 유저 return")
    })
    @GetMapping("/api/user/{userId}")
    public ResponseEntity<Account> findUserById(@PathVariable Long userId) {
        HttpHeaders headers= new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

        Account account = userService.findUserById(userId);
        return new ResponseEntity<>(account, headers, HttpStatus.OK);
    }

    @Operation(summary = "findUserByPinCode", description = "PinCode로 회원조회")
    @ApiResponses({
            @ApiResponse(responseCode = "Account Object", description = "검색 유저 return")
    })
    @GetMapping("/api/user/pinCode/{pinCode}")
    public ResponseEntity<Account> findUserBypinCode(@PathVariable String pinCode) {
        Account account = userService.findUserByPinCode(pinCode);
        return new ResponseEntity<>(account, HttpStatus.OK);

    }

    @Operation(summary = "countUserByRole", description = "Role(USER,GUEST,ADMIN)로 회원수 조회")
    @ApiResponses({
            @ApiResponse(description = "Role에 따른 회원 조횐 return")
    })
    @GetMapping("/api/user/count/{role}")
    public ResponseEntity<Long> countUserByRole(@PathVariable String role){
        Long numberIsRole = userService.countUserByRole(role);
        return new ResponseEntity<>(numberIsRole, HttpStatus.OK);
    }


    @Operation(summary = "전체 회원 수 조회", description = "전체 회원 수 조회")
    @ApiResponses({
            @ApiResponse(description = "Role에 따른 회원 조횐 return")
    })
    @GetMapping("/api/user/count")
    public ResponseEntity<Long> countAllUser(){
        Long numberAllAccount = userService.countAllUser();
        return new ResponseEntity<>(numberAllAccount, HttpStatus.OK);
    }


    @Operation(summary = "이름으로 회원 조회", description = "동명이인 포함 회원 정보 조회")
    @ApiResponses({
            @ApiResponse(description = "이름으로 회원 조회")
    })
    @GetMapping("/api/usernames/{userName}")
    public ResponseEntity<List<Account>> findUserByUserName(@PathVariable String userName){
        List<Account> userListByUserName = userService.findUserByUserName(userName);
        return new ResponseEntity<>(userListByUserName, HttpStatus.OK);
    }


    @Operation(summary = "Role으로 회원 조회", description = "Role(USER,GUEST,ADMIN)로 회원정보 조회 with Page")
    @ApiResponses({
            @ApiResponse(description = "Role에 따른 회원 조횐 return")
    })
    @GetMapping("/api/user/role/{page}/{role}")
    public ResponseEntity<List<Account>> findUserByRole(@PathVariable int page, @PathVariable String role){
        List<Account> userByRole = userService.findUserByRole(page, role);
        return new ResponseEntity<>(userByRole, HttpStatus.OK);
    }


    @Operation(summary = "Email 찾기 기능", description = "Email 찾기 ( 기수, 이름 ) 으로 조회")
    @ApiResponses({
            @ApiResponse(description = "Email에 따른 회원 조횐 return")
    })
    @GetMapping("/api/user/find-email/")
    public ResponseEntity<Account> findEmail(@Valid UserFindDto userFindDto){
        Account userByYearAndUserName = userService.findUserByYearAndUserName(userFindDto.getUserName(), userFindDto.getYear());
        return new ResponseEntity<>(userByYearAndUserName, HttpStatus.OK);
    }

    @Operation(summary = "회원정보 수정", description = "회원정보 수정")
    @ApiResponses({
            @ApiResponse(description = "Role에 따른 회원 조회 return")
    })
    @PostMapping("/api/user/{userId}")
    public Account updateUser(@PathVariable Long userId, UserUpdateRequestDto userUpdateRequestDto) {
            return userService.updateUser(userId, userUpdateRequestDto);
            }

    @Operation(summary = "Email로 회원 조회", description = "이메일로 회원 조회")
    @ApiResponses({
            @ApiResponse(description = "Role에 따른 회원 조횐 return")
    })
    @GetMapping("/api/user/email/{userEmail}")
    public ResponseEntity<Account> findUserByEmail(@PathVariable String userEmail) {
        Account userByUserEmail = userService.findUserByUserEmail(userEmail);
        return new ResponseEntity<>(userByUserEmail,HttpStatus.OK);
    }

    @Operation(summary = "회원삭제", description = "회원 삭제")
    @ApiResponses({
            @ApiResponse(description = "회원 삭제 Response")
    })
    @DeleteMapping("/api/user/{userId}")
    public ResponseEntity<BasicResponse> deleteUser(@PathVariable Long userId) {
        userService.deleteUserById(userId);
        BasicResponse successDeleteResponse = new BasicResponse("회원 삭제 성공", HttpStatus.OK);
        return new ResponseEntity<>(successDeleteResponse, HttpStatus.OK);
    }

    @Operation(summary = "비밀번호 수정전 인증 이메일 보내기", description = "비밀번호수정 이메일 보내기 ")
    @ApiResponses({
            @ApiResponse(description = "이메일 보낸 인증 Code")
    })
    @PostMapping("/api/user/set-password/")
    public ResponseEntity<Account> setPassword(@Valid UserPasswordUpdateDto userPasswordUpdateDto){
        Account account = userService.setPassword(userPasswordUpdateDto);
        return new ResponseEntity<>(account, HttpStatus.OK);
    }
}