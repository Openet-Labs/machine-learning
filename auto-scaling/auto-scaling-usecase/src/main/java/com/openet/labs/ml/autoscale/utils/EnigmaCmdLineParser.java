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
