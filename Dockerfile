FROM adoptopenjdk/openjdk11:alpine AS build
MAINTAINER Romain Boisselle <romain@kodein.net>

RUN apk update && apk upgrade && apk add bash

RUN mkdir -p /app
COPY . /app
WORKDIR /app
RUN ./gradlew build -x test

FROM adoptopenjdk/openjdk11:alpine-jre

RUN apk update && apk upgrade && apk add bash

ENV APPLICATION_USER ktor
RUN adduser -D -g '' $APPLICATION_USER

RUN mkdir /app
RUN chown -R $APPLICATION_USER /app

USER $APPLICATION_USER

COPY --from=build /app/build/libs/modern-cloud-with-kotlin-0.0.1.jar /app.jar

WORKDIR /app
EXPOSE 8001

CMD exec java -jar /app.jar