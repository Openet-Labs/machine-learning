package com.openet.labs.ml.autoscale.json;

import com.openet.labs.ml.autoscale.scale.Scalable;
import java.util.LinkedList;
import java.util.List;

public class Vnf implements Scalable {

    public static final String FLAVOR_PARAM = "{flavor}";
    
    protected String id;
    protected String flavor;
    protected List<String> flavors;
    protected List<Vdu> vdus;
    protected String scaleUpLink;
    protected String scaleDownLink;
    protected String scaleToFlavorLink;

    public Vnf() {
        this(null);
    }

    public Vnf(String id) {
        this.id = id;
        
        vdus = new LinkedList<>();
        flavors = new LinkedList<>();
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFlavor() {
        return flavor;
    }

    public void setFlavor(String flavor) {
        this.flavor = flavor;
    }

    public List<String> getFlavors() {
        return flavors;
    }

    public void setFlavors(List<String> flavors) {
        this.flavors = flavors;
    }

    public List<Vdu> getVdus() {
        return vdus;
    }

    public void setVdus(List<Vdu> vdus) {
        this.vdus = vdus;
    }

    public String getScaleUpLink() {
        return scaleUpLink;
    }

    public void setScaleUpLink(String scaleUpLink) {
        this.scaleUpLink = scaleUpLink;
    }

    public String getScaleDownLink() {
        return scaleDownLink;
    }

    public void setScaleDownLink(String scaleDownLink) {
        this.scaleDownLink = scaleDownLink;
    }

    public String getScaleToFlavorLink() {
        return scaleToFlavorLink;
    }

    public void setScaleToFlavorLink(String scaleToFlavorLink) {
        this.scaleToFlavorLink = scaleToFlavorLink;
    }

    @Override
    public String getScaleToFlavorLinkParam() {
        return FLAVOR_PARAM;
    }

  
    
}
