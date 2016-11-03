package com.openet.labs.ml.autoscale.scale;

import java.util.List;

public interface Scalable {
    List<String> getFlavors();
    String getFlavor();    
    String getScaleUpLink();
    String getScaleDownLink();
    String getScaleToFlavorLink();
    String getScaleToFlavorLinkParam();
}
