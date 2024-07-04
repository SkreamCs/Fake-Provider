FROM openjdk:21-slim

WORKDIR /app

COPY build/libs/provider-0.0.1-SNAPSHOT.jar /app/provider-0.0.1-SNAPSHOT.jar

EXPOSE 8080

CMD ["java", "-jar", "provider-0.0.1-SNAPSHOT.jar"]