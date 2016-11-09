package com.openet.labs.ml.autoscale.scale;

import java.util.concurrent.Callable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class AsyncHttpRequest implements Callable<ResponseEntity<String>> {

    protected final String url;
    protected final HttpMethod httpMethod;

    public AsyncHttpRequest(String url, HttpMethod httpMethod) {
        this.url = url;
        this.httpMethod = httpMethod;
    }
    
    @Override
    public ResponseEntity<String> call() throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url, httpMethod, HttpEntity.EMPTY, String.class);
        return response;
    }
    
}
