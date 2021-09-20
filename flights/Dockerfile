FROM adoptopenjdk/openjdk11:latest
COPY target/*-0.0.1-SNAPSHOT.jar /flights.jar
CMD java -jar /flights.jar
ENTRYPOINT [ "java", "-jar", "/flights.jar" ]