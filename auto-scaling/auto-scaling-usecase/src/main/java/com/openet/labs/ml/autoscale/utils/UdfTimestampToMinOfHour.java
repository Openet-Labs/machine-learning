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
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import org.apache.spark.sql.api.java.UDF1;

public class UdfTimestampToMinOfHour implements UDF1<String, Double>, Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    public Double call(String ts) throws Exception {
        Date date = new Date();
        date.setTime((long) Long.parseLong(ts));

        java.util.Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.setTimeZone(TimeZone.getTimeZone("GMT"));
        return (double) ((cal.get(java.util.Calendar.HOUR_OF_DAY) * 60) + cal.get(java.util.Calendar.MINUTE));
    }

}
