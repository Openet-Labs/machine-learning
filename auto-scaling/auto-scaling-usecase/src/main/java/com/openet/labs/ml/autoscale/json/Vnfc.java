/**************************************************************************
 *
 * Copyright © Openet Telecom, Ltd. 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **************************************************************************/

package com.openet.labs.ml.autoscale.json;

public class Vnfc {

    protected String id;
    protected Double cpu;
    protected Double meory;
    protected Double predictedCpu;
    protected Double predictedMeory;
    //protected Metric metric;

    public Vnfc() {
    }

    public Vnfc(String id) {
        this.id = id;
    }

    public Vnfc(String id, Double cpu, Double meory) {
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

    public Double getCpu() {
        return cpu;
    }

    public void setCpu(Double cpu) {
        this.cpu = cpu;
    }

    public Double getMeory() {
        return meory;
    }

    public void setMeory(Double meory) {
        this.meory = meory;
    }

    /*
    public Metric getMetric() {
        return metric;
    }

    public void setMetric(Metric metric) {
        this.metric = metric;
    }
     */
    public Double getPredictedCpu() {
        return predictedCpu;
    }

    public void setPredictedCpu(Double predictedCpu) {
        this.predictedCpu = predictedCpu;
    }

    public Double getPredictedMeory() {
        return predictedMeory;
    }

    public void setPredictedMeory(Double predictedMeory) {
        this.predictedMeory = predictedMeory;
    }

}
