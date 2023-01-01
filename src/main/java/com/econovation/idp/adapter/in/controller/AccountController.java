package com.econovation.idp.adapter.in.controller;

import com.econovation.idp.application.port.in.AccountJwtUseCase;
import com.econovation.idp.application.port.in.AccountSignUpUseCase;
import com.econovation.idp.application.port.in.JwtProviderUseCase;
import com.econovation.idp.domain.dto.LoginRequestDto;
import com.econovation.idp.domain.dto.LoginResponseDto;
import com.econovation.idp.domain.dto.SignUpRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
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
    private final AccountJwtUseCase accountJwtUseCase;
    private final AccountSignUpUseCase accountSignUpUseCase;
    private final JwtProviderUseCase jwtProviderUseCase;

    //    로그아웃 기능 구현
    @Operation(summary = "logout", description = "로그아웃_에이전트, 로그아웃시 redirect 페이지로 이동")
    @ApiResponse(responseCode = "HttpStatus.OK", description = "OK")
    @GetMapping("/api/accounts/logout")
    public ResponseEntity<String> logout(String redirectUrl, HttpServletRequest request) throws URISyntaxException {
//        7번부터 빼야 bearer(+스페이스바) 빼고 토큰만 추출 가능
        String refreshToken = request.getHeader("Authorization").substring(7);

        accountJwtUseCase.logout(refreshToken);
        URI redirectUri = new URI(redirectUrl);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(redirectUri);
        log.info(redirectUrl + "으로 이동");
        return new ResponseEntity<>("로그아웃 성공", httpHeaders, HttpStatus.OK);
    }

    @Operation(summary = "토큰 재발행", description = "Refresh, Access Token 재발행")
    @ApiResponses({
            @ApiResponse(description = "access, refreshToken"),
            @ApiResponse(responseCode = "HttpStatus.OK", description = "OK")
    })
    @GetMapping("/api/accounts/re-issue")
    public ResponseEntity<LoginResponseDto> reIssue(@Email String userEmail, String refreshToken, HttpServletRequest request) {
        if(!jwtProviderUseCase.validateToken(request,refreshToken).isAuthenticated()){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        LoginResponseDto responseDto = accountJwtUseCase.reIssueAccessToken(userEmail, refreshToken);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    //  회원가입 기능 구현
    // 로그인 기능 구현

    @Operation(summary = "로그인 Agent URL 이동", description = "로그인 페이지로 이동")
    @ApiResponses({
            @ApiResponse(responseCode = "HttpStatus.OK", description = "Header.Location : requestUrl ")
    })
    @PostMapping("/api/accounts/login")
    public ResponseEntity<Model> login(String requestUrl, Model model) throws URISyntaxException {
        HttpHeaders httpHeaders = new HttpHeaders();
        model.addAttribute("requestUrl", requestUrl);
        log.info("requestUrl" + requestUrl);
//        로그인 페이지로 이동 로직 추가 예정
        httpHeaders.setLocation(URI.create(requestUrl));
        return new ResponseEntity<>(model, httpHeaders, HttpStatus.OK);
    }
    // 로그인 인증

    @Operation(summary = "회원가입", description = "회원 가입")
    @ApiResponse(responseCode = "HttpStatus.CREATED", description = "CREATED")
    @PostMapping("/api/accounts/sign-up")
    public ResponseEntity<String> signUp(@Valid SignUpRequestDto signUpUser) {
        accountSignUpUseCase.signUp(signUpUser.getUserName(), signUpUser.getYear(), signUpUser.getUserEmail(), signUpUser.getPassword());
        return new ResponseEntity<>("회원가입 성공", HttpStatus.CREATED);
    }
    @Operation(summary = "로그인 페이지 처리", description = "로그인완료 후 원래 페이지로 이동")
    @ApiResponses({
            @ApiResponse(description = "access, refreshToken"),
            @ApiResponse(responseCode = "HttpStatus.OK", description = "로그인 내부 인증 처리")
    })
    @PostMapping("/api/accounts/login/process")
    public ResponseEntity<Map<String,Object>> login(LoginRequestDto loginDto) throws URISyntaxException {
        LoginResponseDto responseDto = accountJwtUseCase.login(loginDto.getUserEmail(), loginDto.getPassword());
        URI redirectUri = new URI(loginDto.getRedirectUrl());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(redirectUri);
        log.info("redirectUrl" + redirectUri);
        Date expiredTime = jwtProviderUseCase.getExpiredTime(responseDto.getRefreshToken());

        Map<String, Object> loginResponseDto = new HashMap<>();
        loginResponseDto.put("token",responseDto);
        loginResponseDto.put("expiredTime", expiredTime);
        return new ResponseEntity<>(loginResponseDto, httpHeaders, HttpStatus.OK);
    }

    @PostAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "토큰 사용가능 여부 확인", description = "Access Token 사용 가능 여부 확인")
    @ApiResponses({
            @ApiResponse(description = "access, refreshToken"),
            @ApiResponse(responseCode = "HttpStatus.OK", description = "OK")
    })
    @GetMapping("/api/accounts/re-check")
    public ResponseEntity<String> checkValideToken(HttpServletRequest request, String refreshToken) {
        Authentication authentication = jwtProviderUseCase.validateToken(request, refreshToken);
        if (!authentication.isAuthenticated()) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
//        토큰 형식이 잘못되면 BadRequest 반환 예외처리 추가예정
        return new ResponseEntity<>("유효하지 않은 토큰입니다.",HttpStatus.UNAUTHORIZED);
    }

    // 로그인 인증
    @Operation(summary = "로그인 페이지 만료시간 포함 처리", description = "로그인완료 후 원래 페이지로 이동")
    @ApiResponses({
            @ApiResponse(description = "access, refreshToken"),
            @ApiResponse(responseCode = "HttpStatus.OK", description = "로그인 내부 인증 처리")
    })
    @PostMapping("/api/accounts/login/process/expired")
    public ResponseEntity<Map<Date,LoginResponseDto>> loginWithExpiredTime(@Valid LoginRequestDto loginDto) throws URISyntaxException {
        LoginResponseDto responseDto = accountJwtUseCase.login(loginDto.getUserEmail(), loginDto.getPassword());
        Date expiredTime = jwtProviderUseCase.getExpiredTime(responseDto.getRefreshToken());
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
