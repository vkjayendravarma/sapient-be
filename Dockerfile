FROM ubuntu:latest
LABEL authors="vkjayendravarma"
FROM maven:3.8.3-openjdk-17 as build
LABEL authors="vkjayendravarma"

# Set working directory
WORKDIR /app

# Copy the Maven project files to the container
COPY pom.xml .
COPY src ./src

# Run Maven build to generate the JAR
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk
COPY --from=build /app/target/sapient-interview-vkjayendravarma.jar sapient-interview-vkjayendravarma.jar

ENTRYPOINT ["java", "-jar", "sapient-interview-vkjayendravarma.jar"]

EXPOSE 8080