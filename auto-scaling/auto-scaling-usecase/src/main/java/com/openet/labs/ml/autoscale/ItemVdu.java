/*
Copyright © Openet Telecom, Ltd. 
 
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
 
    http://www.apache.org/licenses/LICENSE-2.0
 
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package com.openet.labs.ml.autoscale;

import java.io.Serializable;
import org.apache.spark.ml.PipelineModel;

public class ItemVdu implements Serializable {

    private static final long serialVersionUID = 1L;

    String vduId;
    PipelineModel modelVnfc;
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
