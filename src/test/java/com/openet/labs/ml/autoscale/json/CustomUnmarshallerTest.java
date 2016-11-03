package com.openet.labs.ml.autoscale.json;

import com.openet.labs.ml.autoscale.scale.SimpleVnfAsyncScaler;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.springframework.http.ResponseEntity;

/**
 *
 * @author ehsun7b
 */
public class CustomUnmarshallerTest {
    
    private String json;
    public CustomUnmarshallerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        json = "[\n" +
"{\n" +
"    \"vduid\": \"squid_347\",\n" +
"    \"vnfid\": \"webcach_001\",\n" +
"    \"flavor\": \"small\",\n" +
"    \"flavors\": [\"small\", \"medium\", \"large\"],\n" +
"    \"scale_up\": \"http://localhost:8080/vnf/webcach_001/scale_up\",\n" +
"    \"scale_down\": \"http://localhost:8080/vnf/webcach_001/scale_down\",\n" +
"    \"scale_to_flavor\": \"http://localhost:8080/vnf/webcach_001/scale/{flavor}\",\n" +
"    \"timestamp\": 2354244234,\n" +
"    \"vnfcid\": \"vm250\",\n" +
"    \"cpu\": 70,\n" +
"    \"memory\": 4560000,\n" +
"    \"metric_current\": 274758,\n" +
"    \"metric_threshold\": 400000\n" +
"},\n" +
"{\n" +
"    \"vduid\": \"squid_347\",\n" +
"    \"vnfid\": \"webcach_001\",\n" +
"    \"flavor\": \"small\",\n" +
"    \"flavors\": [\"small\", \"medium\", \"large\"],\n" +
"    \"scale_up\": \"http://localhost:8080/vnf/webcach_001/scale_up\",\n" +
"    \"scale_down\": \"http://localhost:8080/vnf/webcach_001/scale_down\",\n" +
"    \"scale_to_flavor\": \"http://localhost:8080/vnf/webcach_001/scale/{flavor}\",\n" +
"    \"timestamp\": 2354244234,\n" +
"    \"vnfcid\": \"vm250\",\n" +
"    \"cpu\": 70,\n" +
"    \"memory\": 4560000,\n" +
"    \"metric_current\": 274758,\n" +
"    \"metric_threshold\": 400000\n" +
"}\n" +
"]";
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void testSomeMethod() {
        try {
            List<Vnf> vnf = CustomUnmarshaller.parseFlatJson(json);
            
            System.out.println(vnf.size());
            for (Vnf vnf1 : vnf) {
                
                System.out.println(vnf1.getId());
                System.out.println(vnf1.getFlavor());
            }
            
           Assert.assertNotNull(vnf);
           Assert.assertEquals(vnf.size(), 1);
           Assert.assertEquals(vnf.get(0).getId(), "webcach_001");
           
            SimpleVnfAsyncScaler scaler = new SimpleVnfAsyncScaler(Executors.newCachedThreadPool());
            
            for (Vnf vnf1 : vnf) {
                scaler.scale(vnf1);
            }
        } catch (IOException ex) {
            Logger.getLogger(CustomUnmarshallerTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
