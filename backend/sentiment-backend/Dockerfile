# VERSIÓN OPTIMIZADA PARA PRODUCCIÓN Y NUBE
# Mantiene compatibilidad con el flujo de construcción de reportes

# --- ETAPA 1: BUILD ---
# Imagen base con Java 21 y Maven para construir el proyecto
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build

# Directorio de trabajo
WORKDIR /app

# COPIA DE MANERA SELECTIVA (Estructura para aprovechar caché de Docker)
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Descargar dependencias (esto se cachea si el pom.xml no cambia)
RUN mvn dependency:go-offline

# Copiar el código fuente
COPY src ./src

# CONSTRUCCIÓN: Empaqueta el JAR y genera Javadoc + JaCoCo
# Se usa 'verify' para asegurar que los tests se ejecuten y generen los reportes
RUN mvn clean verify javadoc:javadoc -DskipTests=false

# --- ETAPA 2: RUN ---
# Imagen final mucho más ligera (JRE) para ejecución en la nube
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copiamos el JAR generado desde la etapa anterior
COPY --from=build /app/target/*.jar app.jar

# COPIAMOS LOS REPORTES generados para que WebConfig pueda servirlos
# Esto permite que /docs/jacoco y /docs/apidocs funcionen en el contenedor
COPY --from=build /app/target/site ./target/site

# Puerto expuesto
EXPOSE 8080

# COMANDO PARA PRODUCCIÓN
# Se recomienda usar perfiles de Spring para manejar variables de entorno en la nube
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]