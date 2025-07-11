package com.bookreview.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;
import io.jsonwebtoken.ExpiredJwtException;
import com.bookreview.exception.BookAlreadyExistsException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private Map<String, Object> buildBody(HttpStatus status, String error, String message, String path) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", error);
        body.put("message", message);
        body.put("path", path);
        return body;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException ex, WebRequest request) {
        List<String> errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.toList());
        Map<String, Object> body = buildBody(HttpStatus.BAD_REQUEST, "Validation Error", errors.toString(), request.getDescription(false).replace("uri=", ""));
        logger.warn("Validation error: {}", errors);
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        Map<String, Object> body = buildBody(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage(), request.getDescription(false).replace("uri=", ""));
        logger.warn("Illegal argument: {}", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> handleEntityNotFoundException(EntityNotFoundException ex, WebRequest request) {
        Map<String, Object> body = buildBody(HttpStatus.NOT_FOUND, "Resource Not Found", ex.getMessage(), request.getDescription(false).replace("uri=", ""));
        logger.warn("Resource not found: {}", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<?> handleUsernameNotFoundException(UsernameNotFoundException ex, WebRequest request) {
        Map<String, Object> body = buildBody(HttpStatus.NOT_FOUND, "User Not Found", ex.getMessage(), request.getDescription(false).replace("uri=", ""));
        logger.warn("User not found: {}", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDataIntegrityViolationException(DataIntegrityViolationException ex, WebRequest request) {
        String message = ex.getRootCause() != null ? ex.getRootCause().getMessage() : ex.getMessage();
        Map<String, Object> body = buildBody(HttpStatus.CONFLICT, "Data Integrity Violation", message, request.getDescription(false).replace("uri=", ""));
        logger.warn("Data integrity violation: {}", message);
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        Map<String, Object> body = buildBody(HttpStatus.FORBIDDEN, "Access Denied", ex.getMessage(), request.getDescription(false).replace("uri=", ""));
        logger.warn("Access denied: {}", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
    }

    // Custom: User already exists
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<?> handleUserAlreadyExistsException(UserAlreadyExistsException ex, WebRequest request) {
        Map<String, Object> body = buildBody(HttpStatus.CONFLICT, "User Already Exists", ex.getMessage(), request.getDescription(false).replace("uri=", ""));
        logger.warn("User already exists: {}", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    // Custom: Invalid credentials
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<?> handleInvalidCredentialsException(InvalidCredentialsException ex, WebRequest request) {
        Map<String, Object> body = buildBody(HttpStatus.UNAUTHORIZED, "Invalid Credentials", ex.getMessage(), request.getDescription(false).replace("uri=", ""));
        logger.warn("Invalid credentials: {}", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<?> handleNoHandlerFoundException(NoHandlerFoundException ex, WebRequest request) {
        Map<String, Object> body = buildBody(HttpStatus.NOT_FOUND, "Not Found", "No handler found for this endpoint", request.getDescription(false).replace("uri=", ""));
        logger.warn("No handler found: {}", ex.getRequestURL());
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex, WebRequest request) {
        String message = String.format("Parameter '%s' with value '%s' could not be converted to type '%s'", ex.getName(), ex.getValue(), ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown");
        Map<String, Object> body = buildBody(HttpStatus.BAD_REQUEST, "Bad Request", message, request.getDescription(false).replace("uri=", ""));
        logger.warn("Argument type mismatch: {}", message);
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<?> handleResponseStatusException(ResponseStatusException ex, WebRequest request) {
        int status = ex.getStatusCode().value();
        String error = ex.getReason() != null ? ex.getReason() : "Error";
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status);
        body.put("error", error);
        body.put("message", ex.getMessage());
        body.put("path", request.getDescription(false).replace("uri=", ""));
        logger.warn("ResponseStatusException: {} {}", status, error);
        return new ResponseEntity<>(body, ex.getStatusCode());
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<?> handleExpiredJwtException(ExpiredJwtException ex, WebRequest request) {
        Map<String, Object> body = buildBody(
            HttpStatus.UNAUTHORIZED,
            "JWT Expired",
            "Your session has expired. Please log in again.",
            request.getDescription(false).replace("uri=", "")
        );
        logger.warn("Expired JWT: {}", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(BookAlreadyExistsException.class)
    public ResponseEntity<?> handleBookAlreadyExistsException(BookAlreadyExistsException ex, WebRequest request) {
        Map<String, Object> body = buildBody(
            HttpStatus.CONFLICT,
            "Book Already Exists",
            ex.getMessage(),
            request.getDescription(false).replace("uri=", "")
        );
        logger.warn("Book already exists: {}", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleAllExceptions(Exception ex, WebRequest request) {
        Map<String, Object> body = buildBody(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", ex.getMessage(), request.getDescription(false).replace("uri=", ""));
        logger.error("Unhandled exception: ", ex);
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
} 