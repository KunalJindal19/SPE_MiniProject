# Stage 1: Build the JAR using Maven and JDK 21
FROM maven:3.9-eclipse-temurin-21 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy pom.xml first and download dependencies.
# This layer is cached and only rebuilt when pom.xml changes.
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the source code and build the JAR (tests are run in the Jenkins Test stage)
COPY src ./src
RUN mvn package -DskipTests

# Stage 2: Create a lightweight runtime image with only the JRE
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Create a non-root user to run the application
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copy only the built JAR from the build stage
COPY --from=build /app/target/calculator-api.jar app.jar

# Run as the non-root user
USER appuser

# The application listens on port 8080
EXPOSE 8080

# Start the application
ENTRYPOINT ["java", "-jar", "app.jar"]
