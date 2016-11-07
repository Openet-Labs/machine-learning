package com.openet.labs.ml.autoscale.utils;

import java.io.PrintStream;
import java.util.Arrays;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

public class EnigmaCmdLineParser {

    public void parse(String[] args, CmdLineParser parser) throws CmdLineException {
        
        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            // Let output to System.err for these messages
            // Note that since calling this program is controlled by a script,
            // we should not
            // _expect_ problems with options provided.
            PrintStream stream = System.err;

            stream.println("Error parsing options: " + e.getMessage());
            stream.println("Provided Arguments: " + Arrays.toString(args));
            stream.println("Usage:");
            parser.printUsage(stream);

            throw e;
        }
    }
}
