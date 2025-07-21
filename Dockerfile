FROM openjdk:21-jdk-slim

VOLUME /tmp

ARG JAR_FILE=build/libs/app.jar

COPY ${JAR_FILE} app.jar

ENV JAVA_OPTS="-Xms128m -Xmx512m"

ENTRYPOINT ["java", "$JAVA_OPTS", "-jar", "app.jar"]