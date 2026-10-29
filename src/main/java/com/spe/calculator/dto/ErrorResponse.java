package com.spe.calculator.dto;

import java.time.Instant;

/**
 * Structured error body returned for every client error, e.g.
 * {"timestamp":"...","status":400,"error":"Bad Request","message":"Division by zero is not allowed","path":"/api/v1/divide"}
 */
public record ErrorResponse(Instant timestamp, int status, String error, String message, String path) {
}
