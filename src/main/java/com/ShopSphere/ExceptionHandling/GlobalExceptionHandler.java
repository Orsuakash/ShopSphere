package com.ShopSphere.ExceptionHandling;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 404 - resource not found
    @ExceptionHandler(ResourceNotFoundExpection.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            ResourceNotFoundExpection ex,
            HttpServletRequest request) {

        return build(HttpStatus.NOT_FOUND, ex.getMessage(), request, null);
    }

    // 400 - bad request
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(
            BadRequestException ex,
            HttpServletRequest request) {

        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), request, null);
    }

    // 400 - validation errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));

        return build(HttpStatus.BAD_REQUEST, "Validation failed", request, errors);
    }

    // 401 - wrong username/password or not authenticated
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthentication(
            AuthenticationException ex,
            HttpServletRequest request) {

        return build(HttpStatus.UNAUTHORIZED, "Invalid credentials", request, null);
    }

    // 403 - logged in, but wrong role
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
            AccessDeniedException ex,
            HttpServletRequest request) {

        return build(HttpStatus.FORBIDDEN,
                "You do not have permission to perform this action", request, null);
    }

    // 500 - anything unexpected
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(
            Exception ex,
            HttpServletRequest request) {

        log.error("Unhandled exception on {}", request.getRequestURI(), ex);

        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong", request, null);
    }

    // helper so the code is not repeated
    private ResponseEntity<ErrorResponse> build(
            HttpStatus status,
            String message,
            HttpServletRequest request,
            Map<String, String> validationErrors) {

        ErrorResponse body = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(request.getRequestURI())
                .validationErrors(validationErrors)
                .build();

        return ResponseEntity.status(status).body(body);
    }
}