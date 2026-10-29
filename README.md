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

_Coming soon._

## Docker

_Coming soon._

## CI/CD pipeline

_Coming soon._
