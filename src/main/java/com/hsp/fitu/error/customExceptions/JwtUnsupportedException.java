package com.hsp.fitu.error.customExceptions;

import com.hsp.fitu.error.ErrorCode;

public class JwtUnsupportedException extends RuntimeException {
    public JwtUnsupportedException() {
        super(String.valueOf(ErrorCode.JWT_UNSUPPORTED));
    }
}
