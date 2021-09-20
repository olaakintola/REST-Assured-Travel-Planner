FROM adoptopenjdk/openjdk11:latest
# EXPOSE 8761
COPY target/*-0.0.1-SNAPSHOT.jar /eureka.jar
CMD java -jar /eureka.jar
ENTRYPOINT [ "java", "-jar", "/eureka.jar" ]