package com.econovation.idp.global.common.exception;

import com.econovation.idp.global.common.dto.ErrorReason;

public interface BaseErrorCode {
    public ErrorReason getErrorReason();

    String getExplainError() throws NoSuchFieldException;
}
