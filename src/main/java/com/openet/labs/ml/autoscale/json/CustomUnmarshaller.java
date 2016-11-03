package com.openet.labs.ml.autoscale.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Objects;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomUnmarshaller {

    private static final Logger log = LoggerFactory.getLogger(CustomUnmarshaller.class);
    
    private static final String VNF_ID = "vnfid";
    private static final String VDU_ID = "vduid";
    private static final String VNFC_ID = "vnfcid";
    private static final String FLAVOR = "flavor";
    private static final String FLAVORS = "flavors";
    private static final String SCALE_UP = "scale_up";
    private static final String SCALE_DOWN = "scale_down";
    private static final String SCALE_TO = "scale_to_flavor";
    private static final String CPU = "cpu";
    private static final String MEMORY = "memory";
    private static final String METRIC = "metric_current";
    private static final String METRIC_THRESHOLD = "metric_threshold";
    
    public static List<Vnf> parseFlatJson(final String vnfcs) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(vnfcs);

        List<Vnf> result = new LinkedList<>();

        if (node.isArray()) {
            for (JsonNode flatVnfc : node) {
                log.debug(flatVnfc.toString());

                String vnfId = flatVnfc.get(VNF_ID).asText();
                log.debug("vnf id: " + vnfId);

                Vnf vnf;

                try {
                    vnf = findVnfById(result, vnfId);
                } catch (NoSuchElementException ex) {
                    vnf = new Vnf(vnfId);
                    
                    vnf.setFlavor(flatVnfc.get(FLAVOR).asText());
                    vnf.setScaleUpLink(flatVnfc.get(SCALE_UP).asText());
                    vnf.setScaleDownLink(flatVnfc.get(SCALE_DOWN).asText());
                    vnf.setScaleToFlavorLink(flatVnfc.get(SCALE_TO).asText());
                    JsonNode flavorsNode = flatVnfc.get(FLAVORS);
                    
                    if (flavorsNode.isArray()) {
                        for (JsonNode jsonNode : flavorsNode) {
                            vnf.getFlavors().add(jsonNode.asText());
                        }
                    } else {
                        throw new IllegalArgumentException("Flavors in flat json is not an array!");
                    }
                    
                    result.add(vnf);
                }

                String vduId = flatVnfc.get(VDU_ID).asText();
                Vdu vdu;

                try {
                    vdu = findVduById(vnf.getVdus(), vduId);
                } catch (NoSuchElementException ex) {
                    vdu = new Vdu(vduId);
                    vnf.getVdus().add(vdu);
                }

                String vnfcId = flatVnfc.get(VNFC_ID).asText();
                Vnfc vnfc = new Vnfc(vnfcId);
                vnfc.setCpu(flatVnfc.get(CPU).asInt());
                vnfc.setMeory(flatVnfc.get(MEMORY).asInt());
                
                /*
                Metric metric = new Metric(flatVnfc.get(METRIC).asInt(), flatVnfc.get(METRIC_THRESHOLD).asInt());                
                vnfc.setMetric(metric);
                vdu.getVnfcs().add(vnfc);*/
            }
        } else {
            throw new IllegalArgumentException("Flat json is not an array!");
        }

        return result;
    }

    private static Vnf findVnfById(List<Vnf> list, String vnfId) throws NoSuchElementException {
        Optional<Vnf> findFirst = list.stream().filter((vnf) -> (Objects.equal(vnf.getId(), vnfId))).findFirst();
        return findFirst.get();
    }

    private static Vdu findVduById(List<Vdu> list, String vduId) {
        Optional<Vdu> findFirst = list.stream().filter((vdu) -> (Objects.equal(vdu.getId(), vduId))).findFirst();
        return findFirst.get();
    }
}

