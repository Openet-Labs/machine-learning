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
