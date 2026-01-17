package com.hsp.fitu.error.customExceptions;

import com.hsp.fitu.error.BusinessException;
import com.hsp.fitu.error.ErrorCode;

public class FriendshipAlreadyExistsException extends BusinessException {
    public FriendshipAlreadyExistsException() {
        super(ErrorCode.FRIENDSHIP_ALREADY_EXISTS);
    }
}
