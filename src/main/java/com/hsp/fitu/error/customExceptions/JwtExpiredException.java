package com.hsp.fitu.error.customExceptions;

import com.hsp.fitu.error.BusinessException;
import com.hsp.fitu.error.ErrorCode;

public class JwtExpiredException extends BusinessException {
    public JwtExpiredException() {
        super(ErrorCode.JWT_EXPIRED);
    }
}
