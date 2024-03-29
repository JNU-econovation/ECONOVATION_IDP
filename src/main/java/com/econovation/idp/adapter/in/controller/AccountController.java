package com.econovation.idp.adapter.in.controller;

import com.econovation.idp.application.port.in.AccountSignUpUseCase;
import com.econovation.idp.application.port.in.AccountUseCase;
import com.econovation.idp.application.port.in.JwtProviderUseCase;
import com.econovation.idp.domain.dto.*;
import com.econovation.idp.global.common.BasicResponse;
import com.econovation.idp.global.common.exception.GetExpiredTimeException;
import io.micrometer.core.lang.Nullable;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://auth.econovation.kr, http://127.0.0.1:3000",maxAge = 3600)
@RequestMapping("/api")
@Tag(name = "Account 관련 서비스", description = "회원가입, 로그인 등등")
public class AccountController {
    private final AccountUseCase accountUseCase;
    private final AccountSignUpUseCase accountSignUpUseCase;
    private final JwtProviderUseCase jwtProviderUseCase;
    @Value("${login.page.url}")
    private String loginPageUrl;

    //    로그아웃 기능 구현
    @Operation(summary = "logout", description = "로그아웃_에이전트, 로그아웃시 redirect 페이지로 이동",responses = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = BasicResponse.class))
            )})
    @ApiResponse(responseCode = "HttpStatus.OK", description = "OK")
    @GetMapping("/accounts/logout")
    public ResponseEntity<BasicResponse> logout(String redirectUrl, HttpServletRequest request) throws URISyntaxException, GetExpiredTimeException {
//        7번부터 빼야 bearer(+스페이스바) 빼고 토큰만 추출 가능
        String refreshToken = request.getHeader("Authorization").substring(7);

        accountUseCase.logout(refreshToken);
        URI redirectUri = new URI(redirectUrl);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(redirectUri);
        log.info(redirectUrl + "으로 이동");
        BasicResponse result = new BasicResponse("로그아웃 성공", HttpStatus.OK);
        return new ResponseEntity<>(result, httpHeaders, HttpStatus.OK);
    }

    @Operation(summary = "이메일 중복 검증", description = "이메일 중복 확인하기", responses = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = BasicResponse.class)))
    })
    @PostMapping("/accounts/duplicate")
    public ResponseEntity<BasicResponse> duplicateCheck(@RequestParam(value = "userEmail") String userEmail) {
        BasicResponse duplicateEmail = accountSignUpUseCase.isDuplicateEmail(userEmail);
        return new ResponseEntity<>(duplicateEmail, HttpStatus.OK);
    }

    @Operation(summary = "토큰 재발행", description = "Refresh, Access Token 재발행", responses = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = LoginResponseDto.class)))
    })
    @GetMapping("/accounts/re-issue")
    public ResponseEntity<LoginResponseDto> reIssue(String refreshToken, HttpServletRequest request) {
        if(!jwtProviderUseCase.validateToken(request,refreshToken).isAuthenticated()){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        LoginResponseDto responseDto = accountUseCase.reIssueAccessToken(request, refreshToken);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @Operation(summary = "회원가입", description = "회원 가입", responses = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = BasicResponse.class)))
    })
    @PostMapping("/accounts/sign-up")
    public ResponseEntity<BasicResponse> signUp(SignUpRequestDto signUpUser) {
        accountSignUpUseCase.signUp(signUpUser.getUserName(), signUpUser.getYear(), signUpUser.getUserEmail(), signUpUser.getPassword());
        BasicResponse result = new BasicResponse("회원가입 성공", HttpStatus.OK);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }
    @Operation(summary = "로그인 Agent URL 이동", description = "로그인 페이지로 이동", responses = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = void.class)))
    })
    @GetMapping("/accounts/login")
    public ResponseEntity<?> login(@CookieValue(value = "REFRESH_TOKEN",required = false) String refreshToken , @Valid @Nullable String requestUrl, HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 기존 requestUrl을 쿠키로 설정한다.
        HttpHeaders httpHeaders = new HttpHeaders();
        // token이 있으면 검증 후 요청 url 으로 바로 redirect
        if (refreshToken != null) {
            // 검증되지 않으면
            Authentication authentication = jwtProviderUseCase.validateToken(request, refreshToken);
            if (!authentication.isAuthenticated()) {
                BasicResponse result = new BasicResponse("유효하지 않은 토큰입니다.", HttpStatus.OK);
                response.sendRedirect(loginPageUrl +
                        (requestUrl == null
                        ? ""
                        : "?requestUrl=" + requestUrl));
                return new ResponseEntity<>(result,HttpStatus.UNAUTHORIZED);
            }
            // 검증 성공시 바로 redirect
            response.sendRedirect(requestUrl);
            return new ResponseEntity<>(httpHeaders, HttpStatus.PERMANENT_REDIRECT);
        }
//        token이 null이면 loginPage로 redirect
        response.sendRedirect(loginPageUrl + (requestUrl == null
                ? ""
                : "?requestUrl=" + requestUrl));
        return new ResponseEntity<>(httpHeaders, HttpStatus.PERMANENT_REDIRECT);
    }

    @Operation(summary = "로그인 페이지 처리", description = "로그인완료 후 access,refresh,redirectUrl 전송",responses = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = LoginResponseDtoWithExpiredTime.class)))
    })
    @RequestMapping(value = "/accounts/login/process", method = {RequestMethod.GET,RequestMethod.OPTIONS})
    public ResponseEntity<?> login(HttpServletResponse response,LoginRequestDto loginDto) {
        LoginResponseDto responseDto = accountUseCase.login(loginDto.getUserEmail(), loginDto.getPassword());
        String redirectUrl = loginDto.getRedirectUrl();
        // Cookie 삽입 ( refreshToken )
        LoginResponseDtoWithRedirectUrl loginResponseDtoWithRedirectUrl = new LoginResponseDtoWithRedirectUrl(responseDto.getAccessToken(), responseDto.getRefreshToken(), redirectUrl);
        Cookie cookie = new Cookie("refresh_token", responseDto.getRefreshToken());
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setSecure(true);
        response.addCookie(cookie);

        Map<String, String> accessToken = new HashMap<>();
        accessToken.put("accessToken",loginResponseDtoWithRedirectUrl.getAccessToken());
        return new ResponseEntity<>(accessToken, HttpStatus.OK);
    }

    @PostAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "토큰 사용가능 여부 확인", description = "유효하지 않은 토큰입니다, 유효한 토큰입니다 메시지 + 상태코드 반환",responses = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = BasicResponse.class)))
    })
    @GetMapping("/accounts/re-check")
    public ResponseEntity<BasicResponse> checkValideToken(HttpServletRequest request, String refreshToken) {
        Authentication authentication = jwtProviderUseCase.validateToken(request, refreshToken);
        if (!authentication.isAuthenticated()) {
            BasicResponse result = new BasicResponse("유효하지 않은 토큰입니다.", HttpStatus.OK);
            return new ResponseEntity<>(result,HttpStatus.UNAUTHORIZED);
        }
        BasicResponse result = new BasicResponse("유효한 토큰입니다.", HttpStatus.OK);
        return new ResponseEntity<>(result,HttpStatus.OK);

//        토큰 형식이 잘못되면 BadRequest 반환 예외처리 추가예정
    }

    // 로그인 인증
    @Operation(summary = "로그인 페이지 만료시간 포함 처리", description = "로그인완료 후 원래 페이지로 이동",responses = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = LoginResponseDtoWithExpiredTime.class))
            )})
    @PostMapping("/accounts/login/process/expired")
    public ResponseEntity<LoginResponseDtoWithExpiredTime> loginWithExpiredTime(@CookieValue String redirectUrl,LoginRequestDto loginDto) throws URISyntaxException, GetExpiredTimeException {
        LoginResponseDto responseDto = accountUseCase.login(loginDto.getUserEmail(), loginDto.getPassword());
        Date expiredTime = jwtProviderUseCase.getExpiredTime(responseDto.getRefreshToken());
        URI redirectUri = new URI(redirectUrl);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(redirectUri);
        LoginResponseDtoWithExpiredTime loginResponseDtoWithExpiredTime = new LoginResponseDtoWithExpiredTime(expiredTime, responseDto);
        return new ResponseEntity<>(loginResponseDtoWithExpiredTime, httpHeaders, HttpStatus.OK);
    }


    @Operation(summary = "토큰에 따른 유저 정보 조회", description = "accessToken에 일치하는 회원 조회 / accessToken을 Authorization에 넣어주세요")
    @ApiResponses({
            @ApiResponse(description = "이름/기수/uid/email 반환")
    })
    @GetMapping("/users/token")
    public ResponseEntity<UserResponseMatchedTokenDto> simpleRequest(HttpServletRequest request){
        String accessToken = request.getHeader("Authorization").substring(7);
        UserResponseMatchedTokenDto byAccessToken = accountUseCase.findByAccessToken(accessToken);
        return new ResponseEntity<>(byAccessToken, HttpStatus.OK);
    }

    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .path("/")
                .sameSite("None")
                .httpOnly(false)
                .secure(false)
                .maxAge(maxAge)
                .build();
        addSameSiteCookieAttribute(response);
        response.addHeader("Set-Cookie", cookie.toString());
    }
    private static void addSameSiteCookieAttribute(HttpServletResponse response) {
        Collection<String> headers = response.getHeaders(HttpHeaders.SET_COOKIE);
        boolean firstHeader = true;
        // there can be multiple Set-Cookie attributes
        for (String header : headers) {
            if (firstHeader) {
                response.setHeader(HttpHeaders.SET_COOKIE,
                        String.format("%s; %s", header, "SameSite=None"));
                firstHeader = false;
                continue;
            }
            response.addHeader(HttpHeaders.SET_COOKIE,
                    String.format("%s; %s", header, "SameSite=None"));
        }
    }
}

