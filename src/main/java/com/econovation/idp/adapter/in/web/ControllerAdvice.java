package com.econovation.idp.adapter.in.web;

import com.econovation.idp.application.port.out.ErrorResult;
import com.econovation.idp.global.common.exception.BadRequestException;
import jdk.jshell.spi.ExecutionControl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.ap.internal.util.IgnoreJRERequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Date;
import java.util.Optional;

@Slf4j
@RestControllerAdvice
public class ControllerAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorResult illegalExHandle(IllegalArgumentException e) {
        log.error(new Date().getTime() + "  [부적절한 사용자 요청] : ", e.getMessage());
        return new ErrorResult("사용자의 잘못된 요청입니다.", e.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResult> userExHandle(ExecutionControl.UserException e) {
        log.error(new Date().getTime() + "  [Exception] : ", e.getMessage());
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


}
