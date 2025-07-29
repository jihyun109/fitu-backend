package com.hsp.fitu.error.customExceptions;

import com.hsp.fitu.error.ErrorCode;
import lombok.Getter;

@Getter
public class S3UploadFailException extends RuntimeException {
    private final ErrorCode errorCode;

    public S3UploadFailException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
