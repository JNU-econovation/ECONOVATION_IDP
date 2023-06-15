package com.econovation.idpdomain.domains.users.domain;


import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    List<Account> findAll();

    boolean existsAccountByUserEmail(String userEmail);

    //    Optional<Account> findByUserEmail(String userEmail);

    @Query("SELECT u FROM Account u WHERE u.userName = :userName")
    List<Account> findByUserName(@Param("userName") String userName);

    //    @Query("SELECT u FROM Account u WHERE u.userEmail = :userEmail")
    //    Optional<Account> findByUserEmail(@Param("userEmail") String userEmail);

    Optional<Account> findByUserEmail(String userEmail);

    Page<Account> findAll(Pageable pageable);

    Slice<Account> findSliceBy(Pageable pageable);

    Long countAllByRole(String role);

    @Query("SELECT u FROM Account u WHERE u.password = :password")
    Optional<Account> findByPassword(@Param("password") String password);

    Optional<Account> findUserByUserNameAndYear(String userName, Integer year);

    Optional<Account> findById(Long idpId);

    /* 유효성 검사 - 중복 체크
     * 중복 : true
     * 중복이 아닌 경우 : false
     */
}
