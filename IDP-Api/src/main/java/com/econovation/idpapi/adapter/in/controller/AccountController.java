package com.econovation.idpapi.adapter.in.controller;


import com.econovation.idpapi.application.port.in.AccountSignUpUseCase;
import com.econovation.idpapi.application.port.in.AccountUseCase;
import com.econovation.idpapi.application.service.AccountJwtService;
import com.econovation.idpapi.common.BasicResponse;
import com.econovation.idpcommon.exception.GetExpiredTimeException;
import com.econovation.idpcommon.jwt.JwtProvider;
import com.econovation.idpdomain.domains.dto.LoginRequestDto;
import com.econovation.idpdomain.domains.dto.LoginResponseDto;
import com.econovation.idpdomain.domains.dto.LoginResponseDtoWithExpiredTime;
import com.econovation.idpdomain.domains.dto.LoginResponseDtoWithRedirectUrl;
import com.econovation.idpdomain.domains.dto.SignUpRequestDto;
import com.econovation.idpdomain.domains.dto.UserResponseMatchedTokenDto;
import io.reactivex.rxjava3.annotations.Nullable;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api")
@Tag(name = "1. Account 관련 서비스", description = "회원가입, 로그인 등등")
public class AccountController {
    private final AccountUseCase accountUseCase;
    private final AccountSignUpUseCase accountSignUpUseCase;
    private final JwtProvider jwtProvider;
    private final AccountJwtService accountJwtService;

    @Value("${login.page.url}")
    private String loginPageUrl;

    //    로그아웃 기능 구현
    @Operation(summary = "logout", description = "logout_agent, go to redirect page when logout")
    @GetMapping("accounts/logout")
    public ResponseEntity<BasicResponse> logout(
            @RequestParam String redirectUrl, HttpServletRequest request)
            throws URISyntaxException, GetExpiredTimeException {
        String refreshToken = extractRefreshTokenFromHeader(request.getHeader("Authorization"));
        accountUseCase.logout(refreshToken);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(new URI(redirectUrl));
        BasicResponse result = new BasicResponse("로그아웃 성공", HttpStatus.OK);
        return ResponseEntity.ok().headers(httpHeaders).body(result);
    }

    @Operation(summary = "이메일 중복 검증", description = "이메일 중복 확인하기")
    @PostMapping("/accounts/duplicate")
    public ResponseEntity<BasicResponse> duplicateCheck(
            @RequestParam(value = "userEmail") String userEmail) {
        BasicResponse duplicateEmail = accountSignUpUseCase.isDuplicateEmail(userEmail);
        return new ResponseEntity<>(duplicateEmail, HttpStatus.OK);
    }

