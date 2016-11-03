package com.openet.labs.ml.autoscale.json;

public class Vnfc {
    
    protected String id;
    protected Integer cpu;
    protected Integer meory;
    protected Metric metric;

    public Vnfc() {
    }

    public Vnfc(String id) {
        this.id = id;
    }

    public Vnfc(String id, Integer cpu, Integer meory) {
        this.id = id;
        this.cpu = cpu;
        this.meory = meory;
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getCpu() {
        return cpu;
    }

    public void setCpu(Integer cpu) {
        this.cpu = cpu;
    }

    public Integer getMeory() {
        return meory;
    }

    public void setMeory(Integer meory) {
        this.meory = meory;
    }

    public Metric getMetric() {
        return metric;
    }

    public void setMetric(Metric metric) {
        this.metric = metric;
    }
    
    
}
