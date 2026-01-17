package com.hsp.fitu.error.customExceptions;

import com.hsp.fitu.error.BusinessException;
import com.hsp.fitu.error.ErrorCode;

public class InvalidFriendCodeException extends BusinessException {
    public InvalidFriendCodeException() {
        super(ErrorCode.INVALID_FRIEND_CODE);
    }
}
