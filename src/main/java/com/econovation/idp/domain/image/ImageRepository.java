package com.econovation.idp.domain.image;


import com.econovation.idp.domain.user.Account;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findByAccount(Account account);
}
