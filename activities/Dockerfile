FROM adoptopenjdk/openjdk11:latest
COPY target/*-0.0.1-SNAPSHOT.jar /activities.jar
CMD java -jar /activities.jar
ENTRYPOINT [ "java", "-jar", "/activities.jar" ]