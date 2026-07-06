# ==========================================
# Stage 1: Build the application using Maven
# ==========================================
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copy only the pom.xml first to cache dependencies (improves build times)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the actual source code
COPY src ./src

# Package the application (skipping unit tests to speed up the build)
RUN mvn clean package -DskipTests

# ==========================================
# Stage 2: Run the application using JRE
# ==========================================
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copy the compiled JAR from the build stage
# Note: Spring Boot builds target/*.jar. We rename it to app.jar for simplicity.
COPY --from=build /app/target/*.jar app.jar

# Expose the default Spring Boot port
EXPOSE 8080

# Run the application with optimized memory settings and the 'local' profile active
ENTRYPOINT ["java", "-Dspring.profiles.active=local", "-jar", "app.jar"]