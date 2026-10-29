# syntax=docker/dockerfile:1

# =============================================================================
# Stage 1 — build: compile and package the Spring Boot JAR with Maven + JDK 21
# =============================================================================
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build

# Copy only the POM first and download dependencies. This layer is cached and
# re-used on every build until pom.xml changes, so source edits rebuild fast.
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

# Now copy the sources and build. Tests are run by the Jenkins Test stage,
# so they are skipped here to keep image builds fast.
COPY src ./src
RUN mvn -B -q package -DskipTests

# =============================================================================
# Stage 2 — runtime: minimal JRE image that contains only the JAR
# =============================================================================
FROM eclipse-temurin:21-jre-alpine

LABEL org.opencontainers.image.title="calculator-api" \
      org.opencontainers.image.description="Calculator REST API (Spring Boot)" \
      org.opencontainers.image.source="https://github.com/KunalJindal19/SPE_MiniProject"

# Run as an unprivileged user instead of root
RUN addgroup -S spring && adduser -S spring -G spring

WORKDIR /app

# Only the built JAR is copied from the build stage — no sources, no Maven, no JDK
COPY --from=build /build/target/calculator-api.jar app.jar

USER spring

EXPOSE 8080

# Container-aware heap sizing; can be overridden at runtime with -e JAVA_TOOL_OPTIONS=...
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75.0"

# Lets `docker ps` report healthy/unhealthy (busybox wget is available in Alpine)
HEALTHCHECK --interval=15s --timeout=3s --start-period=30s --retries=3 \
    CMD wget -qO- http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
