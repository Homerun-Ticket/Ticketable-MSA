FROM amazoncorretto:17 AS builder
WORKDIR /app
COPY ../gradle/ /app/gradle/
COPY ../gradlew /app/
COPY ../build.gradle /app/
COPY ../settings.gradle /app/
COPY module-gateway/ /app/module-gateway/
RUN ./gradlew :module-gateway:build -x test

FROM amazoncorretto:17
LABEL maintainer="Inhak <inhak1122@google.com>"
WORKDIR /app
COPY --from=builder /app/module-gateway/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]