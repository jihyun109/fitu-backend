package com.hsp.fitu.error.customExceptions;

import com.hsp.fitu.error.BusinessException;
import com.hsp.fitu.error.ErrorCode;

public class InvalidWorkoutIdException extends BusinessException {
    public InvalidWorkoutIdException() {
        super(ErrorCode.INVALID_WORKOUT_ID);
    }
}
