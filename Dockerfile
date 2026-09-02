# Small, JRE-only base image (no full JDK needed to just run a built jar)
FROM eclipse-temurin:25-jre-alpine

WORKDIR /tmp

# Copy the executable jar built by `mvn clean package` into the image
COPY target/*.jar app.jar

# Document that the app listens on 8080 inside the container (informational only,
# doesn't actually publish the port - that happens with `docker run -p`)
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
