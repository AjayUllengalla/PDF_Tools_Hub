# Use official Java 17 image
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy all backend files
COPY . .

# Give permission to mvnw
RUN chmod +x mvnw

# Build the project (skip tests for faster build)
RUN ./mvnw clean package -DskipTests

# Expose port (Render uses dynamic PORT, but 8080 default)
EXPOSE 8080

# Run the Spring Boot app
CMD ["sh", "-c", "java -jar target/*.jar"]
