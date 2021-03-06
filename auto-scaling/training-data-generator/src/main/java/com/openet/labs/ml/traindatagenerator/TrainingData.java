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

package com.openet.labs.ml.traindatagenerator;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.openet.labs.ml.traindatagenerator.output.FileWriter;
import com.openet.labs.ml.traindatagenerator.output.KafkaWriter;
import com.openet.labs.ml.traindatagenerator.output.Writer;
import com.openet.labs.ml.traindatagenerator.strategies.DefaultGenerator;
import com.openet.labs.ml.traindatagenerator.strategies.SquareWaveGenerator;
import com.openet.labs.ml.traindatagenerator.strategies.TrainingDataGenerator;

public class TrainingData {

    
	private static Logger logger = Logger.getLogger(TrainingData.class);
	
    private static String kafkaTrainTopic = "com.openet.autoscaling.test";
    private static String kafkaBroker = "10.3.18.38:9092";
    private static String kafkaGroupId = "enigma";
    private static String generationStrategy = "default";
    private static String vdus = "squid,iptables,antivirus";
    private static String output="file";
    public static void main(String[] args) throws IOException {

    	//setup basic log4j logging
    	BasicConfigurator.configure();

        try {
        	//read some values from application.properties
            AppProperties app = new AppProperties();
            kafkaBroker = app.getProperty("kafka.broker");
            kafkaGroupId = app.getProperty("kafka.group.id");
            kafkaTrainTopic = app.getProperty("kafka.topic.train");
            generationStrategy = app.getProperty("training.strategy");
            vdus = app.getProperty("training.vdus");
            output = app.getProperty("training.output");
        } catch (IOException ex) {
            logger.error(ex);
        }
        
        //decide whether to write to kafka or file (file is useful for generating unit test data)
        Writer writer = null;
        switch(output.toLowerCase().trim()) {
        case "kafka":
        	writer = new KafkaWriter(kafkaTrainTopic, kafkaBroker, kafkaGroupId);
        	break;
    	default:
    		writer = new FileWriter();
        }
        
        //choose the correct generation strategy.
        //default strategy just varies the 'metric' field
        //square strategy generates a square wave with a period of 5 minutes
        TrainingDataGenerator generator = null;
        switch(generationStrategy.toLowerCase().trim()) {
        case "square":
        	generator = new SquareWaveGenerator();
        	break;
        default:
        	generator = new DefaultGenerator();
        }

        TrainingData trainingData = new TrainingData();
        trainingData.generate(generator, writer);
    }
    
    /**
     * Generate some data and write it to a kafka topic in JSON format
     * @param generator
     */
    private void generate(TrainingDataGenerator generator, Writer writer) {
    	
        List<String> vdus = getVdus();
        while (true) {
        	MetricModel model = generator.getNextMetric();
        	//no more values available, so we've reached the end of the training metrics
        	if(null == model) {
        		break;
        	}
            for (String vdu : vdus) {
                JSONObject json = getJsonString(vdu, model.getTimeStamp(), model.getMetric(), model.getCpu(), model.getMemory());
                writer.write(json);
            }
        }
    }

    /**
     * 
     * @return a list of VDU's we want to emulate
     */
    private List<String> getVdus() {
        return Arrays.asList(vdus.split(","));
    }

    /**
     * 
     * @param vdu The name of the VDU
     * @param ts The timestamp the metrics are for
     * @param metric A VNF specific metric (e.g. TPS)
     * @param cpu The % CPU being used on the VM hosting the VDU
     * @param memory The % memory being used on the VM hosting the VDU
     * @return A JSON object representing the metrics for a point in time  
     */
    private JSONObject getJsonString(String vdu, Timestamp ts, Double metric, Double cpu, Double memory) {
        double metricD = metric.doubleValue() * 1.01d;
        double vnfcsCount = (int) Math.ceil(metricD / 100) * 1.00d;
        JSONObject objVdu = new JSONObject();

        objVdu.put("Vdu", vdu);
        objVdu.put("Metric", metricD);
        objVdu.put("Vnfcs", vnfcsCount);
        objVdu.put("Cpu", cpu);
        objVdu.put("Memory", memory);
        String tsStr = String.valueOf(ts.getTime());
        objVdu.put("Timestamp", tsStr);

        return objVdu;
    }
}
