package com.openet.labs.ml.traindatagenerator.strategies;

import com.openet.labs.ml.traindatagenerator.MetricModel;

public class SquareWaveGenerator extends AbstractGenerator {

	@Override
	public MetricModel getNextMetric() {
		//if we've reached the end of our interesting time period, just return null
		if(!tsBegin.before(tsEnd)) {
			return null;
		}
		MetricModel model = new MetricModel();
		
		int minuteOfHour = tsBegin.getMinutes();
		int mod = minuteOfHour % 10;
		if(mod <= 4) {
			model.setCpu(90.0d);
			model.setMemory(85.0d);
			model.setMetric(200d);
		} else {
			model.setCpu(10.0d);
			model.setMemory(15.0d);
			model.setMetric(20d);
		}
		model.setTimeStamp(tsBegin);
		stepTimeForward();
		return model;
	}

	
}
