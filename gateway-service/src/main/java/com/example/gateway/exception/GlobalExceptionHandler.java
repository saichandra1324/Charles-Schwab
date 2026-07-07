package com.example.gateway.exception;

import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<Map<String,Object>> validation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream().findFirst().map(e -> e.getField() + " " + e.getDefaultMessage()).orElse("Invalid request");
        return error(HttpStatus.BAD_REQUEST, message);
    }
    @ExceptionHandler(EventNotFoundException.class)
    ResponseEntity<Map<String,Object>> notFound(EventNotFoundException ex) { return error(HttpStatus.NOT_FOUND, ex.getMessage()); }
    @ExceptionHandler(AccountServiceUnavailableException.class)
    ResponseEntity<Map<String,Object>> unavailable(AccountServiceUnavailableException ex) { return error(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage()); }
    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<Map<String,Object>> illegal(IllegalArgumentException ex) { return error(HttpStatus.BAD_REQUEST, ex.getMessage()); }
    private ResponseEntity<Map<String,Object>> error(HttpStatus status, String message) {
        String traceId = MDC.get("traceId");
        log.warn("request failed status={} message={}", status.value(), message);
        return ResponseEntity.status(status).body(Map.of("timestamp", Instant.now().toString(), "traceId", traceId == null ? "" : traceId, "status", status.value(), "error", status.getReasonPhrase(), "message", message));
    }
}
