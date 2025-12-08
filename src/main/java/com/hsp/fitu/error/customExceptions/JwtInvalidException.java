package com.hsp.fitu.error.customExceptions;

import com.hsp.fitu.error.ErrorCode;

public class JwtInvalidException extends RuntimeException {
    public JwtInvalidException() {
        super(String.valueOf(ErrorCode.INVALID_JWT));
    }
}
