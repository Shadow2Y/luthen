# ============================
# 1️⃣ Build Stage
# ============================
FROM maven:3.9.9-eclipse-temurin-21 AS build

# Set working directory
WORKDIR /app

# Copy pom.xml and download dependencies first (for better caching)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the rest of the source code
COPY src ./src

# Build the application (skip tests for faster builds)
RUN mvn clean package -DskipTests

# ============================
# 2️⃣ Runtime Stage
# ============================
FROM eclipse-temurin:21-jre-alpine

# Set working directory
WORKDIR /app

# Copy only the built JAR from previous stage
COPY --from=build /app/target/*.jar app.jar

# Expose application port (adjust if needed)
EXPOSE 8080

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]
