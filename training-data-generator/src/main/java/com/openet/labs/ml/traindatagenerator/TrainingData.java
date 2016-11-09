package com.openet.labs.ml.traindatagenerator;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import org.json.JSONObject;

/**
 *
 * @author ehsun7b
 */
public class TrainingData {

    private static final String MODEL_RES = "model.csv";

    private static String KAFKA_TRAIN_TOPIC = "com.openet.autoscaling.test";
    private static String KAFKA_BROKER = "10.3.18.38:9092";
    private static String KAFKA_GROUP_ID = "enigma";

    private static Producer<Integer, String> producer;

    public static void main(String[] args) {
        List<String> metrics = new LinkedList<>();

        try (Stream<String> lines = Files.lines(Paths.get(Thread.currentThread().getContextClassLoader().getResource(MODEL_RES).toURI()))) {
            lines.map(line -> line.toString().split(",")[2]).forEach(metrics::add);
        } catch (IOException | URISyntaxException ex) {
            Logger.getLogger(TrainingData.class.getName()).log(Level.SEVERE, null, ex);
        }

        List<String> vdus = getVdus();

        Timestamp tsBegin = Timestamp.valueOf("2015-10-18 00:00:00");
        Timestamp tsEnd = Timestamp.valueOf("2015-10-25 00:00:00");

        Iterator<String> itMetric = metrics.iterator();
        producer = createProducer();
        for (Timestamp ts = tsBegin; ts.before(tsEnd) && itMetric.hasNext();) {
            Double metric = 400.0;

            try {
                String nextMetric = itMetric.next();
                metric = Double.parseDouble(nextMetric);
            } catch (NumberFormatException ex) {
                Logger.getLogger(TrainingData.class.getName()).log(Level.WARNING, "Number format exception!", ex);
            }

            for (String vdu : vdus) {
                JSONObject json = getJsonString(vdu, ts, metric);
                writeToKafkaTopic(json);
            }

            ts = new Timestamp(ts.getTime() + (60 * 1000L));
        }

    }

    private static List<String> getVdus() {
        return Arrays.asList(new String[]{"squid", "iptables", "antivirus"});
    }

    private static JSONObject getJsonString(String vdu, Timestamp ts, Double metric) {
        double metricD = metric.doubleValue() * 1.01d;
        double vnfcsCount = (int) Math.ceil(metricD / 100) * 1.00d;
        double cpu = 50.01d;
        double memory = 50.01d;
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

    private static void writeToKafkaTopic(JSONObject json) {
        System.out.println(json.toString());
        try {
            producer.send(new KeyedMessage<>(KAFKA_TRAIN_TOPIC, json.toString()));
        } catch (kafka.common.FailedToSendMessageException ex) {
            Logger.getLogger(TrainingData.class.getName()).log(Level.SEVERE, "Publishing to Kafka topic failed!", ex);
        }
    }

    private static Producer<Integer, String> createProducer() {
        Properties properties = new Properties();
        properties.put("serializer.class", "kafka.serializer.StringEncoder");
        properties.put("metadata.broker.list", KAFKA_BROKER);
        properties.put("group.id", KAFKA_GROUP_ID);

        Producer<Integer, String> result = new Producer<>(new ProducerConfig(properties));
        return result;
    }
}
