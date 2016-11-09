package com.openet.labs.ml.autoscale.scale;

public class ScaleType {

    public static enum Type {
        UP, DOWN, FLAVOR;
    }
    
    protected Type type;
    protected String flavor;

    public ScaleType() {
    }

    public ScaleType(Type type, String flavor) {
        this.type = type;
        this.flavor = flavor;
    }

    public ScaleType(Type type) {
        this.type = type;
    }
    
    public String getFlavor() {
        return flavor;
    }

    public void setFlavor(String flavor) {
        this.flavor = flavor;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
    
    
}
