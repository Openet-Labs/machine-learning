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

package com.openet.labs.ml.autoscale.scale;

import com.openet.labs.ml.autoscale.json.Vnf;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Ignore;
import org.springframework.http.ResponseEntity;

@Ignore
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
        vnf.setFlavors(Arrays.asList(new String[]{"small", "big"}));
        vnf.setScaleDownLink("http://localhost:1500/echo/scaledown");
        vnf.setScaleUpLink("http://localhost:1500/echo/scaleup");
        vnf.setScaleToFlavorLink("http://localhost:1234/scale");
        vnf.setScaleType(new ScaleType(ScaleType.Type.DOWN));

        SimpleVnfAsyncScaler instance = new SimpleVnfAsyncScaler();
        ExecutorService executor = Executors.newCachedThreadPool();
        instance.setExecutor(executor);
        Future<ResponseEntity<String>> result = instance.scale(vnf);
        System.out.println(result.get().getStatusCode());
        // executor.awaitTermination(30, TimeUnit.SECONDS);
        //nc -l -v 1234

    }

}
