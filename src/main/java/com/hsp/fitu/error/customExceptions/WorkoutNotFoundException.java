package com.hsp.fitu.error.customExceptions;

import com.hsp.fitu.error.ErrorCode;
import lombok.Getter;

@Getter
public class WorkoutNotFoundException extends RuntimeException {
    private final ErrorCode errorCode;

    public WorkoutNotFoundException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public WorkoutNotFoundException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}