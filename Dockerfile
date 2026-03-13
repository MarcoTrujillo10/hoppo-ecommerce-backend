FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

COPY . .

RUN mvn package -DskipTests

FROM eclipse-temurin:21-jdk

WORKDIR /app

# Install curl for healthcheck
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

COPY --from=build /app/target/*.jar app.jar

COPY uploads ./uploads

EXPOSE 8081

CMD ["java", "-jar", "app.jar"]
