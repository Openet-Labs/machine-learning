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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.commons.io.IOUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import org.junit.Before;
import org.junit.Test;

public class AutoScalingMainTest {

    private List<String> trainingData;
    private Properties props;
    private transient SparkConf sparkConf;
    private transient JavaSparkContext jsc;

    @Before
    public void setUp() throws IOException {

        sparkConf = new SparkConf().setAppName("com.openet.enigma.sdn.sfc.placement")
                .setMaster("local[2]");
        sparkConf.set("spark.driver.allowMultipleContexts", "true");
        jsc = new JavaSparkContext(sparkConf);

        // set properties for testing
        AutoScalingTestCommon astc = new AutoScalingTestCommon();
        props = astc.getTestProperties();

        // get training data for testing
        trainingData = new ArrayList<>();
        String trainFilePath = "sampleTrainData.json";
        String result = "";
        ClassLoader classLoader = getClass().getClassLoader();
        result = IOUtils.toString(classLoader.getResourceAsStream(trainFilePath));
        trainingData.add(result);

    }

    @After
    public void tearDown() {
    }

    @Test
    public void testGetUseCaseProperties() throws Exception {
        System.out.println("testGetUseCaseProperties");
        AutoScalingMain instance = new AutoScalingMain();
        instance.setUseCaseProperties(props);
        assertNotNull(instance.getUseCaseProperties());
    }

    /**
     * Test of init method, of class AutoScalingMain.
     */
    @Test
    public void testInit() throws Exception {
        System.out.println("testInit");
        AutoScalingMain instance = new AutoScalingMain();
        instance.setUseCaseProperties(props);
        instance.setJavaSparkContext(jsc);

        assertNull(instance.getEnigmaKafkaUtils());
        assertNull(instance.getParser());
        assertNull(instance.getFutureInterval());
        assertNull(instance.getJavaStreamingContext());
        assertNull(instance.getKafkaBroker());
        assertNull(instance.getKafkaCosumerGroup());
        assertNull(instance.getKafkaHost());
        assertNull(instance.getKafkaTopic());
        assertNull(instance.getKafkaTrainTopic());
        assertNull(instance.getPerTopicKafkaPartitions());
        assertNull(instance.getStreamDuration());

        instance.init();

        assertNotNull(instance.getEnigmaKafkaUtils());
        assertNotNull(instance.getParser());
        assertNotNull(instance.getFutureInterval());
        assertNotNull(instance.getJavaStreamingContext());
        assertNotNull(instance.getKafkaBroker());
        assertNotNull(instance.getKafkaCosumerGroup());
        assertNotNull(instance.getKafkaHost());
        assertNotNull(instance.getKafkaTopic());
        assertNotNull(instance.getKafkaTrainTopic());
        assertNotNull(instance.getPerTopicKafkaPartitions());
        assertNotNull(instance.getStreamDuration());
    }

    @Test
    public void testTrain() throws Exception {
        System.out.println("testTrain");
        AutoScalingMain instance = new AutoScalingMain();
        instance.setUseCaseProperties(props);
        instance.setJavaSparkContext(jsc);
        JavaRDD<String> trainDataRDD = instance.getJavaSparkContext().parallelize(trainingData);

        instance.init();

        int expectedInitialModelSize = 0;
        int initialModelSize = instance.getVduItemsMap().size();
        assertEquals(expectedInitialModelSize, initialModelSize);

        instance.train(trainDataRDD);

        int expectedTrainedModelSize = 1;
        int trainedModelSize = instance.getVduItemsMap().size();

        assertEquals(expectedTrainedModelSize, trainedModelSize);
    }

}
