package com.openet.labs.ml.autoscale;

import com.openet.labs.ml.autoscale.utils.CommandLineOptions;
import java.io.Serializable;

import org.kohsuke.args4j.CmdLineException;

public class AutoScalingOptions extends CommandLineOptions implements Serializable {
    
    private static final long serialVersionUID = 1L;

    public AutoScalingOptions(String[] args) throws CmdLineException {
        super(args);
    }

    
}
