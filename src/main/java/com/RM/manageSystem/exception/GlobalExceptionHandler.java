package com.RM.manageSystem.exception;


import com.RM.manageSystem.common.ErrorCode;
import com.RM.manageSystem.common.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<?> handleOptionsRequest(HttpServletRequest request, HttpRequestMethodNotSupportedException ex) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            log.debug("處理 OPTIONS 請求: {}", request.getRequestURI());
            return ResponseEntity.ok().build();
        }
        // 處理其他 HTTP 方法不支持的情況
        log.warn("不支持的 HTTP 方法: {}", ex.getMessage());
        Result<?> result = Result.error(ErrorCode.SYSTEM_ERROR);
        return new ResponseEntity<>(result, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<?>> handleValidationException(MethodArgumentNotValidException ex) {
        String methodName = Objects.requireNonNull(ex.getParameter().getMethod()).getName();
        String className = ex.getParameter().getMethod().getDeclaringClass().getName();
        log.error("輸入資訊驗未通過驗證 - 方法: {}.{}", className, methodName, ex);
        Result<?> result = Result.error(ErrorCode.PARAMS_ERROR, "輸入資訊驗未通過驗證，請重試");
        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Result<?>> handleConstraintViolationException(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation -> {
            errors.put(violation.getPropertyPath().toString(), violation.getMessage());
        });
        log.info("輸入資訊驗未通過驗證: " + errors);
        Result<?> result = Result.error(ErrorCode.PARAMS_ERROR, "輸入資訊驗未通過驗證，請重試");
        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Result<?>> handleBusinessException(BusinessException e) {
        log.error("BusinessException: ", e.getMessage(), e);
        Result<?> result = Result.error(e.getErrorCode(), e.getMessage());
        return new ResponseEntity<>(result, HttpStatus.valueOf(e.getErrorCode().getHttpStatus()));
    }

    @ExceptionHandler(SystemException.class)
    public ResponseEntity<Result<?>> handleSystemException(SystemException e) {
        log.error("SystemException: ", e.getMessage(), e);
        Result<?> result = Result.error(e.getErrorCode(), e.getMessage());
        return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<?>> handleException(Exception e, HttpServletRequest request) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            log.debug("處理 OPTIONS 請求: {}", request.getRequestURI());
            return ResponseEntity.ok().build();
        }
        log.error("其他系統異常", e);
        Result<?> result = Result.error(ErrorCode.SYSTEM_ERROR);
        return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
