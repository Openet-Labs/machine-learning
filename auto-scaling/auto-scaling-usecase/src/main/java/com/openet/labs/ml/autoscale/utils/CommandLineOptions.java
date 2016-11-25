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
