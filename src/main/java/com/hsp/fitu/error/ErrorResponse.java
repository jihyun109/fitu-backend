package com.hsp.fitu.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@RequiredArgsConstructor
@Getter
// exception 발생시 응답하는 에러 정보 클래스
public class ErrorResponse {
    private int status;
    private String message;
    private String code;
    private LocalDateTime timestamp;
    // 필드별 오류
    @Setter
    private Map<String, String> fieldErrors;

    public ErrorResponse(ErrorCode errorCode) {
        this.status = errorCode.getStatus();
        this.message = errorCode.getMessage();
        this.code = errorCode.getErrorCode();
        this.timestamp = LocalDateTime.now();
    }
}
