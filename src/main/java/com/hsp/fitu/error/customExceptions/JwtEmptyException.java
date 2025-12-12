package com.hsp.fitu.error.customExceptions;

import com.hsp.fitu.error.ErrorCode;

public class JwtEmptyException extends RuntimeException {
    public JwtEmptyException() {
        super(String.valueOf(ErrorCode.JWT_EMPTY));
    }
}
