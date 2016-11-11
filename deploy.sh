#!/usr/bin/env bash

DIR_BIN=bin
APP_JAR=auto-scaling/auto-scaling-usecase/target/auto-scaling-usecase-1.0-SNAPSHOT.jar
APP_PROPS=auto-scaling/auto-scaling-usecase/etc/autoscale.properties
APP_SCRIPT=auto-scaling/auto-scaling-usecase/etc/runAutoScaling.sh
LOG4J_PROPS=auto-scaling/auto-scaling-usecase/etc/log4j.properties
TRAIN_JAR=auto-scaling/training-data-generator/target/training-data-generator-1.0-SNAPSHOT-executable.jar
TRAIN_PROPS=auto-scaling/training-data-generator/application.properties


mkdir -p $DIR_BIN
cp $APP_JAR bin/
cp $APP_PROPS bin/
cp $APP_SCRIPT bin/
cp $LOG4J_PROPS bin/

