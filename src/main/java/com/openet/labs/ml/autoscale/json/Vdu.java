package com.openet.labs.ml.autoscale.json;

import java.util.LinkedList;
import java.util.List;

public class Vdu {
    protected String id;
    protected List<Vnfc> vnfcs;

    public Vdu() {
        this(null);
    }

    public Vdu(String id) {
        this.id = id;
        vnfcs = new LinkedList<>();
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Vnfc> getVnfcs() {
        return vnfcs;
    }

    public void setVnfcs(List<Vnfc> vnfcs) {
        this.vnfcs = vnfcs;
    }
    
    
}
