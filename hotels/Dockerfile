FROM adoptopenjdk/openjdk11:latest
COPY target/*-0.0.1-SNAPSHOT.jar /hotels.jar
CMD java -jar /hotels.jar
ENTRYPOINT [ "java", "-jar", "/hotels.jar" ]