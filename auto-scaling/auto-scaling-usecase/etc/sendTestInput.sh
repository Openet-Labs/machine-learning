#!/usr/bin/env bash

KAFKA_PATH=/home/vagrant/enigma/kafka/kafka_2.10-0.9.0.1/bin
KAFKA_TOPIC_INPUT=com.openet.autoscaling

$KAFKA_PATH/kafka-console-producer.sh --broker-list localhost:9092 --topic $KAFKA_TOPIC_INPUT < sample_input.json