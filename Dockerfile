FROM openjdk:11-jdk-slim

EXPOSE 8080

ARG PROFILE=prod
ENV PROFILE=${PROFILE}

ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]