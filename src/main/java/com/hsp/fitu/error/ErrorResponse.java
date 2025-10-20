package com.hsp.fitu.error;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Getter
@Builder
// exception 발생시 응답하는 에러 정보 클래스
public class ErrorResponse {
    private int status;
    private String message;
    private String code;
    private LocalDateTime timestamp;
    private List<String> details;

    // ErrorCode만 있는 경우
    public static ErrorResponse from(ErrorCode errorCode) {
        return ErrorResponse.builder()
                .status(errorCode.getStatus())
                .code(errorCode.getErrorCode())
                .message(errorCode.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }

    // ErrorCode + details 있는 경우
    public static ErrorResponse from(ErrorCode errorCode, List<String> details) {
        return ErrorResponse.builder()
                .status(errorCode.getStatus())
                .code(errorCode.getErrorCode())
                .message(errorCode.getMessage())
                .timestamp(LocalDateTime.now())
                .details(details)
                .build();
    }

}
