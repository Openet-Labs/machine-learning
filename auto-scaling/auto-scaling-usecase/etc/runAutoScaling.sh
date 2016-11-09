#!/usr/bin/env bash

ENIGMA_SPARK_HOME=/home/openet/enigma/spark/spark-1.6.1-bin-hadoop2.6

getTag()
{
    grep "^$1=" $2 | awk -F= '{ print $2 }'
}

function sparkSubmit()
{    
    APPNAME="com.openet.enigma.autoscaling"
    LOG4J_PROPERTIES_PATH=${ENIGMA_USECASES_HOME}/log4j.properties

    CLASS=${1}
    APPJAR=${2}

    shift 2
    
    MASTER=local[2]    
    NUMBER_OF_CORES=4    
    NUMBER_OF_EXECUTORS=1  
    EXECUTOR_MEMORY=512m    
    DRIVER_MEMORY=1G  
    DURATION=5000
    

 ${ENIGMA_SPARK_HOME}/bin/spark-submit \
 --class ${CLASS} \
 --master ${MASTER} \
 --executor-cores ${NUMBER_OF_CORES} \
 --num-executors ${NUMBER_OF_EXECUTORS} \
 --executor-memory ${EXECUTOR_MEMORY} \
 --driver-memory ${DRIVER_MEMORY} \
 --conf spark.appName=${APPNAME} \
 --conf spark.duration=${DURATION} \
 --driver-java-options "-Dlog4j.configuration=file:${LOG4J_PROPERTIES_PATH} -DENIGMA_HOME=${ENIGMA_HOME} -DAPPNAME=${APPNAME}" \
 --conf "spark.executor.extraJavaOptions=-Dlog4j.configuration=file:${LOG4J_PROPERTIES_PATH} -DENIGMA_HOME=${ENIGMA_HOME} -DAPPNAME=${APPNAME}" \
 --conf "spark.driver.extraJavaOptions=-Dlog4j.configuration=file:${LOG4J_PROPERTIES_PATH} -DENIGMA_HOME=${ENIGMA_HOME} -DAPPNAME=${APPNAME}" \
 ${APPJAR} \
 "$@"
}



CLASSPATH=com.openet.labs.ml.autoscale.AutoScalingMain
JAR=auto-scaling-usecase-1.0-SNAPSHOT.jar
USECASE_PROPERTIES=autoscale.properties

sparkSubmit ${CLASSPATH} ${JAR} --usecase-properties ${USECASE_PROPERTIES}