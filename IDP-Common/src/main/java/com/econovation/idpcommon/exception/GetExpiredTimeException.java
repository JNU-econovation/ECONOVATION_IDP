package com.econovation.idpcommon.exception;


import lombok.Getter;

@Getter
public class GetExpiredTimeException extends Throwable {
    public GetExpiredTimeException(String message) {
        super(message);
    }
}
