package com.hsp.fitu.error.customExceptions;

import com.hsp.fitu.error.BusinessException;
import com.hsp.fitu.error.ErrorCode;
import lombok.Getter;

@Getter
public class WorkoutNotFoundException extends BusinessException {

    public WorkoutNotFoundException() {
        super(ErrorCode.WORKOUT_NOT_FOUND);
    }
}