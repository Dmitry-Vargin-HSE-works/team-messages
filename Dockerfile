FROM gradle:7.0.2-jdk11 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon

FROM gradle:7.0.2-jdk11
RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/*.jar /app/spring-chat.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Dspring.profiles.active=default", "-jar","/app/spring-chat.jar", "--Dserver.port=8080"]