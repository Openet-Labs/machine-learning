/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openet.labs.ml.autoscale;

import java.io.Serializable;
import java.util.Properties;

/**
 *
 * @author openet
 */
public class PropertiesParser implements Serializable {

    private static final long serialVersionUID = 1L;

    public String getKafkaHost(Properties properties) {
        return properties.getProperty("kafka.host");
    }

    public String getKafkaTopic(Properties properties) {
        return properties.getProperty("kafka.topic");
    }

    public String getKafkaTopicTrain(Properties properties) {
        return properties.getProperty("kafka.topic.train");
    }

    public String getKafkaConsumerGroup(Properties properties) {
        return properties.getProperty("kafka.consumer.group");
    }

    public String getKafkaZookeeperQuorum(Properties properties) {
        return properties.getProperty("kafka.zk.quorum");
    }

    public String getKafkaBroker(Properties properties) {
        return properties.getProperty("kafka.broker");
    }

    public Integer getPerTopicKafkaPartitions(Properties properties) {
        try {
            return Integer.parseInt(properties.getProperty("kafka.perTopicKafkaPartitions"));
        } catch (NumberFormatException | NullPointerException ex) {
            return 1;
        }
    }

    public Integer getPredictionInterval(Properties properties) {
        try {
            return Integer.parseInt(properties.getProperty("autoscaling.future.interval"));
        } catch (NumberFormatException | NullPointerException ex) {
            return 1;
        }
    }
    
    public Integer getStreamingDuration(Properties properties) {
        try {
            return Integer.parseInt(properties.getProperty("spark.streaming.duration"));
        } catch (NumberFormatException | NullPointerException ex) {
            return 1;
        }
    }

}
