package com.hsp.fitu.error.customExceptions;

import com.hsp.fitu.error.BusinessException;
import com.hsp.fitu.error.ErrorCode;

public class JwtUnsupportedException extends BusinessException {
    public JwtUnsupportedException() {
        super(ErrorCode.JWT_UNSUPPORTED);
    }
}
