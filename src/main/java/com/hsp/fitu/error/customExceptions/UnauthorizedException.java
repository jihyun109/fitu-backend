package com.hsp.fitu.error.customExceptions;

import com.hsp.fitu.error.BusinessException;
import com.hsp.fitu.error.ErrorCode;
import lombok.Getter;

@Getter
public class UnauthorizedException extends BusinessException {
    public UnauthorizedException() {
        super(ErrorCode.UNAUTHORIZED);
    }
}
