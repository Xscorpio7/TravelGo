# Dockerfile para Backend
FROM maven:3.8.8-openjdk-17 AS builder
WORKDIR /build

# Cache de dependencias
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Build
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime image
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

RUN apt-get update && apt-get install -y netcat && rm -rf /var/lib/apt/lists/*

COPY --from=builder /build/target/*.jar app.jar
COPY wait-for.sh /wait-for.sh
RUN chmod +x /wait-for.sh

ENV SPRING_PROFILES_ACTIVE=production

EXPOSE 9090
ENTRYPOINT ["/wait-for.sh", "mysql:3306", "--", "java", "-jar", "app.jar"]