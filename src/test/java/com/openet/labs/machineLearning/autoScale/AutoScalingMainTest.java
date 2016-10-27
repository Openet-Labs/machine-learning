/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openet.labs.machineLearning.autoScale;

import java.util.Properties;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
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
        instance.init();
        instance.setJavaSparkContext(jsc);
        JavaRDD<String> trainRDD = instance.getTrainingData();
        System.out.println("trainRDD: " + trainRDD.count());
    }

    /**
     * Test of setUseCaseProperties method, of class AutoScalingMain.
     */
//    @Test
//    public void testSetUseCaseProperties() {
//        System.out.println("setUseCaseProperties");
//        Properties properties = null;
//        AutoScalingMain instance = new AutoScalingMain();
//        instance.setUseCaseProperties(properties);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
}
