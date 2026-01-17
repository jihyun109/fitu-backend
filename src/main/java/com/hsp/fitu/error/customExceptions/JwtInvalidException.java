package com.hsp.fitu.error.customExceptions;

import com.hsp.fitu.error.BusinessException;
import com.hsp.fitu.error.ErrorCode;

public class JwtInvalidException extends BusinessException {
    public JwtInvalidException() {
        super(ErrorCode.INVALID_JWT);
    }
}
