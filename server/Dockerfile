# Oriented on equivilat file from the Unicorn Project
# https://spring.io/guides/topicals/spring-boot-docker

FROM openjdk:17-jdk-slim

WORKDIR /server
COPY . .

COPY build.gradle.kts settings.gradle.kts gradlew ./
COPY gradle ./gradle
RUN ./gradlew build --warning-mode all|| return 0

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "build/libs/server-0.0.1-SNAPSHOT.jar", "--server.port=8080"]

