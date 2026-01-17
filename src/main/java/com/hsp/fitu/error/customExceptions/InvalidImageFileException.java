package com.hsp.fitu.error.customExceptions;

import com.hsp.fitu.error.BusinessException;
import com.hsp.fitu.error.ErrorCode;
import lombok.Getter;

@Getter
public class InvalidImageFileException extends BusinessException {
    public InvalidImageFileException(ErrorCode errorCode) {
        super(ErrorCode.INVALID_IMAGE_FILE);
    }
}