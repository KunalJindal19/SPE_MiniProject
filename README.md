# Calculator REST API — CI/CD Pipeline

A Calculator REST API built with **Spring Boot**, delivered through an automated
DevOps pipeline: **Git/GitHub → Jenkins → Docker → Docker Compose**.

> CSE 816 Software Production Engineering — Mini-Project

## Tech stack

| Area | Tool |
|---|---|
| Language / framework | Java 21, Spring Boot 4 (Spring Web MVC, Actuator) |
| Build | Maven (via the Maven Wrapper `./mvnw`) |
| Testing | JUnit (Jupiter), Spring MockMvc |
| Version control | Git / GitHub |
| CI/CD | Jenkins (declarative pipeline) |
| Containers | Docker, Docker Compose |

## Prerequisites

- JDK 21+
- No separate Maven installation is required — use the bundled wrapper (`./mvnw`).

## Build and run

```bash
# Compile, run tests and package the executable JAR
./mvnw clean package

# Run the application
java -jar target/calculator-api.jar
```

Health check:

```bash
curl http://localhost:8080/actuator/health
# {"groups":["liveness","readiness"],"status":"UP"}
```

## API

Base URL: `http://localhost:8080`

| Operation | Endpoint | Behaviour |
|---|---|---|
| Add | `GET /api/v1/add?a=&b=` | Returns `a + b` |
| Subtract | `GET /api/v1/subtract?a=&b=` | Returns `a - b` |
| Multiply | `GET /api/v1/multiply?a=&b=` | Returns `a * b` |
| Divide | `GET /api/v1/divide?a=&b=` | Returns `a / b`; `b=0` → HTTP 400 |
| Health | `GET /actuator/health` | Spring Boot Actuator health probe (`UP`/`DOWN`) |

### Example requests

```bash
curl "http://localhost:8080/api/v1/add?a=2&b=3"
# {"operation":"add","a":2.0,"b":3.0,"result":5.0}

curl "http://localhost:8080/api/v1/divide?a=7&b=2"
# {"operation":"divide","a":7.0,"b":2.0,"result":3.5}
```

### Errors

Every invalid request returns **HTTP 400** with a structured JSON body:

```bash
curl "http://localhost:8080/api/v1/divide?a=5&b=0"
# {"timestamp":"2026-10-29T17:00:07.794795Z","status":400,"error":"Bad Request",
#  "message":"Division by zero is not allowed","path":"/api/v1/divide"}
```

| Cause | Example | `message` |
|---|---|---|
| Division by zero | `/divide?a=5&b=0` | `Division by zero is not allowed` |
| Missing parameter | `/add?a=2` | `Missing required parameter 'b'` |
| Non-numeric parameter | `/add?a=abc&b=2` | `Parameter 'a' must be a valid number` |
| NaN / Infinity operand | `/subtract?a=NaN&b=1` | `Operands must be finite numbers` |
| Overflow | `/multiply?a=1e308&b=10` | `Result is out of the representable numeric range` |

## Testing

```bash
./mvnw test
```

- **Unit tests** (`CalculatorServiceTest`) — JUnit Jupiter tests for every operation, including division by zero, overflow and invalid operands.
- **Integration tests** (`CalculatorControllerIntegrationTest`) — start the Spring context and call the real endpoints through MockMvc, including error responses and `/actuator/health`.
- **Reports** — JUnit XML in `target/surefire-reports/` (published by Jenkins); JaCoCo coverage in `target/site/jacoco/index.html`.

## Docker

_Coming soon._

## CI/CD pipeline

_Coming soon._
