FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

# Copiar desde la subcarpeta alixxpres
COPY alixxpres/mvnw .
COPY alixxpres/.mvn .mvn
COPY alixxpres/pom.xml .
COPY alixxpres/src src

RUN chmod +x ./mvnw
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT [ "java", "-jar", "app.jar" ]
