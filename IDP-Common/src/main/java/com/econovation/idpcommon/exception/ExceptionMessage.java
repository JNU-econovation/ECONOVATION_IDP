package com.econovation.idpcommon.exception;


import lombok.Getter;

@Getter
public enum ExceptionMessage {
    EXPIRED_ACCESS_TOKEN_EXCEPTION("EXPIRED_ACCESS_TOKEN_EXCEPTION: 만료된 AccessToken입니다."),
    FORBIDDEN_ROLE_EXCEPTION("FORBIDDEN_ROLE_EXCEPTION: 허용되지 않는 권한입니다."),
    IMAGE_IO_EXCEPTION("IMAGE_IO_EXCEPTION: ");
    public final String message;

    ExceptionMessage(String message) {
        this.message = message;
    }
}
