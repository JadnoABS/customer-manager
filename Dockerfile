# ---------- Build stage ----------
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests -B

# ---------- Runtime stage ----------
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Se o bug de cgroup aparecer mesmo em container, defina JAVA_TOOL_OPTIONS=-XX:-UseContainerSupport
# no docker-compose.yml (variável de ambiente) em vez de fixar aqui.
ENV JAVA_TOOL_OPTIONS=""

EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_TOOL_OPTIONS -jar app.jar"]
