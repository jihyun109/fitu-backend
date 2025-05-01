package com.hsp.fitu.error.customExceptions;

import com.hsp.fitu.error.ErrorCode;

public class S3UploadFailException extends RuntimeException{
    private ErrorCode errorCode;
    public S3UploadFailException(String message, ErrorCode errorCode){
        super(message);
        this.errorCode = errorCode;
    }
}
