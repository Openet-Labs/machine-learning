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

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.openet.labs.ml.traindatagenerator.MetricModel;

public class SquareWaveGeneratorTest {

	private TrainingDataGenerator generator;
	@Before
	public void setUp() throws Exception {
		generator = new SquareWaveGenerator();
	}

	@Test
	public void testGetNextMetric() {
		//test that we have a square wave that goes from 90% CPU -> 10% CPU -> 90% CPU every 5 minutes
		MetricModel model = generator.getNextMetric();
		assertNotNull(model);
		assertEquals(90d, model.getCpu(), 0d);
		
		model = generator.getNextMetric();
		assertNotNull(model);
		assertEquals(90d, model.getCpu(), 0d);
		
		model = generator.getNextMetric();
		assertNotNull(model);
		assertEquals(90d, model.getCpu(), 0d);
		
		model = generator.getNextMetric();
		assertNotNull(model);
		assertEquals(90d, model.getCpu(), 0d);
		
		model = generator.getNextMetric();
		assertNotNull(model);
		assertEquals(90d, model.getCpu(), 0d);
		
		model = generator.getNextMetric();
		assertNotNull(model);
		assertEquals(10d, model.getCpu(), 0d);
		
		model = generator.getNextMetric();
		assertNotNull(model);
		assertEquals(10d, model.getCpu(), 0d);
		
		model = generator.getNextMetric();
		assertNotNull(model);
		assertEquals(10d, model.getCpu(), 0d);
		
		model = generator.getNextMetric();
		assertNotNull(model);
		assertEquals(10d, model.getCpu(), 0d);
		
		model = generator.getNextMetric();
		assertNotNull(model);
		assertEquals(10d, model.getCpu(), 0d);
		
		model = generator.getNextMetric();
		assertNotNull(model);
		assertEquals(90d, model.getCpu(), 0d);
		
	}
	
	@Test
	public void testGeneratorStopsAfterCorrectNumberOfIterations() {
		int i=0;
		while(true) {
			MetricModel model = generator.getNextMetric();
			if(null == model) {
				break;
			}
			i++;
			
		}
		//1440 minutes per day, times 7 days 
		assertEquals(1440*7, i);
	}

}
