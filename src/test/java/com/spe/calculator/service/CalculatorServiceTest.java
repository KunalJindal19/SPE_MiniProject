package com.spe.calculator.service;

import com.spe.calculator.exception.CalculationException;
import com.spe.calculator.exception.DivisionByZeroException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Plain unit tests: the service is created directly, no Spring context is started.
 */
class CalculatorServiceTest {

    private static final double DELTA = 1e-9;

    private final CalculatorService calculator = new CalculatorService();

    @Nested
    @DisplayName("add")
    class Add {

        @ParameterizedTest(name = "{0} + {1} = {2}")
        @CsvSource({
                "2, 3, 5",
                "-4, -6, -10",
                "-4, 6, 2",
                "0, 0, 0",
                "1.5, 2.25, 3.75",
                "0.1, 0.2, 0.3"
        })
        void addsTwoNumbers(double a, double b, double expected) {
            assertEquals(expected, calculator.add(a, b), DELTA);
        }

        @Test
        void throwsOnOverflow() {
            assertThrows(CalculationException.class, () -> calculator.add(Double.MAX_VALUE, Double.MAX_VALUE));
        }
    }

    @Nested
    @DisplayName("subtract")
    class Subtract {

        @ParameterizedTest(name = "{0} - {1} = {2}")
        @CsvSource({
                "5, 3, 2",
                "3, 5, -2",
                "-4, -6, 2",
                "0, 0, 0",
                "5.5, 2.25, 3.25"
        })
        void subtractsTwoNumbers(double a, double b, double expected) {
            assertEquals(expected, calculator.subtract(a, b), DELTA);
        }

        @Test
        void throwsOnOverflow() {
            assertThrows(CalculationException.class, () -> calculator.subtract(-Double.MAX_VALUE, Double.MAX_VALUE));
        }
    }

    @Nested
    @DisplayName("multiply")
    class Multiply {

        @ParameterizedTest(name = "{0} * {1} = {2}")
        @CsvSource({
                "2, 3, 6",
                "-2, 3, -6",
                "-2, -3, 6",
                "7, 0, 0",
                "1.5, 4, 6"
        })
        void multipliesTwoNumbers(double a, double b, double expected) {
            assertEquals(expected, calculator.multiply(a, b), DELTA);
        }

        @Test
        void throwsOnOverflow() {
            assertThrows(CalculationException.class, () -> calculator.multiply(1e308, 10));
        }
    }

    @Nested
    @DisplayName("divide")
    class Divide {

        @ParameterizedTest(name = "{0} / {1} = {2}")
        @CsvSource({
                "6, 3, 2",
                "7, 2, 3.5",
                "-9, 3, -3",
                "-9, -3, 3",
                "0, 5, 0",
                "1, 3, 0.333333333333"
        })
        void dividesTwoNumbers(double a, double b, double expected) {
            assertEquals(expected, calculator.divide(a, b), 1e-12);
        }

        @ParameterizedTest(name = "{0} / 0 throws")
        @ValueSource(doubles = {1, -1, 0, 123.45})
        void throwsOnDivisionByZero(double a) {
            DivisionByZeroException ex = assertThrows(DivisionByZeroException.class, () -> calculator.divide(a, 0));
            assertEquals("Division by zero is not allowed", ex.getMessage());
        }

        @Test
        void throwsOnDivisionByNegativeZero() {
            assertThrows(DivisionByZeroException.class, () -> calculator.divide(1, -0.0));
        }

        @Test
        void throwsOnOverflow() {
            assertThrows(CalculationException.class, () -> calculator.divide(Double.MAX_VALUE, 0.1));
        }
    }

    @Nested
    @DisplayName("operand validation")
    class OperandValidation {

        @ParameterizedTest(name = "rejects {0}")
        @ValueSource(doubles = {Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY})
        void rejectsNonFiniteOperands(double invalid) {
            assertThrows(CalculationException.class, () -> calculator.add(invalid, 1));
            assertThrows(CalculationException.class, () -> calculator.subtract(1, invalid));
            assertThrows(CalculationException.class, () -> calculator.multiply(invalid, 1));
            assertThrows(CalculationException.class, () -> calculator.divide(1, invalid));
        }
    }
}
