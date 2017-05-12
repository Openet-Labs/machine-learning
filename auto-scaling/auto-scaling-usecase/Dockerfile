FROM pjfanning/docker-spark-jdk8:1.6.2


COPY /etc/autoscale.properties /usr/share/autoScale/autoscale.properties
COPY /etc/log4j.properties /usr/share/autoScale/log4j.properties
COPY /etc/runAutoScaling.sh /usr/share/autoScale/runAutoScaling.sh
COPY /etc/sample_input.json /usr/share/autoScale/sample_input.json
COPY /etc/sendTestInput.sh /usr/share/autoScale/sendTestInput.sh
COPY /etc/startNetcatRestListerner.sh /usr/share/autoScale/startNetcatRestListerner.sh
COPY /etc/bootstrap.sh /usr/share/autoScale/bootstrap.sh

# Add the service itself
COPY /target/auto-scaling-usecase-1.0-SNAPSHOT.jar /usr/share/autoScale/auto-scaling-usecase-1.0-SNAPSHOT.jar

ENTRYPOINT ["/usr/share/autoScale/bootstrap.sh"]