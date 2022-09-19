# Build
FROM maven:3.8.3-jdk-11 as build-env

VOLUME /tmp
WORKDIR /

COPY ./pom.xml .

RUN mvn dependency:go-offline -B

COPY ./src ./src

#ENV AWS_REGION=us-east-2

RUN mvn package -DskipTests=true
RUN ls
RUN mv ./target/*.jar /*.jar

# Package
FROM public.ecr.aws/amazoncorretto/amazoncorretto:11-al2-jdk

WORKDIR /app

COPY --from=build-env /*.jar service.jar

#ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/service.jar"]
ENTRYPOINT exec java -jar service.jar
