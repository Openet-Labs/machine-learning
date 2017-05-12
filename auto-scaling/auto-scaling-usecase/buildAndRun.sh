#!/bin/bash

mvn clean package -DskipTests=true
docker run -it -p 7077:7077 -p 8042:8042 -a STDOUT -a STDERR com.openet.labs.enigma/auto-scaling-usecase:1.0-SNAPSHOT bash

