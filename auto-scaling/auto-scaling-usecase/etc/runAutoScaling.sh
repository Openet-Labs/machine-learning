#/**************************************************************************
# *
# * Copyright © Openet Telecom, Ltd. 
# *
# * Licensed under the Apache License, Version 2.0 (the "License");
# * you may not use this file except in compliance with the License.
# * You may obtain a copy of the License at
# *
# *    http://www.apache.org/licenses/LICENSE-2.0
# *
# * Unless required by applicable law or agreed to in writing, software
# * distributed under the License is distributed on an "AS IS" BASIS,
# * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# * See the License for the specific language governing permissions and
# * limitations under the License.
# **************************************************************************/

#!/usr/bin/env bash

getTag()
{
    grep "^$1=" $2 | awk -F= '{ print $2 }'
}

function sparkSubmit()
{    
    APPNAME="com.openet.enigma.autoscaling"    

    CLASS=${1}
    APPJAR=${2}

    shift 2
    
    MASTER=local[2]    
    NUMBER_OF_CORES=4    
    NUMBER_OF_EXECUTORS=1  
    EXECUTOR_MEMORY=512m    
    DRIVER_MEMORY=1G  
    DURATION=5000
    

 ${SPARK_HOME}/bin/spark-submit \
 --class ${CLASS} \
 --master ${MASTER} \
 --executor-cores ${NUMBER_OF_CORES} \
 --num-executors ${NUMBER_OF_EXECUTORS} \
 --executor-memory ${EXECUTOR_MEMORY} \
 --driver-memory ${DRIVER_MEMORY} \
 --conf spark.appName=${APPNAME} \
 --conf spark.duration=${DURATION} \
 --driver-java-options "-Dlog4j.configuration=file:${LOG4J_PROPERTIES_PATH} -DAPPNAME=${APPNAME}" \
 --conf "spark.executor.extraJavaOptions=-Dlog4j.configuration=file:${LOG4J_PROPERTIES_PATH} -DAPPNAME=${APPNAME}" \
 --conf "spark.driver.extraJavaOptions=-Dlog4j.configuration=file:${LOG4J_PROPERTIES_PATH} -DAPPNAME=${APPNAME}" \
 ${APPJAR} \
 "$@"
}

SPARK_HOME=/home/vagrant/enigma/spark/spark-1.6.1-bin-hadoop2.6

LOG4J_PROPERTIES_PATH=log4j.properties

CLASSPATH=com.openet.labs.ml.autoscale.AutoScalingMain
JAR=auto-scaling-usecase-1.0-SNAPSHOT.jar
USECASE_PROPERTIES=autoscale.properties

sparkSubmit ${CLASSPATH} ${JAR} --usecase-properties ${USECASE_PROPERTIES} &