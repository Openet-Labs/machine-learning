/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openet.labs.ml.autoscale.scale;

import com.openet.labs.ml.autoscale.json.Vnf;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.springframework.http.ResponseEntity;

/**
 *
 * @author ehsun7b
 */
public class SimpleVnfAsyncScalerTest {
    
    public SimpleVnfAsyncScalerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of scale method, of class SimpleVnfAsyncScaler.
     */
    @Test
    public void testScale() throws InterruptedException, ExecutionException {
        System.out.println("scale");
        Vnf vnf = new Vnf("test_vnf");
        vnf.setFlavor("small");
        vnf.setFlavors(Arrays.asList(new String[] {"small", "big"}));
        vnf.setScaleDownLink("http://localhost:1500/echo/scaledown");
        vnf.setScaleUpLink("http://localhost:1500/echo/scaleup");
        vnf.setScaleToFlavorLink("http://localhost:1234/scale");
        vnf.setScaleType(new ScaleType(ScaleType.Type.UP));
        
        SimpleVnfAsyncScaler instance = new SimpleVnfAsyncScaler();
        ExecutorService executor = Executors.newCachedThreadPool();
        instance.setExecutor(executor);        
        Future<ResponseEntity<String>> result = instance.scale(vnf);
        System.out.println(result.get().getStatusCode());
       // executor.awaitTermination(30, TimeUnit.SECONDS);
       //nc -l -v 1234
       
    }
    
}
