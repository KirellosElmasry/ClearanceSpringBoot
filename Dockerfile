FROM openjdk:8
COPY target/docker-spring-boot.jar docker-spring-boot.jar
EXPOSE 8082
CMD ["java", "-jar", "/docker-spring-boot.jar"]
