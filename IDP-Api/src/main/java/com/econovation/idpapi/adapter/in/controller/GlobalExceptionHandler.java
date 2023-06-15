package com.econovation.idpapi.adapter.in.controller;


import com.econovation.idpapi.application.port.out.ErrorResult;
import com.econovation.idpapi.config.security.SecurityUtils;
import com.econovation.idpcommon.dto.ErrorResponse;
import com.econovation.idpcommon.exception.BadRequestException;
import com.econovation.idpcommon.exception.GetExpiredTimeException;
import com.econovation.idpcommon.exception.GlobalErrorCode;
import com.econovation.idpcommon.exception.ImageIOException;
import java.io.IOException;
import java.util.Date;
import java.util.NoSuchElementException;
import javax.servlet.http.HttpServletRequest;
import jdk.jshell.spi.ExecutionControl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorResult illegalExHandle(IllegalArgumentException e) {
        log.error("[부적절한 사용자 요청] : ", e.getMessage());
        return new ErrorResult("사용자의 잘못된 요청입니다.", e.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResult> userExHandle(ExecutionControl.UserException e) {
        log.error("[Exception] : ", e.getMessage());
        ErrorResult errorResult = new ErrorResult("USER", e.getMessage());
        return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResult> signUpExHandle(Exception e) {
        log.error(new Date().getTime() + "  [BadRequestException] : ", e.getMessage());
        ErrorResult errorResult = new ErrorResult("SIGN_UP", e.getMessage());
        return new ResponseEntity<>(errorResult, HttpStatus.CONFLICT);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ResponseEntity<ErrorResult> exHandle(Exception e) {
        log.error(new Date().getTime() + "  [INTERNAL_SERVER_ERROR] : ", e.getMessage());
        ErrorResult errorResult = new ErrorResult("INTERNAL_SERVER_ERROR ", e.getMessage());

        return new ResponseEntity<>(errorResult, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ImageIOException.class)
    public ResponseEntity<ErrorResult> ImageIOexHandle(Exception e) {
        log.warn("IMAGE_IO_EXCEPTION : " + e.getMessage());
        ErrorResult errorResult = new ErrorResult("[IMAGE_IO_EXCEPTION] : ", e.getMessage());
        return new ResponseEntity<>(errorResult, HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResult> NoSuchElementexHandle(Exception e) {
        log.warn("IMAGE_IO_EXCEPTION : " + e.getMessage());
        ErrorResult errorResult = new ErrorResult("[NoSuchElementException] : ", e.getMessage());
        return new ResponseEntity<>(errorResult, HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler(GetExpiredTimeException.class)
    public ResponseEntity<ErrorResult> GetExpiredTimeexHandle(Exception e) {
        log.warn("IMAGE_IO_EXCEPTION : " + e.getMessage());
        ErrorResult errorResult = new ErrorResult("[GetExpiredTimeException] : ", e.getMessage());
        return new ResponseEntity<>(errorResult, HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler({Exception.class})
    protected ResponseEntity<ErrorResponse> handleException(Exception e, HttpServletRequest request)
            throws IOException {
        final ContentCachingRequestWrapper cachingRequest = (ContentCachingRequestWrapper) request;
        final Long userId = SecurityUtils.getCurrentUserId();
        String url =
                UriComponentsBuilder.fromHttpRequest(new ServletServerHttpRequest(request))
                        .build()
                        .toUriString();

        log.error("INTERNAL_SERVER_ERROR", e);
        GlobalErrorCode internalServerError = GlobalErrorCode.INTERNAL_SERVER_ERROR;
        ErrorResponse errorResponse =
                new ErrorResponse(
                        internalServerError.getStatus(),
                        internalServerError.getCode(),
                        internalServerError.getReason(),
                        url);

        slackInternalErrorSender.execute(cachingRequest, e, userId);
        return ResponseEntity.status(HttpStatus.valueOf(internalServerError.getStatus()))
                .body(errorResponse);
    }
}
