# syntax=docker/dockerfile:1
FROM gradle:7.1-jdk16 AS build
ENV APP_HOME=/USD-EUR-converter/
WORKDIR $APP_HOME
COPY . ./
RUN gradle build

FROM openjdk:16-alpine
ENV APP_HOME=/USD-EUR-converter/
WORKDIR $APP_HOME
COPY  --from=build $APP_HOME/build/USD-EUR-converter-1.0-SNAPSHOT.jar ./
COPY  ./token.txt ./

EXPOSE 8888
ENTRYPOINT exec java -jar USD-EUR-converter-1.0-SNAPSHOT.jar
