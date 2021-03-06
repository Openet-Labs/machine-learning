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

import com.openet.labs.ml.autoscale.scale.Scalable;
import com.openet.labs.ml.autoscale.scale.ScaleType;
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
    protected ScaleType scaleType;

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

    public ScaleType getScaleType() {
        return scaleType;
    }

    public void setScaleType(ScaleType scaleType) {
        this.scaleType = scaleType;
    }

    @Override
    public String getScaleToFlavorLinkParam() {
        return FLAVOR_PARAM;
    }

}
