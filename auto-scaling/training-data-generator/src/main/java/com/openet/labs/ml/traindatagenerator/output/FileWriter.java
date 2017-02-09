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
package com.openet.labs.ml.traindatagenerator.output;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.apache.log4j.Logger;
import org.json.JSONObject;

public class FileWriter implements Writer {

	private static Logger logger = Logger.getLogger(FileWriter.class);
	
	private Path path = Paths.get("output.json");
	private String newline;
	
	public FileWriter() {
		newline = System.getProperty("line.separator");
	}
	@Override
	public void write(JSONObject json) {
		try {
			String output = json.toString() + newline;
			Files.write(path, output.getBytes(), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
		} catch(IOException ex) {
			logger.error("Could not write the JSON", ex);
		}

	}

}
