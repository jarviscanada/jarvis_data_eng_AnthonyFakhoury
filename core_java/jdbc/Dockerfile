FROM openjdk:8-jre-slim

RUN mkdir -p /usr/local/app/stockquote

COPY target/jdbc-1.0-SNAPSHOT.jar /usr/local/app/stockquote/stockquote.jar

COPY src/main/resources/properties.txt /usr/local/app/stockquote/src/main/resources/properties.txt

WORKDIR "/usr/local/app/stockquote"

ENTRYPOINT ["java", "-jar", "stockquote.jar"]