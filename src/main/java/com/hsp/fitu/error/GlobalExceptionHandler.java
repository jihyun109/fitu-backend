package com.hsp.fitu.error;

import com.hsp.fitu.error.customExceptions.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 유효성 검사 실패시 발생하는 예외
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        List<String> details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        ErrorResponse response = ErrorResponse.from(ErrorCode.METHOD_ARGUMENT_NOT_VALID, details);

        return ResponseEntity
                .status(ErrorCode.METHOD_ARGUMENT_NOT_VALID.getStatus())  // HTTP 상태 코드(int)
                .body(response);
    }

    // 리소스 없음 예외 처리
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFoundException(NoResourceFoundException ex) {
        log.warn("Resource not found: {}", ex.getMessage(), ex);
        ErrorResponse response = ErrorResponse.from(ErrorCode.NOT_FOUND);
        return ResponseEntity.status(ErrorCode.NOT_FOUND.getStatus()).body(response);
    }

    // 잘못된 인수 예외 처리
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("Illegal argument provided: {}", ex.getMessage(), ex);
        ErrorResponse response = ErrorResponse.from(ErrorCode.METHOD_ARGUMENT_NOT_VALID);
        return ResponseEntity.status(ErrorCode.METHOD_ARGUMENT_NOT_VALID.getStatus()).body(response);
    }

    // 런타임 예외 처리 (구체적인 메시지가 있는 경우)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        log.error("Runtime exception occurred: {}", ex.getMessage(), ex);

        // 메시지에 따라 적절한 에러 코드 선택
        ErrorCode errorCode;
        if (ex.getMessage().contains("운동을 찾을 수 없습니다")) {
            errorCode = ErrorCode.WORKOUT_NOT_FOUND;
        } else if (ex.getMessage().contains("운동 정보 없음")) {
            errorCode = ErrorCode.WORKOUT_NOT_FOUND;
        } else {
            errorCode = ErrorCode.INTER_SERVER_ERROR;
        }

        ErrorResponse response = ErrorResponse.from(errorCode);
        return ResponseEntity.status(errorCode.getStatus()).body(response);
    }

    // 빈 파일 예외 처리
    @ExceptionHandler(EmptyFileException.class)
    public ResponseEntity<ErrorResponse> handleEmptyFileException(EmptyFileException ex) {
        log.warn("Empty file exception: {}", ex.getMessage(), ex);
        ErrorResponse response = ErrorResponse.from(ErrorCode.EMPTY_FILE);
        return ResponseEntity.status(ErrorCode.EMPTY_FILE.getStatus()).body(response);
    }

    // 유효하지 않은 이미지 파일 예외 처리
    @ExceptionHandler(InvalidImageFileException.class)
    public ResponseEntity<ErrorResponse> handleInvalidImageFileException(InvalidImageFileException ex) {
        log.warn("Invalid image file exception: {}", ex.getMessage(), ex);
        ErrorResponse response = ErrorResponse.from(ErrorCode.INVALID_FILE_EXTENSION);
        return ResponseEntity.status(ErrorCode.INVALID_FILE_EXTENSION.getStatus()).body(response);
    }

    // S3 업로드 실패 예외 처리
    @ExceptionHandler(S3UploadFailException.class)
    public ResponseEntity<ErrorResponse> handleS3UploadFailException(S3UploadFailException ex) {
        log.warn("S3 upload failed: {}", ex.getMessage(), ex);
        ErrorResponse response = ErrorResponse.from(ErrorCode.S3_UPLOAD_FAILED);
        return ResponseEntity.status(ErrorCode.S3_UPLOAD_FAILED.getStatus()).body(response);
    }

    // 인증 실패 예외 처리
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(UnauthorizedException ex) {
        log.warn("Unauthorized access: {}", ex.getMessage(), ex);
        ErrorResponse response = ErrorResponse.from(ErrorCode.UNAUTHORIZED);
        return ResponseEntity.status(ErrorCode.UNAUTHORIZED.getStatus()).body(response);
    }

    // 운동 관련 예외 처리
    @ExceptionHandler(WorkoutNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleWorkoutNotFoundException(WorkoutNotFoundException ex) {
        log.warn("Workout not found: {}", ex.getMessage(), ex);
        ErrorResponse response = ErrorResponse.from(ex.getErrorCode());
        return ResponseEntity.status(ex.getErrorCode().getStatus()).body(response);
    }

    // 사용자 관련 예외 처리
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException ex) {
        log.warn("User not found: {}", ex.getMessage(), ex);
        ErrorResponse response = ErrorResponse.from(ex.getErrorCode());
        return ResponseEntity.status(ex.getErrorCode().getStatus()).body(response);
    }

    // 기타 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        log.error("Unexpected exception occurred: {}", ex.getMessage(), ex);
        ErrorResponse response = ErrorResponse.from(ErrorCode.INTER_SERVER_ERROR);
        return ResponseEntity.status(ErrorCode.INTER_SERVER_ERROR.getStatus()).body(response);
    }
}