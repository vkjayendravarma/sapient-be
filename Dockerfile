FROM ubuntu:latest
LABEL authors="vkjayendravarma"

FROM openjdk:17-jdk
ADD /target/sapient-interview-vkjayendravarma.jar sapient-interview-vkjayendravarma.jar

ENTRYPOINT ["java", "-jar", "sapient-interview-vkjayendravarma.jar"]

EXPOSE 8080