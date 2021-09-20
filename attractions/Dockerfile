FROM adoptopenjdk/openjdk11:latest
COPY target/*-0.0.1-SNAPSHOT.jar /attractions.jar
CMD java -jar /attractions.jar
ENTRYPOINT [ "java", "-jar", "/attractions.jar" ]