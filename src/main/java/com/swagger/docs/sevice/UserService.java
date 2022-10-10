package com.swagger.docs.sevice;

import com.swagger.docs.domain.user.Account;
import com.swagger.docs.domain.user.AccountRepository;
import com.swagger.docs.dto.UserCreateRequestDto;
import com.swagger.docs.dto.UserFindDto;
import com.swagger.docs.dto.UserPasswordUpdateDto;
import com.swagger.docs.dto.UserUpdateRequestDto;
import com.swagger.docs.global.common.exception.BadRequestException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class UserService implements UserDetailsService {

    private static final String NOT_FOUND_USER_MESSAGE = "해당 회원을 찾을 수 없습니다";
    private static final String NOT_FOUND_EMAIL_MESSAGE = "해당 이메일을 찾을 수 없습니다.";
    private static final String EXIST_ALREADY_USER_MESSAGE = "해당 이메일은 이미 회원가입 돼 있습니다.";
    private static final String NOT_CORRECT_USER_MESSAGE = "비밀번호나 이메일이 일치하지 않습니다.";
    private static final String OVERLAP_PASSWORD_MESSAGE = "기존의 비밀번호를 입력했습니다.";

    private final AccountRepository userRepository;
    private final ConfirmationTokenService confirmationTokenService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Account account = userRepository.findAccountByUserEmail(email)
                .orElseThrow(() -> new BadRequestException("토큰을 확인해보세요"));
        return new AuthAccount(account);
    }

    @Transactional
    public List<Account> findAll(){
        return userRepository.findAll();
    }

    @Transactional
    public Account setPassword(UserPasswordUpdateDto userPasswordUpdateDto){
        Account user = userRepository.findUserByUserNameAndYear(userPasswordUpdateDto.getUserName(),userPasswordUpdateDto.getYear());
        String encodedPassword = passwordEncoder.encode(userPasswordUpdateDto.getPassword());
        if(user.getPassword() == encodedPassword){
            throw new IllegalArgumentException(OVERLAP_PASSWORD_MESSAGE);
        }
        user.setPassword(encodedPassword);
        return userRepository.save(user);
    }

    /**
     * Get All Account
     * @param int : page, String role
     * @return Account
     */
    @Transactional
    public List<Account> findUserByRole(int page, String role){
        Pageable pageable = PageRequest.of(page, 8);
        return userRepository.findAll(pageable).stream().filter(u->u.getRole() == role).collect(Collectors.toList());
    }

    @Transactional
    public Long countAllUser(){
        return userRepository.count();
    }

    @Transactional
    public Long countUserByRole(String role){
        return userRepository.countAllByRole(role);
    }
    /**
     * Get Account By One userId
     * @param Long : userId
     * @return Account
     */
    @Transactional
    public Account findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(NOT_FOUND_USER_MESSAGE));
    }

    /**
     * Get Account By One userName
     * @param String : userName
     * @return List<UserResponseDto>
     * 동명이인이 있을 수 있어서 List를 받는다.
     */
    @Transactional
    public List<Account> findUserByUserName(String userName) {
        List<Account> users = userRepository.findByUserName(userName);
        if(users.isEmpty()){
            throw new IllegalArgumentException(NOT_FOUND_USER_MESSAGE);
        }
        return users;
    }

    public Account findUserByPinCode(String pinCode){
        return userRepository.findUserByPinCode(pinCode)
                .orElseThrow(() -> new IllegalArgumentException(NOT_FOUND_USER_MESSAGE));
    }

    @Transactional
    public Account findUserByYearAndUserName(String userName, Long year){
        List<Account> findUser = userRepository.findByUserName(userName).stream()
                .filter(m -> m.getYear().equals(year))
                .collect(Collectors.toList());
        if(findUser.isEmpty()){
            throw new IllegalArgumentException(NOT_CORRECT_USER_MESSAGE);
        }
        return findUser.stream().findFirst().get();
    }
    /**
     * Get Account By One userEmail
     * @param String : userEmail
     * @return Account
     */
    @Transactional
    public Account findUserByUserEmail(String userEmail) {
        return userRepository.findByUserEmail(userEmail).get();
    }
//    ----Account Authentication------------------------------------------------------------------



    /**
     * Auth Process : confirmEmail and make Auth process
     * @Param token : String!
     * @return vpod
     */
/**
    public void confirmEmail(String token) {
        UUID uuid = UUID.fromString(token);
        ConfirmationToken findConfirmationToken = confirmationTokenService.findByIdAndExpirationDateAfterAndExpired(uuid);
        log.info(String.valueOf(findConfirmationToken.getId()));
//        여기서 UserId가 Null Exception
        log.info(String.valueOf(findConfirmationToken.getUserId()));
        Account findUser = userRepository.findById(findConfirmationToken.getUserId())
                .orElseThrow(()->new IllegalArgumentException(NOT_FOUND_USER_MESSAGE));
        findConfirmationToken.useToken();	// 토큰 만료 로직을 구현해주면 된다. ex) expired 값을 true로 변경
        findUser.emailVerifiedSuccess();  // 유저의 이메일 인증 값 변경 로직을 구현해주면 된다. ex) emailVerified 값을 true로 변경
    }
*/


//    -------------------------------------------------------------------------------------
    /**
     * delete One Account Data
     * @Param userId
     * @return void
     */
    public void deleteUserById(final Long userId) {
//        관리자만 삭제할 수 있게 관리자 인증 추가 예정
        Account user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(NOT_FOUND_USER_MESSAGE));
        userRepository.delete(user);
    }

    /**
     * update One Account Data
     * @Param userEmail : String!, password : String!, year : Int!, userName : String!
     * @return boolean
     */
    public Account updateUser(Long userId, UserUpdateRequestDto userUpdateRequestDto) {
        Account user = userRepository.findById(userId)
                .orElseThrow(()->new IllegalArgumentException(NOT_FOUND_USER_MESSAGE));
        user.update(userUpdateRequestDto);
        return user;
    }

    /**
     * update Account's Role (ex. ADMIN -> USER,  GUEST -> USER)
     * @Param userId : Int!, role : Role!
     * @return enum Role
     */
    /**
     *
    public String updateUserRole(Long userId, String Role) {
        log.info("Role : " + this.Role + "-> " + Role);
        return Role;
    }
    */


}
