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

import java.sql.Timestamp;

public abstract class AbstractGenerator implements TrainingDataGenerator {

	protected Timestamp tsBegin = Timestamp.valueOf("2015-10-18 00:00:00");
	protected Timestamp tsEnd = Timestamp.valueOf("2015-10-25 00:00:00");

	protected void stepTimeForward() {
		tsBegin = new Timestamp(tsBegin.getTime() + (60 * 1000L));
	}
}
