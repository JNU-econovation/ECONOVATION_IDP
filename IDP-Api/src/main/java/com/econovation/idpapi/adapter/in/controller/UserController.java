package com.econovation.idpapi.adapter.in.controller;


import com.econovation.idpapi.application.port.in.JwtProviderUseCase;
import com.econovation.idpapi.application.port.in.UserUseCase;
import com.econovation.idpapi.common.BasicResponse;
import com.econovation.idpdomain.domains.dto.UserFindDto;
import com.econovation.idpdomain.domains.dto.UserPasswordUpdateDto;
import com.econovation.idpdomain.domains.dto.UserUpdateRequestDto;
import com.econovation.idpdomain.domains.users.domain.Account;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@AllArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "WebApplication User 제공 서비스", description = "유저 정보 조회")
public class UserController {
    private final UserUseCase userService;
    private final JwtProviderUseCase jwtProviderUseCase;

    @GetMapping("/api/users/page/{page}")
    public ResponseEntity<List<Account>> findUserAll(@PathVariable int page) {
        List<Account> listAccount = userService.findAll(page);
        return new ResponseEntity<>(listAccount, HttpStatus.OK);
    }

    @Operation(summary = "maxPage를 포함한 페이징 회원 조회", description = "isMaxpage = true : 전체 페이")
    @ApiResponses({@ApiResponse(description = "Role에 따른 회원 조횐 return")})
    @GetMapping("/api/users/page/{page}/{isMaxpage}")
    public ResponseEntity<Map> findUserAll(
            @PathVariable int page, @PathVariable boolean isMaxpage) {
        if (page <= 0) throw new IllegalArgumentException("1 이상의 페이지를 입력해주세요");
        if (isMaxpage) {
            Map listAccount = userService.findAllWithLastPageInPage(page - 1);
            return new ResponseEntity<>(listAccount, HttpStatus.OK);
        }
        findUserAll(page);
        return null;
    }

    @Operation(summary = "findUserById", description = "Id로 회원조회")
    @ApiResponses({@ApiResponse(responseCode = "Account Object", description = "검색 유저 return")})
    @GetMapping("/api/users/{user-id}")
    public ResponseEntity<Account> findUserById(@PathVariable(value = "user-id") Long userId) {
        HttpHeaders headers = new HttpHeaders();
        Account account = userService.findUserById(userId);
        return new ResponseEntity<>(account, headers, HttpStatus.OK);
    }

    @Operation(summary = "countUserByRole", description = "Role(USER,GUEST,ADMIN)로 회원수 조회")
    @ApiResponses({@ApiResponse(description = "Role에 따른 회원 조횐 return")})
    @GetMapping("/api/users/count/{role}")
    public ResponseEntity<Long> countUserByRole(@PathVariable String role) {
        Long numberIsRole = userService.countUserByRole(role);
        return new ResponseEntity<>(numberIsRole, HttpStatus.OK);
    }

    @Operation(summary = "전체 회원 수 조회", description = "전체 회원 수 조회")
    @ApiResponses({@ApiResponse(description = "Role에 따른 회원 조횐 return")})
    @GetMapping("/api/users/count")
    public ResponseEntity<Long> countAllUser() {
        Long numberAllAccount = userService.countAllUser();
        return new ResponseEntity<>(numberAllAccount, HttpStatus.OK);
    }

    @Operation(summary = "이름으로 회원 조회", description = "동명이인 포함 회원 정보 조회")
    @ApiResponses({@ApiResponse(description = "이름으로 회원 조회")})
    @GetMapping("/api/users")
    public ResponseEntity<List<Account>> findUserByUserName(String userName) {
        List<Account> userListByUserName = userService.findUserByUserName(userName);
        return new ResponseEntity<>(userListByUserName, HttpStatus.OK);
    }

    @Operation(summary = "Role으로 회원 조회", description = "Role(USER,GUEST,ADMIN)로 회원정보 조회 with Page")
    @ApiResponses({@ApiResponse(description = "Role에 따른 회원 조횐 return")})
    @GetMapping("/api/users/role/{page}/{role}")
    public ResponseEntity<List<Account>> findUserByRole(
            @PathVariable int page, @PathVariable String role) {
        List<Account> userByRole = userService.findUserByRole(page, role);
        return new ResponseEntity<>(userByRole, HttpStatus.OK);
    }

    @Operation(summary = "Email 찾기 기능", description = "Email 찾기 ( 기수, 이름 ) 으로 조회")
    @ApiResponses({@ApiResponse(description = "Email에 따른 회원 조횐 return")})
    @GetMapping("/api/users/find-email/")
    public ResponseEntity<String> findEmail(@Valid UserFindDto userFindDto) {
        Account userByYearAndUserName =
                userService.findUserByYearAndUserName(
                        userFindDto.getUserName(), userFindDto.getYear());
        return new ResponseEntity<>(userByYearAndUserName.getProfile().getEmail(), HttpStatus.OK);
    }

    //    @Operation(summary = "Email로 회원 조회", description = "이메일로 회원 조회")
    //    @ApiResponses({
    //            @ApiResponse(description = "Role에 따른 회원 조횐 return")
    //    })
    //    @GetMapping("/api/users/{userEmail}")
    //    public ResponseEntity<Account> findUserByEmail(@PathVariable String userEmail) {
    //        Account userByUserEmail = userService.findUserByUserEmail(userEmail);
    //        return new ResponseEntity<>(userByUserEmail,HttpStatus.OK);
    //    }

    @Operation(summary = "회원정보 수정", description = "로그인된 상태에서, 회원정보 수정")
    @ApiResponses({@ApiResponse(description = "수정된 회원 조회 return")})
    @PostMapping("/api/users/")
    public ResponseEntity<Account> updateUser(
            HttpServletRequest request, UserUpdateRequestDto userUpdateRequestDto) {
        String accessToken = request.getHeader("Authorization").substring(7);

        if (!jwtProviderUseCase.validateToken(request, accessToken).isAuthenticated()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(userService.updateUser(userUpdateRequestDto), HttpStatus.OK);
    }

    @Operation(summary = "회원삭제", description = "회원 삭제")
    @ApiResponses({@ApiResponse(description = "회원 삭제 Response")})
    @DeleteMapping("/api/users/{userId}")
    public ResponseEntity<BasicResponse> deleteUser(@PathVariable Long userId) {
        userService.deleteUserById(userId);
        BasicResponse successDeleteResponse = new BasicResponse("회원 삭제 성공", HttpStatus.OK);
        return new ResponseEntity<>(successDeleteResponse, HttpStatus.OK);
    }

    @Operation(summary = "비밀번호 수정", description = "비밀번호 수정")
    @ApiResponses({@ApiResponse(description = "이메일 보낸 인증 Code")})
    @PostMapping("/api/users/update/password")
    public ResponseEntity<Account> setPassword(@Valid UserPasswordUpdateDto userPasswordUpdateDto) {
        Account account = userService.setPassword(userPasswordUpdateDto);
        return new ResponseEntity<>(account, HttpStatus.OK);
    }
}
