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

import java.util.Properties;

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
