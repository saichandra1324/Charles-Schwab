FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml /app/pom.xml
COPY account-service/pom.xml /app/account-service/pom.xml
COPY gateway-service/pom.xml /app/gateway-service/pom.xml
COPY account-service/src /app/account-service/src
RUN mvn -pl account-service -am clean package -DskipTests
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/account-service/target/account-service-1.0.0.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
