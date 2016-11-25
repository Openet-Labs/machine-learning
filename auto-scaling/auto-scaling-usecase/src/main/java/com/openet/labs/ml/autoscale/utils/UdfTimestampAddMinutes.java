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

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Calendar;
import org.apache.spark.sql.api.java.UDF2;

public class UdfTimestampAddMinutes implements UDF2<Long, Integer, Long>, Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    public Long call(Long ts, Integer minutes) throws Exception {

        Timestamp tsCurrent = new Timestamp(ts);

        java.util.Calendar cal = Calendar.getInstance();
        cal.setTime(tsCurrent);
        cal.add(java.util.Calendar.MINUTE, minutes);

        return cal.getTimeInMillis();
    }

}
