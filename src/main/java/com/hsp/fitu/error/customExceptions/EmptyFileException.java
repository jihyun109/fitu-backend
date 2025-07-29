package com.hsp.fitu.error.customExceptions;


import com.hsp.fitu.error.ErrorCode;
import lombok.Getter;

@Getter
public class EmptyFileException extends RuntimeException {
    private final ErrorCode errorCode;

    public EmptyFileException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
