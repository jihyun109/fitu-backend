package com.hsp.fitu.error.customExceptions;

import com.hsp.fitu.error.ErrorCode;

public class UnauthorizedException extends RuntimeException{
    private ErrorCode errorCode;
    public UnauthorizedException(String message, ErrorCode errorCode){
        super(message);
        this.errorCode = errorCode;
    }
}
