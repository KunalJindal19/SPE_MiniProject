package com.spe.calculator.exception;

/**
 * Raised when a calculation cannot be performed for the given operands
 * (non-finite inputs, overflow, etc.). Mapped to HTTP 400 by the API layer.
 */
public class CalculationException extends RuntimeException {

    public CalculationException(String message) {
        super(message);
    }
}
