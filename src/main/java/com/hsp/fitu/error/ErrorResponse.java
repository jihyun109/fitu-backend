package com.hsp.fitu.error;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        int status,
        String code,
        String message,
        LocalDateTime timestamp,
        List<FieldError> errors
) {

    // 내부 static class였던 FieldError도 record로 간단하게 정의
    public record FieldError(
            String field,
            String value,
            String reason
    ) {}

    // ErrorCode만 있는 경우 (errors는 null 처리)
    public static ErrorResponse from(ErrorCode errorCode) {
        return new ErrorResponse(
                errorCode.getStatus(),
                errorCode.getErrorCode(),
                errorCode.getMessage(),
                LocalDateTime.now(),
                null // 또는 List.of() 로 빈 리스트 반환
        );
    }

    // ErrorCode + 유효성 검사 에러 목록이 있는 경우
    public static ErrorResponse of(ErrorCode errorCode, List<FieldError> errors) {
        return new ErrorResponse(
                errorCode.getStatus(),
                errorCode.getErrorCode(),
                errorCode.getMessage(),
                LocalDateTime.now(),
                errors
        );
    }
}