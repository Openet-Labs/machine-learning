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

import java.util.LinkedList;
import java.util.List;

public class Vdu {

    protected String id;
    protected List<Vnfc> vnfcs;
    protected Integer predictedVnfc;

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

    public int vnfcCount() {
        return vnfcs.size();
    }

    public Integer getPredictedVnfc() {
        return predictedVnfc;
    }

    public void setPredictedVnfc(Integer predictedVnfc) {
        this.predictedVnfc = predictedVnfc;
    }
}
