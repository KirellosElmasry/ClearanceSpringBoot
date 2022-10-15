FROM openjdk:8
ADD target/clearance-0.0.1-SNAPSHOT.jar docker-spring-boot.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "docker-spring-boot.jar"]