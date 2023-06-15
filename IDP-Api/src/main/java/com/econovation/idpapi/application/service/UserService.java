package com.econovation.idpapi.application.service;


import com.econovation.idpapi.application.port.in.UserUseCase;
import com.econovation.idpcommon.exception.BadRequestException;
import com.econovation.idpdomain.domains.dto.UserPasswordUpdateDto;
import com.econovation.idpdomain.domains.dto.UserUpdateRequestDto;
import com.econovation.idpdomain.domains.users.domain.Account;
import com.econovation.idpdomain.domains.users.domain.AccountRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class UserService implements UserUseCase {
    private static final String NOT_FOUND_USER_MESSAGE = "해당 회원을 찾을 수 없습니다";
    private static final String NOT_CORRECT_USER_MESSAGE = "비밀번호나 이메일이 일치하지 않습니다.";
    private static final String OVERLAP_PASSWORD_MESSAGE = "기존의 비밀번호를 입력했습니다.";

    private final AccountRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final Integer PAGE_PER_REQUEST = 1;
    private final Integer SIZE_BATCH_PAGE = 3;

    @Override
    @Transactional
    public List<Account> findAll(Integer page) {
        Pageable pageable = PageRequest.of(page, PAGE_PER_REQUEST);
        return userRepository.findAll(pageable).stream().collect(Collectors.toList());
    }

    @Transactional
    public Map<String, Object> findAllWithLastPageInPage(Integer page) {
        Pageable pageable = PageRequest.of(page, PAGE_PER_REQUEST);

        Slice<Account> usersWithPagination = userRepository.findSliceBy(pageable);
        log.info(" number : " + String.valueOf(usersWithPagination.getNumber()));
        log.info(" size : " + String.valueOf(usersWithPagination.getSize()));
        List<Account> users = usersWithPagination.stream().collect(Collectors.toList());
        Map<String, Object> map = new HashMap();
        map.put("users", users);
        //        map.put("maxPage",page + (users.size() / PAGE_PER_REQUEST));
        //        map.put("maxPage",totalPages);
        if (map.isEmpty()) throw new IllegalArgumentException(NOT_FOUND_USER_MESSAGE);
        return map;
    }

    @Transactional
    public Account setPassword(UserPasswordUpdateDto userPasswordUpdateDto) {
        Account user =
                findUserByYearAndUserName(
                        userPasswordUpdateDto.getUserName(), userPasswordUpdateDto.getYear());
        String encodedPassword = passwordEncoder.encode(userPasswordUpdateDto.getPassword());
        if (user.getPassword().equals(encodedPassword)) {
            throw new IllegalArgumentException(OVERLAP_PASSWORD_MESSAGE);
        }
        user.setPassword(encodedPassword);
        return userRepository.save(user);
    }

    /**
     * Get All Account
     * @return Account
     */
    @Transactional
    public List<Account> findUserByRole(int page, String role) {
        Pageable pageable = PageRequest.of(page, 8);
        return userRepository.findAll(pageable).stream()
                .filter(u -> u.getAccountRole().equals(role))
                .collect(Collectors.toList());
    }

    @Transactional
    public Long countAllUser() {
        return userRepository.count();
    }

    @Override
    @Transactional
    public Long countUserByRole(String role) {
        return userRepository.countAllByRole(role);
    }

    /**
     * Get Account By One userId
     * @return Account
     */
    @Transactional
    public Account findUserById(Long id) {
        return userRepository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException(NOT_FOUND_USER_MESSAGE));
    }

    /**
     * Get Account By One userName
     * @return List<UserResponseDto> 동명이인이 있을 수 있어서 List를 받는다.
     */
    @Transactional
    public List<Account> findUserByUserName(String userName) {
        List<Account> users = userRepository.findByUserName(userName);
        if (users.isEmpty()) {
            throw new IllegalArgumentException(NOT_FOUND_USER_MESSAGE);
        }
        return users;
    }

    @Override
    @Transactional
    public Account findUserByYearAndUserName(String userName, Integer year) {
        Account findUser =
                userRepository.findByUserName(userName).stream()
                        .filter(m -> m.getProfile().getYear().equals(year))
                        .collect(Collectors.toList())
                        .get(0);
        if (findUser == null) {
            throw new IllegalArgumentException(NOT_CORRECT_USER_MESSAGE);
        }
        return findUser;
    }
    /**
     * Get Account By One userEmail
     * @return Account
     */
    @Transactional
    public Account findUserByUserEmail(String idpId) {
        return userRepository
                .findById(Long.valueOf(idpId))
                .orElseThrow(() -> new BadRequestException("없는 이메일입니다."));
    }

    //    ----Account
    // Authentication------------------------------------------------------------------

    /**
     * Auth Process : confirmEmail and make Auth process @Param token : String!
     *
     * @return vpod
     */
    /**
     * public void confirmEmail(String token) { UUID uuid = UUID.fromString(token);
     * ConfirmationToken findConfirmationToken =
     * confirmationTokenService.findByIdAndExpirationDateAfterAndExpired(uuid);
     * log.info(String.valueOf(findConfirmationToken.getId())); // 여기서 UserId가 Null Exception
     * log.info(String.valueOf(findConfirmationToken.getUserId())); Account findUser =
     * userRepository.findById(findConfirmationToken.getUserId()) .orElseThrow(()->new
     * IllegalArgumentException(NOT_FOUND_USER_MESSAGE)); findConfirmationToken.useToken(); // 토큰 만료
     * 로직을 구현해주면 된다. ex) expired 값을 true로 변경 findUser.emailVerifiedSuccess(); // 유저의 이메일 인증 값 변경 로직을
     * 구현해주면 된다. ex) emailVerified 값을 true로 변경 }
     */

    //    -------------------------------------------------------------------------------------
    /**
     * delete One Account Data @Param userId
     *
     * @return void
     */
    public void deleteUserById(final Long userId) {
        //        관리자만 삭제할 수 있게 관리자 인증 추가 예정
        Account user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new IllegalArgumentException(NOT_FOUND_USER_MESSAGE));
        // 삭제 마킹하기
        user.isDeletedUser();
    }

    /**
     * update One Account Data @Param userEmail : String!, password : String!, year : Int!, userName
     * : String!
     *
     * @return boolean
     */
    @Override
    public Account updateUser(UserUpdateRequestDto userUpdateRequestDto) {
        Account user =
                userRepository
                        .findUserByUserNameAndYear(
                                userUpdateRequestDto.getUserName(), userUpdateRequestDto.getYear())
                        .orElseThrow(() -> new IllegalArgumentException(NOT_FOUND_USER_MESSAGE));

        user.update(userUpdateRequestDto);
        return user;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

    /**
     * update Account's Role (ex. ADMIN -> USER, GUEST -> USER) @Param userId : Int!, role : Role!
     *
     * @return enum Role
     */
    /**
     * public String updateUserRole(Long userId, String Role) { log.info("Role : " + this.Role + "->
     * " + Role); return Role; }
     */
}
