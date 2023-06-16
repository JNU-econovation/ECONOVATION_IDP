package com.econovation.idpdomain.domains.auth;


import com.econovation.idpdomain.domains.users.domain.BaseTimeEntity;
import java.time.LocalDateTime;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.GenericGenerator;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
@Slf4j
public class ConfirmationToken extends BaseTimeEntity {

    private static final long EMAIL_TOKEN_EXPIRATION_TIME_VALUE = 5L;

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private UUID id;

    @Column private LocalDateTime expirationDate;

    @Column private boolean expired;

    // FK 사용하지 않고 INPUT으로 받고
    @Column private Long userId;

    /** 이메일 토큰 생성 로직 @Param userId */
    public static ConfirmationToken createEmailConfirmationToken(Long userId) {
        System.out.println(userId);
        ConfirmationToken confirmationToken = new ConfirmationToken();
        confirmationToken.expirationDate =
                LocalDateTime.now().plusMinutes(EMAIL_TOKEN_EXPIRATION_TIME_VALUE);
        confirmationToken.expired = false;
        confirmationToken.userId = userId;
        return confirmationToken;
    }
    /** 토큰 사용 만료 */
    public void useToken() {
        this.expired = true;
    }
}
