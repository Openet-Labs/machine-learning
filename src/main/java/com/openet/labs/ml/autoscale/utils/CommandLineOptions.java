package com.openet.labs.ml.autoscale.utils;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

public abstract class CommandLineOptions {

    @Option(name = "-p", aliases = "--usecase-properties", required = false, usage = "Use case configuration file path")
    protected String useCaseConfFilePath;

    public CommandLineOptions() {
    }

    public CommandLineOptions(String[] args) throws CmdLineException {
        EnigmaCmdLineParser parser = new EnigmaCmdLineParser();
        parser.parse(args, new CmdLineParser(this));
    }

    public String getUseCaseConfFilePath() {
        return useCaseConfFilePath;
    }

    public void setUseCaseConfFilePath(String useCaseConfFilePath) {
        this.useCaseConfFilePath = useCaseConfFilePath;
    }

}
