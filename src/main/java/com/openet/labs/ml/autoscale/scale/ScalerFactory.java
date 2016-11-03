package com.openet.labs.ml.autoscale.scale;

import com.openet.labs.ml.autoscale.json.Vnf;

public class ScalerFactory {

    public Scaler createScaler(Scalable scalable) {
        if (scalable instanceof Vnf) {
            return new SimpleVnfAsyncScaler();
        } else {
            return null;
        }
    }
}
