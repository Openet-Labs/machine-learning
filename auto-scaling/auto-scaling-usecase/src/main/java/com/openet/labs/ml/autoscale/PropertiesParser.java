/**************************************************************************
 *
 * Copyright © Openet Telecom, Ltd. 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **************************************************************************/

package com.openet.labs.ml.autoscale;

import java.io.Serializable;
import java.util.Properties;

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
