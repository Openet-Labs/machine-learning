package com.openet.labs.ml.traindatagenerator.strategies;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.openet.labs.ml.traindatagenerator.MetricModel;

public class DefaultGeneratorTest {

	TrainingDataGenerator generator;
	@Before
	public void setUp() throws Exception {
		generator = new DefaultGenerator();
	}

	@Test
	public void testGeneratorAdvancesThroughTheList() {
		MetricModel model = generator.getNextMetric();
		
		assertEquals(50.01d, model.getCpu(), 0d);
		assertEquals(50.01d, model.getMemory(), 0d);
		assertEquals(389d, model.getMetric(), 0d);
		
		for(int i=0; i< 47; i++) {
			model = generator.getNextMetric();
		}
		assertEquals(50.01d, model.getCpu(), 0d);
		assertEquals(50.01d, model.getMemory(), 0d);
		assertEquals(360d, model.getMetric(), 0d);
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
