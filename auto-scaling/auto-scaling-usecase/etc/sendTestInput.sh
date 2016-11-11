#!/usr/bin/env bash

KAFKA_PATH=/home/openet/Programs/kafka_2.11-0.9.0.0/bin
KAFKA_TOPIC_INPUT=com.openet.autoscaling

$KAFKA_PATH/kafka-console-producer.sh --broker-list localhost:9092 --topic $KAFKA_TOPIC_INPUT < sample_input.json