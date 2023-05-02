FROM docker.io/amazoncorretto:17-alpine-jdk
MAINTAINER "FPR"
COPY m-infrastructure/build/libs/m-infrastructure-0.0.1-SNAPSHOT.jar fpr.jar

RUN apk add curl

ENTRYPOINT ["java","-jar","/fpr.jar"]