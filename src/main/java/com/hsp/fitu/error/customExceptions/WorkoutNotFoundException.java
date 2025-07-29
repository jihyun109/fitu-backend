package com.hsp.fitu.error.customExceptions;

import com.hsp.fitu.error.ErrorCode;
import lombok.Getter;

@Getter
public class WorkoutNotFoundException extends RuntimeException {
    private final ErrorCode errorCode;

    public WorkoutNotFoundException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}