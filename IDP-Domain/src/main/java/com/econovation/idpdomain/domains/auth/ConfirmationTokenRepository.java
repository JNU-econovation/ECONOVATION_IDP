package com.econovation.idpdomain.domains.auth;


import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken, Long> {
    Optional<ConfirmationToken> findByIdAndExpirationDateAfterAndExpired(
            UUID confirmationTokenId, LocalDateTime now, boolean expired);
}
