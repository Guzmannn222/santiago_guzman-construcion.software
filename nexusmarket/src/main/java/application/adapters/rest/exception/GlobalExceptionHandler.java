package application.adapters.rest.exception;

import application.domain.exceptions.DomainException;
import application.domain.exceptions.DomainValidationException;
import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.InvalidCredentialsException;
import application.domain.exceptions.UnauthorizedOperationException;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * Translates every failure into the same JSON envelope
 * {@code {timestamp, status, message, path, requestId}} and echoes {@code X-Request-Id}.
 * Status mapping: 400 validation/unknown catalog code, 401 bad credentials, 403 not authorized,
 * 404 missing entity, 409 business rule violated, 500 unexpected.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidCredentials(
            InvalidCredentialsException ex, HttpServletRequest request) {
        return respond(HttpStatus.UNAUTHORIZED, ex.getMessage(), request, ex);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(EntityNotFoundException ex, HttpServletRequest request) {
        return respond(HttpStatus.NOT_FOUND, ex.getMessage(), request, ex);
    }

    @ExceptionHandler(UnauthorizedOperationException.class)
    public ResponseEntity<Map<String, Object>> handleUnauthorized(
            UnauthorizedOperationException ex, HttpServletRequest request) {
        return respond(HttpStatus.FORBIDDEN, ex.getMessage(), request, ex);
    }

    @ExceptionHandler(DomainValidationException.class)
    public ResponseEntity<Map<String, Object>> handleDomainValidation(
            DomainValidationException ex, HttpServletRequest request) {
        return respond(HttpStatus.BAD_REQUEST, ex.getMessage(), request, ex);
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<Map<String, Object>> handleDomain(DomainException ex, HttpServletRequest request) {
        return respond(HttpStatus.CONFLICT, ex.getMessage(), request, ex);
    }

    /** DomainCatalog.fromCode throws IllegalArgumentException for an unknown code. */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(
            IllegalArgumentException ex, HttpServletRequest request) {
        return respond(HttpStatus.BAD_REQUEST, ex.getMessage(), request, ex);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return respond(HttpStatus.BAD_REQUEST, message, request, ex);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleMalformedBody(
            HttpMessageNotReadableException ex, HttpServletRequest request) {
        return respond(HttpStatus.BAD_REQUEST, "Malformed JSON request body.", request, ex);
    }

    @ExceptionHandler({HandlerMethodValidationException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<Map<String, Object>> handleInvalidParameter(Exception ex, HttpServletRequest request) {
        return respond(HttpStatus.BAD_REQUEST, "Invalid request parameter.", request, ex);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNoResource(
            NoResourceFoundException ex, HttpServletRequest request) {
        String message = "Resource not found: " + request.getMethod() + " " + request.getRequestURI();
        return respond(HttpStatus.NOT_FOUND, message, request, ex);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleUnexpected(Exception ex, HttpServletRequest request) {
        return respond(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error.", request, ex);
    }

    private ResponseEntity<Map<String, Object>> respond(HttpStatus status, String message,
            HttpServletRequest request, Exception ex) {
        String requestId = resolveRequestId(request);
        if (status.value() >= 500) {
            log.error("Unexpected error [requestId={}] {} {}", requestId, request.getMethod(),
                    request.getRequestURI(), ex);
        } else {
            log.warn("{} [requestId={}] {} {} -> {}: {}", status.value(), requestId, request.getMethod(),
                    request.getRequestURI(), ex.getClass().getSimpleName(), message);
        }
        return build(status, message, request.getRequestURI(), requestId);
    }

    /** Package-visible so the envelope can be unit tested without a servlet container. */
    static ResponseEntity<Map<String, Object>> build(HttpStatus status, String message, String path,
            String requestId) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("timestamp", LocalDateTime.now().toString());
        payload.put("status", status.value());
        payload.put("message", message == null ? "" : message);
        payload.put("path", path == null ? "" : path);
        payload.put("requestId", requestId);
        return ResponseEntity.status(status).header("X-Request-Id", requestId).body(payload);
    }

    private String resolveRequestId(HttpServletRequest request) {
        String requestId = request.getHeader("X-Request-Id");
        return requestId == null || requestId.isBlank() ? UUID.randomUUID().toString() : requestId;
    }
}
