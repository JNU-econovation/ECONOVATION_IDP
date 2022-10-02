package com.swagger.docs.domain.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    List<Account> findAll();
    boolean existsAccountByUserEmail(String userEmail);

    Optional<Account> findAccountByUserEmail(String userEmail);

    @Query("SELECT u FROM Account u WHERE u.userName = :userName")
    List<Account> findByUserName(@Param("userName") String userName);

    @Query("SELECT u FROM Account u WHERE u.userEmail = :userEmail")
    Optional<Account> findByUserEmail(@Param("userEmail")String userEmail);

    @Query("SELECT u FROM Account u WHERE u.pinCode = :pinCode")
    Optional<Account> findUserByPinCode(@Param("pinCode")String pinCode);

    Page<Account> findAll(Pageable pageable);

    Long countAllByRole(String role);

    @Query("SELECT u FROM Account u WHERE u.password = :password")
    Optional<Account> findByPassword(@Param("password")String password);

    Account findUserByUserNameAndYear(String userName, Long Year);

    /* 유효성 검사 - 중복 체크
     * 중복 : true
     * 중복이 아닌 경우 : false
     */
}
