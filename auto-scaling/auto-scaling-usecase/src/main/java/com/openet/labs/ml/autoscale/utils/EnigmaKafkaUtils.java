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

package com.openet.labs.ml.autoscale.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import kafka.api.OffsetRequest;
import kafka.api.PartitionOffsetRequestInfo;
import kafka.common.TopicAndPartition;
import kafka.javaapi.OffsetResponse;
import kafka.javaapi.consumer.SimpleConsumer;
import kafka.message.MessageAndMetadata;
import kafka.serializer.StringDecoder;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;
import org.apache.spark.streaming.kafka.OffsetRange;
import scala.Tuple2;

public class EnigmaKafkaUtils implements Serializable {

    private static final long serialVersionUID = 1L;

    public JavaRDD<String> kafkaGetRDD(JavaSparkContext jsc, String host, String kafkaTopicName, String kafkaCosumerGroup, String zookeeperQuorum, String broker, int perTopicKafkaPartitions) {

        //kafka streaming parameters
        Map<String, String> kafkaParams = new HashMap<>();
        kafkaParams.put("zookeeper.connect", zookeeperQuorum);
        kafkaParams.put("group.id", kafkaCosumerGroup);
        kafkaParams.put("auto.commit.enable", "false");
        kafkaParams.put("auto.offset.reset", "smallest");
        kafkaParams.put("metadata.broker.list", broker);

        int kafkaBrokerPort = getBrokerPort(broker);

        long fromOffset = getKafkaOffsets(true, host, kafkaBrokerPort, kafkaTopicName, kafkaCosumerGroup);
        long untilOffset = getKafkaOffsets(false, host, kafkaBrokerPort, kafkaTopicName, kafkaCosumerGroup);

        List<OffsetRange> offsetList = new ArrayList<>();
        for (int i = 0; i < perTopicKafkaPartitions; i++) {
            OffsetRange or = new OffsetRange(kafkaTopicName, i, fromOffset, untilOffset);
            offsetList.add(or);
        }
        OffsetRange[] offsetRange = offsetList.toArray(new OffsetRange[offsetList.size()]);

        JavaPairRDD<String, String> messages = KafkaUtils.createRDD(jsc, String.class, String.class, StringDecoder.class, StringDecoder.class, kafkaParams, offsetRange);

        return messages.map((Tuple2<String, String> tuple2) -> tuple2._2());

    }

    public long getKafkaOffsets(boolean isEarliestOffset, String host, int port, String kafkaTopic, String kafkaCosumerGroup) {

        SimpleConsumer simpleConsumer = new SimpleConsumer(host, port, 100000, 64 * 1024, kafkaTopic);
        TopicAndPartition topicAndPartition = new TopicAndPartition(kafkaTopic, 0);
        Map<TopicAndPartition, PartitionOffsetRequestInfo> requestInfo = new HashMap<>();
        if (isEarliestOffset) {
            requestInfo.put(topicAndPartition, new PartitionOffsetRequestInfo(OffsetRequest.EarliestTime(), 1));
        } else {
            requestInfo.put(topicAndPartition, new PartitionOffsetRequestInfo(OffsetRequest.LatestTime(), 1));
        }

        kafka.javaapi.OffsetRequest request = new kafka.javaapi.OffsetRequest(
                requestInfo, kafka.api.OffsetRequest.CurrentVersion(), kafkaCosumerGroup);
        OffsetResponse response = simpleConsumer.getOffsetsBefore(request);

        long[] offsets = response.offsets(kafkaTopic, 0);
        long resultOffset = offsets[0];

        return resultOffset;
    }

    public int getBrokerPort(String kafkaBroker) {

        int kafkaBrokerPort = Integer.parseInt(kafkaBroker.split(":")[1]);

        return kafkaBrokerPort;
    }

    public JavaDStream<String> getKafkaDirectInputStreamOffset(JavaStreamingContext jsc, String host, String kafkaTopic, String kafkaCosumerGroup, String zookeeperQuorum, String broker, boolean isEarliestOffset) {

        //kafka streaming parameters
        Map<String, String> kafkaParams = new HashMap<>();
        kafkaParams.put("zookeeper.connect", zookeeperQuorum);
        kafkaParams.put("group.id", kafkaCosumerGroup);
        kafkaParams.put("auto.commit.enable", "false");
        kafkaParams.put("auto.offset.reset", "smallest");
        kafkaParams.put("metadata.broker.list", broker);

        int kafkaBrokerPort = getBrokerPort(broker);

        Long startOffset = getKafkaOffsets(false, host, kafkaBrokerPort, kafkaTopic, kafkaCosumerGroup);
        if (isEarliestOffset) {
            startOffset = getKafkaOffsets(true, host, kafkaBrokerPort, kafkaTopic, kafkaCosumerGroup);
        }

        TopicAndPartition topicAndPartition = new TopicAndPartition(kafkaTopic, 0);
        Map<TopicAndPartition, Long> offsets = new HashMap<>();
        offsets.put(topicAndPartition, startOffset);

        JavaInputDStream<String> inputDStream = KafkaUtils.createDirectStream(jsc, String.class, String.class, StringDecoder.class, StringDecoder.class, String.class, kafkaParams, offsets, new Function<MessageAndMetadata<String, String>, String>() {
            public String call(MessageAndMetadata<String, String> v1) throws Exception {
                return v1.message();
            }
        }
        );

        return inputDStream;
    }

}
