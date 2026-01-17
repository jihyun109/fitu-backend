package com.hsp.fitu.error.customExceptions;

import com.hsp.fitu.error.BusinessException;
import com.hsp.fitu.error.ErrorCode;

public class JwtSignatureInvalidException extends BusinessException {
    public JwtSignatureInvalidException() {
        super(ErrorCode.JWT_SIGNATURE_INVALID);
    }
}
