package com.hsp.fitu.error.customExceptions;

import com.hsp.fitu.error.ErrorCode;

public class JwtSignatureInvalidException extends RuntimeException {
    public JwtSignatureInvalidException() {
        super(String.valueOf(ErrorCode.JWT_SIGNATURE_INVALID));
    }
}
