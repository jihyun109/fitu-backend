package com.hsp.fitu.error.customExceptions;


import com.hsp.fitu.error.ErrorCode;

public class EmptyFileException extends RuntimeException{
    private ErrorCode errorCode;
    public EmptyFileException(ErrorCode errorCode){
        this.errorCode = errorCode;
    }
}
