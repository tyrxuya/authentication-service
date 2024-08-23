# Use the official OpenJDK image as the base image
FROM amazoncorretto:21-alpine

# Set the working directory within the container
WORKDIR /authentication

# Copy the packaged JAR file from the target directory into the container
COPY rest/target/authentication.jar authentication.jar
# Expose the port that your application runs on
EXPOSE 8083

# Define the command to run your application
CMD ["java", "-jar", "authentication.jar"]