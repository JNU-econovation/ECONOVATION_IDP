package com.econovation.idpapi.adapter.in.controller;


import com.econovation.idpapi.application.port.in.UserUseCase;
import com.econovation.idpdomain.domains.dto.NonAccountResponseDto;
import com.econovation.idpdomain.domains.users.domain.Accounts;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@Slf4j
@RestController
@Transactional(rollbackFor = Exception.class)
public class NonUserController {
    private final UserUseCase userService;
    //    private final JwtProviderUseCase jwtProviderUseCase;
    @Operation(summary = "findUserById", description = "Id로 회원조회")
    @ApiResponses({@ApiResponse(responseCode = "Account Object", description = "검색 유저 return")})
    @GetMapping("/non/api/users/{user-id}")
    public ResponseEntity<NonAccountResponseDto> findUserById(
            @PathVariable(value = "user-id") Long userId) {
        HttpHeaders headers = new HttpHeaders();
        Accounts account = userService.findUserById(userId);
        NonAccountResponseDto nonAccountResponseDto = account.toNonLoginUser(account);
        return new ResponseEntity<>(nonAccountResponseDto, headers, HttpStatus.OK);
    }
    //
    //    @Operation(summary = "countUserByRole", description = "Role(USER,GUEST,ADMIN)로 회원수 조회")
    //    @ApiResponses({
    //            @ApiResponse(description = "Role에 따른 회원 조횐 return")
    //    })
    //    @GetMapping("/api/users/count/{role}")
    //    public ResponseEntity<Long> countUserByRole(@PathVariable String role){
    //        Long numberIsRole = userService.countUserByRole(role);
    //        return new ResponseEntity<>(numberIsRole, HttpStatus.OK);
    //    }
    //
    //
    //    @Operation(summary = "전체 회원 수 조회", description = "전체 회원 수 조회")
    //    @ApiResponses({
    //            @ApiResponse(description = "Role에 따른 회원 조횐 return")
    //    })
    //    @GetMapping("/api/users/count")
    //    public ResponseEntity<Long> countAllUser(){
    //        Long numberAllAccount = userService.countAllUser();
    //        return new ResponseEntity<>(numberAllAccount, HttpStatus.OK);
    //    }
    //
    //
    //    @Operation(summary = "이름으로 회원 조회", description = "동명이인 포함 회원 정보 조회")
    //    @ApiResponses({
    //            @ApiResponse(description = "이름으로 회원 조회")
    //    })
    //    @GetMapping("/api/users/{userName}")
    //    public ResponseEntity<List<Account>> findUserByUserName(@PathVariable String userName){
    //        List<Account> userListByUserName = userService.findUserByUserName(userName);
    //        return new ResponseEntity<>(userListByUserName, HttpStatus.OK);
    //    }
    //
    //
    //    @Operation(summary = "Role으로 회원 조회", description = "Role(USER,GUEST,ADMIN)로 회원정보 조회 with
    // Page")
    //    @ApiResponses({
    //            @ApiResponse(description = "Role에 따른 회원 조횐 return")
    //    })
    //    @GetMapping("/api/users/role/{page}/{role}")
    //    public ResponseEntity<List<Account>> findUserByRole(@PathVariable int page, @PathVariable
    // String role){
    //        List<Account> userByRole = userService.findUserByRole(page, role);
    //        return new ResponseEntity<>(userByRole, HttpStatus.OK);
    //    }
    //
    //
    //    @Operation(summary = "Email 찾기 기능", description = "Email 찾기 ( 기수, 이름 ) 으로 조회")
    //    @ApiResponses({
    //            @ApiResponse(description = "Email에 따른 회원 조횐 return")
    //    })
    //    @GetMapping("/api/users/find-email/")
    //    public ResponseEntity<String> findEmail(@Valid UserFindDto userFindDto){
    //        Account userByYearAndUserName =
    // userService.findUserByYearAndUserName(userFindDto.getUserName(), userFindDto.getYear());
    //        return new ResponseEntity<>(userByYearAndUserName.getUserEmail(), HttpStatus.OK);
    //    }
    //
    //    @Operation(summary = "Email로 회원 조회", description = "이메일로 회원 조회")
    //    @ApiResponses({
    //            @ApiResponse(description = "Role에 따른 회원 조횐 return")
    //    })
    //    @GetMapping("/api/users/{userEmail}")
    //    public ResponseEntity<Account> findUserByEmail(@PathVariable String userEmail) {
    //        Account userByUserEmail = userService.findUserByUserEmail(userEmail);
    //        return new ResponseEntity<>(userByUserEmail,HttpStatus.OK);
    //    }
    //
    //    @Operation(summary = "회원정보 수정", description = "로그인된 상태에서, 회원정보 수정")
    //    @ApiResponses({
    //            @ApiResponse(description = "수정된 회원 조회 return")
    //    })
    //    @PostMapping("/api/users/")
    //    public ResponseEntity<Account> updateUser(HttpServletRequest request, UserUpdateRequestDto
    // userUpdateRequestDto) {
    //        String accessToken = request.getHeader("Authorization").substring(7);
    //
    //        if(!jwtProviderUseCase.validateToken(request,accessToken).isAuthenticated()){
    //            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    //        }
    //        return new
    // ResponseEntity<>(userService.updateUser(userUpdateRequestDto),HttpStatus.OK);
    //    }
    //
    //    @Operation(summary = "회원삭제", description = "회원 삭제")
    //    @ApiResponses({
    //            @ApiResponse(description = "회원 삭제 Response")
    //    })
    //    @DeleteMapping("/api/users/{userId}")
    //    public ResponseEntity<BasicResponse> deleteUser(@PathVariable Long userId) {
    //        userService.deleteUserById(userId);
    //        BasicResponse successDeleteResponse = new BasicResponse("회원 삭제 성공", HttpStatus.OK);
    //        return new ResponseEntity<>(successDeleteResponse, HttpStatus.OK);
    //    }
    //
    //    @Operation(summary = "비밀번호 수정", description = "비밀번호 수정")
    //    @ApiResponses({
    //            @ApiResponse(description = "이메일 보낸 인증 Code")
    //    })
    //    @PostMapping("/api/users/update/password")
    //    public ResponseEntity<Account> setPassword(@Valid UserPasswordUpdateDto
    // userPasswordUpdateDto){
    //        Account account = userService.setPassword(userPasswordUpdateDto);
    //        return new ResponseEntity<>(account, HttpStatus.OK);
    //    }
}
