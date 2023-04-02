FROM eclipse-temurin:17-jdk-alpine
COPY target/argus.jar argus.jar
ENV ARGUS_SETTINGS_FILE=/argus/settings.yaml
ENTRYPOINT ["java","-jar","/argus.jar"]
