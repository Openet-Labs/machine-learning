package com.openet.labs.ml.autoscale;

import java.io.Serializable;
import org.apache.spark.ml.PipelineModel;

public class ItemVdu implements Serializable {

    private static final long serialVersionUID = 1L;

    String vduId;
    PipelineModel modelVnfc;
//    PipelineModel modelMetric;
    PipelineModel modelCpu;
    PipelineModel modelMemory;

    //constructor
    public ItemVdu(String vduId) {
        this.vduId = vduId;
    }

    //gettter-setters
    public String getVduId() {
        return vduId;
    }

    public void setVduId(String vduId) {
        this.vduId = vduId;
    }

    public PipelineModel getModelVnfc() {
        return modelVnfc;
    }

    public void setModelVnfc(PipelineModel modelVnfc) {
        this.modelVnfc = modelVnfc;
    }

//    public PipelineModel getModelMetric() {
//        return modelMetric;
//    }
//
//    public void setModelMetric(PipelineModel modelMetric) {
//        this.modelMetric = modelMetric;
//    }

    public PipelineModel getModelCpu() {
        return modelCpu;
    }

    public void setModelCpu(PipelineModel modelCpu) {
        this.modelCpu = modelCpu;
    }

    public PipelineModel getModelMemory() {
        return modelMemory;
    }

    public void setModelMemory(PipelineModel modelMemory) {
        this.modelMemory = modelMemory;
    }

}
