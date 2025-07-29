package com.hsp.fitu.error;

import com.hsp.fitu.error.customExceptions.EmptyFileException;
import com.hsp.fitu.error.customExceptions.InvalidImageFileException;
import com.hsp.fitu.error.customExceptions.S3UploadFailException;
import com.hsp.fitu.error.customExceptions.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 유효성 검사 실패시 발생하는 예외
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.warn("Validation error occurred for request: {}", ex.getParameter().getParameterName(), ex);

        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        Map<String, String> errorMap = new HashMap<>();

        for (FieldError fieldError : fieldErrors) {
            errorMap.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        ErrorResponse response = new ErrorResponse(ErrorCode.METHOD_ARGUMENT_NOT_VALID);
        response.setFieldErrors(errorMap);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // 리소스 없음 예외 처리
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFoundException(NoResourceFoundException ex) {
        log.warn("Resource not found: {}", ex.getMessage(), ex);
        ErrorResponse response = new ErrorResponse(ErrorCode.NOT_FOUND);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    // 잘못된 인수 예외 처리
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("Illegal argument provided: {}", ex.getMessage(), ex);
        ErrorResponse response = new ErrorResponse(ErrorCode.METHOD_ARGUMENT_NOT_VALID);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
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

        ErrorResponse response = new ErrorResponse(errorCode);
        return new ResponseEntity<>(response, HttpStatus.valueOf(errorCode.getStatus()));
    }

    // 빈 파일 에외 처리
    @ExceptionHandler(EmptyFileException.class)
    public ResponseEntity<ErrorResponse> handleEmptyFileException(EmptyFileException ex) {
        log.warn("Empty file exception: {}", ex.getMessage(), ex);
        ErrorResponse response = new ErrorResponse(ErrorCode.EMPTY_FILE);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // 유효하지 않은 이미지 파일 예외 처리
    @ExceptionHandler(InvalidImageFileException.class)
    public ResponseEntity<ErrorResponse> handleInvalidImageFileException(InvalidImageFileException ex) {
        log.warn("Invalid image file exception: {}", ex.getMessage(), ex);
        ErrorResponse response = new ErrorResponse(ErrorCode.INVALID_FILE_EXTENSION);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // S3 업로드 실패 예외 처리
    @ExceptionHandler(S3UploadFailException.class)
    public ResponseEntity<ErrorResponse> handleS3UploadFailException(S3UploadFailException ex) {
        log.warn("S3 upload failed: {}", ex.getMessage(), ex);
        ErrorResponse response = new ErrorResponse(ErrorCode.S3_UPLOAD_FAILED);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // 인증 실패 예외 처리
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(UnauthorizedException ex) {
        log.warn("Unauthorized access: {}", ex.getMessage(), ex);
        ErrorResponse response = new ErrorResponse(ErrorCode.UNAUTHORIZED);
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    // 기타 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        log.error("Unexpected exception occurred: {}", ex.getMessage(), ex);
        ErrorResponse response = new ErrorResponse(ErrorCode.INTER_SERVER_ERROR);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}