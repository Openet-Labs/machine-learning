package com.openet.labs.ml.autoscale.json;

public class Metric {
    
    protected String name;
    protected Integer current;
    protected Integer threshold;

    public Metric() {
    }

    public Metric(Integer current, Integer threshold) {
        this.current = current;
        this.threshold = threshold;
    }

    public Metric(String name, Integer current, Integer threshold) {
        this.name = name;
        this.current = current;
        this.threshold = threshold;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCurrent() {
        return current;
    }

    public void setCurrent(Integer current) {
        this.current = current;
    }

    public Integer getThreshold() {
        return threshold;
    }

    public void setThreshold(Integer threshold) {
        this.threshold = threshold;
    }
    
    
}
