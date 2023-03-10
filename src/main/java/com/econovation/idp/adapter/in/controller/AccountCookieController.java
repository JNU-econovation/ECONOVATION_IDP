package com.econovation.idp.adapter.in.controller;

import com.econovation.idp.application.port.in.AccountUseCase;
import com.econovation.idp.application.port.in.JwtProviderUseCase;
import com.econovation.idp.domain.dto.LoginRequestDto;
import com.econovation.idp.domain.dto.LoginResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AccountCookieController{
    private final AccountUseCase accountUseCase;
    private Cookie[] cookies = null;
    private final JwtProviderUseCase jwtProviderUseCase;

    final long tokenInvalidTime = 1000L * 60 * 60; //1h

    @Operation(summary = "로그인 페이지 처리 ( 쿠키 버전 )", description = "로그인완료 후 원래 페이지로 이동",responses = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = String.class)))
    })
    @PostMapping("/api/accounts/cookie/login/process")
    public ResponseEntity<?> login(HttpServletResponse response, LoginRequestDto loginDto) throws URISyntaxException {
        accountUseCase.login(loginDto.getUserEmail(), loginDto.getPassword());
        LoginResponseDto responseDto = accountUseCase.login(loginDto.getUserEmail(), loginDto.getPassword());
        URI redirectUri = new URI(loginDto.getRedirectUrl());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(redirectUri);
        // create a cookie
        Cookie cookie = new Cookie("refreshToken",responseDto.getRefreshToken());
        // 7 day
        cookie.setMaxAge((int) tokenInvalidTime);
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);

        return new ResponseEntity<>(responseDto.getAccessToken(), httpHeaders, HttpStatus.OK);
    }
    @Operation(summary = "토큰 재발행", description = "Refresh, Access Token 재발행", responses = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = LoginResponseDto.class)))
    })
    @GetMapping("/api/accounts/cookie/re-issue")
    public ResponseEntity<LoginResponseDto> reIssue(HttpServletRequest request) {
        cookies = request.getCookies();
        String refreshToken = getCookieValue(request, "refreshToken");
        log.info(refreshToken);
        if(!jwtProviderUseCase.validateToken(request,refreshToken).isAuthenticated()){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        LoginResponseDto responseDto = accountUseCase.reIssueAccessToken(request, refreshToken);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }


    private String getCookieValue(HttpServletRequest req, String cookieName) {
        return Arrays.stream(req.getCookies())
                .filter(c -> c.getName().equals("refreshToken"))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }
}
