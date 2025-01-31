FROM openjdk:17-alpine
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
EXPOSE 8080
ENTRYPOINT [ "java", "-jar", "/app.jar" ]
LABEL org.opencontainers.image.source=https://github.com/mako3/found \
    org.opencontainers.image.description="Simple viewer of archived chat log specialized for Google Chat."\
    org.opencontainers.image.licenses=MIT