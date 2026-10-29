package com.spe.calculator.exception;

/**
 * Raised when the divisor of a division is zero.
 */
public class DivisionByZeroException extends CalculationException {

    public DivisionByZeroException() {
        super("Division by zero is not allowed");
    }
}
