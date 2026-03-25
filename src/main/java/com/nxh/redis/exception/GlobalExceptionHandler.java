package com.nxh.redis.exception;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler {
    // 1. Custom exception
    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorResponse> handleAppException(
            AppException ex,
            HttpServletRequest request
    ) {
        ErrorCode code = ex.getErrorCode();

        return ResponseEntity.status(code.getStatus())
                .body(buildResponse(code, request));
    }

    // 2. Validation lỗi
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .findFirst()
                .orElse("Invalid input");

        return ResponseEntity.badRequest()
                .body(new ErrorResponse(
                        LocalDateTime.now(),
                        400,
                        "VALIDATION_ERROR",
                        message,
                        request.getRequestURI(),
                        UUID.randomUUID().toString()
                ));
    }

    // 3. IllegalArgumentException (wheel not found, preset invalid...)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex,
            HttpServletRequest request
    ) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(
                        java.time.LocalDateTime.now(),
                        400,
                        "BAD_REQUEST",
                        ex.getMessage(),
                        request.getRequestURI(),
                        java.util.UUID.randomUUID().toString()
                ));
    }

    // 4. IllegalStateException (wheel no items...)
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(
            IllegalStateException ex,
            HttpServletRequest request
    ) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(new ErrorResponse(
                        java.time.LocalDateTime.now(),
                        422,
                        "UNPROCESSABLE_ENTITY",
                        ex.getMessage(),
                        request.getRequestURI(),
                        java.util.UUID.randomUUID().toString()
                ));
    }

    // 5. Lỗi không xác định
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnknown(
            Exception ex,
            HttpServletRequest request
    ) {
        return ResponseEntity.status(500)
                .body(new ErrorResponse(
                        LocalDateTime.now(),
                        500,
                        ErrorCode.INTERNAL_ERROR.name(),
                        ex.getMessage(),
                        request.getRequestURI(),
                        UUID.randomUUID().toString()
                ));
    }

    // helper
    private ErrorResponse buildResponse(ErrorCode code, HttpServletRequest request) {
        return new ErrorResponse(
                LocalDateTime.now(),
                code.getStatus(),
                code.name(),
                code.getMessage(),
                request.getRequestURI(),
                UUID.randomUUID().toString()
        );
    }
}
