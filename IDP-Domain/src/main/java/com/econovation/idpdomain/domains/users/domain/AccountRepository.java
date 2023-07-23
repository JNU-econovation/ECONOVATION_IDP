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

    @Query(
            "SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Account a WHERE a.profile.email = :email")
    boolean existsAccountByUserEmail(@Param("email") String email);

    @Query("SELECT u FROM Account u WHERE u.profile.name = :name")
    List<Account> findByUserName(@Param("name") String name);

    @Query("SELECT u FROM Account u WHERE u.profile.email = :email")
    Optional<Account> findByUserEmail(@Param("email") String email);

    Page<Account> findAll(Pageable pageable);

    Slice<Account> findSliceBy(Pageable pageable);

    @Query("SELECT COUNT(a) FROM Account a WHERE a.accountRole = :role")
    Long countAllByRole(@Param("role") AccountRole role);

    @Query("SELECT u FROM Account u WHERE u.password = :password")
    Optional<Account> findByPassword(@Param("password") String password);

    @Query("SELECT u FROM Account u WHERE u.profile.name = :name AND u.profile.year = :year")
    Optional<Account> findUserByUserNameAndYear(
            @Param("name") String name, @Param("year") Integer year);

    /* 유효성 검사 - 중복 체크
     * 중복 : true
     * 중복이 아닌 경우 : false
     */
}
