package com.econovation.idp.global.common.exception;

import lombok.Getter;

@Getter
public class GetExpiredTimeException extends Throwable {
    public GetExpiredTimeException(String message) {
        super(message);
    }
}
