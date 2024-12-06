FROM ubuntu:20.04 AS build
RUN apt-get update
RUN apt-get install openjdk-21-jdk -y
COPY . .
RUN ./gradlew bootJar --no-daemon

FROM openjdk:21-jdk-slim
EXPOSE 8080
COPY --from=build ./build/libs/software_project_sem4-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
