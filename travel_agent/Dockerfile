FROM adoptopenjdk/openjdk11:latest
COPY target/*-0.0.1-SNAPSHOT.jar /travel_agent.jar
CMD java -jar /travel_agent.jar
ENTRYPOINT [ "java", "-jar", "/travel_agent.jar" ]