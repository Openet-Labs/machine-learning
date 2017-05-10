FROM java:8

ENTRYPOINT ["/usr/bin/java", "-jar", "/usr/share/myservice/auto-scaling-usecase-1.0-SNAPSHOT.jar"]

# Add the service itself
ADD target/auto-scaling-usecase-1.0-SNAPSHOT.jar /usr/share/myservice/auto-scaling-usecase-1.0-SNAPSHOT.jar