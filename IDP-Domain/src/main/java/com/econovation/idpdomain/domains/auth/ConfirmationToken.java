package com.econovation.idpdomain.domains.auth;


import com.econovation.idpdomain.domains.users.domain.BaseTimeEntity;
import java.time.LocalDateTime;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Entity
@Getter
@Slf4j
@NoArgsConstructor
public class ConfirmationToken extends BaseTimeEntity {

    private static final long EMAIL_TOKEN_EXPIRATION_TIME_VALUE = 100L;

    @Id
    @Column(columnDefinition = "BINARY(16)", length = 16, nullable = false, unique = true)
    private UUID id;

    @Column private LocalDateTime expirationDate;

    @Column private Boolean expired;

    // FK 사용하지 않고 INPUT으로 받고
    @Column private Long userId;

    public ConfirmationToken(Long userId) {
        this.id = UUID.randomUUID();
        this.expirationDate = LocalDateTime.now().plusMinutes(EMAIL_TOKEN_EXPIRATION_TIME_VALUE);
        this.expired = Boolean.FALSE;
        this.userId = userId;
    }

    /** 이메일 토큰 생성 로직 @Param userId */
    //    public ConfirmationToken createEmailConfirmationToken(Long userId) {
    //        ConfirmationToken confirmationToken = new ConfirmationToken();
    //        confirmationToken.expirationDate =
    //                LocalDateTime.now().plusMinutes(EMAIL_TOKEN_EXPIRATION_TIME_VALUE);
    //        confirmationToken.id = UUID.randomUUID();
    //        confirmationToken.expired = false;
    //        confirmationToken.userId = userId;
    //        return confirmationToken;
    //    }
    /** 토큰 사용 만료 */
    public void useToken() {
        this.expired = true;
    }

    @Override
    public String toString() {
        return "ConfirmationToken{"
                + "id="
                + id
                + ", expirationDate="
                + expirationDate
                + ", expired="
                + expired
                + ", userId="
                + userId
                + '}';
    }
}
