# Version: 0.1
FROM adoptopenjdk/openjdk9-openj9:latest
MAINTAINER Jakub Jozwicki jakub.jozwicki@gmail.com
COPY TimeZoneService-0.0.1-SNAPSHOT.jar /tmp/
ENTRYPOINT java --add-modules=java.xml.bind --illegal-access=permit -Xgcpolicy:metronome -Xquickstart -XX:+IdleTuningGcOnIdle -Xtune:virtualized -jar /tmp/TimeZoneService-0.0.1-SNAPSHOT.jar
