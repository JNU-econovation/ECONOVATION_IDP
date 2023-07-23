package com.econovation.idpdomain.domains.images;


import com.econovation.idpdomain.domains.users.domain.Account;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findByAccount(Account account);
}
