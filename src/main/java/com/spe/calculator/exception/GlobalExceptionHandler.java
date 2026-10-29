package com.spe.calculator.exception;

import com.spe.calculator.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;

/**
 * Translates exceptions thrown anywhere in the controllers into HTTP 400 responses
 * with a consistent {@link ErrorResponse} body.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Division by zero, NaN/Infinity operands, overflow
    @ExceptionHandler(CalculationException.class)
    public ResponseEntity<ErrorResponse> handleCalculation(CalculationException ex, HttpServletRequest request) {
        return badRequest(ex.getMessage(), request);
    }

    // e.g. /api/v1/add?a=2  (b is missing)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParameter(MissingServletRequestParameterException ex,
                                                                HttpServletRequest request) {
        return badRequest("Missing required parameter '" + ex.getParameterName() + "'", request);
    }

    // e.g. /api/v1/add?a=abc&b=2  (a is not a number)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex,
                                                            HttpServletRequest request) {
        return badRequest("Parameter '" + ex.getName() + "' must be a valid number", request);
    }

    private static ResponseEntity<ErrorResponse> badRequest(String message, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErrorResponse body = new ErrorResponse(Instant.now(), status.value(), status.getReasonPhrase(),
                message, request.getRequestURI());
        return ResponseEntity.status(status).body(body);
    }
}
