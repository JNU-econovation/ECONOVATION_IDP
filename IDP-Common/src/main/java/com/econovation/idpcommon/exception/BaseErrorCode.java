package com.econovation.idpcommon.exception;


import com.econovation.idpcommon.dto.ErrorReason;

public interface BaseErrorCode {
    public ErrorReason getErrorReason();

    String getExplainError() throws NoSuchFieldException;
}
