/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openet.labs.ml.autoscale;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author openet
 */
public class AutoScalingTestCommon {

    public Properties getTestProperties() {

        Properties props = new Properties();

        // Kafka
        props.setProperty("kafka.host", "localhost");
        props.setProperty("kafka.topic", "com.openet.autoscaling");
        props.setProperty("kafka.topic.train", "com.openet.autoscaling.train");
        props.setProperty("kafka.consumer.group", "enigma");
        props.setProperty("kafka.perTopicKafkaPartitions", "1");
        props.setProperty("kafka.zk.quorum", "localhost:2181");
        props.setProperty("kafka.broker", "localhost:9092");

        // Spark         
        props.setProperty("spark.streaming.duration", "2000");

        // Use Case
        props.setProperty("autoscaling.future.interval", "60");

        return props;

    }

}
