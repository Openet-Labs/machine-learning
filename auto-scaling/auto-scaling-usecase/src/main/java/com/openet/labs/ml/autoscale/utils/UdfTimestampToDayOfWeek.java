package com.openet.labs.ml.autoscale.utils;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import org.apache.spark.sql.api.java.UDF1;

public class UdfTimestampToDayOfWeek implements UDF1<String, Double>, Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    public Double call(String ts) throws Exception {
        Date date = new Date();
        date.setTime((long) Long.parseLong(ts));

        java.util.Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        return (double) cal.get(java.util.Calendar.DAY_OF_WEEK);
    }

}
