# Build the JAR first:
#   ./mvnw clean package -DskipTests -Dskip.frontend=true
# Then:
#   docker-compose -f docker/docker-compose.yml --env-file docker/.env build app-backend

# ── Stage 1: extract ──────────────────────────────────────────────────────────
# Unpack the JAR so classpath resources are real filesystem files.
# Required because LoginNotificationConfig.databaseReader() calls
# ClassPathResource.getFile(), which fails when the resource is nested
# inside a JAR archive.
FROM eclipse-temurin:21-jre-jammy AS extractor
WORKDIR /app
COPY target/*.jar app.jar
RUN java -Djarmode=layertools -jar app.jar extract

# ── Stage 2: runtime ──────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

RUN groupadd -r spring && useradd -r -g spring spring

COPY --from=extractor /app/dependencies/          ./
COPY --from=extractor /app/spring-boot-loader/    ./
COPY --from=extractor /app/snapshot-dependencies/ ./
COPY --from=extractor /app/application/           ./

USER spring

EXPOSE 8080

ENTRYPOINT ["java", \
            "-XX:MaxRAMPercentage=75.0", \
            "-XX:+ExitOnOutOfMemoryError", \
            "org.springframework.boot.loader.JarLauncher"]

            