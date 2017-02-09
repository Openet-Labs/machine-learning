package com.openet.labs.ml.traindatagenerator.strategies;

import java.sql.Timestamp;

public abstract class AbstractGenerator implements TrainingDataGenerator {

	protected Timestamp tsBegin = Timestamp.valueOf("2015-10-18 00:00:00");
	protected Timestamp tsEnd = Timestamp.valueOf("2015-10-25 00:00:00");

	protected void stepTimeForward() {
		tsBegin = new Timestamp(tsBegin.getTime() + (60 * 1000L));
	}
}
