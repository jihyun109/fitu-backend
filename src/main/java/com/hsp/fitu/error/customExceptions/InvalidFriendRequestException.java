package com.hsp.fitu.error.customExceptions;

import com.hsp.fitu.error.BusinessException;
import com.hsp.fitu.error.ErrorCode;

public class InvalidFriendRequestException extends BusinessException {
    public InvalidFriendRequestException() {
        super(ErrorCode.INVALID_FRIEND_REQUEST);
    }
}
