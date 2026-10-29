package com.spe.calculator.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests: starts the full Spring application context and sends real HTTP
 * requests through the MVC stack (controller -> service -> exception handler) via MockMvc.
 */
@SpringBootTest
@AutoConfigureMockMvc
class CalculatorControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @ParameterizedTest(name = "GET /api/v1/{0}?a={1}&b={2} -> {3}")
    @CsvSource({
            "add,      2,   3,  5.0",
            "subtract, 10,  4,  6.0",
            "multiply, 2.5, 4,  10.0",
            "divide,   7,   2,  3.5"
    })
    void returnsResultForEachOperation(String operation, String a, String b, double expected) throws Exception {
        mockMvc.perform(get("/api/v1/" + operation).param("a", a).param("b", b))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.operation").value(operation))
                .andExpect(jsonPath("$.a").value(Double.parseDouble(a)))
                .andExpect(jsonPath("$.b").value(Double.parseDouble(b)))
                .andExpect(jsonPath("$.result").value(expected));
    }

    @Test
    void divideByZeroReturns400WithErrorBody() throws Exception {
        mockMvc.perform(get("/api/v1/divide").param("a", "5").param("b", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Division by zero is not allowed"))
                .andExpect(jsonPath("$.path").value("/api/v1/divide"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void missingParameterReturns400() throws Exception {
        mockMvc.perform(get("/api/v1/add").param("a", "2"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Missing required parameter 'b'"));
    }

    @Test
    void nonNumericParameterReturns400() throws Exception {
        mockMvc.perform(get("/api/v1/multiply").param("a", "abc").param("b", "2"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Parameter 'a' must be a valid number"));
    }

    @Test
    void nonFiniteParameterReturns400() throws Exception {
        mockMvc.perform(get("/api/v1/subtract").param("a", "NaN").param("b", "1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Operands must be finite numbers"));
    }

    @Test
    void overflowReturns400() throws Exception {
        mockMvc.perform(get("/api/v1/multiply").param("a", "1e308").param("b", "10"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Result is out of the representable numeric range"));
    }

    @Test
    void actuatorHealthIsUp() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }
}
