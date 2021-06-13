FROM gradle:7.0.2-jdk11 AS build
COPY --chown=gradle:gradle . /home
WORKDIR /home
RUN gradle build

FROM bellsoft/liberica-openjdk-alpine:11
COPY --from=build /home/build/libs/*.jar Application.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar","Application.jar", "-Dspring.profiles.active=default", "--Dserver.port=8080"]