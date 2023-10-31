#Stage 1
# initialize build and set base image for first stage
FROM maven:3.6.3-adoptopenjdk-11 AS stage1
# speed up Maven JVM a bit
ENV MAVEN_OPTS="-XX:+TieredCompilation -XX:TieredStopAtLevel=1"
# set working directory
WORKDIR /opt/bank-account
# copy just pom.xml
COPY pom.xml .
# go-offline using the pom.xml
RUN mvn dependency:go-offline
# copy your other files
COPY ../src ./src
COPY ../.env .
# compile the source code and package it in a jar file
RUN mvn clean install -DskipTests=true
#Stage 2
# set base image for second stage
FROM adoptopenjdk/openjdk11:jre-11.0.9_11-alpine
# set deployment directory
WORKDIR /opt/bank-account
# copy over the built artifact from the maven image
COPY --from=stage1 /opt/bank-account/target/*.jar app.jar
CMD ["java", "-jar", "app.jar"]