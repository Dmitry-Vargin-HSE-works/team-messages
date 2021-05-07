FROM gradle:jdk11 as TEMP_BUILD_IMAGE
FROM adoptopenjdk:11-jre-hotspot
ENV APP_HOME=/usr/src/app/
COPY . $APP_HOME
WORKDIR $APP_HOME
RUN ./gradlew build || return 0
COPY . .
RUN ./gradlew build

FROM gradle:jdk11
ENV ARTIFACT_NAME=your-application.jar
ENV APP_HOME=/usr/app/
WORKDIR $APP_HOME
COPY --from=TEMP_BUILD_IMAGE $APP_HOME/build/libs/$ARTIFACT_NAME .
EXPOSE 8080
CMD ["java","-jar", "$ARTIFACT_NAME"]
