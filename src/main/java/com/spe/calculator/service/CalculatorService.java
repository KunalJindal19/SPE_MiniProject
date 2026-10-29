package com.spe.calculator.service;

import com.spe.calculator.exception.CalculationException;
import com.spe.calculator.exception.DivisionByZeroException;
import org.springframework.stereotype.Service;

/**
 * Core arithmetic operations. All validation of operands and results lives here,
 * so the REST layer only has to translate exceptions into HTTP responses.
 */
@Service
public class CalculatorService {

    public double add(double a, double b) {
        requireFiniteOperands(a, b);
        return requireFiniteResult(a + b);
    }

    public double subtract(double a, double b) {
        requireFiniteOperands(a, b);
        return requireFiniteResult(a - b);
    }

    public double multiply(double a, double b) {
        requireFiniteOperands(a, b);
        return requireFiniteResult(a * b);
    }

    public double divide(double a, double b) {
        requireFiniteOperands(a, b);
        if (b == 0) {
            throw new DivisionByZeroException();
        }
        return requireFiniteResult(a / b);
    }

    // NaN and Infinity are valid doubles but not valid calculator inputs
    private static void requireFiniteOperands(double a, double b) {
        if (!Double.isFinite(a) || !Double.isFinite(b)) {
            throw new CalculationException("Operands must be finite numbers");
        }
    }

    // Guards against overflow, e.g. 1e308 * 10 would otherwise return Infinity
    private static double requireFiniteResult(double result) {
        if (!Double.isFinite(result)) {
            throw new CalculationException("Result is out of the representable numeric range");
        }
        return result;
    }
}
