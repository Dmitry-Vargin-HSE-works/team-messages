FROM gradle:7.0.2-jdk11 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon

FROM amazoncorretto:11-alpine
COPY --from=build /home/gradle/src/build/libs/*.jar Application.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar","Application.jar", "-Dspring.profiles.active=default", "--Dserver.port=8080"]