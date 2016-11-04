/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openet.labs.ml.autoscale;

import com.openet.labs.ml.autoscale.AutoScalingMain;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.DataFrame;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author openet
 */
public class AutoScalingMainTest {

    private transient SparkConf sparkConf;
    private transient JavaSparkContext jsc;

    @Before
    public void setUp() {

        sparkConf = new SparkConf().setAppName("com.openet.enigma.sdn.sfc.placement").setMaster("local[2]");
        jsc = new JavaSparkContext(sparkConf);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of main method, of class AutoScalingMain.
     */
//    @Test
//    public void testMain() {
//        System.out.println("main");
//        String[] args = null;
//        AutoScalingMain.main(args);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
    /**
     * Test of getUseCaseProperties method, of class AutoScalingMain.
     */
    @Test
    public void testGetUseCaseProperties() throws Exception {
        System.out.println("getUseCaseProperties");
        AutoScalingMain instance = new AutoScalingMain();
//        Properties expResult = null;
        Properties result = instance.getUseCaseProperties();
//        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }

    /**
     * Test of init method, of class AutoScalingMain.
     */
    @Test
    public void testInit() throws Exception {
        System.out.println("testInit");
        AutoScalingMain instance = new AutoScalingMain();
        instance.setJavaSparkContext(jsc);
        instance.init();
    }

    @Test
    public void testTrain() throws Exception {
        System.out.println("testTrain");
        AutoScalingMain instance = new AutoScalingMain();
        instance.setJavaSparkContext(jsc);
        instance.init();
        instance.train();
    }

    @Test
    public void testStreamingInput() throws Exception {
        System.out.println("testStreamingInput");
        AutoScalingMain instance = new AutoScalingMain();
        instance.setJavaSparkContext(jsc);
        instance.init();
        instance.train();
        instance.processInputStream();
        instance.getJavaStreamingContext().start();
        instance.getJavaStreamingContext().awaitTermination();
        instance.close();

    }

    /**
     * Test of setUseCaseProperties method, of class AutoScalingMain.
     */
    @Test
    public void testParseJsonInput() throws IOException {
        System.out.println("testParseJsonInput");

        List<String> inputData = new ArrayList<>();
        String input1 = "{\"timestamp\":2354244234,\"vnfs\":[{\"id\":\"webcach_001\",\"flavor\":\"small\",\"flavors\":[\"small\",\"medium\",\"large\"],\"vdus\":[{\"id\":\"squid_347\",\"vnfcs\":[{\"id\":\"vm250\",\"cpu\":70,\"memory\":4560000,\"metric\":{\"current\":274758,\"threshold\":400000}},{\"id\":\"vm251\",\"cpu\":70,\"memory\":4560000,\"metric\":{\"current\":274758,\"threshold\":400000}},{\"id\":\"vm253\",\"cpu\":70,\"memory\":4560000,\"metric\":{\"current\":274758,\"threshold\":400000}}]},{\"id\":\"request_logger_437\",\"vnfcs\":[{\"id\":\"vm350\",\"cpu\":70,\"memory\":4560000,\"metric\":{\"current\":274758,\"threshold\":400000}}]}],\"_links\":{\"scale_up\":{\"href\":\"http://localhost:8080/vnf/webcach_001/scale_up\"},\"scale_down\":{\"href\":\"http://localhost:8080/vnf/webcach_001/scale_down\"},\"scale_to_flavor\":{\"href\":\"http://localhost:8080/vnf/webcach_001/scale/{flavor}\"}}}]}";
        inputData.add(input1);
        JavaRDD inputRDD = jsc.parallelize(inputData);

        AutoScalingMain instance = new AutoScalingMain();
        instance.setJavaSparkContext(jsc);
        instance.init();
        instance.parseJsonInput(inputRDD);
    }

    /**
     * Test of setUseCaseProperties method, of class AutoScalingMain.
     */
    @Test
    public void testPredictInput() throws IOException {
        System.out.println("testParseJsonInput");

        List<String> inputData = new ArrayList<>();
        String input1 = "{\"timestamp\":2354244234,\"vnfs\":[{\"id\":\"webcach_001\",\"flavor\":\"small\",\"flavors\":[\"small\",\"medium\",\"large\"],\"vdus\":[{\"id\":\"squid_347\",\"vnfcs\":[{\"id\":\"vm250\",\"cpu\":70,\"memory\":4560000},{\"id\":\"vm251\",\"cpu\":70,\"memory\":4560000},{\"id\":\"vm253\",\"cpu\":70,\"memory\":4560000}]},{\"id\":\"request_logger_437\",\"vnfcs\":[{\"id\":\"vm350\",\"cpu\":70,\"memory\":4560000}]}],\"_links\":{\"scale_up\":{\"href\":\"http://localhost:8080/vnf/webcach_001/scale_up\"},\"scale_down\":{\"href\":\"http://localhost:8080/vnf/webcach_001/scale_down\"},\"scale_to_flavor\":{\"href\":\"http://localhost:8080/vnf/webcach_001/scale/{flavor}\"}}}]}";
        inputData.add(input1);
        JavaRDD inputRDD = jsc.parallelize(inputData);

        AutoScalingMain instance = new AutoScalingMain();
        instance.setJavaSparkContext(jsc);
        instance.init();
        instance.train();
        instance.parseJsonInput(inputRDD);
    }
    
    
    /**
     * Test of testVoting method, of class AutoScalingMain.
     */
    @Test
    public void testVoting() throws IOException, InterruptedException, ExecutionException {
        System.out.println("testVoting");

        List<String> inputData = new ArrayList<>();
//        String input1 = "{\"timestamp\":2354244234,\"vnfs\":[{\"id\":\"webcach_001\",\"flavor\":\"small\",\"flavors\":[\"small\",\"medium\",\"large\"],\"vdus\":[{\"id\":\"squid_347\",\"vnfcs\":[{\"id\":\"vm250\",\"cpu\":70,\"memory\":4560000},{\"id\":\"vm251\",\"cpu\":70,\"memory\":4560000},{\"id\":\"vm253\",\"cpu\":70,\"memory\":4560000}]},{\"id\":\"request_logger_437\",\"vnfcs\":[{\"id\":\"vm350\",\"cpu\":70,\"memory\":4560000}]}],\"_links\":{\"scale_up\":{\"href\":\"http://localhost:8080/vnf/webcach_001/scale_up\"},\"scale_down\":{\"href\":\"http://localhost:8080/vnf/webcach_001/scale_down\"},\"scale_to_flavor\":{\"href\":\"http://localhost:8080/vnf/webcach_001/scale/{flavor}\"}}}]}";
        String input1 = "{\"timestamp\":2354244234,\"vnfs\":[{\"id\":\"webcach_001\",\"flavor\":\"small\",\"flavors\":[\"small\",\"medium\",\"large\"],\"vdus\":[{\"id\":\"squid_347\",\"vnfcs\":[{\"id\":\"vm250\",\"cpu\":50,\"memory\":40},{\"id\":\"vm251\",\"cpu\":70,\"memory\":40},{\"id\":\"vm253\",\"cpu\":50,\"memory\":40}]},{\"id\":\"request_logger_437\",\"vnfcs\":[{\"id\":\"vm350\",\"cpu\":50,\"memory\":65}]}],\"_links\":{\"scale_up\":{\"href\":\"http://localhost:8080/vnf/webcach_001/scale_up\"},\"scale_down\":{\"href\":\"http://localhost:8080/vnf/webcach_001/scale_down\"},\"scale_to_flavor\":{\"href\":\"http://localhost:8080/vnf/webcach_001/scale/{flavor}\"}}}]}";        
        String input2 ="{\"timestamp\":2354244234,\"vnfs\":[{\"id\":\"webcach_002\",\"flavor\":\"small\",\"flavors\":[\"small\",\"medium\",\"large\"],\"vdus\":[{\"id\":\"request_logger_437\",\"vnfcs\":[{\"id\":\"vm350\",\"cpu\":50,\"memory\":50},{\"id\":\"vm351\",\"cpu\":50,\"memory\":50},{\"id\":\"vm352\",\"cpu\":50,\"memory\":50},{\"id\":\"vm353\",\"cpu\":50,\"memory\":50},{\"id\":\"vm354\",\"cpu\":50,\"memory\":50},{\"id\":\"vm355\",\"cpu\":50,\"memory\":50}]}],\"_links\":{\"scale_up\":{\"href\":\"http://localhost:8080/vnf/webcach_001/scale_up\"},\"scale_down\":{\"href\":\"http://localhost:8080/vnf/webcach_001/scale_down\"},\"scale_to_flavor\":{\"href\":\"http://localhost:8080/vnf/webcach_001/scale/{flavor}\"}}}]}";
        inputData.add(input1);
        inputData.add(input2);
        JavaRDD inputRDD = jsc.parallelize(inputData);

        AutoScalingMain instance = new AutoScalingMain();
        instance.setJavaSparkContext(jsc);
        instance.init();
        instance.train();
        DataFrame finalPredictedDF = instance.parseJsonInput(inputRDD);
        instance.voting(finalPredictedDF);
    }

}
