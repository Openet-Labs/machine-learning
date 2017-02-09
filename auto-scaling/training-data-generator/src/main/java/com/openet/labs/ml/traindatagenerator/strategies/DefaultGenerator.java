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
package com.openet.labs.ml.traindatagenerator.strategies;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.openet.labs.ml.traindatagenerator.MetricModel;
/**
 * A simple generation strategy that allows the user to have a consistent CPU and Memory load, but with a varying metric
 * 
 *
 */
public class DefaultGenerator extends AbstractGenerator {

	private static Logger logger = Logger.getLogger(DefaultGenerator.class);
	
	private static final String MODEL_RES = "model.csv";
	private List<String> metrics = new LinkedList<>();
	private Iterator<String> itMetric;
	
	
	public DefaultGenerator() throws IOException {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream(MODEL_RES)))) {
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                metrics.add(line);
            }
        } catch (IOException ex) {
            logger.debug("Something went wrong when loading the model" , ex);
            throw ex;
        }
		
		itMetric = metrics.iterator();
	}
	@Override
	public MetricModel getNextMetric() {
		//if we've reached the end of our interesting time period, just return null
		if(!tsBegin.before(tsEnd)) {
			return null;
		}
		MetricModel model = new MetricModel();
		if(!itMetric.hasNext()) {
			itMetric = metrics.iterator();
		}
		model.setMetric(Double.parseDouble(itMetric.next()));
		model.setCpu(50.01d);
		model.setMemory(50.01d);
		model.setTimeStamp(tsBegin);
		stepTimeForward();
		
		return model;
	}
}
