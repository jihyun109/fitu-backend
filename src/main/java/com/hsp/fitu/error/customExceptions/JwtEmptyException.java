package com.hsp.fitu.error.customExceptions;

import com.hsp.fitu.error.BusinessException;
import com.hsp.fitu.error.ErrorCode;

public class JwtEmptyException extends BusinessException {
    public JwtEmptyException() {
        super(ErrorCode.JWT_EMPTY);
    }
}
