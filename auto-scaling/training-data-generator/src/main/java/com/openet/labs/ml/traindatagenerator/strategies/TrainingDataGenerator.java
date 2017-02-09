package com.openet.labs.ml.traindatagenerator.strategies;

import com.openet.labs.ml.traindatagenerator.MetricModel;

public interface TrainingDataGenerator {
	
	public MetricModel getNextMetric();
}
