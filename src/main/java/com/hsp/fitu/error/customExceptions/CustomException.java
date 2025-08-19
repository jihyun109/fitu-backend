package com.hsp.fitu.error.customExceptions;

import com.hsp.fitu.error.ErrorCode;

public class CustomException extends RuntimeException{
    private final ErrorCode errorCode;

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
