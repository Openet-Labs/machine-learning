package com.openet.labs.ml.autoscale.json;

import com.openet.labs.ml.autoscale.scale.Scaler;
import com.openet.labs.ml.autoscale.scale.ScalerFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author ehsun7b
 */
public class CustomUnmarshallerTest {

    private static final Logger log = Logger.getLogger(CustomUnmarshallerTest.class);
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
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("intermediate.json");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

            StringBuilder builder = new StringBuilder();
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                builder.append(line);
            }

            json = builder.toString();
        } catch (IOException ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testSomeMethod() {
        try {
            List<Vnf> vnf = FlatJsonUnmarshaller.parseFlatJson(json);

            System.out.println(vnf.size());
            for (Vnf vnf1 : vnf) {

                System.out.println(vnf1.getId());
                System.out.println(vnf1.getFlavor());
            }

            Assert.assertNotNull(vnf);
            Assert.assertEquals(vnf.size(), 1);
            Assert.assertEquals(vnf.get(0).getId(), "webcach_001");

            Scaler scaler = new ScalerFactory().createScaler(vnf.get(0));           
                    
            for (Vnf vnf1 : vnf) {
                scaler.scale(vnf1);
            }
        } catch (IOException ex) {
            log.error(ex.getMessage(), ex);
        }
    }

}
