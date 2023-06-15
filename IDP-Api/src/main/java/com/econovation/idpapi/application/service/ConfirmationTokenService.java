package com.econovation.idpapi.application.service;


import com.econovation.idpcommon.exception.BadRequestException;
import com.econovation.idpdomain.domains.auth.ConfirmationToken;
import com.econovation.idpdomain.domains.auth.ConfirmationTokenRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class ConfirmationTokenService {
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final EmailSenderService emailSenderService;
    //    @Value("${sending.email}")
    @Value("localhost:8080")
    private String destinationEmail;

    //    public ConfirmationTokenService(ConfirmationTokenRepository confirmationTokenRepository,
    // EmailSenderService emailSenderService) {
    //        this.confirmationTokenRepository = confirmationTokenRepository;
    //        this.emailSenderService = emailSenderService;
    //    }

    private final String TOKEN_NOT_FOUND = "Token 존재하지 않는다";
    //    ValidationConstant.TOKEN_NOT_FOUND

    /** 이메일 인증 랜덤 토큰 생성 토큰 생성 != 인증 이후 인증을 해야 토큰에 인증 expired 를 true로 바꿔준다. */
    public UUID createEmailConfirmationToken(Long userId, String receiverEmail) {
        ConfirmationToken emailConfirmationToken =
                ConfirmationToken.createEmailConfirmationToken(userId);
        confirmationTokenRepository.save(emailConfirmationToken);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(receiverEmail);
        mailMessage.setSubject("회원가입 이메일 인증");
        mailMessage.setText("Econovation TechBlog 회원가입 인증 URL");
        mailMessage.setText(
                "http://"
                        + destinationEmail
                        + "/api/confirm-email/"
                        + emailConfirmationToken.getId());
        emailSenderService.sendEmail(mailMessage);

        return emailConfirmationToken.getId();
    }
    /** 이메일 인증 번호(6자리) 생성 토큰 생성 != 인증 이후 인증을 해야 토큰에 인증 expired 를 true로 바꿔준다. */
    public String createEmailConfirmationToken(String receiverEmail) {
        String code = makeRandomCode();
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(receiverEmail);
        mailMessage.setSubject("비밀번호 검색 인증");
        mailMessage.setText("Econovation TechBlog 회원가입 인증 URL");

        mailMessage.setText(code);
        emailSenderService.sendEmail(mailMessage);
        return code;
    }

    //   6자리 랜덤 코드 생성
    public String makeRandomCode() {
        int leftLimit = 0; // letter 'a'
        int rightLimit = 9; // letter 'z'
        int targetStringLength = 6;
        Random random = new Random();
        String generatedString =
                random.ints(leftLimit, rightLimit + 1)
                        .limit(targetStringLength)
                        .collect(
                                StringBuilder::new,
                                StringBuilder::appendCodePoint,
                                StringBuilder::append)
                        .toString();
        System.out.println(generatedString);
        return generatedString;
    }
    /**
     * 유효한 토큰 가져오기
     *
     * @param confirmationTokenId
     */
    public ConfirmationToken findByIdAndExpirationDateAfterAndExpired(UUID confirmationTokenId) {
        Optional<ConfirmationToken> confirmationToken =
                confirmationTokenRepository.findByIdAndExpirationDateAfterAndExpired(
                        confirmationTokenId, LocalDateTime.now(), false);
        if (confirmationToken.isEmpty()) {
            throw new BadRequestException("잘못된 토큰입니다.");
        }
        return confirmationToken.get();
    }
}
