FROM gradle:7.0.2-jdk11 AS build
COPY --chown=gradle:gradle ./.gradle /home/app/.gradle
COPY --chown=gradle:gradle ./gradle /home/app/gradle
COPY --chown=gradle:gradle ./src /home/app/src
COPY --chown=gradle:gradle ./build.gradle /home/app/build.gradle
COPY --chown=gradle:gradle ./settings.gradle /home/app/settings.gradle
WORKDIR /home/app

RUN gradle build

FROM bellsoft/liberica-openjdk-alpine:11
COPY --from=build /home/app/build/libs/*.jar Application.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar","Application.jar", "-Dspring.profiles.active=default", "--Dserver.port=8080"]

# wget http://security.ubuntu.com/ubuntu/pool/main/a/apt/apt_1.0.1ubuntu2.19_amd64.deb -O apt.deb
