package com.openet.labs.ml.autoscale.scale;

import com.openet.labs.ml.autoscale.json.Vnf;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public class SimpleVnfAsyncScaler extends Scaler {
    
    private static final Logger log = LoggerFactory.getLogger(SimpleVnfAsyncScaler.class);
        
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
