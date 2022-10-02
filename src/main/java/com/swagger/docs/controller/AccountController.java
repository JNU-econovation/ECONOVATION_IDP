package com.swagger.docs.controller;

import com.swagger.docs.domain.user.Account;
import com.swagger.docs.domain.user.AuthUser;
import com.swagger.docs.dto.LoginRequestDto;
import com.swagger.docs.dto.LoginResponseDto;
import com.swagger.docs.dto.SignUpRequestDto;
import com.swagger.docs.sevice.*;
import com.swagger.docs.global.common.BasicResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
@Tag(name = "Account 관련 서비스", description = "회원가입, 로그인 등등")
public class AccountController {
    private final AccountService accountService;

    //    로그아웃 기능 구현
    @Operation(summary = "logout", description = "로그아웃")
    @ApiResponses({
            @ApiResponse(responseCode = "HttpStatus.OK", description = "OK")
    })
    @GetMapping("/api/account/logout")
    public ResponseEntity<BasicResponse> logout(@AuthUser Account account, HttpServletRequest request) {
        String accessToken = request.getHeader("Authorization").substring(7);
        accountService.logout(account.getUserEmail(), accessToken);
        BasicResponse response = new BasicResponse("로그아웃 완료", HttpStatus.OK);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    //    RefreshToken, AccessToken 재발행
//    @ApiOperation(value = "reIssue")
    @Operation(summary = "토큰 재발행", description = "Refresh, Access Token 재발행")
    @ApiResponses({
            @ApiResponse(description = "access, refreshToken"),
            @ApiResponse(responseCode = "HttpStatus.OK", description = "OK")
    })
    @GetMapping("/api/account/re-issue")
    public ResponseEntity<LoginResponseDto> reIssue(@RequestParam("email") String email, @RequestParam("refreshToken") String refreshToken) {
        LoginResponseDto responseDto = accountService.reIssueAccessToken(email, refreshToken);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    //  회원가입 기능 구현
    @Operation(summary = "회원가입", description = "회원 가입")
    @ApiResponses({
            @ApiResponse(responseCode = "HttpStatus.CREATED", description = "CREATED")
    })
    @PostMapping("/api/account/sign-up")
    public ResponseEntity<BasicResponse> signUp(@RequestBody SignUpRequestDto signUpUser) {
        accountService.signUp(signUpUser.getUserName(),signUpUser.getYear(),signUpUser.getUserEmail(), signUpUser.getPinCode(), signUpUser.getPassword());
        BasicResponse response = new BasicResponse("회원가입 성공", HttpStatus.CREATED);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    //     로그인 기능 구현
    @Operation(summary = "로그인", description = "로그인")
    @ApiResponses({
            @ApiResponse(description = "access, refreshToken"),
            @ApiResponse(responseCode = "HttpStatus.OK", description = "CREATED")
    })
    @PostMapping("/api/account/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginDto) {
        LoginResponseDto responseDto = accountService.login(loginDto.getUserEmail(), loginDto.getPassword());
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

}
