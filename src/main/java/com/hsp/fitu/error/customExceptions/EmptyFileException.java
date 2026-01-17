package com.hsp.fitu.error.customExceptions;


import com.hsp.fitu.error.BusinessException;
import com.hsp.fitu.error.ErrorCode;
import lombok.Getter;

@Getter
public class EmptyFileException extends BusinessException {
    public EmptyFileException() {
        super(ErrorCode.EMPTY_FILE);
    }
}
