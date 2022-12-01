package com.econovation.idp.adapter.in.web;

import com.econovation.idp.application.port.in.LoginRequestDto;
import com.econovation.idp.application.port.in.SignUpRequestDto;
import com.econovation.idp.application.port.out.LoginResponseDto;

import javax.validation.Valid;
import com.econovation.idp.application.service.AccountJwtService;
import com.econovation.idp.global.config.jwt.JwtProvider;
import com.econovation.idp.application.service.AccountSignUpService;
import com.econovation.idp.global.common.BasicResponse;
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
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Email;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
@Tag(name = "Account 관련 서비스", description = "회원가입, 로그인 등등")
public class AccountController {
    private final AccountJwtService accountJwtService;
    private final AccountSignUpService accountSignUpService;
    private final JwtProvider jwtProvider;
    @Value("${login.redirect_url}")
    private String loginPageUrl;

    //    로그아웃 기능 구현
    @Operation(summary = "logout", description = "로그아웃_에이전트, 로그아웃시 redirect 페이지로 이동")
    @ApiResponse(responseCode = "HttpStatus.OK", description = "OK")
    @GetMapping("/api/account/logout")
    public ResponseEntity<BasicResponse> logout(String redirectUrl, HttpServletRequest request) throws URISyntaxException {
//        7번부터 빼야 bearer(+스페이스바) 빼고 토큰만 추출 가능
        String refreshToken = request.getHeader("Authorization").substring(7);

        accountJwtService.logout(refreshToken);
        URI redirectUri = new URI(redirectUrl);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(redirectUri);
        log.info(redirectUrl + "으로 이동");
        BasicResponse response = new BasicResponse("로그아웃 완료", HttpStatus.OK);
        return new ResponseEntity<>(response, httpHeaders, HttpStatus.OK);
    }

    @Operation(summary = "토큰 재발행", description = "Refresh, Access Token 재발행")
    @ApiResponses({
            @ApiResponse(description = "access, refreshToken"),
            @ApiResponse(responseCode = "HttpStatus.OK", description = "OK")
    })
    @GetMapping("/api/account/re-issue")
    public ResponseEntity<LoginResponseDto> reIssue(@Email String userEmail, String refreshToken, HttpServletRequest request) {
        if(!jwtProvider.validateToken(request,refreshToken).isAuthenticated()){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        LoginResponseDto responseDto = accountJwtService.reIssueAccessToken(userEmail, refreshToken);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    //  회원가입 기능 구현
    @Operation(summary = "회원가입", description = "회원 가입")
    @ApiResponse(responseCode = "HttpStatus.CREATED", description = "CREATED")
    @PostMapping("/api/account/sign-up")
    public ResponseEntity<BasicResponse> signUp(@Valid SignUpRequestDto signUpUser) {
        accountSignUpService.signUp(signUpUser.getUserName(), signUpUser.getYear(), signUpUser.getUserEmail(), signUpUser.getPinCode(), signUpUser.getPassword());
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
        HttpHeaders httpHeaders = new HttpHeaders();
        model.addAttribute("requestUrl", requestUrl);
        log.info("requestUrl" + requestUrl);
//        로그인 페이지로 이동 로직 추가 예정
        httpHeaders.setLocation(URI.create(requestUrl));
        return new ResponseEntity<>(model, httpHeaders, HttpStatus.OK);
    }

    // 로그인 인증
    @Operation(summary = "로그인 페이지 처리", description = "로그인완료 후 원래 페이지로 이동")
    @ApiResponses({
            @ApiResponse(description = "access, refreshToken"),
            @ApiResponse(responseCode = "HttpStatus.OK", description = "로그인 내부 인증 처리")
    })
    @PostMapping("/api/account/login/process")
    public ResponseEntity<LoginResponseDto> login(@Valid LoginRequestDto loginDto) throws URISyntaxException {
        LoginResponseDto responseDto = accountJwtService.login(loginDto.getUserEmail(), loginDto.getPassword());
        URI redirectUri = new URI(loginDto.getRedirectUrl());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(redirectUri);
        log.info("redirectUrl" + redirectUri);
        return new ResponseEntity<>(responseDto, httpHeaders, HttpStatus.OK);
    }

    @Operation(summary = "토큰 사용가능 여부 확인", description = "Access Token 사용 가능 여부 확인")
    @ApiResponses({
            @ApiResponse(description = "access, refreshToken"),
            @ApiResponse(responseCode = "HttpStatus.OK", description = "OK")
    })
    @GetMapping("/api/account/re-check")
    public ResponseEntity<BasicResponse> checkValideToken(HttpServletRequest request, String refreshToken) {
        Authentication authentication = jwtProvider.validateToken(request, refreshToken);
        if (!authentication.isAuthenticated()) {

            return new ResponseEntity<>(HttpStatus.OK);
        }
//        토큰 형식이 잘못되면 BadRequest 반환 예외처리 추가예정

        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    // 로그인 인증
    @Operation(summary = "로그인 페이지 만료시간 포함 처리", description = "로그인완료 후 원래 페이지로 이동")
    @ApiResponses({
            @ApiResponse(description = "access, refreshToken"),
            @ApiResponse(responseCode = "HttpStatus.OK", description = "로그인 내부 인증 처리")
    })
    @PostMapping("/api/account/login/process/expired")
    public ResponseEntity<Map<Date,LoginResponseDto>> loginWithExpiredTime(@Valid LoginRequestDto loginDto) throws URISyntaxException {
        LoginResponseDto responseDto = accountJwtService.login(loginDto.getUserEmail(), loginDto.getPassword());
        Date expiredTime = jwtProvider.getExpiredTime(responseDto.getRefreshToken());
        URI redirectUri = new URI(loginDto.getRedirectUrl());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(redirectUri);
        log.info("redirectUrl" + redirectUri);
        Map<Date, LoginResponseDto> loginResponseDto = new HashMap<>();
        loginResponseDto.put(expiredTime, responseDto);
        return new ResponseEntity<>(loginResponseDto, httpHeaders, HttpStatus.OK);
    }

    /**
     *  Token 이 유효하지 않을 때 재요청 하는 로직
    * */

    /**
     *  Token 이 없을때 simple Request하는 로직
     * */

    /**
     * Token 요청에 따른 개인정보 요청
    * */

    /**
     * 토큰을 주면 uid 하나만 반환하는 simple 요청
    * */

    /**
     * 토큰 반환이 유효하지 않은 토큰을 줄때, 토큰재발행 -> 재요청 실시 요청
    * */

    /**
     * 토큰이 없어도 조회가 가능한 서비스 ( private 서비스 )
     * */
}
