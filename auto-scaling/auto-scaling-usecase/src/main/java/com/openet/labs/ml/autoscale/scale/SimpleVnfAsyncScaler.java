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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.apache.log4j.Logger;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public class SimpleVnfAsyncScaler extends Scaler {

    private static final Logger log = Logger.getLogger(SimpleVnfAsyncScaler.class);

    protected ExecutorService executor;

    public SimpleVnfAsyncScaler() {
        log.debug("New thread pool is being created with size of physical cores.");
        executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    public void setExecutor(ExecutorService executor) {
        this.executor = executor;
    }

    @Override
    public Future<ResponseEntity<String>> scale(Scalable scalable) {
        Vnf vnf = (Vnf) scalable;
        log.debug("Scaling VNF " + vnf.getId());

        ScaleType type = vnf.getScaleType(); // by right should call calculateScaleType method
        String link = getScaleLink(vnf, type);
        HttpMethod httpMethod = getScaleHttpMethod(type);

        log.debug("Scale url: " + link);

        AsyncHttpRequest asyncHttpRequest = new AsyncHttpRequest(link, httpMethod);
        Future<ResponseEntity<String>> response = executor.submit(asyncHttpRequest);
        return response;
    }

    /*
    private ScaleType calculateScaleType(Vnf vnf) {
        return vnf.getScaleType();
    }*/
    private String getScaleLink(Vnf vnf, ScaleType type) {
        switch (type.getType()) {
            case UP:
                return vnf.getScaleUpLink();
            case DOWN:
                return vnf.getScaleDownLink();
            case FLAVOR:
                return vnf.getScaleToFlavorLink().replace(vnf.getScaleToFlavorLinkParam(), type.getFlavor());
            default:
                throw new IllegalArgumentException("Scale type should be one of UP, DOWN, or FLAVOR");
        }
    }

    private HttpMethod getScaleHttpMethod(ScaleType type) {
        //TODO: some logic to choose the http method. GET/POST/...
        return HttpMethod.GET;
    }

}
