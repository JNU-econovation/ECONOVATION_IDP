package com.swagger.docs.controller;

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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Account 관련 서비스", description = "회원가입, 로그인 등등")
public class AccountController {
    private final AccountJwtService accountJwtService;
    private final AccountSignUpService accountSignUpService;

    @Value("${login.redirect_url}")
    private String loginPageUrl;

    //    로그아웃 기능 구현
    @Operation(summary = "logout", description = "로그아웃_에이전트, 로그아웃시 redirect 페이지로 이동")
    @ApiResponses({
            @ApiResponse(responseCode = "HttpStatus.OK", description = "OK")
    })
    @GetMapping("/api/account/logout")
    public ResponseEntity<BasicResponse> logout(@RequestParam String redirectUrl, HttpServletRequest request) throws URISyntaxException {
//        7번부터 빼야 bearer(+스페이스바) 빼고 토큰만 추출 가능
        String refreshToken = request.getHeader("Authorization").substring(7);

        accountJwtService.logout(refreshToken);
        URI redirectUri = new URI(redirectUrl);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(redirectUri);

        BasicResponse response = new BasicResponse("로그아웃 완료", HttpStatus.OK);
        return new ResponseEntity<>(response,httpHeaders, HttpStatus.OK);
    }

    @Operation(summary = "토큰 재발행", description = "Refresh, Access Token 재발행")
    @ApiResponses({
            @ApiResponse(description = "access, refreshToken"),
            @ApiResponse(responseCode = "HttpStatus.OK", description = "OK")
    })
    @GetMapping("/api/account/re-issue")
    public ResponseEntity<LoginResponseDto> reIssue(@RequestParam("userEmail") String userEmail, @RequestParam("refreshToken") String refreshToken) {
        LoginResponseDto responseDto = accountJwtService.reIssueAccessToken(userEmail, refreshToken);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    //  회원가입 기능 구현
    @Operation(summary = "회원가입", description = "회원 가입")
    @ApiResponses({
            @ApiResponse(responseCode = "HttpStatus.CREATED", description = "CREATED")
    })
    @PostMapping("/api/account/sign-up")
    public ResponseEntity<BasicResponse> signUp(@RequestBody SignUpRequestDto signUpUser) {
        accountSignUpService.signUp(signUpUser.getUserName(),signUpUser.getYear(),signUpUser.getUserEmail(), signUpUser.getPinCode(), signUpUser.getPassword());
        BasicResponse response = new BasicResponse("회원가입 성공", HttpStatus.CREATED);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 로그인 기능 구현
    @Operation(summary = "로그인 Agent URL 이동", description = "로그인 페이지로 이동")
    @ApiResponses({
            @ApiResponse(responseCode = "HttpStatus.OK", description = "Header.Location : requestUrl ")
    })
    @PostMapping("/api/account/login")
    public ResponseEntity<Model> login(String requestUrl, Model model) throws URISyntaxException {
        URI redirectUri = new URI(loginPageUrl);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(redirectUri);
        model.addAttribute("requestUrl", requestUrl);
        return new ResponseEntity<>(model,httpHeaders, HttpStatus.OK);
    }

    // 로그인 인증
    @Operation(summary = "로그인 페이지 처리", description = "로그인완료 후 원래 페이지로 이동")
    @ApiResponses({
            @ApiResponse(description = "access, refreshToken"),
            @ApiResponse(responseCode = "HttpStatus.OK", description = "CREATED")
    })
    @PostMapping("/api/account/login/process")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginDto) throws URISyntaxException {
        LoginResponseDto responseDto = accountJwtService.login(loginDto.getUserEmail(), loginDto.getPassword());
        URI redirectUri = new URI(loginDto.getRedirectUrl());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(redirectUri);
        return new ResponseEntity<>(responseDto, httpHeaders, HttpStatus.OK);
    }
}
