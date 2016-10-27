/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openet.labs.machineLearning.autoScale;

import java.util.Properties;

/**
 *
 * @author openet
 */
public class PropertiesParser {
    
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

}