    @Operation(summary = "토큰 재발행", description = "Refresh, Access Token 재발행")
    @GetMapping("/accounts/re-issue")
    public ResponseEntity<LoginResponseDto> reIssue(
            String refreshToken, HttpServletRequest request) {
        if (!accountJwtService.validateToken(request, refreshToken).isAuthenticated()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        LoginResponseDto responseDto = accountUseCase.reIssueAccessToken(request, refreshToken);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @Operation(summary = "회원가입", description = "회원 가입")
    @PostMapping("/accounts/sign-up")
    public ResponseEntity<BasicResponse> signUp(SignUpRequestDto signUpUser) {
        accountSignUpUseCase.signUp(
                signUpUser.getUserName(),
                signUpUser.getYear(),
                signUpUser.getUserEmail(),
                signUpUser.getPassword());
        BasicResponse result = new BasicResponse("회원가입 성공", HttpStatus.OK);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @Operation(summary = "로그인 Agent URL 이동", description = "로그인 페이지로 이동")
    @GetMapping("/accounts/login")
    public ResponseEntity<?> login(
            @CookieValue(value = "refresh_token", required = false) String refreshToken,
            @RequestParam(value = "request-url") @Nullable String requestUrl,
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        if (refreshToken != null) {
            // 토큰 검증
            Authentication authentication = accountJwtService.validateToken(request, refreshToken);
            if (!authentication.isAuthenticated()) {
                // 검증 실패
                BasicResponse result = new BasicResponse("유효하지 않은 토큰입니다.", HttpStatus.OK);
                redirectLoginWithRequestUrl(response, requestUrl);
                return new ResponseEntity<>(result, HttpStatus.UNAUTHORIZED);
            }
            // 검증 성공 시 바로 redirect
            redirect(response, requestUrl);
            return new ResponseEntity<>(HttpStatus.PERMANENT_REDIRECT);
        }
        // 토큰이 null이면 loginPage로 redirect
        redirectLoginWithRequestUrl(response, requestUrl);
        return new ResponseEntity<>(HttpStatus.PERMANENT_REDIRECT);
    }

    private String extractRefreshTokenFromHeader(String authorizationHeader) {
        // Extract the token except the "Bearer " prefix
        return authorizationHeader.substring(7);
    }

    private void redirect(HttpServletResponse response, String requestUrl) throws IOException {
        response.sendRedirect(requestUrl);
    }

    private void redirectLoginWithRequestUrl(HttpServletResponse response, String requestUrl)
            throws IOException {
        String redirectUrl = loginPageUrl + (requestUrl == null ? "" : "?requestUrl=" + requestUrl);
        response.sendRedirect(redirectUrl);
    }

    @Operation(summary = "로그인 페이지 처리", description = "로그인 완료 후 access, refresh, redirectUrl 전송")
    @RequestMapping(
            value = "/accounts/login/process",
            method = {RequestMethod.GET, RequestMethod.OPTIONS})
    public ResponseEntity<LoginResponseDtoWithRedirectUrl> login(
            HttpServletResponse response, LoginRequestDto loginDto) {
        LoginResponseDto responseDto =
                accountUseCase.login(loginDto.getUserEmail(), loginDto.getPassword());
        String redirectUrl = loginDto.getRedirectUrl();
        // Cookie 삽입 (refreshToken)

        Cookie refreshTokenCookie = createRefreshTokenCookie(responseDto.getRefreshToken());
        response.addCookie(refreshTokenCookie);

        LoginResponseDtoWithRedirectUrl loginResponseDtoWithRedirectUrl =
                new LoginResponseDtoWithRedirectUrl(
                        responseDto.getAccessToken(), responseDto.getRefreshToken(), redirectUrl);

        return ResponseEntity.ok(loginResponseDtoWithRedirectUrl);
    }

    private Cookie createRefreshTokenCookie(String refreshToken) {
        Cookie cookie = new Cookie("refresh_token", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setSecure(true);
        return cookie;
    }

    @PostAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "토큰 사용가능 여부 확인", description = "유효하지 않은 토큰입니다, 유효한 토큰입니다 메시지 + 상태코드 반환")
    @GetMapping("/accounts/re-check")
    public ResponseEntity<BasicResponse> checkValideToken(
            HttpServletRequest request, String refreshToken) {
        Authentication authentication = accountJwtService.validateToken(request, refreshToken);
        if (!authentication.isAuthenticated()) {
            BasicResponse result = new BasicResponse("유효하지 않은 토큰입니다.", HttpStatus.OK);
            return new ResponseEntity<>(result, HttpStatus.UNAUTHORIZED);
        }
        BasicResponse result = new BasicResponse("유효한 토큰입니다.", HttpStatus.OK);
        return new ResponseEntity<>(result, HttpStatus.OK);

        //        토큰 형식이 잘못되면 BadRequest 반환 예외처리 추가예정
    }

    // 로그인 인증
    @Deprecated
    @Operation(summary = "로그인 페이지 만료시간 포함 처리", description = "로그인완료 후 원래 페이지로 이동")
    @PostMapping("/accounts/login/process/expired")
    public ResponseEntity<LoginResponseDtoWithExpiredTime> loginWithExpiredTime(
            @CookieValue String redirectUrl, LoginRequestDto loginDto)
            throws URISyntaxException, GetExpiredTimeException {
        LoginResponseDto responseDto =
                accountUseCase.login(loginDto.getUserEmail(), loginDto.getPassword());
        Date expiredTime = jwtProvider.getExpiredTime(responseDto.getRefreshToken());
        URI redirectUri = new URI(redirectUrl);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(redirectUri);
        LoginResponseDtoWithExpiredTime loginResponseDtoWithExpiredTime =
                new LoginResponseDtoWithExpiredTime(expiredTime, responseDto);
        return new ResponseEntity<>(loginResponseDtoWithExpiredTime, httpHeaders, HttpStatus.OK);
    }

    @Operation(
            summary = "토큰에 따른 유저 정보 조회",
            description = "accessToken에 일치하는 회원 조회 / accessToken을 Authorization에 넣어주세요")
    @GetMapping("/users/token")
    public ResponseEntity<UserResponseMatchedTokenDto> simpleRequest(HttpServletRequest request) {
        String accessToken = request.getHeader("Authorization").substring(7);
        UserResponseMatchedTokenDto byAccessToken = accountUseCase.findByAccessToken(accessToken);
        return new ResponseEntity<>(byAccessToken, HttpStatus.OK);
    }
}
