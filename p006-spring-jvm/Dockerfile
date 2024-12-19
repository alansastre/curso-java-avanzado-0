# OBLIGATORIO: EJECUTAR PRIMERO MAVEN: ./mvnw clean package -DskipTests
FROM eclipse-temurin:23-jre-alpine
# copiar el jar
COPY target/*.jar app.jar
# ejecutar el jar, añadir VM options si se quiere
CMD ["java", "-jar", "app.jar"]

# Construir la imagen
# docker build -t spring-thymeleaf:1.0.0 .

# Ejecutar la imagen
# docker run -p 8080:8080 spring-thymeleaf:1.0.0