FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

# Copiar solo los archivos de configuración primero (para cachear dependencias)
COPY alixxpres/mvnw .
COPY alixxpres/.mvn .mvn
COPY alixxpres/pom.xml .

# Descargar dependencias (esta capa se cachea)
RUN chmod +x ./mvnw
RUN ./mvnw dependency:go-offline

# Copiar el código fuente (esta capa cambia más seguido)
COPY alixxpres/src src

# Compilar la aplicación
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT [ "java", "-jar", "app.jar" ]
