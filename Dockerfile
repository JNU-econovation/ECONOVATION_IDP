FROM openjdk:11-jdk-slim

EXPOSE 8080

ARG PROFILE=prod
ENV PROFILE=${PROFILE}

ENTRYPOINT ["java","-Dspring.profiles.active=${PROFILE}", "-Djava.security.egd=file:/dev/./urandom","-jar","-Duser.timezone=Asia/Seoul","/app.jar"]
