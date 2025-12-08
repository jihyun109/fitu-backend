package com.hsp.fitu.error.customExceptions;

import com.hsp.fitu.error.ErrorCode;

public class JwtExpiredException extends RuntimeException {
    public JwtExpiredException() {
        super(String.valueOf(ErrorCode.JWT_EXPIRED));
    }
}
