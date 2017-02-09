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
package com.openet.labs.ml.traindatagenerator.output;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

public class KafkaWriter implements Writer {

	private static Logger logger = Logger.getLogger(KafkaWriter.class);
	
	private String kafkaTrainTopic;
	private String kafkaBroker;
	private String kafkaGroupId;
	private Producer<Integer, String> producer;
	
	public KafkaWriter(String kafkaTrainTopic, String kafkaBroker, String kafkaGroupId) {
		super();
		this.kafkaTrainTopic = kafkaTrainTopic;
		this.kafkaBroker = kafkaBroker;
		this.kafkaGroupId = kafkaGroupId;
	}

	@Override
	public void write(JSONObject json) {
		if(null == producer) {
			producer = createProducer();
		}
		writeToKafkaTopic(producer, json);
	}

	/**
     * Write some JSON data to kafka
     * @param json The JSON object that we're going to push to Kafka
     */
    private void writeToKafkaTopic(Producer<Integer, String> producer, JSONObject json) {
        logger.debug(json.toString());
        try {
            producer.send(new KeyedMessage<>(kafkaTrainTopic, json.toString()));
        } catch (kafka.common.FailedToSendMessageException ex) {
            logger.error("Publishing to Kafka topic failed!", ex);
        }
    }

    /**
     * Get a Kafka client we can use to write data to the kafka broker with
     * @return
     */
    private Producer<Integer, String> createProducer() {
        Properties properties = new Properties();
        properties.put("serializer.class", "kafka.serializer.StringEncoder");
        properties.put("metadata.broker.list", kafkaBroker);
        properties.put("group.id", kafkaGroupId);

        Producer<Integer, String> result = new Producer<>(new ProducerConfig(properties));
        return result;
    }
}
