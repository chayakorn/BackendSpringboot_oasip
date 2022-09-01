#define base docker image

FROM maven AS build-stage
WORKDIR /backend
COPY . .
RUN mvn clean install -DskipTests

FROM openjdk:11
COPY --from=build-stage /backend/target/backend-0.0.1-SNAPSHOT.jar backend.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar" , "backend.jar"]