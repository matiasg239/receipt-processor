FROM sbtscala/scala-sbt:eclipse-temurin-23.0.1_11_1.10.7_3.6.3 AS stage0
WORKDIR /app
COPY . ./
RUN sbt assembly
EXPOSE 8080
CMD ["java", "-jar", "/app/target/scala-3.3.3/receiptProcessor-assembly-0.0.1.jar"]
